
**Event-driven architecture** - paradigm promoting the producton, detection, consumption and reaction to events.

Producers of events don't know about consumers and vice versa.

**Microservice architecture** - paradigm where unit of functionality could be deployed and scaled independently. They communicate through HTTP, Web Sockets or messagins such as AMQP.

**Listen-to-yourself** pattern - when the same service acts as a producer and consumer of events. This might be done for logging reasons, etc. Also it will allow to exctract such functionality into separate service more easily if needed.

**Eventual consistence** - in EDA means that some things might be inconsistent due to message recieveing time or service fail but at some point it will become consistent.


Pros:
  - scalability (independent services could be scaled on demand);
  - fault tolerance (some services might go down but other service will work fine, eventual consistence);
  - observability (in EDA events could be also treated as logs);
  - versatility (migth do a "monolith" with listen-to-yourself or microservices).

Cons:
  - any synchronous service might be inconvenient to write in this pattern. e.g. withdrawal of money from account - we need to synchronously verify if customer has sufficient money.

**CQRS** - Command Query Responsibility Segregation - promotes the idea of separating writing and reading parts. 

E.g.: Separate insances of Writing database and Reading database with replication between. Reading database is *eventually consistent* since replication could happen later in time. Separation migth allow separate optimization for each side.

By saving each message in a log we could replay them in future to reconstruct the current state of the application which is called **Event sourcing**.

Command - Write side, Query - Read side.

The most mainstream framework for CQRS is Akka Persistence.

Aecor - FP-style on top of Akka.