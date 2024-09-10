# 런타임 이미지로 지정
FROM eclipse-temurin:17

# 애플리케이션을 실행할 작업 디렉토리를 생성
WORKDIR /app

# 생성된 JAR 파일을 런타임 이미지로 복사
COPY /build/libs/*SNAPSHOT.jar /app/map-reservation.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "map-reservation.jar"]
