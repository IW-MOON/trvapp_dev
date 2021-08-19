#!/bin/bash

source /home/ubuntu/app/trvapp/deploy_env.sh
# Crawl current connected port of WAS
TARGET_URL=0
echo ${DEPLOYMENT_ACTIVE}
# Toggle port Number
if [ ${DEPLOYMENT_ACTIVE} == "dev" ]; then
  TARGET_URL=${DEPLOYMENT_DEV_URL}
elif [ ${DEPLOYMENT_ACTIVE} == "prod" ]; then
  TARGET_URL=${DEPLOYMENT_PROD_URL}
else
  echo "> No WAS is connected to nginx"
  exit 1
fi


echo "> Start health check of WAS at '${TARGET_URL}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
do
  echo "> #${RETRY_COUNT} trying..."
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" ${TARGET_URL}/health)

  if [ ${RESPONSE_CODE} -eq 200 ]; then
    echo "> New WAS successfully running"
    exit 0
  elif [ ${RETRY_COUNT} -eq 21 ]; then
    echo "> Health check failed."
    exit 1
  fi
  sleep 10
done
