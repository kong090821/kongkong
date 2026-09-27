[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " [MaternityBag] Cloud Sync Cache & Build Cleaner" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan

$rootDir = $PSScriptRoot

$foldersToClean = @(
    ".gradle",
    "build",
    "app\build",
    ".idea\caches",
    ".idea\libraries"
)

foreach ($relPath in $foldersToClean) {
    $targetPath = Join-Path $rootDir $relPath
    if (Test-Path $targetPath) {
        try {
            Remove-Item -Path $targetPath -Recurse -Force -ErrorAction Stop
            Write-Host "[Deleted] $relPath" -ForegroundColor Green
        } catch {
            Write-Host "[Skipped] $relPath (Locked or In Use)" -ForegroundColor Yellow
        }
    }
}

Write-Host "`nChecking for OneDrive conflict duplicate files..." -ForegroundColor Gray
$conflictFiles = Get-ChildItem -Path $rootDir -Recurse -File | Where-Object {
    $_.Name -match '\s\([0-9]+\)\.' -or $_.Name -match '-DESKTOP-' -or $_.Name -match '충돌'
}

if ($conflictFiles.Count -gt 0) {
    Write-Host "[Notice] Found potential conflict files:" -ForegroundColor Yellow
    foreach ($file in $conflictFiles) {
        Write-Host " - $($file.FullName.Replace($rootDir, ''))" -ForegroundColor Yellow
    }
    Write-Host "Please check and remove them manually if not needed." -ForegroundColor Cyan
} else {
    Write-Host "[OK] No conflict duplicate files found. Clean!" -ForegroundColor Green
}

Write-Host "----------------------------------------------------------" -ForegroundColor DarkGray
Write-Host "[SUCCESS] Cloud temp cache cleaned successfully!" -ForegroundColor Green
Write-Host "You can now safely switch to another PC." -ForegroundColor White
Write-Host "----------------------------------------------------------" -ForegroundColor DarkGray
