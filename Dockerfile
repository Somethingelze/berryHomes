# --- Этап 1: Сборка приложения ---
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Копируем файлы сборки Gradle для кэширования зависимостей
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Скачиваем зависимости (без сборки самого приложения)
RUN ./gradlew dependencies --no-daemon || true

# Копируем исходный код и собираем fat-jar (пропуская тесты)
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# --- Этап 2: Запуск приложения ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Создаем безопасного системного пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем собранный jar-файл из предыдущего этапа
COPY --from=builder /app/build/libs/berryHomes-0.0.1.jar app.jar

# Открываем стандартный порт Spring Boot
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]