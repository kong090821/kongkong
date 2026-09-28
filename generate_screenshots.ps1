Add-Type -AssemblyName System.Drawing

function Create-RoundedRectanglePath {
    param($rect, $radius)
    $path = New-Object System.Drawing.Drawing2D.GraphicsPath
    $d = $radius * 2
    $path.AddArc($rect.X, $rect.Y, $d, $d, 180, 90)
    $path.AddArc($rect.Right - $d, $rect.Y, $d, $d, 270, 90)
    $path.AddArc($rect.Right - $d, $rect.Bottom - $d, $d, $d, 0, 90)
    $path.AddArc($rect.X, $rect.Bottom - $d, $d, $d, 90, 90)
    $path.CloseFigure()
    return $path
}

$outputDir = "C:\Users\user\.gemini\antigravity\scratch\kongkong"
$iconPath = Join-Path $outputDir "playstore_icon_512.png"

# Color definitions
$bgPeach = [System.Drawing.Color]::FromArgb(255, 255, 248, 245)
$primary = [System.Drawing.Color]::FromArgb(255, 255, 111, 89)
$primaryLight = [System.Drawing.Color]::FromArgb(255, 255, 237, 232)
$darkText = [System.Drawing.Color]::FromArgb(255, 45, 37, 34)
$mutedText = [System.Drawing.Color]::FromArgb(255, 125, 114, 111)
$surface = [System.Drawing.Color]::White
$borderCol = [System.Drawing.Color]::FromArgb(255, 240, 229, 223)

# Fonts
$fontHuge = [System.Drawing.Font]::new("Malgun Gothic", [single]32, [System.Drawing.FontStyle]::Bold)
$fontLarge = [System.Drawing.Font]::new("Malgun Gothic", [single]26, [System.Drawing.FontStyle]::Bold)
$fontMedium = [System.Drawing.Font]::new("Malgun Gothic", [single]20, [System.Drawing.FontStyle]::Bold)
$fontRegular = [System.Drawing.Font]::new("Malgun Gothic", [single]17, [System.Drawing.FontStyle]::Regular)
$fontSmall = [System.Drawing.Font]::new("Malgun Gothic", [single]14, [System.Drawing.FontStyle]::Regular)
$fontBadge = [System.Drawing.Font]::new("Malgun Gothic", [single]13, [System.Drawing.FontStyle]::Bold)

# -------------------------------------------------------------
# SCREENSHOT 1: 맞춤 온보딩 & 1초 로그인
# -------------------------------------------------------------
$bmp1 = New-Object System.Drawing.Bitmap 1080, 1920
$g1 = [System.Drawing.Graphics]::FromImage($bmp1)
$g1.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g1.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit

# Background
$g1.Clear($bgPeach)

# Header Title
$g1.DrawString("설레는 아기와의 만남, 완벽 준비", $fontMedium, [System.Drawing.SolidBrush]::new($primary), [single]80, [single]100)
$g1.DrawString("D-Day & 분만 형태 맞춤 출산가방", $fontHuge, [System.Drawing.SolidBrush]::new($darkText), [single]80, [single]150)

# Phone Card Frame
$phoneRect = New-Object System.Drawing.Rectangle 100, 260, 880, 1560
$phonePath = Create-RoundedRectanglePath $phoneRect 48
$g1.FillPath([System.Drawing.SolidBrush]::new($surface), $phonePath)
$g1.DrawPath([System.Drawing.Pen]::new($borderCol, 4), $phonePath)

# Phone Content - Icon
if (Test-Path $iconPath) {
    $ic = [System.Drawing.Image]::FromFile($iconPath)
    $g1.DrawImage($ic, 440, 360, 200, 200)
    $ic.Dispose()
}

$sfCenter = [System.Drawing.StringFormat]::new()
$sfCenter.Alignment = [System.Drawing.StringAlignment]::Center

