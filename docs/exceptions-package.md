# Error Handling & Global Exception Management

This section describes how the application handles business rule violations, providing proper HTTP status mapping as required by the project specifications.

## 1. The Global Exception Handler (`GlobalExceptionHandler.java`)

### Role: The Translator
The `GlobalExceptionHandler` intercepts exceptions thrown by the `BankAccount` aggregate or the Axon Framework and translates them into standard, readable, and structured JSON HTTP responses. This ensures that the API remains helpful even when errors occur.

### Status Code Mappings

| HTTP Status | Exception Root Cause | Description |
| :--- | :--- | :--- |
| **404 Not Found** | `AggregateNotFoundException` | Triggered when a command or query is sent to an `accountId` that does not exist in the event store. |
| **409 Conflict** | `CommandExecutionException` | Triggered when a core business rule is violated (e.g., "Insufficient funds" or "Account balance must be exactly zero to close"). |

> [!IMPORTANT]
> For **409 Conflict** errors, the handler catches Axon's `CommandExecutionException` and unwraps it to find the underlying `IllegalStateException` or `IllegalArgumentException` thrown during aggregate processing.

---

## 2. Structured Error Response Format

All errors return a consistent JSON body to help clients understand the failure.

### Example: Insufficient Funds
If a withdrawal fails due to insufficient funds:

**JSON Response Body**:
```json
{
  "error": "Business Rule Violation",
  "details": "Insufficient funds"
}
```

This structured format allows for easy parsing by frontend applications and provides clear feedback to the end-user about why their request could not be processed.
