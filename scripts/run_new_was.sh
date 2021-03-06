# run_new_was.sh
#
# !/bin/bash
#
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)

cp /home/ubuntu/app/trvapp/application.yml /home/ubuntu/app/trvapp/trvapp/build/libs/application.yml
cp /home/ubuntu/app/trvapp/application-oauth.yml /home/ubuntu/app/trvapp/trvapp/build/libs/application-oauth.yml
source /home/ubuntu/app/trvapp/deploy_env.sh

CURRENT_PORT=$(cat /etc/nginx/conf.d/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."
echo ${DEPLOYMENT_ACTIVE}
if [ ${DEPLOYMENT_ACTIVE} == "dev" ]; then
  cp /home/ubuntu/app/trvapp/application-dev.yml /home/ubuntu/app/trvapp/trvapp/build/libs/application-dev.yml
  cp /home/ubuntu/app/trvapp/AuthKey_555KUM5BUR.p8 /home/ubuntu/app/trvapp/trvapp/build/libs/AuthKey_555KUM5BUR.p8
  docker-compose -f /home/ubuntu/app/trvapp/trvapp/docker-compose.yml up -d --force-recreate
elif [ ${DEPLOYMENT_ACTIVE} == "prod" ]; then
  cp /home/ubuntu/app/trvapp/trvapp/build/libs/* /home/ubuntu/app/trvapp/trvapp_prod/
  docker-compose -f /home/ubuntu/app/trvapp/trvapp_prod/docker-compose.yml up -d
else
  echo "> DEPLOYMENT_ACTIVE is not correct "
fi



#nohup java -jar -Dserver.port=${TARGET_PORT} /home/ubuntu/trvapp/trvapp/build/libs/* > /home/ubuntu/nohup.out 2>&1 &
#echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0
