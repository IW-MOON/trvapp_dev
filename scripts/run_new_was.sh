# run_new_was.sh
#
# !/bin/bash
#

source /home/ubuntu/trvapp/deploy_env.sh

CURRENT_PORT=$(cat /etc/nginx/conf.d/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."
echo ${DEPLOYMENT_ACTIVE}
if [ ${DEPLOYMENT_ACTIVE} -eq "dev" ]; then
  docker-compose up -f /home/ubuntu/trvapp/trvapp/docker-compose.yml -d
elif [ ${DEPLOYMENT_ACTIVE} -eq "prod" ]; then
  docker-compose up -f /home/ubuntu/trvapp/trvapp_prod/docker-compose.yml -d
else
  echo "> DEPLOYMENT_ACTIVE is not correct "
fi



#nohup java -jar -Dserver.port=${TARGET_PORT} /home/ubuntu/trvapp/trvapp/build/libs/* > /home/ubuntu/nohup.out 2>&1 &
#echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0
