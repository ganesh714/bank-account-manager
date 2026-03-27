# Command Side (Write Model)

This document explains how the application handles requests to change data, ensuring that all modifications follow strict business rules and are correctly audited.

## 1. The Aggregate (`BankAccount.java`)

### Role: The Gatekeeper
The `BankAccount` class acts as the strict **"gatekeeper"** of the system. It is the first point of contact for any command that intends to modify an account. It receives commands and enforces business invariants (rules) before allowing any state changes to occur.

### Event Sourcing
In this system, state is **not persisted directly** in a traditional database table. Instead, the aggregate maintains its integrity by storing events.
- **State Reconstruction**: The aggregate recalculates its current state (such as `balance` and `status`) by replaying historical events using `@EventSourcingHandler` methods.
- **Fact-Based Truth**: The system's "source of truth" is the immutable stream of events in the PostgreSQL event store.

### Business Rules Enforced
The following rules are strictly enforced within the aggregate before an event is applied:

- **Creation**: The initial balance of a new account **cannot be negative**.
- **Deposits/Withdrawals**: Actions cannot be performed on an account that has a **"CLOSED"** status. All transaction amounts must be **greater than zero**.
- **Withdrawals**: The system **prevents overdrafts**. A withdrawal is only permitted if the resulting balance would not drop below zero.
- **Closure**: An account can only be closed if its balance is **exactly zero**.

---

## 2. The Command REST API (`BankAccountCommandController.java`)

### Role: Command Dispatcher
The `BankAccountCommandController` exposes HTTP POST endpoints to accept user requests. Its primary role is to translate these external requests into internal Command objects and dispatch them to the Aggregate using Axon's **`CommandGateway`**.

### Required Endpoints

#### **Create Account**
- **Endpoint**: `POST /api/accounts`
- **Payload**: `{ "initialBalance": 1000.00, "ownerName": "John Doe" }`
- **Success Response (201 Created)**: Returns a `Location` header pointing to the new resource and the new `accountId` in the response body.

#### **Deposit Money**
- **Endpoint**: `POST /api/accounts/{accountId}/deposit`
- **Payload**: `{ "amount": 500.00 }`
- **Success Response (200 OK)**: Empty body.

#### **Withdraw Money**
- **Endpoint**: `POST /api/accounts/{accountId}/withdraw`
- **Payload**: `{ "amount": 200.00 }`
- **Success Response (200 OK)**: Empty body.

#### **Close Account**
- **Endpoint**: `POST /api/accounts/{accountId}/close`
- **Payload**: None
- **Success Response (200 OK)**: Empty body.
