$max=30
$ok=$false
for ($i=0;$i -lt $max;$i++){
  try {
    $r=Invoke-RestMethod -Uri 'http://localhost:8080/tasks' -Method Get -TimeoutSec 2 -ErrorAction Stop
    $ok=$true
    break
  } catch {
    Start-Sleep -Seconds 1
  }
}
if (-not $ok) {
  Write-Output "UNREACHABLE after $max seconds"
  exit 1
}
Write-Output "REACHABLE"
# run smoke
powershell -NoProfile -File .\smoke-test.ps1
