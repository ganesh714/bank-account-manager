$ErrorActionPreference = "Stop"

Write-Host "1. Creating a New Account..."
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method Post -ContentType "application/json" -Body '{"initialBalance": 1000.00, "ownerName": "John Doe"}'
$AccountId = $response
Write-Host "   Account Created with ID: $AccountId`n"

Start-Sleep -Seconds 1

Write-Host "2. Depositing 500.00..."
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/deposit" -Method Post -ContentType "application/json" -Body '{"amount": 500.00}'
Write-Host "   Deposit Successful.`n"

Start-Sleep -Seconds 1

Write-Host "3. Withdrawing 200.00..."
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/withdraw" -Method Post -ContentType "application/json" -Body '{"amount": 200.00}'
Write-Host "   Withdrawal Successful.`n"

Start-Sleep -Seconds 1

Write-Host "4. Checking Current Account State..."
$state = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId" -Method Get
Write-Host "   State: $($state | ConvertTo-Json -Compress)`n"

Write-Host "5. Checking Transaction History..."
$history = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/history" -Method Get
Write-Host "   History: $($history | ConvertTo-Json -Compress)`n"

Write-Host "6. Viewing Raw Event Stream..."
$events = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/events" -Method Get
Write-Host "   Events: $($events | ConvertTo-Json -Compress)`n"

Write-Host "7. Temporal Query (Balance at a point in time - Right Now)..."
$Timestamp = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
$temporal = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/balance-at/$Timestamp" -Method Get
Write-Host "   Temporal Balance: $($temporal | ConvertTo-Json -Compress)`n"

Write-Host "8. Emptying the Account & Closing It..."
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/withdraw" -Method Post -ContentType "application/json" -Body '{"amount": 1300.00}'
Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId/close" -Method Post
$finalState = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/$AccountId" -Method Get
Write-Host "   Final State: $($finalState | ConvertTo-Json -Compress)`n"

Write-Host "9. Trigger Event Replay (Admin)..."
$replayResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/replay/current-account-view" -Method Post
Write-Host "   Replay Response: $replayResponse`n"

Write-Host "All tests completed successfully!"
