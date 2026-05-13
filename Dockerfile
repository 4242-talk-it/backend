# 1단계: 빌드 스테이지 (Gradle 빌드 수행)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 프로젝트 파일 전체 복사
COPY . .

# Gradle 실행 권한 부여 및 JAR 파일 생성
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행 스테이지 (가벼운 이미지 생성)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 빌드 스테이지에서 생성된 jar만 복사 (경로 주의)
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# 환경 변수 및 실행 설정
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]