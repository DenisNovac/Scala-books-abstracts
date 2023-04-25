# Schema evolution

Compatibility between changes across time.

Two ways: compatibility and versioning.

## Compatibility

Means that new versions are still capable to process older messages and vice-versa.

Compatibility could be:

- backward: newer instances can read data produced from older instances;
- forward: older can read data produced by newer;
- full: both.

In most messaging systems it is enough to have backward compatibility.

## Versioning

In HTTP we would have version in URL: /api/v1/users 

Breaking change would require to add /api/v2/users

In messaging we could have versioned topics. But deploying the new version of topic will generate a huge backlog because consumer might be not ready to read it yet.

In such cases we should start by deploying changes on the consumer side (it will just read an empty topic which is not breaks anything).

Then we could check if v1 messages are processed and remove this topic.

## Schema registry

Apache Kafka can operate with schema registry such as Avro. Pulsar comes with a built-in schema registry which enables clients to upload data schemas per topic. 

