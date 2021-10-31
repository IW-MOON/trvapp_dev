FROM openjdk:11-jre-slim
WORKDIR /root
COPY ./build/libs/trvapp-0.0.1-SNAPSHOT.jar .

CMD java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${active} trvapp-0.0.1-SNAPSHOT.jar