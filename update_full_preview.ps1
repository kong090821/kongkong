$enc = [System.Text.Encoding]::UTF8
$html = [System.IO.File]::ReadAllText('preview/index.html', $enc)
$html = $html.Replace("`r`n", "`n")

# 1. CSS 추가
$cssMarker = "    .tag-chip-purchase.newitem { background: #e8f5e9; color: #2e7d32; }"
$newCss = @'
    .tag-chip-purchase.newitem { background: #e8f5e9; color: #2e7d32; }

    /* 검색 및 필터 UI */
    .search-filter-box {
      background: var(--surface);
      border: 1px solid var(--border-color);
      border-radius: 12px;
      padding: 8px 10px;
      margin-bottom: 8px;
      display: flex;
      flex-direction: column;
      gap: 6px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.02);
    }
    .search-input-wrapper {
      display: flex;
      align-items: center;
      background: var(--bg-warm);
      border-radius: 8px;
      padding: 4px 8px;
      border: 1px solid #ebdcd5;
    }
    .search-input-wrapper input {
      flex: 1;
      border: none;
      background: transparent;
      outline: none;
      font-size: 12.5px;
      color: var(--text-dark);
      padding: 2px 6px;
    }
    .search-clear-btn {
      background: none;
      border: none;
      font-size: 13px;
      color: var(--text-muted);
      cursor: pointer;
      padding: 0 4px;
      display: none;
    }
    .search-clear-btn.visible {
      display: block;
    }
    .filter-chips-row {
      display: flex;
      gap: 5px;
      overflow-x: auto;
      padding: 2px 0;
      scrollbar-width: none;
    }
    .filter-chips-row::-webkit-scrollbar {
      display: none;
    }
    .filter-chip {
      padding: 3px 9px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: 600;
      background: #f4ece8;
      color: var(--text-muted);
      cursor: pointer;
      white-space: nowrap;
      border: 1px solid transparent;
      transition: all 0.15s;
      user-select: none;
    }
    .filter-chip:hover {
      background: #ebdcd5;
    }
    .filter-chip.active {
      background: var(--primary);
      color: #fff;
      border-color: var(--primary);
    }

    /* 정렬 가능한 컬럼 헤더 */
    .sortable-th {
      cursor: pointer;
      user-select: none;
      display: inline-flex;
      align-items: center;
      gap: 2px;
      transition: all 0.15s;
    }
    .sortable-th:hover {
      color: var(--primary);
    }
    .sortable-th.sorted {
      color: var(--primary);
      font-weight: 800;
    }
    .sort-icon {
      font-size: 9px;
      opacity: 0.45;
      margin-left: 2px;
    }
    .sortable-th.sorted .sort-icon {
      opacity: 1;
      font-weight: 900;
    }
'@
$newCss = $newCss.Replace("`r`n", "`n")
if (-not $html.Contains(".search-filter-box")) {
    $html = $html.Replace($cssMarker, $newCss)
}

# 2. 1번 화면: 출산가방 헤더 & 테이블 헤더 교체
$oldBagHeader = @'
        <!-- 테이블 헤더 -->
        <div class="bag-table-header">
          <div class="bag-col-name">준비물</div>
          <div class="bag-col-qty">추천수량</div>
          <div class="bag-col-tags">태그</div>
          <div class="bag-col-check" id="bagColCheckTitle">내가방 넣기</div>
        </div>
'@
$oldBagHeader = $oldBagHeader.Replace("`r`n", "`n")

