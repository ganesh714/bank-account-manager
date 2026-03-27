# Core API Reference (Messaging)

This document provides a detailed technical reference for the commands and events that form the messaging foundation of the Bank Account Manager application. All messages are implemented as Java **`record`s** for immutability and concise syntax.

## Commands (Write Side)

Commands represent the **intent** to change the system's state. In Axon, they are routed to the appropriate Aggregate instance.

### Common Annotations
*   **`@TargetAggregateIdentifier`**: Used in every command to mark the field that identifies which Aggregate instance (bank account) should handle the command.

| Command Class | Description | Fields | Key Annotations |
| :--- | :--- | :--- | :--- |
| `CreateAccountCommand` | Intent to open a new account. | `String accountId`, `BigDecimal initialBalance`, `String ownerName` | `@TargetAggregateIdentifier` (on `accountId`) |
| `DepositMoneyCommand` | Intent to add funds to an account. | `String accountId`, `BigDecimal amount` | `@TargetAggregateIdentifier` (on `accountId`) |
| `WithdrawMoneyCommand`| Intent to remove funds from an account. | `String accountId`, `BigDecimal amount` | `@TargetAggregateIdentifier` (on `accountId`) |
| `CloseAccountCommand`  | Intent to permanently close an account. | `String accountId` | `@TargetAggregateIdentifier` (on `accountId`) |

---

## Events (Read Side & Audit)

Events represent **historical facts**—changes that have already occurred in the system. They are stored in the Event Store and used to update projections.

| Event Class | Description | Fields |
| :--- | :--- | :--- |
| `AccountCreatedEvent` | Fact: A new account was successfully created. | `String accountId`, `BigDecimal initialBalance`, `String ownerName` |
| `MoneyDepositedEvent` | Fact: Funds were successfully added. | `String accountId`, `BigDecimal amount` |
| `MoneyWithdrawnEvent` | Fact: Funds were successfully removed. | `String accountId`, `BigDecimal amount` |
| `AccountClosedEvent`  | Fact: The account was closed. | `String accountId` |

---

## Usage in Axon Framework

### Command Dispatching
Commands are dispatched via the `CommandGateway`. Axon uses the `@TargetAggregateIdentifier` to locate the specific `BankAccount` aggregate in the event store.

### Event Handling
Events are handled by:
1.  **The Aggregate**: To update its internal state (`@EventSourcingHandler`).
2.  **Projections**: To update read-optimized database views (`@EventHandler`).
