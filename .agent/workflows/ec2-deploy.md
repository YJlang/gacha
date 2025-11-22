---
description: AWS EC2에 Spring Boot JAR 파일 수동 배포하기
---

# AWS EC2 Spring Boot 배포 가이드

이 가이드는 JAR 파일을 직접 EC2에 업로드하여 Spring Boot 애플리케이션을 실행하는 방법을 설명합니다.

## 사전 준비사항

- AWS EC2 인스턴스 (Ubuntu 20.04/22.04 또는 Amazon Linux 2 권장)
- EC2 SSH 접속 가능 (.pem 키 파일)
- 보안 그룹에서 8080 포트 오픈 (또는 원하는 포트)

---

## 1단계: 로컬에서 JAR 파일 빌드

프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 JAR 파일을 생성합니다:

```bash
./gradlew clean build -x test
```

Windows에서는:
```bash
gradlew.bat clean build -x test
```

빌드가 완료되면 `build/libs/gacha-0.0.1-SNAPSHOT.jar` 파일이 생성됩니다.

---

## 2단계: EC2 인스턴스에 Java 21 설치

EC2 인스턴스에 SSH로 접속합니다:

```bash
ssh -i your-key.pem ubuntu@your-ec2-ip
```

### Ubuntu/Debian 계열:

```bash
# 패키지 업데이트
sudo apt update

# OpenJDK 21 설치
sudo apt install -y openjdk-21-jdk

# Java 버전 확인
java -version
```

### Amazon Linux 2023:

```bash
# Java 21 설치
sudo dnf install -y java-21-amazon-corretto

# Java 버전 확인
java -version
```

---

## 3단계: 애플리케이션 디렉토리 생성

```bash
# 애플리케이션 디렉토리 생성
sudo mkdir -p /opt/gacha
sudo chown $USER:$USER /opt/gacha

# 로그 디렉토리 생성
mkdir -p /opt/gacha/logs

# 업로드 디렉토리 생성 (이미지 저장용)
mkdir -p /opt/gacha/uploads/memories
```

---

## 4단계: JAR 파일을 EC2로 전송

로컬 터미널에서 SCP를 사용하여 JAR 파일을 EC2로 전송합니다:

```bash
scp -i your-key.pem build/libs/gacha-0.0.1-SNAPSHOT.jar ubuntu@your-ec2-ip:/opt/gacha/
```

---

## 5단계: application.properties 설정 확인

EC2에서 실행할 때 필요한 환경변수나 설정을 확인합니다.

현재 프로젝트는 다음 설정을 사용합니다:
- **데이터베이스**: AWS RDS MySQL (이미 설정됨)
- **포트**: 8080
- **파일 업로드 경로**: uploads/memories
- **파일 업로드 URL**: https://gachalikealion.duckdns.org/uploads/memories

필요시 EC2에서 환경변수로 설정을 오버라이드할 수 있습니다.

---

## 6단계: Spring Boot 애플리케이션 실행

### 방법 1: 포그라운드 실행 (테스트용)

```bash
cd /opt/gacha
java -jar gacha-0.0.1-SNAPSHOT.jar
```

### 방법 2: 백그라운드 실행 (nohup 사용)

```bash
cd /opt/gacha
nohup java -jar gacha-0.0.1-SNAPSHOT.jar > logs/application.log 2>&1 &
```

### 방법 3: 백그라운드 실행 (메모리 설정 포함)

```bash
cd /opt/gacha
nohup java -Xms512m -Xmx1024m -jar gacha-0.0.1-SNAPSHOT.jar > logs/application.log 2>&1 &
```

프로세스 ID를 확인하려면:
```bash
ps aux | grep gacha
```

---

## 7단계: 애플리케이션 상태 확인

### 로그 확인:
```bash
tail -f /opt/gacha/logs/application.log
```

### API 테스트:
```bash
curl http://localhost:8080/api/health
```

