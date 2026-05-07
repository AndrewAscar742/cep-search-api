# comando para buildar a imagem: docker build -t {image_name}:latest
# comando para rodar o container com a imagem: docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE={prd OR dsv} {image_name}:latest

# ===============================
# Stage 1 - Builder
# ===============================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /cep-search-jar

RUN apt-get update \
    && apt-get install -y --no-install-recommends maven

COPY pom.xml .
COPY src src

RUN mvn clean package

# ===============================
# Stage 2 - Runtime
# ===============================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /cep-search-jar

COPY --from=builder /cep-search-jar/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Xms512m -Xmx1024m -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prd}"]