$g1.DrawString("꽁꽁 출산가방", $fontHuge, [System.Drawing.SolidBrush]::new($primary), [single]540, [single]590, $sfCenter)
$g1.DrawString("내 출산 예정일과 분만법에 맞춘 스마트 추천", $fontRegular, [System.Drawing.SolidBrush]::new($mutedText), [single]540, [single]650, $sfCenter)

# Onboarding Questionnaire Preview Cards
$q1Rect = New-Object System.Drawing.Rectangle 170, 750, 740, 180
$q1Path = Create-RoundedRectanglePath $q1Rect 24
$g1.FillPath([System.Drawing.SolidBrush]::new($primaryLight), $q1Path)
$g1.DrawString("📅 출산 예정일 설정 (D-Day 자동 계산)", $fontMedium, [System.Drawing.SolidBrush]::new($primary), [single]210, [single]780)
$g1.DrawString("2026년 10월 15일  ➔  D-17 (임신 37주차)", $fontLarge, [System.Drawing.SolidBrush]::new($darkText), [single]210, [single]830)

$q2Rect = New-Object System.Drawing.Rectangle 170, 970, 740, 180
$q2Path = Create-RoundedRectanglePath $q2Rect 24
$g1.FillPath([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 245, 245, 245)), $q2Path)
$g1.DrawString("🏥 분만 형태 선택", $fontMedium, [System.Drawing.SolidBrush]::new($darkText), [single]210, [single]1000)
$g1.DrawString("• 자연분만 (회음부 방석, 좌욕제 추천)`n• 제왕절개 (산후 복대, 흉터 밴드 추천)", $fontRegular, [System.Drawing.SolidBrush]::new($mutedText), [single]210, [single]1045)

$q3Rect = New-Object System.Drawing.Rectangle 170, 1190, 740, 180
$q3Path = Create-RoundedRectanglePath $q3Rect 24
$g1.FillPath([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 245, 245, 245)), $q3Path)
$g1.DrawString("🏢 산후조리원 이용 여부 & 거주 지역", $fontMedium, [System.Drawing.SolidBrush]::new($darkText), [single]210, [single]1220)
$g1.DrawString("조리원 전용 유축 용품 + 서울시 지자체 출산지원금 매칭", $fontRegular, [System.Drawing.SolidBrush]::new($mutedText), [single]210, [single]1270)

# Social Login Buttons
$btnRect = New-Object System.Drawing.Rectangle 170, 1450, 740, 110
$btnPath = Create-RoundedRectanglePath $btnRect 28
$g1.FillPath([System.Drawing.SolidBrush]::new($primary), $btnPath)
$g1.DrawString("1초 간편 시작 & 맞춤 가방 확인 ➔", $fontLarge, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White), [single]540, [single]1485, $sfCenter)

$bmp1.Save((Join-Path $outputDir "screenshot_1.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$g1.Dispose()
$bmp1.Dispose()

# -------------------------------------------------------------
# SCREENSHOT 2: 출산 준비 홈 (대시보드)
# -------------------------------------------------------------
$bmp2 = New-Object System.Drawing.Bitmap 1080, 1920
$g2 = [System.Drawing.Graphics]::FromImage($bmp2)
$g2.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g2.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
$g2.Clear($bgPeach)

$g2.DrawString("한눈에 확인하는 출산 로드맵", $fontMedium, [System.Drawing.SolidBrush]::new($primary), [single]80, [single]100)
$g2.DrawString("D-Day 배너와 4대 필수 준비물 가이드", $fontHuge, [System.Drawing.SolidBrush]::new($darkText), [single]80, [single]150)

$phoneRect2 = New-Object System.Drawing.Rectangle 100, 260, 880, 1560
$phonePath2 = Create-RoundedRectanglePath $phoneRect2 48
$g2.FillPath([System.Drawing.SolidBrush]::new($surface), $phonePath2)
$g2.DrawPath([System.Drawing.Pen]::new($borderCol, 4), $phonePath2)

# Profile Banner
$banRect = New-Object System.Drawing.Rectangle 160, 340, 760, 200
$banPath = Create-RoundedRectanglePath $banRect 30
$g2.FillPath([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 255, 111, 89)), $banPath)
$g2.DrawString("🌸 꽁꽁이맘님, 반가워요!", $fontLarge, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White), [single]200, [single]380)
$g2.DrawString("출산까지 D-17일! 지금 가방을 쌀 골든타임이에요 👜", $fontRegular, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 255, 235, 230)), [single]200, [single]440)
$g2.DrawString("제왕절개 • 조리원 2주 • 서울시 송파구", $fontBadge, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White), [single]200, [single]490)

