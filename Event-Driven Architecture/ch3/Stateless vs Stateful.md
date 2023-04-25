# Stateless vs Stateful

System is described as *stateful* if it is designed to remember proceding events or user interaction.

Stateful service generally persists state in external database or cache to survive restarts and to sync writes with other instances which highly increases its complexity. Not any database write makes service stateful. App will be stateful if it needs initial state from this database at the start.

Stateless service doesn't need to sync.

## Stateless services, stateful brokers

In ED most of the state lives on a message broker. Service perform computations bases on incoming messages and publish the result back to broker (similar to actor model).

If some result needs to be persisted - third service could consume messages for writing to database (only this service is stateful while computation services are stateless).

## Stateful service

> Does it need an initial state from external storage to start operating?

If answer is yes - then it is a stateful service.

## Application clustering

Opposite example of stateless services and stateful brokers are clustered services. Such apps need to run in a network together with other nodes.

In such systems it is common to have leader (master) election and worker nodes (slave), which are elected via consensus algorithms.

Akka Cluster - most popular for this. 

## Message-driven architecture

Both ED and Actor-bases archs are message-driven. But main diffrence is:

> Messages are addressed to specific destination while events are not

### Delivery guarantees

- at-most-once (zero or one, might be lost);
  Easiest - fire and forget. Ok in systems where previous messages are not relevant, eg IoT.
- at-least-once (one or more, can't be lost);
  Most common.
- exactly-once (no lost or duplication)
  Nearly impossible to achieve - needs acknowledgment from a receiver. But if receiver did get a message but didn't sent ack - guarantee is lost.

### Apache Kafka

### Apache Pulsar

Unlike Kafka, topics are not partitioned by default and served by single broker. This feature is optional making it much easier to deal with topics.

#### Subscriptions

In Pulsar there are multiple subscription types. 

- Exclusive - single consumer allowed;
- Fail-over - multiple consumers, but master picked and only one to recieve the messages. others will be a fallback in case of master fail;
- Shared - multiple consumers allowed, messages recieved by round-robin;
- Key-shared - same as Shared, but messages delivered by key.

#### Deduplication

Pulsar deduplicates by sequence ID which should be in every message. Might be enabled on system level or on a namespace or topic.

In distributed systems we need transactions so message will not be produced without acknowledgment sent (in case of crash other instance will re-process the mesage and send it again with different sequence ID). Pulsar has a transactions which are not cheap. Or we could use a consumer-side deduplication. 

#### Topic compaction

Compacted topics might be used for event-sourcing when we don't need entire story of messages to produce the same output. Compaction will allow for faster rewinds.

It works by setting a partition key to every message.

#### Transaction

Allows to consume, process and produce messages in atomic operation.

Allows to coordinate consumption and production of multiple messages (possibly involving multiple topics) - as single operation.

#### Pulsar IO

Allows for a interaction with external systems like Apache Cassandra and others (analog to Kafka Connect).

Also includes CDC connectors (see previous file).


### What to use

Both brokers are similar and are having the same features.

Kafka requires more tuning (at least with ZooKeeper). 

Kafka with ZooKeeper is most battle-tested solution. 

Partitioned topics from Kafka is one of the main difficulties. However usually such topics are higher throughput. 




