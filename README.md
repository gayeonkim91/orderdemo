# orderdemo

[![CI](https://github.com/gayeonkim91/orderdemo/actions/workflows/ci.yml/badge.svg)](https://github.com/gayeonkim91/orderdemo/actions/workflows/ci.yml)

## 프로젝트 소개

`orderdemo`는 Spring Boot로 작성한 주문 데모 프로젝트다.
주문 생성과 주문 조회에 집중했고, 이 과정에서 필요한 기본 검증과 예외 응답을 포함했다.

## 현재 구현 범위

구현한 기능은 아래와 같다.

- 주문 생성 API
- 주문 단건 조회 API
- 주문 요청 입력값 검증
- 중복 상품 ID 주문 방지
- 존재하지 않는 상품 검증
- 재고 부족 검증과 재고 차감
- 주문 총액 계산
- 주문 시점 상품명/주문 단가 저장
- 공통 예외 응답 처리

구현하지 않은 항목은 아래와 같다.

- 상품 등록/조회 API
- 주문 취소/상태 변경
- 결제/배송 처리
- 동시성 제어

재고 차감은 단일 트랜잭션 안에서 처리하며, 동시 주문 경쟁 상황의 정합성은 아직 별도로 다루지 않았다.

## 기술 스택

- Java 21
- Spring Boot 3.5.12
- Spring Web
- Spring Data JPA
- Spring Validation
- Flyway
- MySQL
- Gradle
- JUnit 5
- Docker Compose
- Testcontainers
- GitHub Actions
- `ulid-creator`

## 패키지 구조

```text
src/main/java/com/example/orderdemo
├── api
│   ├── error
│   └── order
├── application
│   └── order
├── common
│   ├── config
│   └── exception
├── domain
│   ├── order
│   └── product
└── repository
```

패키지별 역할은 아래와 같다.

- `api/order`
  [OrderController.java](src/main/java/com/example/orderdemo/api/order/OrderController.java) 에서 `POST /api/orders`, `GET /api/orders/{orderNumber}`를 처리한다.
  요청 DTO는 `CreateOrderRequest`, `CreateOrderItemRequest`, 응답 DTO는 `OrderCreateResponse`, `OrderDetailResponse`, `OrderItemResponse`로 분리했다.
- `api/error`
  [GlobalExceptionHandler.java](src/main/java/com/example/orderdemo/api/error/GlobalExceptionHandler.java) 가 `BusinessException`, validation 예외, 기타 예외를 API 응답으로 변환한다.
- `application/order`
  [OrderCreateService.java](src/main/java/com/example/orderdemo/application/order/OrderCreateService.java) 가 주문 생성 흐름을 처리하고, [OrderQueryService.java](src/main/java/com/example/orderdemo/application/order/OrderQueryService.java) 가 주문 조회를 처리한다.
  주문번호 생성은 [UlidOrderNumberGenerator.java](src/main/java/com/example/orderdemo/application/order/UlidOrderNumberGenerator.java) 가 담당한다.
- `domain/order`
  [Order.java](src/main/java/com/example/orderdemo/domain/order/Order.java), [OrderItem.java](src/main/java/com/example/orderdemo/domain/order/OrderItem.java), [OrderStatus.java](src/main/java/com/example/orderdemo/domain/order/OrderStatus.java) 가 주문 상태와 주문 항목을 표현한다.
- `domain/product`
  [Product.java](src/main/java/com/example/orderdemo/domain/product/Product.java) 에 재고 차감 로직이 있다.
- `repository`
  `OrderRepository`, `ProductRepository`가 JPA 접근을 담당한다.
- `common`
  에러 코드와 비즈니스 예외, JPA Auditing 설정이 들어 있다.

## 핵심 설계 결정

### 1. 레이어드 패키징 선택
현재 프로젝트 범위는 주문 생성/조회 중심의 작은 애플리케이션이므로, 기능별 패키징보다 레이어드 패키징을 선택했다.
도메인 경계가 충분히 자라지 않은 시점이어서, `api`, `application`, `domain`, `repository`, `common` 레이어를 나누는 쪽이 더 단순하고 설명 가능하다고 판단했다.

### 2. API 모델과 Application 모델 분리
HTTP 요청/응답 모델과 서비스 입력/출력 모델을 분리했다.
컨트롤러는 요청 검증과 응답 반환에, 서비스는 유스케이스 처리에 집중하도록 책임을 나눴다.

### 3. Order를 aggregate root로 두고 OrderItem은 상품 스냅샷을 보관
`Order`를 aggregate root로 두고, `OrderItem`은 `Product` 엔티티를 직접 참조하지 않도록 설계했다.
주문 시점의 `productId`, `productName`, `orderUnitPrice`, `quantity`를 저장해 이후 상품 정보 변경이 기존 주문 데이터에 영향을 주지 않도록 했다.

### 4. 도메인 규칙은 엔티티에 배치
주문 생성 과정의 핵심 규칙은 가능한 한 도메인 객체가 직접 가지도록 했다.
`Order`는 빈 주문 방지, 총액 계산, 상태 초기화를 담당하고, `Product`는 재고 차감을 담당한다.
`OrderCreateService`는 상품 조회, 중복 검사, 주문 생성 흐름을 조합하는 application service 역할만 수행한다.

### 5. 동일 상품의 중복 주문 라인 금지
한 주문 요청 안에서 같은 `productId`를 여러 번 보내는 것은 허용하지 않았다.
현재 모델에는 옵션이나 판매자 단위로 라인을 구분할 이유가 없어서, 중복 요청을 차단하는 편이 API 계약을 더 단순하게 만든다고 판단했다.

### 6. 주문번호와 내부 PK 분리
주문 엔티티는 DB PK 외에 별도의 `orderNumber`를 가진다.
외부 식별자와 내부 식별자를 분리해 식별자 정책을 분리했다.

### 7. 과설계 지양
현재 단계에서는 서비스 인터페이스, CQRS 분리, factory/policy/port-adapter 구조 같은 추상화를 도입하지 않았다.
작은 범위에서 필요한 수준의 구조만 유지하고, 설명 가능성과 테스트 가능성을 우선했다.

### 8. 테스트를 계층별로 분리
도메인 규칙은 unit test, 주문 생성 유스케이스는 service integration test, HTTP 요청/응답 계약은 controller test(MockMvc)로 검증했다.
테스트 목적이 섞이지 않도록 책임을 나눴다.

## API 예시

### 주문 생성

`POST /api/orders`

요청 예시:

```json
{
  "items": [
    { "productId": 1, "quantity": 1 },
    { "productId": 2, "quantity": 2 }
  ]
}
```

성공 응답 예시:

```json
{
  "orderId": 10,
  "orderNumber": "ORDER-01HV...",
  "status": "CREATED",
  "totalAmount": 5600
}
```

실패 응답 예시:

```json
{
  "code": "OUT_OF_STOCK",
  "message": "재고가 부족합니다. productId = 2"
}
```

### 주문 조회

`GET /api/orders/{orderNumber}`

성공 응답 예시:

```json
{
  "orderId": 10,
  "orderNumber": "ORDER-01HV...",
  "status": "CREATED",
  "totalAmount": 5600,
  "orderItems": [
    {
      "productId": 1,
      "productName": "상품1",
      "orderUnitPrice": 1000,
      "quantity": 1,
      "lineAmount": 1000
    },
    {
      "productId": 2,
      "productName": "상품2",
      "orderUnitPrice": 2300,
      "quantity": 2,
      "lineAmount": 4600
    }
  ]
}
```

실패 응답 예시:

```json
{
  "code": "ORDER_NOT_FOUND",
  "message": "주문을 찾을 수 없습니다. orderNumber = ORDER-UNKNOWN"
}
```

## 테스트 설명

테스트 파일은 아래와 같다.

- [ProductTest.java](src/test/java/com/example/orderdemo/domain/product/ProductTest.java)
- [OrderTest.java](src/test/java/com/example/orderdemo/domain/order/OrderTest.java)
- [OrderItemTest.java](src/test/java/com/example/orderdemo/domain/order/OrderItemTest.java)
- [OrderControllerTest.java](src/test/java/com/example/orderdemo/api/order/OrderControllerTest.java)
- [OrderCreateServiceTest.java](src/test/java/com/example/orderdemo/application/order/OrderCreateServiceTest.java)
- [OrderdemoApplicationTests.java](src/test/java/com/example/orderdemo/OrderdemoApplicationTests.java)

역할은 아래와 같다.

- 도메인 테스트
  `Product`, `Order`, `OrderItem`의 생성 규칙과 계산을 확인한다.
- API 테스트
  `OrderControllerTest`에서 요청 검증과 응답 형식을 확인한다.
- 통합 테스트
  `OrderCreateServiceTest`, `OrderdemoApplicationTests`는 스프링 컨텍스트와 DB 연결이 필요한 테스트다.

애플리케이션 기본 datasource 값은 Docker Compose 기준으로 `db` 호스트를 사용한다.
Flyway가 테스트 DB 스키마를 적용하고, 통합 테스트는 Testcontainers로 MySQL 컨테이너를 띄워 실행한다.

### 테스트 실행

```bash
./gradlew test
```

Docker daemon이 실행 중이면 통합 테스트가 MySQL 컨테이너를 자동으로 띄운다.
별도로 `docker compose up -d db`를 먼저 실행할 필요는 없다.

## CI

GitHub Actions로 CI를 구성했다.
push와 pull request에서 `./gradlew test`를 실행하며, 이 과정에서 Flyway와 Testcontainers 기반 통합 테스트도 함께 검증한다.

## 다음 단계 계획

현재 코드에 없는 계획 항목은 아래와 같다.

- Kafka: planned
- Redis: planned