# 4 Core Feature Cards
$menuItems = @(
    @{ title = "1. 출산가방 체크리스트"; desc = "병원 & 조리원 입소 필수품 67개 전수 수록"; col = [System.Drawing.Color]::FromArgb(255, 255, 111, 89); bg = [System.Drawing.Color]::FromArgb(255, 255, 243, 240); y = 580 },
    @{ title = "2. 필수 육아용품 가이드"; desc = "수유, 수면, 의류, 위생 새제품 vs 당근 추천"; col = [System.Drawing.Color]::FromArgb(255, 67, 160, 71); bg = [System.Drawing.Color]::FromArgb(255, 241, 248, 241); y = 800 },
    @{ title = "3. 시기별 할일 (To-Do)"; desc = "임신 초기~출산 후 행정 수속 40가지 일정"; col = [System.Drawing.Color]::FromArgb(255, 126, 87, 194); bg = [System.Drawing.Color]::FromArgb(255, 247, 243, 253); y = 1020 },
    @{ title = "4. 출산 지원금 & 정부 혜택"; desc = "첫만남이용권, 부모급여 + 지자체 축하금"; col = [System.Drawing.Color]::FromArgb(255, 230, 81, 0); bg = [System.Drawing.Color]::FromArgb(255, 255, 243, 230); y = 1240 }
)

foreach ($m in $menuItems) {
    $mRect = New-Object System.Drawing.Rectangle 160, $m.y, 760, 180
    $mPath = Create-RoundedRectanglePath $mRect 24
    $g2.FillPath([System.Drawing.SolidBrush]::new($m.bg), $mPath)
    $g2.DrawString($m.title, $fontLarge, [System.Drawing.SolidBrush]::new($m.col), [single]210, [single]($m.y + 35))
    $g2.DrawString($m.desc, $fontRegular, [System.Drawing.SolidBrush]::new($darkText), [single]210, [single]($m.y + 95))
}

# Bottom MyBag Floating Button
$myBagRect = New-Object System.Drawing.Rectangle 160, 1460, 760, 100
$myBagPath = Create-RoundedRectanglePath $myBagRect 24
$g2.FillPath([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 45, 37, 34)), $myBagPath)
$g2.DrawString("내 출산가방 모아보기 (34개 담김) ➔", $fontLarge, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White), [single]540, [single]1495, $sfCenter)

