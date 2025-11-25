# Remove generated starter artifacts to keep repo clean
Write-Host "Removing starter archives and extracted folders if present..."
$files = @('starter.zip','spring-selected.zip')
foreach ($f in $files) {
    $p = Join-Path $PSScriptRoot $f
    if (Test-Path $p) { Remove-Item $p -Force -ErrorAction SilentlyContinue; Write-Host "Removed $f" }
}
$dirs = @('starter-extract','generated-spring/target')
foreach ($d in $dirs) {
    $p = Join-Path $PSScriptRoot $d
    if (Test-Path $p) { Remove-Item $p -Recurse -Force -ErrorAction SilentlyContinue; Write-Host "Removed $d" }
}
Write-Host "Done."
