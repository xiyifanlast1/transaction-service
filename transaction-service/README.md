# Banking Transaction Service

This is a banking transaction service built on Java 21 and Spring Boot, offering functionalities to create, query, modify, and delete transaction records. 
The system stores data in-memory, supports caching mechanisms for performance optimization, and includes comprehensive unit and stress tests.

## Main Functions

- Create new transaction records
- Query transactions by ID
- Retrieve paginated lists of all transactions
- Update existing transaction information
- Delete transaction records
- Transaction data caching
- Exception handling and data validation
  

## Dependencies

- Java 21
- Spring Boot 3.2.0
- Spring Web
- Spring Cache
- Redis（缓存）
- Maven（项目管理）
- Docker（容器化）
- Kubernetes（部署）
  - Gatling（压力测试）
### spring-boot-starter-web
For building REST API and Web application 
### lombok
For simplifying get/set in data class
### springdoc-openapi-starter-webmvc-ui
For generating swagger api doc
### spring-boot-starter-cache
For using spring cache (ConcurrentHashMap by default)
### spring-boot-starter-test
For building unit tests
### contiperf + junit:junit
for building stress tests

## API Doc

Please visit swagger page:  /swagger-ui.html