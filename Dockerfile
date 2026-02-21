# Multi-stage build for Yapnet application
FROM maven:3.8.4-openjdk-17 AS backend-builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Frontend build stage
FROM node:16-alpine AS frontend-builder

WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci --only=production

COPY frontend/ ./
RUN npm run build

# Final stage
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy backend JAR
COPY --from=backend-builder /app/target/ics-wtp-yapnet-0.0.1-SNAPSHOT.jar app.jar

# Copy frontend build
COPY --from=frontend-builder /app/dist ./frontend/dist

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 