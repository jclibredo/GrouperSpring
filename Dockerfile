#DEPLOYEMNT STARTS HERE
#===============================================
# # Stage 1: Build stage using JDK 17
# FROM eclipse-temurin:17-jdk-alpine AS build
# WORKDIR /app

# # Copy Maven wrapper and POM for layer caching
# COPY .mvn/ .mvn
# COPY mvnw pom.xml ./
# RUN chmod +x mvnw
# RUN ./mvnw dependency:go-offline -B

# # Copy source code and build jar
# COPY src ./src
# RUN ./mvnw clean package -DskipTests

# # Stage 2: Minimal runtime stage using JRE 17
# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app

# # Create spring user and create upload directory
# RUN addgroup -S spring && adduser -S spring -G spring && \
#     mkdir -p /app/reports && \
#     chown -R spring:spring /app

# USER spring

# # Copy JAR file from build stage
# COPY --from=build /app/target/*.jar app.jar

# EXPOSE 8081

# ENTRYPOINT ["java", "-jar", "app.jar"]

#CODE BASE DEVELOPMENT STARTS HERE
#===============================================
# --- Stage 1: Build the JAR using Maven Wrapper ---
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

# Copy Maven wrapper and dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]