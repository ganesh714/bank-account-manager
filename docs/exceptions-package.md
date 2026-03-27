# Exceptions & Error Handling

This document describes the application's strategy for handling business rules violations, validation errors, and infrastructure failures.

## 1. Global Exception Handling

The application uses a **Global Exception Handler** (`@ControllerAdvice`) to capture and transform exceptions into meaningful, structured JSON error responses. This ensures a consistent API experience for clients.

| Exception Class | HTTP Status | Business Scenario |
| :--- | :--- | :--- |
| `AggregateNotFoundException` | **404 Not Found** | Thrown when an `accountId` provided in a command or query does not exist in the system. |
| `CommandExecutionException` | **409 Conflict** | Wraps business rule violations (e.g., `IllegalStateException`) thrown from inside the aggregate. |
| `IllegalStateException` | **409 Conflict** | Thrown for violations like closing an account with a non-zero balance or insufficient funds. |
| `IllegalArgumentException`| **400 Bad Request** | Thrown for invalid input, such as a negative initial balance. |

---

## 2. Business Rule Violations (Conflict)

When a command is dispatched, the `BankAccount` aggregate performs validation before applying any events. If a rule is violated, a standard Java exception is thrown, which is then caught by Axon and wrapped in a `CommandExecutionException`.

### Example: Insufficient Funds
If a withdrawal is requested for an amount greater than the current balance:
1.  The `BankAccount` aggregate throws an `IllegalStateException`.
2.  Axon wraps this in a `CommandExecutionException`.
3.  The `GlobalExceptionHandler` extracts the cause and returns a **409 Conflict**.

**Response Body**:
```json
{
  "error": "Business Rule Violation",
  "details": "Insufficient funds: required 500.00 but current balance is 100.00"
}
```

---

## 3. Resource Not Found

When an operation (Command or Query) targets an account that does not exist in the Event Store, Axon throws an `AggregateNotFoundException`.

**Response Body**:
```json
{
  "error": "Account not found",
  "details": "Aggregate not found with identifier [123-abc]"
}
```