$bmp2.Save((Join-Path $outputDir "screenshot_2.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$g2.Dispose()
$bmp2.Dispose()

# -------------------------------------------------------------
# SCREENSHOT 3: 출산가방 체크리스트
# -------------------------------------------------------------
$bmp3 = New-Object System.Drawing.Bitmap 1080, 1920
$g3 = [System.Drawing.Graphics]::FromImage($bmp3)
$g3.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g3.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
$g3.Clear($bgPeach)

$g3.DrawString("병원 & 조리원 필수품 67종 전수 수록", $fontMedium, [System.Drawing.SolidBrush]::new($primary), [single]80, [single]100)
$g3.DrawString("맘카페 1위 꿀템과 맞춤 체크리스트", $fontHuge, [System.Drawing.SolidBrush]::new($darkText), [single]80, [single]150)

$phoneRect3 = New-Object System.Drawing.Rectangle 100, 260, 880, 1560
$phonePath3 = Create-RoundedRectanglePath $phoneRect3 48
$g3.FillPath([System.Drawing.SolidBrush]::new($surface), $phonePath3)
$g3.DrawPath([System.Drawing.Pen]::new($borderCol, 4), $phonePath3)

# Tab Bar
$g3.DrawString("산모 용품 (30)", $fontLarge, [System.Drawing.SolidBrush]::new($primary), [single]180, [single]340)
$g3.DrawString("신생아 용품 (16)", $fontLarge, [System.Drawing.SolidBrush]::new($mutedText), [single]460, [single]340)
$g3.DrawString("보호자 (21)", $fontLarge, [System.Drawing.SolidBrush]::new($mutedText), [single]760, [single]340)
$g3.FillRectangle([System.Drawing.SolidBrush]::new($primary), 170, 395, 230, 6)

# Checklist Rows
$items = @(
    @{ title = "수유브라 / 수유나시 (3~4개)"; tags = "#병원 #조리원 #필수"; checked = $true; y = 440 },
    @{ title = "입는 안심팬티 (2~3팩)"; tags = "#병원 #조리원 #맘카페1위"; checked = $true; y = 570 },
    @{ title = "산후복대 (1개) - 제왕절개 필수"; tags = "#제왕절개 #병원"; checked = $true; y = 700 },
    @{ title = "마이비데 / 비데물티슈 (3개)"; tags = "#조리원 #회복필수"; checked = $false; y = 830 },
    @{ title = "모유저장팩 & 수유패드 (1~2팩)"; tags = "#조리원 #수유용품"; checked = $true; y = 960 },
    @{ title = "압박스타킹 & 손목보호대"; tags = "#붓기관련 #필수"; checked = $false; y = 1090 },
    @{ title = "산모 슬리퍼 (미끄럼방지 폭신한 것)"; tags = "#병원 #조리원"; checked = $true; y = 1220 },
    @{ title = "가슴 쿨링팩 / 카보크림"; tags = "#울혈대비 #선택"; checked = $false; y = 1350 }
)

foreach ($it in $items) {
    $rowRect = New-Object System.Drawing.Rectangle 160, $it.y, 760, 110
    $rowPath = Create-RoundedRectanglePath $rowRect 16
    $g3.FillPath([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 252, 250, 249)), $rowPath)
    
    # Checkbox
    $cbRect = New-Object System.Drawing.Rectangle 190, ($it.y + 35), 40, 40
    if ($it.checked) {
        $g3.FillEllipse([System.Drawing.SolidBrush]::new($primary), $cbRect)
        $g3.DrawString("✓", $fontMedium, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White), [single]198, [single]($it.y + 36))
    } else {
        $g3.DrawEllipse([System.Drawing.Pen]::new($mutedText, 3), $cbRect)
    }

    $g3.DrawString($it.title, $fontMedium, [System.Drawing.SolidBrush]::new($darkText), [single]250, [single]($it.y + 25))
    $g3.DrawString($it.tags, $fontSmall, [System.Drawing.SolidBrush]::new($primary), [single]250, [single]($it.y + 65))
}

# Bottom Bag Count
$cntRect = New-Object System.Drawing.Rectangle 160, 1490, 760, 80
$cntPath = Create-RoundedRectanglePath $cntRect 20
$g3.FillPath([System.Drawing.SolidBrush]::new($primaryLight), $cntPath)
$g3.DrawString("👜 담긴 준비물: 67개 중 34개 완료 (51% 달성)", $fontMedium, [System.Drawing.SolidBrush]::new($primary), [single]540, [single]1515, $sfCenter)

