# State snapshots

Instead of replaying all events we could create some snapshot of the state so app will use this + some recent messages instead of full replaying.

It is not always possible to have a compact topic (if we need full story for auditability) so snapshot could be useful.

Akka Persistence provides snapshots.

## Retention policy

Storing all events could be heavy on disk so it is good thing to move them into some DWH or in DB. In this case snapshot is also needed. 

