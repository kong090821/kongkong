# Full preview synchronization script
$enc = [System.Text.Encoding]::UTF8

Write-Host "Syncing root files to preview and android assets..." -ForegroundColor Cyan

# 1. Root index.html is the single source of truth
$rootHtml = [System.IO.File]::ReadAllText('index.html', $enc)

# Copy to preview/index.html
if (-not (Test-Path "preview")) { New-Item -ItemType Directory -Path "preview" -Force }
[System.IO.File]::WriteAllText('preview/index.html', $rootHtml, $enc)

# Copy to preview.html
[System.IO.File]::WriteAllText('preview.html', $rootHtml, $enc)

# Copy to Android assets
if (-not (Test-Path "android/app/src/main/assets")) { New-Item -ItemType Directory -Path "android/app/src/main/assets" -Force }
[System.IO.File]::WriteAllText('android/app/src/main/assets/index.html', $rootHtml, $enc)

# 2. Sync master_data.js
if (Test-Path "master_data.js") {
    $masterData = [System.IO.File]::ReadAllText('master_data.js', $enc)
    [System.IO.File]::WriteAllText('preview/master_data.js', $masterData, $enc)
    [System.IO.File]::WriteAllText('android/app/src/main/assets/master_data.js', $masterData, $enc)
}

# 3. Sync firebase_config.json
if (Test-Path "firebase_config.json") {
    $firebaseCfg = [System.IO.File]::ReadAllText('firebase_config.json', $enc)
    [System.IO.File]::WriteAllText('preview/firebase_config.json', $firebaseCfg, $enc)
    [System.IO.File]::WriteAllText('android/app/src/main/assets/firebase_config.json', $firebaseCfg, $enc)
}

# 4. Sync data_export.json
if (Test-Path "data_export.json") {
    $exportJson = [System.IO.File]::ReadAllText('data_export.json', $enc)
    [System.IO.File]::WriteAllText('preview/data_export.json', $exportJson, $enc)
    [System.IO.File]::WriteAllText('android/app/src/main/assets/data_export.json', $exportJson, $enc)
    if (Test-Path "app/src/main/assets") {
        [System.IO.File]::WriteAllText('app/src/main/assets/data_export.json', $exportJson, $enc)
    }
}

Write-Host "Updated all preview and asset files successfully with 100% exact content!" -ForegroundColor Green
