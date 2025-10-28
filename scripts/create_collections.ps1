# Creates sample documents to ensure MongoDB collections exist (workers, food logs, salaries)
# Usage: Run this while the app is running on http://localhost:8080
# It will use demo credentials admin:guest seeded by DataLoader.

$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080'
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# fetch login page to read CSRF meta tags
$resp = Invoke-WebRequest -Uri "$base/login" -WebSession $session -UseBasicParsing
$content = $resp.Content
$token = ([regex]::Match($content,'<meta name="_csrf"[^>]*content="([^"]+)"').Groups[1].Value)
$headerName = ([regex]::Match($content,'<meta name="_csrf_header"[^>]*content="([^"]+)"').Groups[1].Value)
Write-Output "CSRF header: $headerName"
# Build headers with basic auth and CSRF
$auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:guest'))
$h = @{ }
if ($headerName -and $token) { $h[$headerName] = $token }
$h['Authorization'] = "Basic $auth"
$h['Content-Type'] = 'application/json'

# find or create a canteen
Write-Output "Fetching canteens..."
$canteens = Invoke-RestMethod -Uri "$base/api/canteens" -Method Get -Headers $h -WebSession $session
if ($canteens -and $canteens.Count -gt 0) {
    $canteenId = $canteens[0].id
    Write-Output "Using existing canteen id: $canteenId"
} else {
    Write-Output "No canteen found. Creating one..."
    $new = @{ name='Auto Canteen'; location='Auto'; defaultPlatePrice=5.0 } | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "$base/api/canteens" -Method Post -Headers $h -Body $new -WebSession $session
    $canteenId = $created.id
    Write-Output "Created canteen id: $canteenId"
}

# create a worker
Write-Output "Creating sample worker..."
$workerBody = @{ name='Auto Worker'; role='LABOUR' } | ConvertTo-Json
$createdWorker = Invoke-RestMethod -Uri "$base/api/workers" -Method Post -Headers $h -Body $workerBody -WebSession $session
$workerId = $createdWorker.id
Write-Output "Created worker id: $workerId"

# post a purchase (plates bought)
Write-Output "Creating sample purchase (10 plates)..."
$purchaseBody = @{ platesBought = 10 } | ConvertTo-Json
$purchase = Invoke-RestMethod -Uri "$base/api/canteens/$canteenId/purchases" -Method Post -Headers $h -Body $purchaseBody -WebSession $session
Write-Output "Purchase response: $(ConvertTo-Json $purchase -Depth 5)"

# post a sale (plates sold)
Write-Output "Creating sample sale (3 plates)..."
$saleBody = @{ platesSold = 3 } | ConvertTo-Json
$sale = Invoke-RestMethod -Uri "$base/api/canteens/$canteenId/sales" -Method Post -Headers $h -Body $saleBody -WebSession $session
Write-Output "Sale response: $(ConvertTo-Json $sale -Depth 5)"

# mark a salary entry
Write-Output "Creating sample salary mark for worker..."
$salaryBody = @{ workerId = $workerId; status = 'PENDING' } | ConvertTo-Json
$salary = Invoke-RestMethod -Uri "$base/api/salaries/mark" -Method Post -Headers $h -Body $salaryBody -WebSession $session
Write-Output "Salary mark response: $(ConvertTo-Json $salary -Depth 5)"

Write-Output "Done. Collections (workers, food logs, salaries) should now exist in your Atlas DB."
