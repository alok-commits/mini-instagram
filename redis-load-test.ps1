# Controlled local Mini Instagram HTTP rate-limit test.
# Windows PowerShell 5.1 compatible.
param(
    [string]$Url = 'http://localhost:8080/api/v1/posts?page=0&size=5',
    [ValidateRange(1, 50)][int]$Workers = 10,
    [ValidateRange(1, 100)][int]$RequestsPerWorker = 10
)

$ErrorActionPreference = 'Stop'
if (-not (Get-Command curl.exe -ErrorAction SilentlyContinue)) {
    Write-Error 'curl.exe was not found. Ensure Windows curl is available.'
    exit 1
}

$total = $Workers * $RequestsPerWorker
Write-Host "Starting local rate-limit test: $total HTTP GET requests using $Workers concurrent workers."
Write-Host "Target: $Url"

$timer = [System.Diagnostics.Stopwatch]::StartNew()
$jobs = @()
try {
    for ($worker = 1; $worker -le $Workers; $worker++) {
        $jobs += Start-Job -ArgumentList $Url, $RequestsPerWorker -ScriptBlock {
            param([string]$Target, [int]$Count)
            for ($i = 0; $i -lt $Count; $i++) {
                & curl.exe --silent --output NUL --connect-timeout 3 --max-time 10 --write-out '%{http_code} %{time_total}\n' $Target
            }
        }
    }
    $null = $jobs | Wait-Job
    $lines = @($jobs | Receive-Job)
}
finally {
    $timer.Stop()
    if ($jobs.Count -gt 0) { $jobs | Remove-Job -Force -ErrorAction SilentlyContinue }
}

$data = @($lines | ForEach-Object {
    $parts = ([string]$_).Trim() -split '\s+'
    if ($parts.Count -eq 2 -and $parts[0] -match '^\d{3}$') {
        $value = 0.0
        if ([double]::TryParse($parts[1], [System.Globalization.NumberStyles]::Float,
                [System.Globalization.CultureInfo]::InvariantCulture, [ref]$value)) {
            [PSCustomObject]@{ Status = $parts[0]; LatencyMs = $value * 1000 }
        }
    }
})

Write-Host "`n=== HTTP RATE-LIMIT TEST RESULTS ==="
Write-Host "Requests planned:       $total"
Write-Host "Responses collected:    $($data.Count)"
Write-Host "Elapsed (seconds):      $([math]::Round($timer.Elapsed.TotalSeconds, 2))"
if ($timer.Elapsed.TotalSeconds -gt 0) {
    Write-Host "Throughput (req/sec):   $([math]::Round($data.Count / $timer.Elapsed.TotalSeconds, 2))"
}
if ($data.Count -gt 0) {
    $avg = ($data | Measure-Object -Property LatencyMs -Average).Average
    $sorted = @($data | Sort-Object LatencyMs)
    $p95 = $sorted[[math]::Ceiling($sorted.Count * 0.95) - 1].LatencyMs
    Write-Host "Average latency (ms):  $([math]::Round($avg, 2))"
    Write-Host "P95 latency (ms):      $([math]::Round($p95, 2))"
    Write-Host "`nHTTP status counts:"
    $data | Group-Object Status | Sort-Object Name | Select-Object Name, Count | Format-Table -AutoSize
} else {
    Write-Warning 'No valid HTTP results were collected. Verify the API gateway is reachable.'
}
if ($data.Count -ne $total) {
    Write-Warning 'Some requests did not return parseable results. Check curl errors and gateway logs.'
}
Write-Host 'Note: Reported throughput includes PowerShell job-startup overhead.'
