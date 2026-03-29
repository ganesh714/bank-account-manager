# Query Side (Read Model)

This section explains the **Query Model** of the Bank Account Manager, describing how data is projected into read-optimized formats and how the system serves high-performance transactional queries.

## 1. CQRS Separation
The application follows a strict **Command Query Responsibility Segregation (CQRS)** pattern. While the Command side handles business logic via Aggregates, the Query side is responsible for projecting events into denormalized tables for fast data retrieval.

### Data Flow
1. **Event Dispatching**: Every state change (Deposit, Withdrawal, etc.) is emitted as an event by the Aggregate.
2. **Projection**: Event handlers in the `query` package intercept these events and update the Read Store (PostgreSQL).
3. **Query Delivery**: The `BankAccountQueryController` exposes GET endpoints to serve this data directly to users/clients.

---

## 2. View Models (JPA Entities)

### Current Account View
**Purpose**: Stores the latest balance and status of every account for instantaneous lookups.
- **`accountId`**: Primary identification from the Command side.
- **`ownerName`**: Snapshot of the account holder's name.
- **`balance`**: Current calculated balance from all replayed events.
- **`status`**: Current state (`ACTIVE`, `CLOSED`).

### Transaction History
**Purpose**: An audit log of all financial activities for an account.
- **`id`**: Unique ID for the history record.
- **`accountId`**: Reference to the owner account.
- **`type`**: The nature of the entry (`DEPOSIT`, `WITHDRAWAL`, `CREATION`).
- **`amount`**: The value involved in the transaction.
- **`timestamp`**: Precisely when the event occurred in the Event Store.

---

## 3. Projections (Event Handlers)
Projections are responsible for maintaining the Read Model in real-time as events occur.

### CurrentAccountViewProjection
- **`@EventHandler` de AccountCreatedEvent**: Initializes the view with the start balance and `ACTIVE` status.
- **`@EventHandler` de MoneyDepositedEvent**: Performs an in-place addition (`balance += amount`).
- **`@EventHandler` de MoneyWithdrawnEvent**: Performs an in-place subtraction (`balance -= amount`).
- **`@EventHandler` de AccountClosedEvent**: Updates status to `CLOSED`.

### TransactionHistoryProjection
Identical to the above, but instead of updating a record, it creates a **new entry** in the `transaction_history` table for every relevant event, ensuring a complete audit trail.

---

## 4. Query REST API

### GET /api/accounts/{accountId}
Returns the current state of a specific bank account.
- **Response**: `200 OK` with JSON matching the `CurrentAccountView`.
- **Error**: `404 Not Found` if the ID is invalid.

### GET /api/accounts/{accountId}/history
Provides a chronological list of all transactions for the account.
- **Response**: `200 OK` with a JSON array of `TransactionHistory` objects.
- **Sorting**: Most recent transactions appear first.

---

## 5. Temporal Auditability
One of the key strengths of this architecture is the ability to reconstruct state, which is detailed in the [**Temporal Queries**](./temporal-queries.md) guide.
