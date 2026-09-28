[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$jsonPath = Join-Path $PSScriptRoot "data_export.json"
$jsonRaw = Get-Content -Path $jsonPath -Raw -Encoding UTF8
$data = $jsonRaw | ConvertFrom-Json

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$excel.DisplayAlerts = $false

$workbook = $excel.Workbooks.Add()

while ($workbook.Sheets.Count -gt 1) {
    $workbook.Sheets.Item($workbook.Sheets.Count).Delete()
}

function Format-HeaderRow($sheet, $colCount) {
    $range = $sheet.Range($sheet.Cells.Item(1, 1), $sheet.Cells.Item(1, $colCount))
    $range.Font.Bold = $true
    $range.Font.Color = 0xFFFFFF # White
    $range.Font.Name = "Malgun Gothic"
    $range.Font.Size = 11
    $range.Interior.Color = 0x596FFF # Peach Primary (#FF6F59 in BGR)
    $range.RowHeight = 28
    $range.VerticalAlignment = -4108 # xlCenter
    $range.HorizontalAlignment = -4108 # xlCenter
}

# 1. Sheet 1: Maternity Bag
Write-Host "Creating Sheet 1: 출산가방 체크리스트..."
$sheet1 = $workbook.Sheets.Item(1)
$sheet1.Name = "출산가방 체크리스트"
$headers1 = @("ID", "구분(탭)", "상세분류", "품목명", "권장수량", "장소태그", "비고", "맘카페 언급 1위", "선배맘 추천 TOP1", "선배맘 추천 TOP2", "선배맘 추천 TOP3")
for ($c = 0; $c -lt $headers1.Length; $c++) {
    $sheet1.Cells.Item(1, $c + 1) = $headers1[$c]
}
Format-HeaderRow $sheet1 $headers1.Length

$r = 2
foreach ($item in $data.maternityBag) {
    $sheet1.Cells.Item($r, 1) = [string]$item.id
    $sheet1.Cells.Item($r, 2) = [string]$item.tabCategory
    $sheet1.Cells.Item($r, 3) = [string]$item.section
    $sheet1.Cells.Item($r, 4) = [string]$item.title
    $sheet1.Cells.Item($r, 5) = [string]$item.recommendedQty
    $sheet1.Cells.Item($r, 6) = [string]$item.locationTags
    $sheet1.Cells.Item($r, 7) = [string]$item.note
    $sheet1.Cells.Item($r, 8) = [string]$item.momcafe1st
    $sheet1.Cells.Item($r, 9) = [string]$item.top1
    $sheet1.Cells.Item($r, 10) = [string]$item.top2
    $sheet1.Cells.Item($r, 11) = [string]$item.top3
    $r++
}
$sheet1.Columns.AutoFit()

# 2. Sheet 2: Baby Supplies
Write-Host "Creating Sheet 2: 육아용품 체크리스트..."
$sheet2 = $workbook.Sheets.Add([System.Reflection.Missing]::Value, $sheet1)
$sheet2.Name = "육아용품 체크리스트"
$headers2 = @("ID", "카테고리", "소분류", "용품명", "사용시기", "구매형태(새제품/당근)", "상세설명", "맘카페 언급 1위", "선배맘 추천 TOP1", "선배맘 추천 TOP2", "선배맘 추천 TOP3")
for ($c = 0; $c -lt $headers2.Length; $c++) {
    $sheet2.Cells.Item(1, $c + 1) = $headers2[$c]
}
Format-HeaderRow $sheet2 $headers2.Length

$r = 2
foreach ($item in $data.babySupplies) {
    $sheet2.Cells.Item($r, 1) = [string]$item.id
    $sheet2.Cells.Item($r, 2) = [string]$item.category
    $sheet2.Cells.Item($r, 3) = [string]$item.section
    $sheet2.Cells.Item($r, 4) = [string]$item.title
    $sheet2.Cells.Item($r, 5) = [string]$item.period
    $sheet2.Cells.Item($r, 6) = [string]$item.purchaseTag
    $sheet2.Cells.Item($r, 7) = [string]$item.description
    $sheet2.Cells.Item($r, 8) = [string]$item.momcafe1st
    $sheet2.Cells.Item($r, 9) = [string]$item.top1
    $sheet2.Cells.Item($r, 10) = [string]$item.top2
    $sheet2.Cells.Item($r, 11) = [string]$item.top3
    $r++
}
$sheet2.Columns.AutoFit()

# 3. Sheet 3: Todos
Write-Host "Creating Sheet 3: 시기별할일..."
$sheet3 = $workbook.Sheets.Add([System.Reflection.Missing]::Value, $sheet2)
$sheet3.Name = "시기별할일"
$headers3 = @("ID", "시기구분", "담당역할(#아빠/#엄마/#부부)", "할일제목", "상세팁/가이드")
for ($c = 0; $c -lt $headers3.Length; $c++) {
    $sheet3.Cells.Item(1, $c + 1) = $headers3[$c]
}
Format-HeaderRow $sheet3 $headers3.Length

$r = 2
foreach ($item in $data.todos) {
    $sheet3.Cells.Item($r, 1) = [string]$item.id
    $sheet3.Cells.Item($r, 2) = [string]$item.category
    $sheet3.Cells.Item($r, 3) = [string]$item.role
    $sheet3.Cells.Item($r, 4) = [string]$item.title
    $sheet3.Cells.Item($r, 5) = [string]$item.tip
    $r++
}
$sheet3.Columns.AutoFit()

# 4. Sheet 4: Benefits
Write-Host "Creating Sheet 4: 출산혜택정리..."
$sheet4 = $workbook.Sheets.Add([System.Reflection.Missing]::Value, $sheet3)
$sheet4.Name = "출산혜택정리"
$headers4 = @("ID", "지역구분", "혜택명", "지원형태(바우처/현금)", "지원금액 및 내용", "신청자격", "신청시기", "신청처")
for ($c = 0; $c -lt $headers4.Length; $c++) {
    $sheet4.Cells.Item(1, $c + 1) = $headers4[$c]
}
Format-HeaderRow $sheet4 $headers4.Length

$r = 2
foreach ($item in $data.benefits) {
    $sheet4.Cells.Item($r, 1) = [string]$item.id
    $sheet4.Cells.Item($r, 2) = [string]$item.region
    $sheet4.Cells.Item($r, 3) = [string]$item.title
    $sheet4.Cells.Item($r, 4) = [string]$item.type
    $sheet4.Cells.Item($r, 5) = [string]$item.amount
    $sheet4.Cells.Item($r, 6) = [string]$item.eligibility
    $sheet4.Cells.Item($r, 7) = [string]$item.timing
    $sheet4.Cells.Item($r, 8) = [string]$item.place
    $r++
}
$sheet4.Columns.AutoFit()

# 5. Sheet 5: Custom Recommendation Rules (🎯 맞춤_추천_설정)
Write-Host "Creating Sheet 5: 맞춤_추천_설정..."
$sheet5 = $workbook.Sheets.Add([System.Reflection.Missing]::Value, $sheet4)
$sheet5.Name = "🎯 맞춤_추천_설정"
$headers5 = @("구분", "추천 품목 ID 목록 (쉼표 구분)", "설명 및 포함 품목 안내")
for ($c = 0; $c -lt $headers5.Length; $c++) {
    $sheet5.Cells.Item(1, $c + 1) = $headers5[$c]
}
Format-HeaderRow $sheet5 $headers5.Length

$recommendationRules = @(
    @("제왕절개", "m_cloth_7, m_hyg_7, m_hyg_1, m_cloth_5, g_life_1", "산후복대, 흉터시트, 맘스안심팬티, 압박스타킹, 꺾인빨대 텀블러"),
    @("자연분만", "m_hyg_8, m_hyg_3, m_hyg_2", "회음부방석, 마이비데, 오버나이트 생리대 세트"),
    @("조리원이용", "m_feed_1, m_feed_2, m_feed_4, m_feed_6, m_feed_7, m_cloth_6, b_care_3, b_care_4", "수유패드, 저장팩, 유두크림, 유축깔때기, 손목보호대, 아기로션/영양제"),
    @("자택조리", "m_feed_1, m_feed_4, b_care_3", "기본 수유패드, 유두보호크림, 아기로션"),
    @("공통산모", "m_cloth_1, m_cloth_2, m_cloth_3, m_cloth_4, m_cloth_8, m_sk_1, m_sk_2, m_sk_3", "수유브라, 산모팬티, 무압박양말, 슬리퍼, 세면/화장품 세트"),
    @("공통신생아", "b_cloth_1, b_cloth_2, b_cloth_3, b_care_1, b_safe_1", "배냇저고리, 속싸개, 겉싸개, 손수건, 카시트"),
    @("공통보호자", "g_doc_1, g_doc_2, g_life_4", "산모수첩/신분증, 결제수단, 충전기/멀티탭")
)

$r = 2
foreach ($rule in $recommendationRules) {
    $sheet5.Cells.Item($r, 1) = [string]$rule[0]
    $sheet5.Cells.Item($r, 2) = [string]$rule[1]
    $sheet5.Cells.Item($r, 3) = [string]$rule[2]
    $r++
}
$sheet5.Columns.AutoFit()

# 6. Sheet 6: User Activity Statistics (📊 사용자_활동_통계)
Write-Host "Creating Sheet 6: 사용자_활동_통계..."
$sheet6 = $workbook.Sheets.Add([System.Reflection.Missing]::Value, $sheet5)
$sheet6.Name = "📊 사용자_활동_통계"
$headers6 = @("기록일시", "사용자구분", "이벤트", "분만법", "조리원여부", "아기성별", "지역", "출산예정일", "담은_출산가방수", "담은_출산가방_품목ID", "담은_육아용품수", "담은_육아용품_품목ID")
for ($c = 0; $c -lt $headers6.Length; $c++) {
    $sheet6.Cells.Item(1, $c + 1) = $headers6[$c]
}
Format-HeaderRow $sheet6 $headers6.Length
$sheet6.Columns.AutoFit()

# Save
$outputFile = Join-Path $PSScriptRoot "출산준비물_통합데이터.xlsx"
if (Test-Path $outputFile) {
    Remove-Item -Path $outputFile -Force
}

$workbook.SaveAs($outputFile, 51)
$workbook.Close($false)
$excel.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
[System.GC]::Collect()
[System.GC]::WaitForPendingFinalizers()

Write-Host "SUCCESS: $outputFile created successfully!" -ForegroundColor Green