$newBagHeader = @'
        <!-- 검색 및 필터 -->
        <div class="search-filter-box">
          <div class="search-input-wrapper">
            <span style="font-size:13px; color:var(--text-muted);">🔍</span>
            <input type="text" id="bagSearchInput" placeholder="출산가방 준비물 검색 (이름, 태그, 메모)..." oninput="handleBagSearch(this.value)" />
            <button class="search-clear-btn" id="bagSearchClear" onclick="clearBagSearch()">✕</button>
          </div>
          <div class="filter-chips-row">
            <div class="filter-chip active" id="bagChip_all" onclick="setBagFilter('전체')">전체</div>
            <div class="filter-chip" id="bagChip_req" onclick="setBagFilter('#필수')">#필수</div>
            <div class="filter-chip" id="bagChip_opt" onclick="setBagFilter('#선택')">#선택</div>
            <div class="filter-chip" id="bagChip_hosp" onclick="setBagFilter('#병원')">#병원</div>
            <div class="filter-chip" id="bagChip_care" onclick="setBagFilter('#조리원')">#조리원</div>
            <div class="filter-chip" id="bagChip_disch" onclick="setBagFilter('#퇴원퇴소')">#퇴원퇴소</div>
            <div class="filter-chip" id="bagChip_saved" onclick="setBagFilter('👜담김')">👜 담긴것</div>
          </div>
        </div>

        <!-- 테이블 헤더 (목차 클릭 시 정렬) -->
        <div class="bag-table-header">
          <div class="bag-col-name sortable-th" id="th_bag_title" onclick="handleBagSort('title')">
            준비물 <span class="sort-icon" id="icon_bag_title">⇅</span>
          </div>
          <div class="bag-col-qty sortable-th" id="th_bag_qty" onclick="handleBagSort('qty')">
            추천수량 <span class="sort-icon" id="icon_bag_qty">⇅</span>
          </div>
          <div class="bag-col-tags sortable-th" id="th_bag_tag" onclick="handleBagSort('tag')">
            태그 <span class="sort-icon" id="icon_bag_tag">⇅</span>
          </div>
          <div class="bag-col-check sortable-th" id="bagColCheckTitle" onclick="handleBagSort('saved')">
            내가방 넣기 <span class="sort-icon" id="icon_bag_saved">⇅</span>
          </div>
        </div>
'@
$newBagHeader = $newBagHeader.Replace("`r`n", "`n")
$html = $html.Replace($oldBagHeader, $newBagHeader)

# 3. 2번 화면: 육아용품 헤더 & 테이블 헤더 교체
$oldBabyHeader = @'
        <div class="bag-table-header">
          <div class="bag-col-name">준비물</div>
          <div class="bag-col-qty" style="width: 75px;">시기</div>
          <div class="bag-col-tags" style="width: 75px;">구입경로</div>
          <div class="bag-col-check" id="babyColCheckTitle">내가방에 넣기</div>
        </div>
'@
$oldBabyHeader = $oldBabyHeader.Replace("`r`n", "`n")

$newBabyHeader = @'
        <!-- 검색 및 필터 -->
        <div class="search-filter-box">
          <div class="search-input-wrapper">
            <span style="font-size:13px; color:var(--text-muted);">🔍</span>
            <input type="text" id="babySearchInput" placeholder="육아용품 검색 (품목명, 시기, 당근)..." oninput="handleBabySearch(this.value)" />
            <button class="search-clear-btn" id="babySearchClear" onclick="clearBabySearch()">✕</button>
          </div>
          <div class="filter-chips-row">
            <div class="filter-chip active" id="babyChip_all" onclick="setBabyFilter('전체')">전체</div>
            <div class="filter-chip" id="babyChip_carrot" onclick="setBabyFilter('#당근')">#당근 (중고)</div>
            <div class="filter-chip" id="babyChip_new" onclick="setBabyFilter('#새제품')">#새제품 (신품)</div>
            <div class="filter-chip" id="babyChip_nb" onclick="setBabyFilter('#신생아')">#신생아</div>
            <div class="filter-chip" id="babyChip_inf" onclick="setBabyFilter('#영아')">#영아</div>
            <div class="filter-chip" id="babyChip_tod" onclick="setBabyFilter('#유아')">#유아</div>
            <div class="filter-chip" id="babyChip_saved" onclick="setBabyFilter('👜찜')">👜 찜한것</div>
          </div>
        </div>

        <!-- 테이블 헤더 (목차 클릭 시 정렬) -->
        <div class="bag-table-header">
          <div class="bag-col-name sortable-th" id="th_baby_title" onclick="handleBabySort('title')">
            준비물 <span class="sort-icon" id="icon_baby_title">⇅</span>
          </div>
          <div class="bag-col-qty sortable-th" style="width: 75px;" id="th_baby_period" onclick="handleBabySort('period')">
            시기 <span class="sort-icon" id="icon_baby_period">⇅</span>
          </div>
          <div class="bag-col-tags sortable-th" style="width: 75px;" id="th_baby_purchase" onclick="handleBabySort('purchase')">
            구입경로 <span class="sort-icon" id="icon_baby_purchase">⇅</span>
          </div>
          <div class="bag-col-check sortable-th" id="babyColCheckTitle" onclick="handleBabySort('saved')">
            내가방에 넣기 <span class="sort-icon" id="icon_baby_saved">⇅</span>
          </div>
        </div>
