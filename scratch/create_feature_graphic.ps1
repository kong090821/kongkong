[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
Add-Type -AssemblyName System.Drawing

$width = 1024
$height = 500

$bmp = New-Object System.Drawing.Bitmap($width, $height, [System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
$g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

# 1. Background gradient
$rect = New-Object System.Drawing.Rectangle(0, 0, $width, $height)
$c1 = [System.Drawing.ColorTranslator]::FromHtml('#FFF7F4')
$c2 = [System.Drawing.ColorTranslator]::FromHtml('#FFEBE5')
$brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush($rect, $c1, $c2, 35.0)
$g.FillRectangle($brush, $rect)
$brush.Dispose()

# 2. Left Icon
$iconSize = 380
$iconX = 75
$iconY = [int](($height - $iconSize) / 2)

$iconFile = 'C:\진규어플\출산준비물어플\playstore_icon_512.png'
if (Test-Path $iconFile) {
    $iconImg = [System.Drawing.Image]::FromFile($iconFile)
    
    # Rounded clip for the icon
    $iconPath = New-Object System.Drawing.Drawing2D.GraphicsPath
    $iRadius = 40
    $iconPath.AddArc($iconX, $iconY, $iRadius, $iRadius, 180, 90)
    $iconPath.AddArc(($iconX + $iconSize - $iRadius), $iconY, $iRadius, $iRadius, 270, 90)
    $iconPath.AddArc(($iconX + $iconSize - $iRadius), ($iconY + $iconSize - $iRadius), $iRadius, $iRadius, 0, 90)
    $iconPath.AddArc($iconX, ($iconY + $iconSize - $iRadius), $iRadius, $iRadius, 90, 90)
    $iconPath.CloseFigure()
    
    $prevClip = $g.Clip
    $g.SetClip($iconPath)
    $g.DrawImage($iconImg, $iconX, $iconY, $iconSize, $iconSize)
    $g.Clip = $prevClip
    $iconImg.Dispose()
    $iconPath.Dispose()
}

# 3. Right Typography
$fontFamily = 'Malgun Gothic'
$titleFont = New-Object System.Drawing.Font($fontFamily, 46, [System.Drawing.FontStyle]::Bold)
$subFont = New-Object System.Drawing.Font($fontFamily, 22, [System.Drawing.FontStyle]::Bold)
$descFont = New-Object System.Drawing.Font($fontFamily, 15, [System.Drawing.FontStyle]::Regular)

$titleBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#FF6B50'))
$subBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#2C3437'))
$descBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#6C757D'))

$textX = 500
$titleY = 120
$subY = 220
$descY = 280

$g.DrawString('꽁꽁 출산가방', $titleFont, $titleBrush, $textX, $titleY)
$g.DrawString('D-Day & 분만 맞춤 스마트 체크리스트', $subFont, $subBrush, $textX, $subY)
$g.DrawString('산모·신생아 필수 준비물부터 정부 출산혜택까지 한눈에', $descFont, $descBrush, $textX, $descY)

# Feature badge pills
$badgeY = 340
$badgeFont = New-Object System.Drawing.Font($fontFamily, 12, [System.Drawing.FontStyle]::Bold)
$badgeBgBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#FFE5DF'))
$badgeTextBrush = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#DE4F32'))

$badges = @('광고 없는 순수 앱', '분만 형태 맞춤', '오프라인 100% 저장')
$currX = $textX
foreach ($b in $badges) {
    $size = $g.MeasureString($b, $badgeFont)
    $pw = [int]($size.Width + 24)
    $ph = [int]($size.Height + 10)
    $pRect = New-Object System.Drawing.Rectangle($currX, $badgeY, $pw, $ph)
    
    $pillPath = New-Object System.Drawing.Drawing2D.GraphicsPath
    $pr = 14
    $pillPath.AddArc($pRect.X, $pRect.Y, $pr, $pr, 180, 90)
    $pillPath.AddArc(($pRect.Right - $pr), $pRect.Y, $pr, $pr, 270, 90)
    $pillPath.AddArc(($pRect.Right - $pr), ($pRect.Bottom - $pr), $pr, $pr, 0, 90)
    $pillPath.AddArc($pRect.X, ($pRect.Bottom - $pr), $pr, $pr, 90, 90)
    $pillPath.CloseFigure()
    
    $g.FillPath($badgeBgBrush, $pillPath)
    $g.DrawString($b, $badgeFont, $badgeTextBrush, ($currX + 12), ($badgeY + 5))
    $currX += ($pw + 14)
    $pillPath.Dispose()
}

$g.Dispose()

$outputPath = 'C:\진규어플\출산준비물어플\playstore_feature_graphic_1024x500.png'
if (Test-Path $outputPath) {
    Remove-Item $outputPath -Force
}
$bmp.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()

Write-Host 'Generated feature graphic successfully!' -ForegroundColor Green