# Backend. Se construye en el servidor con compose.prod.yaml (docker compose -f compose.prod.yaml up -d --build)

FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
# Primero solo lo que define las dependencias, para que Docker las guarde en caché entre builds
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -q -B dependency:go-offline
COPY src src
# Los tests van en CI; aquí solo se empaqueta
RUN ./mvnw -q -B -DskipTests package && cp target/*.jar app.jar

FROM eclipse-temurin:25-jre
RUN useradd --system --no-create-home hueco
USER hueco
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