'@
$newBabyHeader = $newBabyHeader.Replace("`r`n", "`n")
$html = $html.Replace($oldBabyHeader, $newBabyHeader)

# 4. 3번 화면: 시기별 할일 헤더 & 테이블 헤더 교체
$oldTodoHeader = @'
        <div class="bag-table-header">
          <div class="bag-col-name" style="flex: 1.1;">해야할일</div>
          <div class="bag-col-qty" style="flex: 1.5; text-align: left; padding-left: 6px;">팁</div>
          <div class="bag-col-tags" style="width: 65px;">누가?</div>
          <div class="bag-col-check" id="todoColCheckTitle" style="width: 75px;">내가방에 넣기</div>
        </div>
'@
$oldTodoHeader = $oldTodoHeader.Replace("`r`n", "`n")

$newTodoHeader = @'
        <!-- 검색 및 필터 -->
        <div class="search-filter-box">
          <div class="search-input-wrapper">
            <span style="font-size:13px; color:var(--text-muted);">🔍</span>
            <input type="text" id="todoSearchInput" placeholder="할일 검색 (해야할일, 팁, 아빠, 엄마)..." oninput="handleTodoSearch(this.value)" />
            <button class="search-clear-btn" id="todoSearchClear" onclick="clearTodoSearch()">✕</button>
          </div>
          <div class="filter-chips-row">
            <div class="filter-chip active" id="todoChip_all" onclick="setTodoFilter('전체')">전체</div>
            <div class="filter-chip" id="todoChip_dad" onclick="setTodoFilter('#아빠')">#아빠</div>
            <div class="filter-chip" id="todoChip_mom" onclick="setTodoFilter('#엄마')">#엄마</div>
            <div class="filter-chip" id="todoChip_couple" onclick="setTodoFilter('#부부')">#부부</div>
            <div class="filter-chip" id="todoChip_done" onclick="setTodoFilter('✅완료')">✅ 완료됨</div>
            <div class="filter-chip" id="todoChip_undone" onclick="setTodoFilter('⏳미완료')">⏳ 미완료</div>
          </div>
        </div>

        <!-- 테이블 헤더 (목차 클릭 시 정렬) -->
        <div class="bag-table-header">
          <div class="bag-col-name sortable-th" style="flex: 1.1;" id="th_todo_title" onclick="handleTodoSort('title')">
            해야할일 <span class="sort-icon" id="icon_todo_title">⇅</span>
          </div>
          <div class="bag-col-qty sortable-th" style="flex: 1.5; text-align: left; padding-left: 6px;" id="th_todo_tip" onclick="handleTodoSort('tip')">
            팁 <span class="sort-icon" id="icon_todo_tip">⇅</span>
          </div>
          <div class="bag-col-tags sortable-th" style="width: 65px;" id="th_todo_role" onclick="handleTodoSort('role')">
            누가? <span class="sort-icon" id="icon_todo_role">⇅</span>
          </div>
          <div class="bag-col-check sortable-th" style="width: 75px;" id="todoColCheckTitle" onclick="handleTodoSort('check')">
            체크 <span class="sort-icon" id="icon_todo_check">⇅</span>
          </div>
        </div>
'@
$newTodoHeader = $newTodoHeader.Replace("`r`n", "`n")
$html = $html.Replace($oldTodoHeader, $newTodoHeader)

# 5. 4번 화면: 전국, 지역별, 개인별 혜택 화면 헤더 & 테이블 헤더 교체
$oldNatBenefits = @'
        <div class="action-btn-row">
          <button class="btn-action-add" style="background:#E65100;" onclick="openBenefitAddModal()">+ 혜택 추가</button>
          <button class="btn-action-delete" id="btnNationalDeleteToggle" onclick="toggleBenefitDeleteMode()">🗑️ 혜택 삭제</button>
        </div>

        <div id="nationalBenefitsList"></div>