또는 외부에서:
```bash
curl http://your-ec2-ip:8080/api/health
```

---

## 8단계: 애플리케이션 중지

실행 중인 프로세스 찾기:
```bash
ps aux | grep gacha
```

프로세스 종료:
```bash
kill <PID>
```

또는 강제 종료:
```bash
kill -9 <PID>
```

---

## 9단계: systemd 서비스 설정 (선택사항 - 자동 시작)

애플리케이션을 시스템 서비스로 등록하여 자동 시작 및 관리를 원하는 경우:

서비스 파일 생성:
```bash
sudo nano /etc/systemd/system/gacha.service
```

다음 내용 입력:
```ini
[Unit]
Description=Gacha Travel Spring Boot Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/gacha
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/gacha/gacha-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=append:/opt/gacha/logs/application.log
StandardError=append:/opt/gacha/logs/application.log

[Install]
WantedBy=multi-user.target
```

서비스 활성화 및 시작:
```bash
# 서비스 리로드
sudo systemctl daemon-reload

# 서비스 시작
sudo systemctl start gacha

# 부팅 시 자동 시작 활성화
sudo systemctl enable gacha

# 서비스 상태 확인
sudo systemctl status gacha

# 로그 확인
sudo journalctl -u gacha -f
```

서비스 관리 명령어:
```bash
# 중지
sudo systemctl stop gacha

# 재시작
sudo systemctl restart gacha

# 상태 확인
sudo systemctl status gacha
```

---

## 10단계: 보안 그룹 설정

AWS 콘솔에서 EC2 보안 그룹 설정:

1. EC2 인스턴스의 보안 그룹으로 이동
2. 인바운드 규칙 편집
3. 다음 규칙 추가:
   - **유형**: Custom TCP
   - **포트**: 8080
   - **소스**: 0.0.0.0/0 (모든 IP) 또는 특정 IP만 허용

---

## 재배포 시 (업데이트)

1. 로컬에서 새로운 JAR 빌드:
   ```bash
   ./gradlew clean build -x test
   ```

2. 기존 애플리케이션 중지:
   ```bash
   # nohup 사용 시
   kill <PID>
   
   # systemd 사용 시
   sudo systemctl stop gacha
   ```

3. 새 JAR 파일 전송:
   ```bash
   scp -i your-key.pem build/libs/gacha-0.0.1-SNAPSHOT.jar ubuntu@your-ec2-ip:/opt/gacha/
   ```

4. 애플리케이션 재시작:
   ```bash
   # nohup 사용 시
   cd /opt/gacha
   nohup java -Xms512m -Xmx1024m -jar gacha-0.0.1-SNAPSHOT.jar > logs/application.log 2>&1 &
   
   # systemd 사용 시
   sudo systemctl start gacha
   ```

---

## 트러블슈팅

### 포트가 이미 사용 중인 경우:
```bash
# 8080 포트를 사용하는 프로세스 찾기
sudo lsof -i :8080

# 또는
sudo netstat -tulpn | grep 8080
```

### 메모리 부족 에러:
JVM 메모리 설정 조정:
```bash
java -Xms256m -Xmx512m -jar gacha-0.0.1-SNAPSHOT.jar
```

### 데이터베이스 연결 실패:
- RDS 보안 그룹에서 EC2 인스턴스의 IP 허용 확인
- application.properties의 DB 연결 정보 확인

### 파일 업로드 경로 권한 문제:
```bash
chmod 755 /opt/gacha/uploads
chmod 755 /opt/gacha/uploads/memories
```

---

## 참고사항

- **프로덕션 환경**에서는 Nginx를 리버스 프록시로 사용하는 것을 권장합니다 (80/443 포트)
- **SSL/TLS 인증서**는 Let's Encrypt를 사용하여 무료로 설정 가능합니다
- **로그 로테이션** 설정으로 디스크 공간 관리가 필요합니다
- **모니터링** 도구 (CloudWatch, Prometheus 등) 사용을 권장합니다
