# Amazon Linux 2023에서 Docker Compose 설치
# 참조: https://kdev.ing/install-docker-compose-in-amazon-linux-2023/

# 시스템 패키지를 최신 상태로 업데이트한다.
sudo dnf update -y

# DNF를 사용하여 Docker를 설치한다.
sudo dnf install docker -y

# 사용자 계정을 docker 그룹에 추가 - 사용자 계정이 Docker 데몬과 상호작용할 수 있는 권한 획득(sudo 없이 docker 명령어 실행)
# 이 명령어 실행 후에는 사용자가 변경 사항을 적용하기 위해 로그아웃하고 다시 로그인해야 할 수 있다.
sudo usermod -aG docker "$USER"

# Docker 서비스를 시작하고 부팅 시 자동으로 실행되도록 설정한다.
sudo systemctl start docker
sudo systemctl enable docker

# 최신 버전의 Docker Compose Plugin을 설치한다.
sudo mkdir -p /usr/local/lib/docker/cli-plugins/
sudo curl -SL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-$(uname -m)" -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

docker compose version