'@
$oldNatBenefits = $oldNatBenefits.Replace("`r`n", "`n")

$newNatBenefits = @'
        <div class="action-btn-row">
          <button class="btn-action-add" style="background:#E65100;" onclick="openBenefitAddModal()">+ 혜택 추가</button>
          <button class="btn-action-delete" id="btnNationalDeleteToggle" onclick="toggleBenefitDeleteMode()">🗑️ 혜택 삭제</button>
        </div>

        <!-- 혜택 검색 및 필터 -->
        <div class="search-filter-box">
          <div class="search-input-wrapper">
            <span style="font-size:13px; color:var(--text-muted);">🔍</span>
            <input type="text" id="natBenefitSearchInput" placeholder="전국 공통 혜택 검색 (혜택명, 지원금, 자격)..." oninput="handleBenefitSearch(this.value, 'nat')" />
            <button class="search-clear-btn" id="natBenefitSearchClear" onclick="clearBenefitSearch('nat')">✕</button>
          </div>
          <div class="filter-chips-row">
            <div class="filter-chip active" onclick="setBenefitFilter('전체', 'nat', this)">전체</div>
            <div class="filter-chip" onclick="setBenefitFilter('현금', 'nat', this)">현금 지원</div>
            <div class="filter-chip" onclick="setBenefitFilter('바우처', 'nat', this)">바우처</div>
            <div class="filter-chip" onclick="setBenefitFilter('감면', 'nat', this)">요금 감면</div>
            <div class="filter-chip" onclick="setBenefitFilter('의료', 'nat', this)">의료·보육</div>
            <div class="filter-chip" onclick="setBenefitFilter('👜담김', 'nat', this)">👜 담긴 혜택</div>
          </div>
        </div>

        <!-- 혜택 테이블 헤더 (목차 정렬) -->
        <div class="bag-table-header">
          <div class="bag-col-name sortable-th" id="th_benefit_nat_title" onclick="handleBenefitSort('title')">
            혜택 <span class="sort-icon" id="icon_benefit_nat_title">⇅</span>
          </div>
          <div class="bag-col-qty sortable-th" style="flex: 1.2; text-align: left; padding-left: 6px;" id="th_benefit_nat_elig" onclick="handleBenefitSort('eligibility')">
            자격 및 조건 <span class="sort-icon" id="icon_benefit_nat_elig">⇅</span>
          </div>
          <div class="bag-col-check sortable-th" id="natBenefitColCheckTitle" onclick="handleBenefitSort('saved')">
            내가방 넣기 <span class="sort-icon" id="icon_benefit_nat_saved">⇅</span>
          </div>
        </div>

        <div id="nationalBenefitsList"></div>
'@
$newNatBenefits = $newNatBenefits.Replace("`r`n", "`n")
$html = $html.Replace($oldNatBenefits, $newNatBenefits)

$oldRegBenefits = @'
        <div class="region-chip-container" id="regionChips"></div>
        <div id="regionalBenefitsList"></div>
'@
$oldRegBenefits = $oldRegBenefits.Replace("`r`n", "`n")

$newRegBenefits = @'
        <div class="region-chip-container" id="regionChips"></div>

        <!-- 혜택 검색 및 필터 -->
        <div class="search-filter-box">
          <div class="search-input-wrapper">
            <span style="font-size:13px; color:var(--text-muted);">🔍</span>
            <input type="text" id="regBenefitSearchInput" placeholder="지역별 혜택 검색..." oninput="handleBenefitSearch(this.value, 'reg')" />
            <button class="search-clear-btn" id="regBenefitSearchClear" onclick="clearBenefitSearch('reg')">✕</button>
          </div>
          <div class="filter-chips-row">
            <div class="filter-chip active" onclick="setBenefitFilter('전체', 'reg', this)">전체</div>
            <div class="filter-chip" onclick="setBenefitFilter('축하금', 'reg', this)">축하금</div>
            <div class="filter-chip" onclick="setBenefitFilter('지원금', 'reg', this)">지원금</div>
            <div class="filter-chip" onclick="setBenefitFilter('👜담김', 'reg', this)">👜 담긴 혜택</div>
          </div>
        </div>

        <!-- 혜택 테이블 헤더 (목차 정렬) -->
        <div class="bag-table-header">
          <div class="bag-col-name sortable-th" id="th_benefit_reg_title" onclick="handleBenefitSort('title')">
            혜택 <span class="sort-icon" id="icon_benefit_reg_title">⇅</span>
          </div>
          <div class="bag-col-qty sortable-th" style="flex: 1.2; text-align: left; padding-left: 6px;" id="th_benefit_reg_elig" onclick="handleBenefitSort('eligibility')">
            자격 및 조건 <span class="sort-icon" id="icon_benefit_reg_elig">⇅</span>
          </div>
          <div class="bag-col-check sortable-th" id="regBenefitColCheckTitle" onclick="handleBenefitSort('saved')">
            내가방 넣기 <span class="sort-icon" id="icon_benefit_reg_saved">⇅</span>
          </div>
        </div>

        <div id="regionalBenefitsList"></div>
