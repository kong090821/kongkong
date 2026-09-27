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
