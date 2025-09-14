# JAVA_Spring

### MyJpaShop(개인프로젝트)


**담당**

- DB설계 및 관계매칭, 구현(JPA)
- 회원가입, 상품등록, 상품주문 기능 구현

-![ezgif com-video-to-gif (1)](https://github.com/DongHoonYu96/JAVA_Spring/assets/50190387/bb73b84d-aaff-4fac-87b7-b4784ffb9d5d)

**DB 테이블 설계도**

![db1](https://github.com/DongHoonYu96/JAVA_Spring/assets/50190387/dc110340-5623-46cb-8518-64f1b2277120)

![db2](https://github.com/DongHoonYu96/JAVA_Spring/assets/50190387/889f556c-0e3a-4ffb-817f-ccc485f92235)

**ERD**

![image](https://github.com/user-attachments/assets/9d002545-f062-4273-865f-9ecc10c2d0c6)


#### 주요 문제 해결 사례

- **재고감소 로직 동시성 문제 해결**  
  - 문제: Test Code 실행 시 재고감소 로직이 의도대로 작동하지 않음  
  - 분석: `synchronized`는 proxy에 적용 불가, 낙관적 락은 재시도로 실행시간 증가(3655ms)  
  - 해결: **비관적 락** 적용 → DB 락에 의존, 재시도 불필요  
  - 결과: 실행시간 455ms, 충돌이 빈번한 상황에서 효과적인 동시성 제어  
  - [관련 Wiki](https://mini-96.tistory.com/865)  

- **Offset 기반 페이징 성능 문제 해결**  
  - 문제: 상품목록 API에서 10만번째 페이지 요청 시 600ms 지연 발생  
  - 해결:  
    1. ID 목록만 조회 후 IN 절로 필요한 필드 가져오기 (230ms)  
    2. No-Offset 페이징(where 조건 활용)으로 필요한 행만 탐색 (3ms)  
  - 결과: 600ms → 3ms로 성능 개선  
  - [관련 Wiki](https://mini-96.tistory.com/882)  

- **컬렉션 조회 N+1 문제 해결 (페이징 포함)**  
  - 문제: 주문과 연관된 상품 조회 시 N+1 문제 발생, 페이징을 WAS에서 처리  
  - 해결:  
    - ToOne 관계 → 페치조인으로 1번의 SQL 조회  
    - ToMany 관계 → 지연 로딩 + BatchSize로 IN 절 조회  
  - 결과: `1+N+M`개의 쿼리 → `1+1+1`개 쿼리로 최적화  
  - [관련 Wiki](https://mini-96.tistory.com/829)  

- **상품 할인 정책 구현 문제 해결**  
  - 문제: 할인 정책을 `discountType` + if-else로 구현 → 새로운 정책 추가 시 기존 코드 수정 필요 (OCP 위반)  
  - 해결: **상속 대신 합성**을 사용, `DiscountPolicy`를 `Item`에 주입  
  - 결과: 할인 없음, 중복 할인 등 다양한 요구사항을 기존 코드 수정 없이 해결 가능 → 클래스 1개 추가만으로 확장 가능  
  - [관련 Wiki](https://mini-96.tistory.com/928)

- **API 예외처리 문제 해결**  
  - 문제: API 호출에서 예외 발생 시 WAS가 `/error` 경로로 재요청 → 컨트롤러에 정상 처리/예외 처리 코드가 혼재  
  - 해결: `@ExceptionHandler`로 불필요한 재요청 방지, `@ControllerAdvice`로 예외처리 로직 분리  
  - 결과: 보다 정밀한 API 응답 제공, 컨트롤러 코드 간결화  
  - [관련 Wiki](https://mini-96.tistory.com/876)  
