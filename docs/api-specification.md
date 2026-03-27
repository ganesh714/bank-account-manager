# API Specification

This document describes the REST API endpoints available in the Bank Account Manager application.

## Endpoints Summary

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/api/accounts` | Create a new bank account. |
| POST | `/api/accounts/{accountId}/deposit` | Deposit money into an existing account. |
| POST | `/api/accounts/{accountId}/withdraw` | Withdraw money from an account. |
| POST | `/api/accounts/{accountId}/close` | Close a bank account. |
| GET | `/api/accounts/{accountId}` | Retrieve the current state of an account. |
| GET | `/api/accounts/{accountId}/events` | Retrieve the raw event stream for an account. |
| GET | `/api/accounts/{accountId}/balance-at/{timestamp}` | Retrieve the account balance at a specific point in time. |

## Detailed Endpoint Documentation

### Create Account
**Endpoint**: `POST /api/accounts`
**Request Body**:
```json
{
  "initialBalance": 1000.00,
  "ownerName": "John Doe"
}
```
**Success Response (201 Created)**:
- **Headers**: `Location: /api/accounts/{accountId}`
- **Body**: Unique `accountId` (string)

---

### Deposit Money
**Endpoint**: `POST /api/accounts/{accountId}/deposit`
**Request Body**:
```json
{
  "amount": 500.00
}
```
**Success Response (200 OK)**: Empty body.
**Error Response (404 Not Found)**: If the `{accountId}` does not exist.

---

### Withdraw Money
**Endpoint**: `POST /api/accounts/{accountId}/withdraw`
**Request Body**:
```json
{
  "amount": 200.00
}
```
**Success Response (200 OK)**: Empty body.
**Error Responses**:
- `404 Not Found`: If the `{accountId}` does not exist.
- `409 Conflict`: If the withdrawal amount exceeds the current balance (insufficient funds).

---

### Close Account
**Endpoint**: `POST /api/accounts/{accountId}/close`
**Request Body**: Empty.
**Success Response (200 OK)**: Empty body.
**Error Responses**:
- `404 Not Found`: If `{accountId}` does not exist.
- `409 Conflict`: If the account balance is not zero.

---

### Get Account State
**Endpoint**: `GET /api/accounts/{accountId}`
**Success Response (200 OK)**:
```json
{
  "accountId": "string",
  "ownerName": "string",
  "balance": 1300.00,
  "status": "ACTIVE"
}
```
**Error Response (404 Not Found)**: If `{accountId}` does not exist in the projection.

---

### Get Event Stream
**Endpoint**: `GET /api/accounts/{accountId}/events`
**Success Response (200 OK)**:
```json
[
  {
    "type": "AccountCreatedEvent",
    "payload": { "initialBalance": 1000.00, "ownerName": "John Doe" }
  },
  {
    "type": "MoneyDepositedEvent",
    "payload": { "amount": 500.00 }
  }
]
```

---

### Temporal Query (Balance at Time)
**Endpoint**: `GET /api/accounts/{accountId}/balance-at/{timestamp}`
**Path Parameters**:
- `timestamp`: An ISO 8601 formatted timestamp (e.g., `2023-10-27T10:00:00Z`).
**Success Response (200 OK)**:
```json
{
  "accountId": "string",
  "balanceAsOf": "2023-10-27T10:00:00Z",
  "balance": 1000.00
}
```
