# 프로젝트 소개
![thumb](https://github.com/lotteon2/BB-ORDER-QUERY-SERVICE/assets/25142537/b7042ed7-b0a0-475f-82e2-bd440fd5a31d)

Blooming Blooms는 픽업과 배송이 가능한 컴포저블 서비스로 MSA기반 화훼 쇼핑몰 플랫폼입니다. 다양한 주문(배달주문, 픽업주문, 구독주문)이 존재하며, 몰인몰 형태로 Seller가 입점해 제품을 판매하는 형태의 쇼핑몰입니다.

# OrderQuery서비스 소개

OrderQuery서비스는 픽업 및 예약주문의 Read테이블입니다. 픽업 및 예약주문은 CQRS패턴으로 구현되어 Command와 Query가 분리되어 있습니다. Read데이터를 반환할 때 불필요한 JOIN 및 외부 서비스와의 통신을 줄이기 위해 NoSQL을 사용했으며, NoSQL중에서도 CQRS의 확장 가능성을 고려해 완전관리형 DB인 DynamoDB를 선택했습니다.

# CQRS 사용 전략

![cqrs1](https://github.com/lotteon2/BB-ORDER-QUERY-SERVICE/assets/25142537/48d61e7d-b705-4278-8ac4-9252d6e51de5)
사용자의 주문 요청은 Order서비스가 받게 됩니다. Order서비스는 자신의 DB에 주문 정보를 저장한 뒤 Kafka를 통해 OrderQuery서비스에게 이벤트를 전송합니다. OrderQuery서비스는 이벤트를 소비해 DynamoDB에 데이터를 저장합니다. 

![cqrs2](https://github.com/lotteon2/BB-ORDER-QUERY-SERVICE/assets/25142537/1d880c6d-ee8e-41a2-aece-f8ab03918d3a)
이후 사용자의 Read요청은 OrderQuery서비스를 통해 응답합니다. Order서비스는 Read요청에 영향을 받지 않으며, Read요청이 많다면 OrderQuery서비스만, CUD요청이 많다면 OrderService만 독립적으로 확장하는 전략을 취할 수 있습니다.

# DynamoDB 사용 전략

- 작성해야 할 쿼리가 복잡하지 않아 SDK나 DynamoDBMapper를 사용하지 않고 SpringDataDynamoDB를 사용했습니다.
- 주문의 PK를 DynamoDB에서도 동일하게 PartitionKey로 사용했습니다. PK를 기준으로 찾은 값을 정렬할 경우는 없어서 RangeKey와 LSI는 사용하지 않았습니다. 대신 PK외에 userId 또는 storeId등으로 조회하는 경우가 있었기 때문에 GSI를 사용했습니다.

## 아쉬운 점

실사용자의 요청을 받지는 않다 보니 프로젝트 당시 ProvisionedThroughput에 대해서 깊게 고민하지 못했던게 아쉽습니다. <br> 
dynamoDB는 사용한 만큼 요금을 지불하는 onDemand모드가 지원되지만 예산은 유한하기 때문에 우리는 언제나 최소의 비용으로 더 좋은 효율을 낼 수 있어야 합니다. <br>
이러한 관점에서 onDemand는 적절하지 않습니다. 그렇다고 처리량을 무작정 작게 설정하면 처리량을 초과한 요청은 처리되지 않고 그대로 유실될 가능성이 존재합니다. <br>
이를 방지하기 위해서 DLQ처럼 취소된 요청을 보관해 뒀다가 이를 다시 처리하는 절차가 필요해 보이지만 이러한 내용을 고민하고 작업하지 못한 점이 아쉽습니다. <br>
