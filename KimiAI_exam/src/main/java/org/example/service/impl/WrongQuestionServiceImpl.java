package org.example.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.example.config.MinioProperties;
import org.example.entity.Question;
import org.example.service.QuestionService;
import org.example.service.WrongQuestionService;
import org.example.vo.WrongQuestionFavoriteRequest;
import org.example.vo.WrongQuestionVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WrongQuestionServiceImpl implements WrongQuestionService {

    private static final TypeReference<List<WrongQuestionVo>> WRONG_LIST_TYPE = new TypeReference<>() {};
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final QuestionService questionService;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final ObjectMapper objectMapper;

    public WrongQuestionServiceImpl(QuestionService questionService,
                                    MinioClient minioClient,
                                    MinioProperties minioProperties,
                                    ObjectMapper objectMapper) {
        this.questionService = questionService;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<WrongQuestionVo> listByUserKey(String userKey) throws Exception {
        return readWrongQuestions(normalizeUserKey(userKey));
    }

    @Override
    public synchronized List<WrongQuestionVo> favorite(WrongQuestionFavoriteRequest request) throws Exception {
        String userKey = normalizeUserKey(request.getUserKey());
        if (request.getQuestionId() == null) {
            throw new IllegalArgumentException("questionId must not be null");
        }

        Question question = questionService.getById(request.getQuestionId());
        if (question == null) {
            throw new IllegalArgumentException("question does not exist");
        }

        List<WrongQuestionVo> list = readWrongQuestions(userKey);
        list.removeIf(item -> request.getQuestionId().equals(item.getQuestionId()));

        WrongQuestionVo favorite = new WrongQuestionVo();
        favorite.setQuestionId(question.getId());
        favorite.setTitle(question.getTitle());
        favorite.setType(question.getType());
        favorite.setUserAnswer(request.getUserAnswer());
        favorite.setCorrectAnswer(question.getAnswer());
        favorite.setAnalysis(question.getAnalysis());
        favorite.setChoices(question.getChoices());
        favorite.setCollectedAt(LocalDateTime.now().format(TIME_FORMATTER));

        list.add(0, favorite);
        writeWrongQuestions(userKey, list);
        return sortByCollectedAtDesc(list);
    }

    @Override
    public synchronized List<WrongQuestionVo> remove(String userKey, Long questionId) throws Exception {
        if (questionId == null) {
            throw new IllegalArgumentException("questionId must not be null");
        }

        String normalizedUserKey = normalizeUserKey(userKey);
        List<WrongQuestionVo> list = readWrongQuestions(normalizedUserKey);
        list.removeIf(item -> questionId.equals(item.getQuestionId()));
        writeWrongQuestions(normalizedUserKey, list);
        return sortByCollectedAtDesc(list);
    }

    private List<WrongQuestionVo> readWrongQuestions(String userKey) throws Exception {
        ensureBucketExists();
        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(buildObjectName(userKey))
                .build())) {
            List<WrongQuestionVo> list = objectMapper.readValue(inputStream, WRONG_LIST_TYPE);
            return sortByCollectedAtDesc(list == null ? new ArrayList<>() : list);
        } catch (ErrorResponseException ex) {
            if ("NoSuchKey".equalsIgnoreCase(ex.errorResponse().code())) {
                return new ArrayList<>();
            }
            throw ex;
        }
    }

    private void writeWrongQuestions(String userKey, List<WrongQuestionVo> wrongQuestions) throws Exception {
        ensureBucketExists();
        byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(wrongQuestions);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(buildObjectName(userKey))
                    .stream(inputStream, bytes.length, -1)
                    .contentType("application/json")
                    .build());
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucketName())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build());
        }
    }

    private String buildObjectName(String userKey) {
        return "favorites/" + sanitize(userKey) + ".json";
    }

    private String normalizeUserKey(String userKey) {
        return StringUtils.hasText(userKey) ? userKey.trim() : "default-user";
    }

    private String sanitize(String userKey) {
        return userKey.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private List<WrongQuestionVo> sortByCollectedAtDesc(List<WrongQuestionVo> list) {
        list.sort(Comparator.comparing(
                item -> StringUtils.hasText(item.getCollectedAt()) ? item.getCollectedAt() : "",
                Comparator.reverseOrder()
        ));
        return list;
    }
}
