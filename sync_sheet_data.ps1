[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " [MaternityBag] Syncing Sheet Data to App & Preview" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan

$rootDir = $PSScriptRoot
$excelPath = Join-Path $rootDir "출산준비물_통합데이터.xlsx"
$jsonPath = Join-Path $rootDir "data_export.json"

if (-not (Test-Path $excelPath)) {
    Write-Host "[Error] '출산준비물_통합데이터.xlsx' 파일을 찾을 수 없습니다." -ForegroundColor Red
    exit 1
}

Write-Host "Reading data from '출산준비물_통합데이터.xlsx'..." -ForegroundColor Gray

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$excel.DisplayAlerts = $false

$wb = $excel.Workbooks.Open($excelPath)

function Parse-SheetRows($sheet, $colCount, $headerRow = 1) {
    $rows = @()
    $lastRow = $sheet.UsedRange.Rows.Count
    if ($lastRow -le $headerRow) { return $rows }
    
    for ($r = ($headerRow + 1); $r -le $lastRow; $r++) {
        $rowVals = @()
        for ($c = 1; $c -le $colCount; $c++) {
            $val = $sheet.Cells.Item($r, $c).Text
            $rowVals += [string]$val
        }
        $rows += ,$rowVals
    }
    return $rows
}

# 1. 출산가방
$s1 = $wb.Sheets.Item("출산가방 체크리스트")
$bagRows = Parse-SheetRows $s1 11
$bagList = @()
foreach ($row in $bagRows) {
    if (-not $row[0]) { continue }
    $bagList += @{
        id = $row[0]
        tabCategory = $row[1]
        section = $row[2]
        title = $row[3]
        recommendedQty = $row[4]
        locationTags = $row[5]
        note = $row[6]
        momcafe1st = $row[7]
        top1 = $row[8]
        top2 = $row[9]
        top3 = $row[10]
    }
}

# 2. 육아용품
$s2 = $wb.Sheets.Item("육아용품 체크리스트")
$babyRows = Parse-SheetRows $s2 11
$babyList = @()
foreach ($row in $babyRows) {
    if (-not $row[0]) { continue }
    $babyList += @{
        id = $row[0]
        category = $row[1]
        section = $row[2]
        title = $row[3]
        period = $row[4]
        purchaseTag = $row[5]
        description = $row[6]
        momcafe1st = $row[7]
        top1 = $row[8]
        top2 = $row[9]
        top3 = $row[10]
    }
}

# 3. 시기별할일
$s3 = $wb.Sheets.Item("시기별할일")
$todoRows = Parse-SheetRows $s3 5
$todoList = @()
foreach ($row in $todoRows) {
    if (-not $row[0]) { continue }
    $todoList += @{
        id = $row[0]
        category = $row[1]
        role = $row[2]
        title = $row[3]
        tip = $row[4]
    }
}

# 4. 출산혜택
$s4 = $wb.Sheets.Item("출산혜택정리")
$benefitRows = Parse-SheetRows $s4 8
$benefitList = @()
foreach ($row in $benefitRows) {
    if (-not $row[0]) { continue }
    $benefitList += @{
        id = $row[0]
        region = $row[1]
        title = $row[2]
        type = $row[3]
        amount = $row[4]
        eligibility = $row[5]
        timing = $row[6]
        place = $row[7]
    }
}

$wb.Close($false)
$excel.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
[System.GC]::Collect()

$masterData = @{
    maternityBag = $bagList
    babySupplies = $babyList
    todos = $todoList
    benefits = $benefitList
}

# 1. JSON 저장
$jsonContent = $masterData | ConvertTo-Json -Depth 5
[System.IO.File]::WriteAllText($jsonPath, $jsonContent, [System.Text.Encoding]::UTF8)

# 2. 웹 프리뷰용 master_data.js 저장 (루트 및 preview)
$jsContent = "window.MASTER_DATA = " + $jsonContent + ";"
$jsPath = Join-Path $rootDir "master_data.js"
[System.IO.File]::WriteAllText($jsPath, $jsContent, [System.Text.Encoding]::UTF8)

$previewJsPath = Join-Path $rootDir "preview\master_data.js"
if (Test-Path (Join-Path $rootDir "preview")) {
    [System.IO.File]::WriteAllText($previewJsPath, $jsContent, [System.Text.Encoding]::UTF8)
}

# 3. 안드로이드 앱용 assets/data_export.json 저장
$assetsDir = Join-Path $rootDir "app\src\main\assets"
if (-not (Test-Path $assetsDir)) {
    New-Item -ItemType Directory -Path $assetsDir -Force | Out-Null
}
$androidJsonPath = Join-Path $assetsDir "data_export.json"
[System.IO.File]::WriteAllText($androidJsonPath, $jsonContent, [System.Text.Encoding]::UTF8)

Write-Host "[OK] data_export.json & master_data.js updated!" -ForegroundColor Green
Write-Host " - 출산가방: $($bagList.Count)개" -ForegroundColor Gray
Write-Host " - 육아용품: $($babyList.Count)개" -ForegroundColor Gray
Write-Host " - 시기별할일: $($todoList.Count)개" -ForegroundColor Gray
Write-Host " - 출산혜택: $($benefitList.Count)개" -ForegroundColor Gray
Write-Host " - 안드로이드 에셋: $androidJsonPath" -ForegroundColor Gray
Write-Host " - 웹 프리뷰 데이터: $jsPath" -ForegroundColor Gray

Write-Host "----------------------------------------------------------" -ForegroundColor DarkGray
Write-Host "[SUCCESS] 엑셀 데이터가 앱 및 프리뷰에 즉시 동기화되었습니다!" -ForegroundColor Green
Write-Host "          (브라우저에서 새로고침 F5를 누르면 즉시 반영됩니다)" -ForegroundColor Cyan
Write-Host "----------------------------------------------------------" -ForegroundColor DarkGray