$bmp3.Save((Join-Path $outputDir "screenshot_3.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$g3.Dispose()
$bmp3.Dispose()

# -------------------------------------------------------------
# SCREENSHOT 4: 정부 지원금 & 내 가방
# -------------------------------------------------------------
$bmp4 = New-Object System.Drawing.Bitmap 1080, 1920
$g4 = [System.Drawing.Graphics]::FromImage($bmp4)
$g4.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g4.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
$g4.Clear($bgPeach)

$g4.DrawString("놓치면 손해인 출산 지원금 모아보기", $fontMedium, [System.Drawing.SolidBrush]::new($primary), [single]80, [single]100)
$g4.DrawString("전국 공통 혜택부터 지자체 지원금까지", $fontHuge, [System.Drawing.SolidBrush]::new($darkText), [single]80, [single]150)

$phoneRect4 = New-Object System.Drawing.Rectangle 100, 260, 880, 1560
$phonePath4 = Create-RoundedRectanglePath $phoneRect4 48
$g4.FillPath([System.Drawing.SolidBrush]::new($surface), $phonePath4)
$g4.DrawPath([System.Drawing.Pen]::new($borderCol, 4), $phonePath4)

# Benefit Cards
$benefits = @(
    @{ title = "첫만남이용권 (국민행복카드 바우처)"; amount = "첫째 200만 원 / 둘째 이상 300만 원"; target = "출생 아동 전원 지급 (유효기간 1년)"; y = 350; col = [System.Drawing.Color]::FromArgb(255, 230, 81, 0) },
    @{ title = "부모급여 (현금 지원)"; amount = "0세 월 100만 원 / 1세 월 50만 원"; target = "생후 0~23개월 아동 부모"; y = 570; col = [System.Drawing.Color]::FromArgb(255, 67, 160, 71) },
    @{ title = "아동수당"; amount = "매월 10만 원 (만 8세 전까지)"; target = "만 0~7세 아동 (최대 96개월간 지급)"; y = 790; col = [System.Drawing.Color]::FromArgb(255, 25, 118, 210) },
    @{ title = "지자체 출산축하금 (서울시 송파구)"; amount = "송파구 출산지원금 20만 원 + 산후조리비 50만 원"; target = "송파구 6개월 이상 거주 산모 가정"; y = 1010; col = [System.Drawing.Color]::FromArgb(255, 126, 87, 194) },
    @{ title = "전기요금 출산가구 할인"; amount = "매월 전기요금 30% 감면 (최대 16,000원)"; target = "출생 후 3년 미만 영아가 1인 이상인 가구"; y = 1230; col = [System.Drawing.Color]::FromArgb(255, 255, 111, 89) }
)

foreach ($b in $benefits) {
    $bRect = New-Object System.Drawing.Rectangle 160, $b.y, 760, 190
    $bPath = Create-RoundedRectanglePath $bRect 24
    $g4.FillPath([System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(255, 253, 251, 250)), $bPath)
    $g4.DrawPath([System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(255, 240, 230, 225), 2), $bPath)
    
    $g4.DrawString($b.title, $fontLarge, [System.Drawing.SolidBrush]::new($b.col), [single]190, [single]($b.y + 25))
    $g4.DrawString($b.amount, $fontMedium, [System.Drawing.SolidBrush]::new($darkText), [single]190, [single]($b.y + 75))
    $g4.DrawString($b.target, $fontRegular, [System.Drawing.SolidBrush]::new($mutedText), [single]190, [single]($b.y + 125))
}

# Bottom Total Benefit Sum Banner
$sumRect = New-Object System.Drawing.Rectangle 160, 1460, 760, 100
$sumPath = Create-RoundedRectanglePath $sumRect 24
$g4.FillPath([System.Drawing.SolidBrush]::new($primary), $sumPath)
$g4.DrawString("🎉 내가 받을 수 있는 정부 지원금: 2,000만 원+ ➔", $fontMedium, [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White), [single]540, [single]1495, $sfCenter)

$bmp4.Save((Join-Path $outputDir "screenshot_4.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$g4.Dispose()
$bmp4.Dispose()

Write-Host "All 4 screenshots generated successfully!"
