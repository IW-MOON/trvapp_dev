# run_new_was.sh
#
# !/bin/bash
#
CURRENT_PORT=$(cat /etc/nginx/conf.d/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."

if [ "$DEPLOYMENT_ACTIVE" -eq "dev" ]; then
  TARGET_PORT=8081
elif [ "$DEPLOYMENT_ACTIVE" -eq "prod" ]; then
  TARGET_PORT=8082
else
  echo "> DEPLOYMENT_ACTIVE is not correct "
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ "$DEPLOYMENT_ACTIVE" -eq "dev" ]; then
  docker-compose up -f ../docker-compose.yml -d
elif [ "$DEPLOYMENT_ACTIVE" -eq "prod" ]; then
  docker-compose up -f ../../trvapp_prod/docker-compose.yml -d


#nohup java -jar -Dserver.port=${TARGET_PORT} /home/ubuntu/trvapp/trvapp/build/libs/* > /home/ubuntu/nohup.out 2>&1 &
#echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0