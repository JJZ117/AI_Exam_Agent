package org.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.AiService;
import org.example.vo.AiGenerateRequestVo;
import org.example.vo.QuestionImportVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
public class AiServiceImpl implements AiService {

    private static final int DEFAULT_COUNT = 5;
    private static final int MAX_COUNT = 20;

    @Value("${kimi.api-key}")
    private String apiKey;

    @Value("${kimi.url}")
    private String url;

    @Value("${kimi.chat-model:moonshot-v1-8k}")
    private String chatModel;

    @Value("${kimi.vision-model:moonshot-v1-32k-vision-preview}")
    private String visionModel;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<QuestionImportVo> aiGenerateQuestions(AiGenerateRequestVo request) throws Exception {
        int count = normalizeCount(request == null ? null : request.getCount());
        String topic = request == null ? null : request.getTopic();
        List<String> questionTypes = normalizeQuestionTypes(request == null ? null : request.getQuestionTypes());
        if (!StringUtils.hasText(topic)) {
            throw new IllegalArgumentException("topic must not be blank");
        }

        String prompt = """
                Generate %d questions about \"%s\" and return only a JSON array.
                The generated questions must use only these types: %s.
                Each question object must contain these fields:
                title: question text
                type: only CHOICE, JUDGE, or TEXT
                answer: the correct answer
                analysis: a short explanation in 1-3 sentences
                choices: only for CHOICE questions, format [{\"content\":\"option text\"}]
                For JUDGE questions, answer must be true or false.
                Try to distribute the questions across the allowed types when more than one type is requested.
                """.formatted(count, topic.trim(), String.join(", ", questionTypes));

        return parse(sendChatRequest(buildTextRequest(prompt, chatModel)), questionTypes);
    }

    @Override
    public List<QuestionImportVo> aiGenerateSimilarQuestionsByImage(MultipartFile image, String topic, Integer count, List<String> questionTypes) throws Exception {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("image must not be empty");
        }

        int normalizedCount = normalizeCount(count);
        List<String> normalizedTypes = normalizeQuestionTypes(questionTypes);
        String prompt = buildImagePrompt(topic, normalizedCount, normalizedTypes);

