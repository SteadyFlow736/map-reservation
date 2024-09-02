if [ -z "${API_SERVER_IMAGE_NAME}" ]; then
  echo "스크립트 실행 전 API_SERVER_IMAGE_NAME 환경변수 값을 설정해 주세요!"
  exit 1
fi

docker compose -f docker-compose-prod.yml --project-name map-reservation-prod up -d
