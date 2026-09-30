[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
Add-Type -AssemblyName System.Drawing

$srcImageFile = "C:\Users\cbe\.gemini\antigravity\brain\366b3fd4-bcac-486b-aa63-6469bad6905e\.user_uploaded\media_1790727555730.png"

if (-not (Test-Path $srcImageFile)) {
    Write-Host "Error: Source image not found at $srcImageFile" -ForegroundColor Red
    exit 1
}

Write-Host "Source image found: $srcImageFile" -ForegroundColor Green

$srcImg = [System.Drawing.Image]::FromFile($srcImageFile)

function Save-ResizedImage($sourceImg, $targetPath, $width, $height) {
    $dir = [System.IO.Path]::GetDirectoryName($targetPath)
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }

    $bmp = New-Object System.Drawing.Bitmap($width, $height)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality

    $g.DrawImage($sourceImg, 0, 0, $width, $height)
    $g.Dispose()

    if (Test-Path $targetPath) {
        Remove-Item $targetPath -Force
    }

    if ($targetPath.EndsWith(".jpg") -or $targetPath.EndsWith(".jpeg")) {
        $bmp.Save($targetPath, [System.Drawing.Imaging.ImageFormat]::Jpeg)
    } else {
        $bmp.Save($targetPath, [System.Drawing.Imaging.ImageFormat]::Png)
    }

    $bmp.Dispose()
    Write-Host "Generated: $targetPath (${width}x${height})" -ForegroundColor Cyan
}

$densities = @(
    @{ folder = "mipmap-mdpi"; size = 48 },
    @{ folder = "mipmap-hdpi"; size = 72 },
    @{ folder = "mipmap-xhdpi"; size = 96 },
    @{ folder = "mipmap-xxhdpi"; size = 144 },
    @{ folder = "mipmap-xxxhdpi"; size = 192 }
)

# 1. Update android/app/src/main/res
foreach ($d in $densities) {
    $targetPng = Join-Path "android/app/src/main/res/$($d.folder)" "ic_launcher.png"
    Save-ResizedImage $srcImg $targetPng $d.size $d.size

    $targetRoundPng = Join-Path "android/app/src/main/res/$($d.folder)" "ic_launcher_round.png"
    Save-ResizedImage $srcImg $targetRoundPng $d.size $d.size
}

# 2. Update app/src/main/res
foreach ($d in $densities) {
    $targetPng = Join-Path "app/src/main/res/$($d.folder)" "ic_launcher.png"
    Save-ResizedImage $srcImg $targetPng $d.size $d.size

    $targetRoundPng = Join-Path "app/src/main/res/$($d.folder)" "ic_launcher_round.png"
    Save-ResizedImage $srcImg $targetRoundPng $d.size $d.size
}

# 3. Update root and assets icons
Save-ResizedImage $srcImg "playstore_icon_512.png" 512 512
Save-ResizedImage $srcImg "ggong_icon.png" 512 512
Save-ResizedImage $srcImg "ggong_icon.jpg" 512 512
Save-ResizedImage $srcImg "android/app/src/main/assets/ggong_icon.png" 512 512
Save-ResizedImage $srcImg "android/app/src/main/assets/ggong_icon.jpg" 512 512
Save-ResizedImage $srcImg "preview/ggong_icon.png" 512 512
Save-ResizedImage $srcImg "preview/ggong_icon.jpg" 512 512

# 4. Update base64 icon text file for inline HTML usage if needed
$bytes = [System.IO.File]::ReadAllBytes("ggong_icon.png")
$b64 = [System.Convert]::ToBase64String($bytes)
[System.IO.File]::WriteAllText("icon_b64.txt", $b64, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText("preview/ggong_icon_base64.txt", $b64, [System.Text.Encoding]::UTF8)

# 5. Remove any overriding adaptive-icon XML that causes green robot default launcher icon on Android 8+
$adaptiveFolders = @("android/app/src/main/res/mipmap-anydpi-v26", "app/src/main/res/mipmap-anydpi-v26")
foreach ($af in $adaptiveFolders) {
    if (Test-Path $af) {
        Remove-Item $af -Recurse -Force
        Write-Host "Removed adaptive icon folder to prevent default green robot override: $af" -ForegroundColor Yellow
    }
}

# Also remove old ic_launcher.webp files if present in app/src/main/res
Get-ChildItem "app/src/main/res" -Recurse -Filter "*.webp" | ForEach-Object {
    Remove-Item $_.FullName -Force
    Write-Host "Removed legacy webp icon: $($_.FullName)" -ForegroundColor Yellow
}

$srcImg.Dispose()

Write-Host "App icon updated successfully in all Android mipmap densities and assets!" -ForegroundColor Green
