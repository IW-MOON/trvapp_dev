#!/bin/bash

source /home/ubuntu/app/trvapp/deploy_env.sh
# Crawl current connected port of WAS
TARGET_PORT=0
echo ${DEPLOYMENT_ACTIVE}
# Toggle port Number
if [ ${DEPLOYMENT_ACTIVE} == "dev" ]; then
  TARGET_PORT=8081
elif [ ${DEPLOYMENT_ACTIVE} == "prod" ]; then
  TARGET_PORT=8082
else
  echo "> No WAS is connected to nginx"
  exit 1
fi


echo "> Start health check of WAS at 'https://127.0.0.1:${TARGET_PORT}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
do
  echo "> #${RETRY_COUNT} trying..."
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" https://127.0.0.1:${TARGET_PORT}/health)

  if [ ${RESPONSE_CODE} -eq 200 ]; then
    echo "> New WAS successfully running"
    exit 0
  elif [ ${RETRY_COUNT} -eq 21 ]; then
    echo "> Health check failed."
    exit 1
  fi
  sleep 10
done
