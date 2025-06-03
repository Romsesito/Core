# Usa una imagen base con JDK 21
FROM eclipse-temurin:21-jdk-jammy

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de Gradle necesarios para construir el proyecto
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Descarga las dependencias de Gradle
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# Copia el código fuente de la aplicación
COPY src src

# Construye la aplicación generando el archivo JAR ejecutable
RUN ./gradlew bootJar --no-daemon

# Limpia archivos innecesarios para reducir el tamaño del contenedor
RUN rm -rf gradle src build.gradle settings.gradle gradlew

# Expone el puerto en el que se ejecuta la aplicación Spring Boot
EXPOSE 8080

# Define el comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]