'@
$newRegBenefits = $newRegBenefits.Replace("`r`n", "`n")
$html = $html.Replace($oldRegBenefits, $newRegBenefits)

$oldPerBenefits = @'
        <div class="action-btn-row">
          <button class="btn-action-add" style="background:#E65100;" onclick="openBenefitAddModal()">+ 혜택 추가</button>
          <button class="btn-action-delete" id="btnPersonalDeleteToggle" onclick="toggleBenefitDeleteMode()">🗑️ 혜택 삭제</button>
        </div>

        <div id="personalBenefitsList"></div>
'@
$oldPerBenefits = $oldPerBenefits.Replace("`r`n", "`n")

$newPerBenefits = @'
        <div class="action-btn-row">
          <button class="btn-action-add" style="background:#E65100;" onclick="openBenefitAddModal()">+ 혜택 추가</button>
          <button class="btn-action-delete" id="btnPersonalDeleteToggle" onclick="toggleBenefitDeleteMode()">🗑️ 혜택 삭제</button>
        </div>

        <!-- 혜택 검색 및 필터 -->
        <div class="search-filter-box">
          <div class="search-input-wrapper">
            <span style="font-size:13px; color:var(--text-muted);">🔍</span>
            <input type="text" id="perBenefitSearchInput" placeholder="개인별 혜택 검색 (공무원, 보험, 세액공제)..." oninput="handleBenefitSearch(this.value, 'per')" />
            <button class="search-clear-btn" id="perBenefitSearchClear" onclick="clearBenefitSearch('per')">✕</button>
          </div>
          <div class="filter-chips-row">
            <div class="filter-chip active" onclick="setBenefitFilter('전체', 'per', this)">전체</div>
            <div class="filter-chip" onclick="setBenefitFilter('공무원', 'per', this)">공무원/교직원</div>
            <div class="filter-chip" onclick="setBenefitFilter('보험', 'per', this)">보험 환급</div>
            <div class="filter-chip" onclick="setBenefitFilter('공제', 'per', this)">연말정산</div>
            <div class="filter-chip" onclick="setBenefitFilter('👜담김', 'per', this)">👜 담긴 혜택</div>
          </div>
        </div>

        <!-- 혜택 테이블 헤더 (목차 정렬) -->
        <div class="bag-table-header">
          <div class="bag-col-name sortable-th" id="th_benefit_per_title" onclick="handleBenefitSort('title')">
            혜택 <span class="sort-icon" id="icon_benefit_per_title">⇅</span>
          </div>
          <div class="bag-col-qty sortable-th" style="flex: 1.2; text-align: left; padding-left: 6px;" id="th_benefit_per_elig" onclick="handleBenefitSort('eligibility')">
            자격 및 조건 <span class="sort-icon" id="icon_benefit_per_elig">⇅</span>
          </div>
          <div class="bag-col-check sortable-th" id="perBenefitColCheckTitle" onclick="handleBenefitSort('saved')">
            내가방 넣기 <span class="sort-icon" id="icon_benefit_per_saved">⇅</span>
          </div>
        </div>

        <div id="personalBenefitsList"></div>
'@
$newPerBenefits = $newPerBenefits.Replace("`r`n", "`n")
$html = $html.Replace($oldPerBenefits, $newPerBenefits)

# 파일 저장
[System.IO.File]::WriteAllText('preview/index.html', $html, $enc)
Write-Host "Updated HTML successfully!"
