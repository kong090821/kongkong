# Full preview synchronization script
$enc = [System.Text.Encoding]::UTF8

Write-Host "Syncing root index.html to preview/index.html and assets..."

# Root index.html is the single source of truth
$rootHtml = [System.IO.File]::ReadAllText('index.html', $enc)

# Copy to preview/index.html
if (-not (Test-Path "preview")) { New-Item -ItemType Directory -Path "preview" -Force }
[System.IO.File]::WriteAllText('preview/index.html', $rootHtml, $enc)

# Copy to preview.html
[System.IO.File]::WriteAllText('preview.html', $rootHtml, $enc)

# Copy to Android assets
if (-not (Test-Path "android/app/src/main/assets")) { New-Item -ItemType Directory -Path "android/app/src/main/assets" -Force }
[System.IO.File]::WriteAllText('android/app/src/main/assets/index.html', $rootHtml, $enc)

Write-Host "Updated all preview and asset files successfully with 100% exact content!"
