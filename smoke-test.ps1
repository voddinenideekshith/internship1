Start-Sleep -Seconds 6
try {
  $postBody = @{title='Test Task'; description='Created by CI'; userId='1'} | ConvertTo-Json
  $created = Invoke-RestMethod -Uri 'http://localhost:8080/tasks' -Method Post -Body $postBody -ContentType 'application/json'
  Write-Output 'CREATED:'
  $created | ConvertTo-Json -Depth 5 | Write-Output
  $all = Invoke-RestMethod -Uri 'http://localhost:8080/tasks' -Method Get
  Write-Output 'ALL:'
  $all | ConvertTo-Json -Depth 5 | Write-Output
  $id = $created.id
  $putBody = @{title='Updated Task'; description='Updated desc'; userId='1'} | ConvertTo-Json
  $updated = Invoke-RestMethod -Uri "http://localhost:8080/tasks/$id" -Method Put -Body $putBody -ContentType 'application/json'
  Write-Output 'UPDATED:'
  $updated | ConvertTo-Json -Depth 5 | Write-Output
  Invoke-RestMethod -Uri "http://localhost:8080/tasks/$id" -Method Delete
  Write-Output ("DELETED ID " + $id)
  exit 0
} catch {
  Write-Output ("ERROR: " + $_.Exception.Message)
  exit 1
}
