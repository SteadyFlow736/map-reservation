# map-reservation
- 네이버맵을 모티브로 삼은 예약 애플리케이션
- 네이버맵과 같이 맵 화면에서 상점을 검색하고 원하는 상점에서 서비스를 예약
- 현재는 헤어샵 검색과 헤어샵 예약만 가능
- 추후 다른 종류의 상점(음식점 등) 추가 예정
## 사용 기술 및 개발 환경
- Java, Spring Boot, MySQL, Spring Data JPA, QueryDsl
- TypeScript, Nextjs, TanStack Query, Jotai
- EC2, GitHub Actions, AWS Amplify
- 배포: https://map-reservation.oldrabbit.info
## 전체적인 구조
![structure.drawio.png](structure.drawio.png)
## ERD
![erd.png](erd.png)
## Git Convention
| type     | description |
|----------|-------------|
| feat     | 새로운 기능 추가 |
| fix      | 버그 및 로직 수정 |
| refactor | 기능 변경 없는 코드 구조, 변수/메소드/클래스 이름 등 수정 |
| style    | 코드 위치 변경 및 포맷팅, 빈 줄 추가/제거, 불필요한 import 제거 |
| test     | 테스트 코드 작성 및 리팩토링 |
| setup    | build.gradle, application.yml 등 환경 설정 |
| docs     | 문서 작업 |

```bash
# commit title format
git commit -m "{커밋 유형} #{이슈번호}: #{내용}"

# example of git conventions
git commit -m "refactor #125: `ChatService` 중복 로직 추출

예외 압축
메소드 위치 변경
메소드 이름 변경
"
```

* git branch rules
```bash
# branch name format
git checkout -b "feat/#{이슈번호}-{내용}"
```
  
