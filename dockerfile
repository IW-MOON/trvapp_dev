FROM ubuntu:16.04
RUN apt-get update && apt-get install -y \
curl

FROM openjdk:11-jre-slim
WORKDIR /root
COPY ./build/libs/trvapp-0.0.1-SNAPSHOT.jar .

CMD java -jar -Dspring.profiles.active=${active} trvapp-0.0.1-SNAPSHOT.jar