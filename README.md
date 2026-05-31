# AI_Exam_Agent
# KimiAI Exam System

## 1. Project Overview

KimiAI Exam System is a Spring Boot based online examination backend project. It is designed to support question management, exam creation, answer submission, wrong-question tracking, and AI-assisted question generation. The system combines traditional exam workflows with AI capabilities to improve question creation efficiency and learning support.

This project integrates:

- Spring Boot for backend service development
- MyBatis for database persistence
- MySQL for data storage
- MinIO for object storage
- EasyExcel for Excel-based question import
- Kimi AI API for AI-generated and image-based question generation

## 2. Main Features

- Create exams and return the generated exam ID
- Query exam details by exam ID
- Submit answers and receive grading results
- Generate questions with AI based on topic and type
- Generate similar questions from uploaded images
- Manage wrong-question records
- Favorite and remove wrong questions
- Provide static frontend pages for exam, AI generation, and wrong-question book

## 3. Tech Stack

- Java 21
- Spring Boot 3.2.5
- Maven
- MyBatis
- MySQL
- Lombok
- EasyExcel 3.3.2
- Fastjson2
- OkHttp
- MinIO Java SDK
- Spring Validation
- Swagger / SpringDoc OpenAPI

## 4. Project Structure

```text
KimiAI_exam
├── src
│   ├── main
│   │   ├── java
│   │   │   └── org/example
│   │   │       ├── common
│   │   │       ├── config
│   │   │       ├── controller
│   │   │       ├── entity
│   │   │       ├── mapper
│   │   │       ├── service
│   │   │       └── vo
│   │   └── resources
│   │       ├── application.yml
│   │       ├── mapper
│   │       └── static
│   └── test
├── pom.xml
└── README.md
```

## 5. Core Modules

### 5.1 Exam Module

Responsible for creating exams and retrieving exam details.

- `POST /api/exam/create`
- `GET /api/exam/{id}`

### 5.2 Answer Submission Module

Responsible for receiving user answers and returning grading results.

- `POST /api/questions/submit`

### 5.3 AI Question Generation Module

Supports AI-based question generation using text prompts or uploaded images.

- `POST /api/questions/batch/ai-generate`
- `POST /api/questions/batch/ai-generate-by-image`

### 5.4 Wrong Question Book Module

Supports listing, favoriting, and removing wrong questions.

- `GET /api/wrong-questions`
- `POST /api/wrong-questions/favorite`
- `DELETE /api/wrong-questions/{questionId}`

## 6. Environment Requirements

Before running the project, make sure the following software is installed:

- JDK 21
- Maven 3.9 or above
- MySQL 8.x
- MinIO server
- IntelliJ IDEA or another Java IDE

## 7. Configuration

The main configuration file is:

- `src/main/resources/application.yml`

The project currently requires configuration for:

- MySQL database connection
- Kimi AI API
- MinIO object storage

Example configuration:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/exam_database?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: classpath:/mapper/*.xml

kimi:
  url: https://api.moonshot.cn/v1/chat/completions
  api-key: your_kimi_api_key
  chat-model: moonshot-v1-8k
  vision-model: moonshot-v1-32k-vision-preview

minio:
  endpoint: http://localhost:9000
  access-key: your_minio_access_key
  secret-key: your_minio_secret_key
  bucket-name: wrong-question-book
```

Important:

- Do not commit real passwords, secret keys, or API keys to GitHub
- Replace all sensitive values with placeholders before submission

## 8. Database Preparation

Create a MySQL database before starting the project:

```sql
CREATE DATABASE exam_database;
```

Then configure the correct username and password in `application.yml`.

If your project includes table creation scripts, import them into MySQL before starting the backend. If table scripts are managed elsewhere, make sure the database schema is prepared in advance.

## 9. Build and Run

### 9.1 Run with Maven

```bash
mvn spring-boot:run
```

### 9.2 Package the Project

```bash
mvn clean package
```

After packaging:

```bash
java -jar target/KimiAI_exam-1.0-SNAPSHOT.jar
```

## 10. Frontend Pages

The project includes static frontend pages under:

- `src/main/resources/static/exam.html`
- `src/main/resources/static/ai.html`
- `src/main/resources/static/wrong-book.html`

Related frontend resources:

- `src/main/resources/static/exam.css`
- `src/main/resources/static/exam.js`

These pages can be used as simple test or demo interfaces for the backend services.

## 11. API Summary

### Exam APIs

| Method | Path | Description |
|---|---|---|
| POST | `/api/exam/create` | Create a new exam |
| GET | `/api/exam/{id}` | Get exam details by ID |

### Question and Answer APIs

| Method | Path | Description |
|---|---|---|
| POST | `/api/questions/submit` | Submit answers and receive grading result |
| POST | `/api/questions/batch/ai-generate` | Generate questions with AI |
| POST | `/api/questions/batch/ai-generate-by-image` | Generate similar questions from image upload |

### Wrong Question APIs

| Method | Path | Description |
|---|---|---|
| GET | `/api/wrong-questions` | List wrong questions |
| POST | `/api/wrong-questions/favorite` | Favorite wrong questions |
| DELETE | `/api/wrong-questions/{questionId}` | Remove a wrong question |

## 12. Example Workflow

A typical system workflow is:

1. Prepare the database and storage service
2. Configure `application.yml`
3. Start the Spring Boot project
4. Open static frontend pages or use Postman to test APIs
5. Create exams and submit answers
6. Use AI to generate extra questions
7. Review wrong questions in the wrong-question book

## 13. How to Push This Project to GitHub

If Git is installed on your machine, run the following commands in the project root:

```bash
git init
git add .
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/UOA-CS732-S1-2026/cs732-tech-tutorial-JJZ117.git
git push -u origin main
```

If the repository has already been initialized locally, use:

```bash
git add .
git commit -m "update project"
git push -u origin main
```



