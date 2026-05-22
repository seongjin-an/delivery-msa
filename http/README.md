# 통합 테스트 시나리오

## 실행 순서

```
1. docker compose up -d
2. eureka-server 기동 (8761)
3. api-gateway 기동 (8080)
4. user-service, restaurant-service, order-service, payment-service, delivery-service, notification-service 기동
```

---

## 전체 플로우

```
[SETUP]
owner.http [1~6]  회원가입 → 로그인 → 가게등록 → 메뉴등록 × 2 → 오픈

[ORDER]
customer.http [1~2]  회원가입 → 로그인
customer.http [3~4]  가게목록 → 가게상세(menuId 확인)
customer.http [5]    주문 생성 → orderId 획득
customer.http [6]    결제

   → Kafka: order.created 발행 → notification-service 알림 생성
   → Kafka: payment.completed 발행 → order-service 상태 PAID 변경
                                    → notification-service 알림 생성

[OWNER ACCEPT]
owner.http [7]   주문 목록 조회 (PAID 상태 확인)
owner.http [8]   주문 수락 → order.accepted 발행
owner.http [9]   픽업 준비 완료 → order.ready 발행

[DELIVERY]
rider.http [1~2]  회원가입 → 로그인
rider.http [3]    배달 가능 목록 조회
rider.http [4]    배달 수락 → delivery.assigned 발행
rider.http [5]    위치 업데이트
rider.http [6]    픽업 완료 → delivery.picked_up 발행
rider.http [7]    배달 완료 → delivery.completed 발행

[VERIFY]
customer.http [9]  알림 목록 (order.created, payment.completed, order.accepted 알림 확인)
rider.http [9]     알림 목록 (delivery.assigned, delivery.picked_up, delivery.completed 알림 확인)
```

---

## Kafka 이벤트 흐름 확인

Kafka UI: http://localhost:8989

| Topic | 발행 | 구독 |
|-------|------|------|
| order.created | order-service | payment-service, notification-service |
| payment.completed | payment-service | order-service, notification-service |
| payment.failed | payment-service | order-service, notification-service |
| order.accepted | order-service | notification-service |
| order.ready | order-service | delivery-service |
| delivery.assigned | delivery-service | notification-service |
| delivery.picked_up | delivery-service | notification-service |
| delivery.completed | delivery-service | notification-service |

---

## 서비스 포트

| 서비스 | 포트 |
|--------|------|
| api-gateway | 8080 |
| eureka-server | 8761 |
| user-service | 8081 |
| restaurant-service | 8082 |
| order-service | 8083 |
| payment-service | 8084 |
| delivery-service | 8085 |
| notification-service | 8086 |
| Kafka UI | 8989 |
| RedisInsight | 5540 |
| PostgreSQL | 5432 |
| Redis | 6379 |
