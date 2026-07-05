# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila el proyecto saltando las pruebas para que el despliegue sea más rápido
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Expone el puerto
EXPOSE 8080
# Comando para iniciar Spring Boot.
# -XX:TieredStopAtLevel=1 y -XX:+UseSerialGC recortan el tiempo de arranque en frío en
# instancias con CPU compartida/limitada (como Render Free/Starter): evitan la compilación
# JIT de nivel superior y el overhead de un recolector de basura pensado para multi-core,
# ninguno de los cuales aporta nada durante los ~140s de boot de un proceso de una sola
# petición a la vez (WEB_CONCURRENCY=1).
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-jar", "app.jar"]