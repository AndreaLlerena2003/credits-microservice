# Credits Microservice
## Description
This microservice is responsible for managing all operations related to bank credits. It allows the creation, querying, updating, and deletion of different types of credits, as well as recording and tracking transactions associated with each credit.
## Main Features
### Credit Management
- Creation of new credits
- Querying credits by ID
- Updating information of existing credits
- Deletion of credits
- Obtaining complete lists of credits

### Transaction Management
- Recording new transactions associated with credits
- Querying transactions by credit ID
- Querying all transactions in the system

### Technical Features
- Hexagonal architecture for clear separation of responsibilities
- Reactive implementation with Spring WebFlux
- Data persistence with reactive MongoDB
- Complete logging system with Logback
- RESTful endpoints for integration with other microservices

## Microservice Flowchart

![img.png](img.png)