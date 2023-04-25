# Distributed systems

is a system whose components are either located on different networked computers or are autonomous processes that run on the same physical computer. They communicate and coordinate their actions by passing messages to one another from any system.

## Points of failure

DS is fault tolerant to some extent. There are one or more critical services without which it does not operate anymore. Such essential services need special treatment. 

If both services are stateless - they could be done fault-tolerant just by creating replicas.

If services are stateful - replicas could be tricky to do. We need to guarantee idempotency and consistency. Such services need to "remember" about previous states. Ideally state should be moved to some database like Redis (for speed) or transactional db (for consistency).

### Consensuns protocols

Consensuns algorithms like Paxos and Raft are used for distributed databases like Google Spanner (SQL).

## Consistency vs Availability

**CAP theorem**, also named *Brewer's theorem*, states that any distributed data store can provide only two of the following three guarantees

Consistency
  - Every read receives the most recent write or an error.
Availability
  - Every request receives a (non-error) response, without the guarantee that it contains the most recent write.
Partition tolerance
  - The system continues to operate despite an arbitrary number of messages being dropped (or delayed) by the network between nodes.

## Idempotence

Idempotence is vital in eventually-consistent services. A must-have property for *at-least-once* delivery guarantees.


## Deduplication

Most modern brokers like Kafka nad Pulsar can deduplicate messages before sending them to customers.

Deduplication could be producer-side or consumer-side (stateful app).

## Atomic operations

An atomic operation is, by definition, a single operation that either succeeds completely or fails completely.

> In Swift, operations on a dictionary are not atomic. This means that in a multithreaded application, a dictionary might be changed from one thread while another thread is reading from it. No thread or operation has exclusive access to your dictionary.

> If the operation was atomic, the first read operation would have to finish before the write can start.

## Change Data Capture (CDC)

It solves the "atomically write to multiple stores" issue. 

E.g.: we need to write new entry into Redis, PostreSQL and Pulsar after it created. It is impossible to guarantee atomicity for those operations.

CDC pattern simply uses the output of service 1 to write in service 2. Instead of writing to three services asynchronically we werite to PostgreSQL and use it's result to write to next service (for example, by reading PostgreSQL transactional log).

So services become producers of data for next services.

## Distributed locks 

Distributed Lock Manager (DLM) is essential to synchronize access to shared resources. Lightweight lock can be implemented on Redis (which is safe by design, dead-lock free and fault-tolerant).

Client can acquire locak by creating a key with expiration time (TTL):

```
SET my_lock client_uuid NX PX 30000
redis.set("my_lock", "client_uuid", SetArgs(Nx, Px(30000.millis)))
```

If key `my_lock` exists - usually it means another client holding the lock and we need to retry.

Client might delete the lock after resource is complete or it will be deleted after TTL (usually when client crashed).