        return parse(sendChatRequest(buildImageRequest(prompt, image, visionModel)), normalizedTypes);
    }

    private List<QuestionImportVo> parse(String response, List<String> allowedTypes) throws Exception {
        JsonNode root;
        try {
            root = mapper.readTree(response);
        } catch (Exception ignored) {
            root = mapper.readTree(extractJsonPayload(response));
        }
        if (!root.isArray()) {
            throw new RuntimeException("AI response is not a JSON array");
        }

        List<QuestionImportVo> list = new ArrayList<>();
        for (JsonNode item : root) {
            QuestionImportVo vo = new QuestionImportVo();
            vo.setTitle(item.path("title").asText(""));
            vo.setType(normalizeType(item.path("type").asText("")));
            if (!allowedTypes.contains(vo.getType())) {
                continue;
            }
            vo.setAnswer(normalizeAnswer(vo.getType(), item.path("answer").asText("")));
            vo.setAnalysis(item.path("analysis").asText(""));

            if ("CHOICE".equals(vo.getType())) {
                List<QuestionImportVo.Choice> choices = new ArrayList<>();
                JsonNode choiceNodes = item.path("choices");
                if (choiceNodes.isArray()) {
                    for (JsonNode choiceNode : choiceNodes) {
                        String choiceContent = choiceNode.path("content").asText("").trim();
                        if (!choiceContent.isBlank()) {
                            choices.add(new QuestionImportVo.Choice(choiceContent));
                        }
                    }
                }
                vo.setChoices(choices);
            }

            list.add(vo);
        }

        if (list.isEmpty()) {
            throw new RuntimeException("AI did not generate any questions");
        }

        return list;
    }

    private String sendChatRequest(String requestBody) throws Exception {
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = conn.getResponseCode();
        try (InputStream responseStream = statusCode >= 400 ? conn.getErrorStream() : conn.getInputStream()) {
            if (responseStream == null) {
                throw new RuntimeException("AI request failed with status " + statusCode);
            }

            JsonNode response = mapper.readTree(responseStream);
            String content = response.path("choices").path(0).path("message").path("content")
                    .asText("")
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            if (content.isBlank()) {
                String errorMessage = response.path("error").path("message").asText("");
                if (StringUtils.hasText(errorMessage)) {
                    throw new RuntimeException("AI request failed: " + errorMessage);
                }
                throw new RuntimeException("AI returned empty content");
            }
            return content;
        }
    }

    private String buildTextRequest(String prompt, String model) throws Exception {
        JsonNode body = mapper.createObjectNode()
                .put("model", model)
                .set("messages", mapper.createArrayNode()
                        .add(mapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt)));
        return mapper.writeValueAsString(body);
    }

    private String buildImageRequest(String prompt, MultipartFile image, String model) throws Exception {
        JsonNode body = mapper.createObjectNode()
                .put("model", model)
                .set("messages", mapper.createArrayNode()
                        .add(mapper.createObjectNode()
                                .put("role", "user")
                                .set("content", mapper.createArrayNode()
                                        .add(mapper.createObjectNode()
                                                .put("type", "text")
                                                .put("text", prompt))
                                        .add(mapper.createObjectNode()
                                                .put("type", "image_url")
                                                .set("image_url", mapper.createObjectNode()
                                                        .put("url", buildDataUrl(image)))))));
        return mapper.writeValueAsString(body);
    }

    private String buildImagePrompt(String topic, int count, List<String> questionTypes) {
        String topicHint = StringUtils.hasText(topic)
                ? "Prefer the topic direction: " + topic.trim() + "."
                : "If the image contains a clear subject, keep the generated questions around the same knowledge point.";

        return """
                Read the uploaded image carefully. It may contain one or more questions, options, diagrams, formulas, or handwritten notes.
                First infer the original question style, knowledge point, and difficulty from the image content.
                Then generate %d similar but not identical questions and return only a JSON array.
                %s
                The generated questions must use only these types: %s.
                Each question object must contain these fields:
                title: question text
                type: only CHOICE, JUDGE, or TEXT
                answer: the correct answer
                analysis: a short explanation in 1-3 sentences
                choices: only for CHOICE questions, format [{"content":"option text"}]
                For JUDGE questions, answer must be true or false.
                Make the new questions different from the original wording while staying similar in topic and difficulty.
                Try to distribute the questions across the allowed types when more than one type is requested.
                """.formatted(count, topicHint, String.join(", ", questionTypes));
    }

    private String buildDataUrl(MultipartFile image) throws Exception {
        String contentType = StringUtils.hasText(image.getContentType()) ? image.getContentType() : "image/jpeg";
        String base64 = Base64.getEncoder().encodeToString(image.getBytes());
        return "data:" + contentType + ";base64," + base64;
    }

    private int normalizeCount(Integer count) {
        if (count == null) {
            return DEFAULT_COUNT;
        }
        return Math.max(1, Math.min(MAX_COUNT, count));
    }

    private List<String> normalizeQuestionTypes(List<String> questionTypes) {
        Set<String> normalizedTypes = new LinkedHashSet<>();
        if (questionTypes != null) {
            for (String type : questionTypes) {
                String normalized = normalizeType(type);
                if (type != null && !type.trim().isBlank()) {
                    normalizedTypes.add(normalized);
                }
            }
        }

        if (normalizedTypes.isEmpty()) {
            normalizedTypes.add("CHOICE");
            normalizedTypes.add("JUDGE");
            normalizedTypes.add("TEXT");
        }

        return new ArrayList<>(normalizedTypes);
    }

    private String normalizeType(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        return switch (normalized) {
            case "CHOICE", "SINGLE_CHOICE", "MULTIPLE_CHOICE" -> "CHOICE";
            case "JUDGE", "TRUE_FALSE", "BOOLEAN" -> "JUDGE";
            case "TEXT", "ESSAY", "SHORT_ANSWER" -> "TEXT";
            default -> "CHOICE";
        };
    }

    private String normalizeAnswer(String type, String answer) {
        String normalized = answer == null ? "" : answer.trim();
        if ("JUDGE".equals(type)) {
            if ("true".equalsIgnoreCase(normalized) || "correct".equalsIgnoreCase(normalized) || "right".equalsIgnoreCase(normalized)) {
                return "true";
            }
            if ("false".equalsIgnoreCase(normalized) || "wrong".equalsIgnoreCase(normalized)) {
                return "false";
            }
        }
        return normalized;
    }

    private String extractJsonPayload(String text) {
        if (text == null || text.isBlank()) {
            throw new RuntimeException("AI returned empty content");
        }

        int arrayStart = text.indexOf('[');
        if (arrayStart >= 0) {
            String arrayPayload = extractBalanced(text, arrayStart, '[', ']');
            if (arrayPayload != null) {
                return arrayPayload;
            }
        }

        int objectStart = text.indexOf('{');
        if (objectStart >= 0) {
            String objectPayload = extractBalanced(text, objectStart, '{', '}');
            if (objectPayload != null) {
                return objectPayload;
            }
        }

        throw new RuntimeException("Unable to extract JSON payload from AI response: " + text);
    }

    private String extractBalanced(String text, int start, char openChar, char closeChar) {
        boolean inString = false;
        boolean escaped = false;
        int depth = 0;

        for (int i = start; i < text.length(); i++) {
            char current = text.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (current == '\\') {
                escaped = true;
                continue;
            }

            if (current == '"') {
                inString = !inString;
                continue;
            }

            if (inString) {
                continue;
            }

            if (current == openChar) {
                depth++;
            } else if (current == closeChar) {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1);
                }
            }
        }

        return null;
    }
}
