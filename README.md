# Lotto Spring

이전에 구현했던 프로젝트의 웹 어플리케이션 포팅 버전

## Rest API Documentation

- [v0.0.1-SNAPSHOT](http://www.lotto-spring.kro.kr:8080/docs/index.html)
  - URL 접속 안되면 [파일로 보기](https://github.com/BI-kotlin-in-action-2ND/HyeonminShin-LottoSpring/tree/main/src/main/resources/static/docs/index.html)

## Wiki

- [Lotto Spring Wiki](https://github.com/BI-kotlin-in-action-2ND/HyeonminShin-LottoSpring/wiki)

## 기능 명세

### 필수 요구사항

1. `유저`
   - 다수의 유저 존재 가능
   - 로또 구매 기능 (구매 수량 지정, 구매 방식(수동/자동) 선택, 남은 돈 저장)
   - 충전 기능 (서버 저장, 유저마다 별도의 공간 마련 -> `지갑`)
   - 출금 기능
2. `로또`
   - 로또 생성 (수동/자동)
     - 승리 로또는 10분마다 자동 생성
   - 로또 조회 (현재 회차를 제외하고 과거 로또 번호 조회 가능)
   - 동시성 이슈 해결
3. `Restful API`
   - Restful API 구현
   - API 명세 작성

### 선택 요구사항

**한 개 이상** 적용해보기
적용한 선택사항에는 체크 표시 ✅

1. ✅ 모든 정보를 DB에 넣고 관리해보세요
   - In memory DB(h2 , HSQLDB, derby)도 좋고 상용 DBMS(mysql, oracle)도 좋습니다
2. 사용자 회원가입과 로그인 및 세션관리 등을 해보세요
3. 로그 시스템을 만들어서 자신의 로그뿐만 아니라 타인의 로또 구매 로그 및 당첨 로그도 볼수 있도록 해보세요
   - 중앙 로그 시스템(loglogstash, fluentd) 사용하기
   - 내부 에러 기록용, 외부 노출용 따로 만들기
4. ✅ 스웨거 혹은 RestDoc과 같은 API 문서화 툴을 써보세요
5. ✅ 당첨 시스템을 현실 세계와 똑같이 로또를 모았다가 한번에 맞춰보는 시스템으로 구현해보세요
   - WinningLottoController의 matchUserLottoByRound()로 한번에 조회 가능
6. 간단한 화면을 만들어 보세요
7. ✅ aws나 oracle cloud, azure등에 서버를 올려보세요
8. ✅ 관리자를 정의해보고 관리자는 현재의 로또 번호를 확인할수 있도록 해보세요
9. 관리자를 정의해보고 관리자가 유저를 탈퇴/ 잔고 회수 / 잔고 추가 등의 동작을 할 수 있도록 해보세요
   - (8,9번) Spring Security로 임시 관리자 프로세스 적용해보기
10. `(WIP)` MSA환경을 고려해서 개발해보세요
