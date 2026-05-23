# Estágio 1: Build da aplicação
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução (Runtime)
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
# Copia apenas o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]