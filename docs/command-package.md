# Command Package Documentation

The `command` package represents the **Write Model** (Command side) of the application. It is responsible for handling intent-based requests, validating business rules, and ensuring data consistency before emitting events into the Event Store.

## 1. Aggregate (`BankAccount`)
The `BankAccount` class is an Axon **Aggregate**. It encapsulates the current state of a bank account and acts as the strict gatekeeper that enforces domain-specific business rules.

### Annotation Usage
- **`@Aggregate`**: Informs Axon that this class is an aggregate managed by calculations within its own lifecycle.
- **`@AggregateIdentifier`**: Marks the field (`accountId`) that serves as the unique key for this aggregate instance.
- **`@CommandHandler`**: Annotates methods (and constructors) that respond to incoming commands.
- **`@EventSourcingHandler`**: Annotates methods that update the internal state based on past events, used when reconstructing the aggregate from the Event Store.

### Handled Commands
- **`CreateAccountCommand`**: Initializes a new account.
    - *Validation*: Ensures the initial balance is non-negative.
- **`DepositMoneyCommand`**: Adds funds to the account.
    - *Action*: Applies a `MoneyDepositedEvent`.
- **`WithdrawMoneyCommand`**: Removes funds from the account.
    - *Validation*: Checks for sufficient funds (current balance >= withdrawal amount).
- **`CloseAccountCommand`**: Permanently closes the account.
    - *Validation*: Ensures the current balance is exactly zero.

---

## 2. Controllers (`BankAccountCommandController`)
The `BankAccountCommandController` provides the RESTful entry point for clients to interact with the Command Model.

### Key Components
- **`CommandGateway`**: Axon's gateway used to asynchronous dispatch commands to the aggregate.
- **Endpoint Mapping**:
    - `POST /api/accounts`: Dispatches `CreateAccountCommand`.
    - `POST /api/accounts/{id}/deposit`: Dispatches `DepositMoneyCommand`.
    - `POST /api/accounts/{id}/withdraw`: Dispatches `WithdrawMoneyCommand`.
    - `POST /api/accounts/{id}/close`: Dispatches `CloseAccountCommand`.

### Data Transfer Objects (DTOs)
The controller uses local Java **`record`s** (e.g., `CreateAccountRequest`, `AmountRequest`) to cleanly map incoming JSON request bodies to internal command parameters.
