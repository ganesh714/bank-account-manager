# Temporal Queries

The Bank Account Manager enables **time-traveling queries**, a core benefit of Event Sourcing. This allows users to see exactly what their balance was at any past point in time without having to store separate historical snapshots manually.

## 1. Concept
Since the "system of record" is the **Event Store** (an immutable list of all things that ever happened), we can calculate the state of an account at any specific time by simply:
1. Fetching all events for that account from the store.
2. Filtering out any event that occurred **after** the requested timestamp.
3. Replaying the remaining events in chronological order to arrive at the final balance.

---

## 2. Implementation Logic
In `BankAccountQueryController.java`, the system performs the following steps when a temporal query is received:

### Step 1: Query Input
The user provides an `accountId` and a `timestamp` (ISO-8601 format).

### Step 2: Event Replay
The system uses the Axon `EventStore` to open an event stream for the specified ID:
```java
// Logic used to calculate balance at a specific instant
BigDecimal balanceAtTime = eventStore.readEvents(accountId)
    .asStream()
    .filter(event -> event.getTimestamp().isBefore(requestedTime))
    .map(event -> event.getPayload())
    .reduce(BigDecimal.ZERO, (currentBalance, payload) -> {
        if (payload instanceof AccountCreatedEvent) return ((AccountCreatedEvent) payload).initialBalance();
        if (payload instanceof MoneyDepositedEvent) return currentBalance.add(((MoneyDepositedEvent) payload).amount());
        if (payload instanceof MoneyWithdrawnEvent) return currentBalance.subtract(((MoneyWithdrawnEvent) payload).amount());
        return currentBalance;
    }, BigDecimal::add);
```

---

## 3. Query REST API

### GET /api/accounts/{accountId}/balance-at?timestamp={ISO_DATE}
Calculates and returns the balance for the specified account at the given point in time.

**Example Request**:
`GET /api/accounts/acc123/balance-at?timestamp=2023-10-27T10:00:00Z`

**Example Response**:
```json
{
  "accountId": "acc123",
  "balance": 1500.50,
  "at": "2023-10-27T10:00:00Z"
}
```

---

## 4. Performance Considerations
While temporal queries are extremely powerful for auditing and dispute resolution, they require replaying historical events. For accounts with millions of transactions, this can be computationally expensive.
- **Tip**: For high-volume accounts, consider combining this with snapshots or specialized read-models for specific intervals.
