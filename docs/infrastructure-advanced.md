# Advanced Infrastructure & Maintenance

In production environments, Event Sourcing systems like this one require specialized maintenance strategies to ensure **performance** and **consistency**.

## 1. Snapshotting
As the number of events for an account grows over time, loading the full event history into memory can become slow.

### Trigger Policy
The system is configured in `AxonConfig.java` to trigger a **snapshot** after every **5 events**.
```java
// Logic for snapshot triggering
return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
```

### Process
1.  **Count Exhaustion**: After 5 new events, Axon captures the current state (balance, status) of the `BankAccount`.
2.  **Snapshot Creation**: This state is stored in the `snapshot_event_entry` table.
3.  **Loading**: Next time the Aggregate is needed, Axon loads the **latest snapshot** first and only replays the events that happened **after** that snapshot, significantly improving performance.

---

## 2. Event Replay (Rebuilding Read Models)
If the Read Store (PostgreSQL) is corrupted, or if we introduce a new feature that needs historical data, we can **replay** all historical events to reconstruct the read-model from scratch.

### Replay Procedure
1.  **Management API**: The `BankAccountQueryController` exposes a POST endpoint for maintenance tasks.
2.  **Bash Script**: Use the [**`replay-events.sh`**](file:///d:/Ganesh-D/Projects/GPP/Task%2016/bank-account-manager/replay-events.sh) utility to trigger this process.

### How to Run:
```bash
# Triggers a replay for the default projection group
./replay-events.sh
```

### Internal Mechanism
- **Reset**: The system clears the `current_account_view` and `transaction_history` tables for the relevant group.
- **Replay**: Axon's `StreamingProcessor` reads every event in the Event Store from sequence `0` and feeds them back into the `Projection` handlers.

---

## 3. PostgreSQL Event Store
The system is configured to use **PostgreSQL** as its event storage engine (Axon Server is disabled).
- **Table**: `domain_event_entry`
- **Audit Log**: This table serves as the primary, immutable, and append-only record of all activity in the bank.
