$ErrorActionPreference = 'Stop'
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$resp = Invoke-WebRequest -Uri 'http://localhost:8080/login' -WebSession $session -UseBasicParsing
$content = $resp.Content
$token = ([regex]::Match($content,'<meta name="_csrf"[^>]*content="([^"]+)"').Groups[1].Value)
$headerName = ([regex]::Match($content,'<meta name="_csrf_header"[^>]*content="([^"]+)"').Groups[1].Value)
Write-Output "csrf header: $headerName"
Write-Output "csrf token: $token"
$auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:guest'))
$h = @{ }
if ($headerName -and $token) { $h[$headerName] = $token }
$h['Authorization'] = "Basic $auth"
$h['Content-Type'] = 'application/json'

Write-Output 'GET /api/workers'
Invoke-RestMethod -Uri 'http://localhost:8080/api/workers' -Method Get -Headers $h | ConvertTo-Json -Depth 5 | Write-Output

Write-Output 'POST /api/workers'
$body = @{ name='PS Test Worker'; role='LABOUR' } | ConvertTo-Json
$create = Invoke-RestMethod -Uri 'http://localhost:8080/api/workers' -Method Post -Headers $h -Body $body
$create | ConvertTo-Json -Depth 5 | Write-Output
$id = $create.id
Write-Output "Created id: $id"

Write-Output 'PUT /api/workers/{id}'
$updBody = @{ name='PS Updated Worker'; role='LABOUR' } | ConvertTo-Json
$upd = Invoke-RestMethod -Uri "http://localhost:8080/api/workers/$id" -Method Put -Headers $h -Body $updBody
$upd | ConvertTo-Json -Depth 5 | Write-Output

Write-Output 'DELETE /api/workers/{id}'
Invoke-RestMethod -Uri "http://localhost:8080/api/workers/$id" -Method Delete -Headers $h

Write-Output 'After delete GET /api/workers'
Invoke-RestMethod -Uri 'http://localhost:8080/api/workers' -Method Get -Headers $h | ConvertTo-Json -Depth 5 | Write-Output
