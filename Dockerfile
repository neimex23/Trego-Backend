# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cachear dependencias: primero el pom, luego el codigo
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# t3.micro tiene 1 GB de RAM: limitar el heap para evitar OOM.
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
