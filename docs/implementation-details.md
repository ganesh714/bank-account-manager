# Implementation Details

This document provides a deep dive into the core components of the Bank Account Manager application.

## 1. Core API (Messaging Foundation)

The messaging between the command and query sides is the backbone of the application. For a complete list of all messages and their annotations, see the [Core API Reference](core-api-reference.md). Both the commands and the events are implemented using Java **`record`s** to ensure immutability and concise data transfer.

* **Commands (Intent-based)**:
  - Authored as intent messages: `CreateAccountCommand`, `DepositMoneyCommand`, `WithdrawMoneyCommand`, `CloseAccountCommand`.
  - Use `@TargetAggregateIdentifier` to ensure Axon routes them to the correct aggregate instance.
* **Events (Fact-based)**:
  - Authored as historical facts: `AccountCreatedEvent`, `MoneyDepositedEvent`, `MoneyWithdrawnEvent`, `AccountClosedEvent`.
  - These represent state changes that have already occurred and are stored in the event store.

## 2. Aggregates (Write Model)

The `BankAccount` aggregate acts as the strict gatekeeper that enforces business rules and validation logic before emitting any events.

- **Command Handlers**: Methods annotated with `@CommandHandler`. They validate incoming commands (e.g., checking for sufficient funds) and apply events.
- **Event Sourcing Handlers**: Methods annotated with `@EventSourcingHandler`. They update the aggregate's internal state (e.g., updating the balance) based on applied events.

## 3. Projections (Read Model)

Projections are responsible for building queryable views of the data.

- **CurrentAccountViewProjection**: Maintains a synchronized state of each account (ID, owner, current balance).
- **TransactionHistoryProjection**: Creates a detailed log of all transactions (deposits and withdrawals) for each account.

## 4. Snapshotting

To improve performance, a snapshotting policy is configured for the `BankAccount` aggregate.

- **Snapshot Trigger**: A snapshot is automatically created after every **5 events**.
- **Storage**: Snapshots are stored in the `snapshot_event_entry` table in PostgreSQL.

## 5. Temporal Queries

To reconstruct the state at a specific point in time, the application:

1. Loads the aggregate's event stream from the Event Store.
2. Replays events up to the specified timestamp.
3. Calculates the resulting state (balance).

## 6. Event Replay

The system supports replaying events to rebuild projections from scratch.

- **Triggering Replay**: A management endpoint allows resetting the tracking token of a projection's processor.
- **Replay Script**: The `replay-events.sh` script automates the process of triggered a replay via the management API.
