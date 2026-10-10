[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " [MaternityBag] Syncing Sheet Data to App & Preview" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan

$rootDir = $PSScriptRoot
$excelPath = Join-Path $rootDir "출산준비물_통합데이터.xlsx"
$jsonPath = Join-Path $rootDir "data_export.json"
$configPath = Join-Path $rootDir "google_sheet_config.json"

$masterData = $null
$syncedFromCloud = $false

# 1. Check if Google Sheets Cloud API URL is configured
if (Test-Path $configPath) {
    try {
        $cfg = Get-Content $configPath -Raw | ConvertFrom-Json
        if ($cfg.webAppUrl -and $cfg.webAppUrl.Trim().StartsWith("http")) {
            $cloudUrl = $cfg.webAppUrl.Trim()
            Write-Host "Connecting to Google Sheets Cloud Web App..." -ForegroundColor Cyan
            Write-Host "URL: $cloudUrl" -ForegroundColor DarkGray

            $response = Invoke-RestMethod -Uri $cloudUrl -Method Get -TimeoutSec 15
            if ($response -and $response.data) {
                $useCloud = $true
                if (Test-Path $excelPath) {
                    $localTime = (Get-Item $excelPath).LastWriteTime
                    if ($response.lastUpdated) {
                        try {
                            $cloudTime = [datetime]::ParseExact($response.lastUpdated, "yyyy-MM-dd HH:mm:ss", $null)
                            if ($localTime -gt $cloudTime) {
                                Write-Host "[Notice] 로컬 엑셀($localTime)이 클라우드($cloudTime)보다 최신입니다. 로컬 엑셀 데이터를 우선 반영합니다." -ForegroundColor Yellow
                                $useCloud = $false
                            }
                        } catch {}
                    }
                }
                if ($useCloud) {
                    $masterData = $response.data
                    $syncedFromCloud = $true
                    Write-Host "[OK] Successfully retrieved live data from Google Sheets Cloud! (Updated: $($response.lastUpdated))" -ForegroundColor Green
                    
                    # 육아용품 품목 수 검증 및 로컬 85개 카탈로그 보존 방어 로직
                    if ($masterData.babySupplies -and $masterData.babySupplies.Count -lt 50 -and (Test-Path $jsonPath)) {
                        try {
                            $localExport = Get-Content $jsonPath -Raw -Encoding UTF8 | ConvertFrom-Json
                            if ($localExport.babySupplies -and $localExport.babySupplies.Count -ge 50) {
                                Write-Host "[Notice] 클라우드 육아용품($($masterData.babySupplies.Count)개)보다 로컬 카탈로그($($localExport.babySupplies.Count)개)가 더 완전합니다. 로컬 육아용품 데이터를 우선 유지합니다." -ForegroundColor Yellow
                                $masterData.babySupplies = $localExport.babySupplies
                            }
                        } catch {}
                    }
                }
            }
        }
    } catch {
        Write-Host "[Notice] Cloud sync skipped/failed ($($_)). Falling back to local Excel..." -ForegroundColor Yellow
    }
}

# 2. Fallback to local Excel if cloud sync was not performed
if (-not $syncedFromCloud) {
    if (-not (Test-Path $excelPath)) {
        Write-Host "[Error] '출산준비물_통합데이터.xlsx' 파일을 찾을 수 없습니다." -ForegroundColor Red
        exit 1
    }

    Write-Host "Reading data from local '출산준비물_통합데이터.xlsx'..." -ForegroundColor Gray

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

    $defaultCoupangUrl = "https://link.coupang.com/a/hDXnz86Thk"

    # 1. 출산가방
    $s1 = $wb.Sheets.Item("출산가방 체크리스트")
    $bagRows = Parse-SheetRows $s1 14
    $bagList = @()
    foreach ($row in $bagRows) {
        if (-not $row[0]) { continue }
        $top1Url = if ($row.Count -ge 12 -and $row[11] -and $row[11].Trim()) { $row[11].Trim() } else { $defaultCoupangUrl }
        $top2Url = if ($row.Count -ge 13 -and $row[12] -and $row[12].Trim()) { $row[12].Trim() } else { $defaultCoupangUrl }
        $top3Url = if ($row.Count -ge 14 -and $row[13] -and $row[13].Trim()) { $row[13].Trim() } else { $defaultCoupangUrl }
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
            top1_url = $top1Url
            top2_url = $top2Url
            top3_url = $top3Url
        }
    }

    # 2. 육아용품
    $s2 = $wb.Sheets.Item("육아용품 체크리스트")
    $babyRows = Parse-SheetRows $s2 14
    $babyList = @()
    foreach ($row in $babyRows) {
        if (-not $row[0]) { continue }
        $top1Url = if ($row.Count -ge 12 -and $row[11] -and $row[11].Trim()) { $row[11].Trim() } else { $defaultCoupangUrl }
        $top2Url = if ($row.Count -ge 13 -and $row[12] -and $row[12].Trim()) { $row[12].Trim() } else { $defaultCoupangUrl }
        $top3Url = if ($row.Count -ge 14 -and $row[13] -and $row[13].Trim()) { $row[13].Trim() } else { $defaultCoupangUrl }
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
            top1_url = $top1Url
            top2_url = $top2Url
            top3_url = $top3Url
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
}

# 2.5 Ensure top1_url, top2_url, top3_url exist
$defaultCoupangUrl = "https://link.coupang.com/a/hDXnz86Thk"
if ($masterData.maternityBag) {
    foreach ($item in $masterData.maternityBag) {
        if (-not $item.top1_url) { $item | Add-Member -NotePropertyName "top1_url" -NotePropertyValue $defaultCoupangUrl -Force }
        if (-not $item.top2_url) { $item | Add-Member -NotePropertyName "top2_url" -NotePropertyValue $defaultCoupangUrl -Force }
        if (-not $item.top3_url) { $item | Add-Member -NotePropertyName "top3_url" -NotePropertyValue $defaultCoupangUrl -Force }
    }
}
if ($masterData.babySupplies) {
    foreach ($item in $masterData.babySupplies) {
        if (-not $item.top1_url) { $item | Add-Member -NotePropertyName "top1_url" -NotePropertyValue $defaultCoupangUrl -Force }
        if (-not $item.top2_url) { $item | Add-Member -NotePropertyName "top2_url" -NotePropertyValue $defaultCoupangUrl -Force }
        if (-not $item.top3_url) { $item | Add-Member -NotePropertyName "top3_url" -NotePropertyValue $defaultCoupangUrl -Force }
    }
}

# 3. JSON 저장
$jsonContent = $masterData | ConvertTo-Json -Depth 5
[System.IO.File]::WriteAllText($jsonPath, $jsonContent, [System.Text.Encoding]::UTF8)

# 4. 웹 프리뷰용 master_data.js 저장 (루트 및 preview)
$jsContent = "window.MASTER_DATA = " + $jsonContent + ";"
$jsPath = Join-Path $rootDir "master_data.js"
[System.IO.File]::WriteAllText($jsPath, $jsContent, [System.Text.Encoding]::UTF8)

$previewJsPath = Join-Path $rootDir "preview\master_data.js"
if (Test-Path (Join-Path $rootDir "preview")) {
    [System.IO.File]::WriteAllText($previewJsPath, $jsContent, [System.Text.Encoding]::UTF8)
}

# 5. 안드로이드 앱용 assets/data_export.json 저장
$assetsDir = Join-Path $rootDir "app\src\main\assets"
if (-not (Test-Path $assetsDir)) {
    New-Item -ItemType Directory -Path $assetsDir -Force | Out-Null
}
$androidJsonPath = Join-Path $assetsDir "data_export.json"
[System.IO.File]::WriteAllText($androidJsonPath, $jsonContent, [System.Text.Encoding]::UTF8)

Write-Host "[OK] data_export.json & master_data.js updated!" -ForegroundColor Green
Write-Host " - 출산가방: $($masterData.maternityBag.Count)개" -ForegroundColor Gray
Write-Host " - 육아용품: $($masterData.babySupplies.Count)개" -ForegroundColor Gray
Write-Host " - 시기별할일: $($masterData.todos.Count)개" -ForegroundColor Gray
Write-Host " - 출산혜택: $($masterData.benefits.Count)개" -ForegroundColor Gray
Write-Host " - 안드로이드 에셋: $androidJsonPath" -ForegroundColor Gray
Write-Host " - 웹 프리뷰 데이터: $jsPath" -ForegroundColor Gray

Write-Host "----------------------------------------------------------" -ForegroundColor DarkGray
if ($syncedFromCloud) {
    Write-Host "[SUCCESS] 구글 스프레드시트(클라우드)에서 실시간 동기화 완료!" -ForegroundColor Green
} else {
    Write-Host "[SUCCESS] 엑셀 데이터가 앱 및 프리뷰에 즉시 동기화되었습니다!" -ForegroundColor Green
}
Write-Host "          (브라우저에서 새로고침 F5를 누르면 즉시 반영됩니다)" -ForegroundColor Cyan
Write-Host "----------------------------------------------------------" -ForegroundColor DarkGray