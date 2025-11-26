# 베이스 이미지
FROM eclipse-temurin:21-jdk

# 작업 디렉토리
WORKDIR /app

# JAR 파일 복사 (빌드 후)
COPY build/libs/app-0.0.1-SNAPSHOT.jar /app/app.jar

# 포트 설정
EXPOSE 8080

# 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
# ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
# ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]

