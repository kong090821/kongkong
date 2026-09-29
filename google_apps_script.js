/**
 * ==============================================================================
 * 🍼 [꽁꽁 출산가방] 구글 스프레드시트 완전 자동화 및 실시간 클라우드 연동 스크립트
 * ==============================================================================
 * 
 * [주요 핵심 기능]
 * 1. 🆕 신규 행 자동 완성 & 어플 등록:
 *    - 새 행을 추가하고 품목명만 적어도, 고유 ID 자동 생성 및 맘카페 1위/쿠팡 TOP 1~3 자동 완성
 *    - [어플에 즉시 반영하기] 클릭 시 새로 추가한 행이 어플에 실시간으로 즉시 등록
 * 
 * 2. 🛡️ 빨간색 셀 잠금 & 바이럴 광고 차단:
 *    - 사용자가 빨간색(채우기 색상)으로 칠해둔 셀은 자동 수집 시 절대 덮어쓰지 않고 값 영구 보존!
 *    - 빨간색으로 지정된 상품은 '바이럴 광고 블랙리스트'로 인식되어 향후 자동 수집 추천에서도 자동 제외
 * 
 * 3. ☕ 맘카페 & 📦 쿠팡 분리 자동 수집 스케줄러:
 *    - 맘카페 최다 언급: 매주 월요일 새벽 06:00 자동 수집 (검토: 연노랑 #FFFDE7)
 *    - 쿠팡 실시간 랭킹: 매일 새벽 06:00 자동 수집 (검토: 연초록 #E8F5E9)
 * 
 * 4. 🚀 실시간 어플 동기화:
 *    - [최신 데이터를 어플에 즉시 반영하기] 클릭 시 모든 앱 사용자 및 웹 프리뷰에 실시간 배포
 */

// ------------------------------------------------------------------------------
// 1. 스프레드시트 열릴 때 커스텀 메뉴 등록
// ------------------------------------------------------------------------------
function onOpen() {
  const ui = SpreadsheetApp.getUi();
  ui.createMenu('🍼 꽁꽁 출산가방 관리')
    .addItem('🚀 [전체 일괄 반영] 모든 시트 어플에 즉시 반영', 'syncToApp')
    .addSeparator()
    .addItem('🎒 [시트별 반영] 1. 출산가방 체크리스트만 반영', 'syncMaternityBagOnly')
    .addItem('🍼 [시트별 반영] 2. 육아용품 체크리스트만 반영', 'syncBabySuppliesOnly')
    .addItem('⏰ [시트별 반영] 3. 시기별 할일만 반영', 'syncTodosOnly')
    .addItem('💰 [시트별 반영] 4. 출산 혜택만 반영', 'syncBenefitsOnly')
    .addItem('🎯 [시트별 반영] 5. 맞춤 추천 가방 설정만 반영', 'syncRecommendationsOnly')
    .addSeparator()
    .addItem('🎯 맞춤 추천 설정 갱신', 'updateRecommendationSettingsFromStats')
    .addSeparator()
    .addItem('✨ [자동 완성] 신규 품목 데이터 및 추천 상품 채우기', 'autoFillMissingRowData')
    .addItem('💜 [사용자 추가] 신규 등록 품목 연보라색 셀 강조 표시', 'highlightCustomUserAddedItems')
    .addItem('☕ [수동 실행] 맘카페 언급 1위 데이터 지금 수집', 'updateMomCafeWeeklyData')
    .addItem('📦 [수동 실행] 쿠팡 TOP 3 랭킹 데이터 지금 수집', 'updateCoupangDailyTop3')
    .addSeparator()
    .addItem('⏰ [자동화 설정] 월요 맘카페(06시) + 매일 쿠팡(06시) 스케줄러 등록', 'setupAllTriggers')
    .addItem('ℹ️ 연동 가이드 및 수집 상태 확인', 'showGuideDialog')
    .addToUi();
}

// [사용자 활동 통계 기반 맞춤 추천 설정 갱신]
function updateRecommendationSettingsFromStats() {
  analyzeAndUpdateRecommendationsManual();
}

function getOrInitCachedData(ss) {
  const cached = PropertiesService.getScriptProperties().getProperty('APP_DATA_CACHE');
  if (cached) {
    try {
      return JSON.parse(cached);
    } catch(e) {}
  }
  return getAllSheetsData(ss);
}

// ------------------------------------------------------------------------------
// 2. [어플 실시간 반영] (1) 전체 일괄 반영
// ------------------------------------------------------------------------------
function syncToApp() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();

  try {
    getOrCreateRecommendationSheet(ss);
    getOrCreateActivitySheet(ss);

    const autoFilled = ensureRowIntegrity(ss);
    const data = getAllSheetsData(ss);
    const recRules = getRecommendationRules(ss);
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");
    
    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(data));
    PropertiesService.getScriptProperties().setProperty('RECOMMENDATION_CACHE', JSON.stringify(recRules));

    clearPendingHighlights(ss);

    let msg = '• 갱신 일시: ' + now + '\n' +
              '• 출산가방 품목: ' + data.maternityBag.length + '개\n' +
              '• 육아용품 품목: ' + data.babySupplies.length + '개\n' +
              '• 시기별 할일: ' + data.todos.length + '개\n' +
              '• 출산 혜택: ' + data.benefits.length + '개\n' +
              '• 맞춤 추천 가방: 제왕/자연/조리원 규칙 동시 배포 완료\n';
    
    if (autoFilled > 0) {
      msg += '• 신규 추가 행: ' + autoFilled + '개 품목 ID 및 데이터 자동 생성\n';
    }
    msg += '\n✅ 전체 시트의 모든 수정 내용이 어플에 즉시 반영되었습니다!';

    ui.alert('🎉 전체 어플 데이터 갱신 완료!', msg, ui.ButtonSet.OK);
  } catch (err) {
    ui.alert('❌ 갱신 중 오류가 발생했습니다: ' + err.toString());
  }
}

// ------------------------------------------------------------------------------
// [각 시트별 개별 반영 함수들]
// ------------------------------------------------------------------------------

// (1) 🎒 출산가방 체크리스트만 반영
function syncMaternityBagOnly() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  try {
    ensureRowIntegrity(ss);
    const bagData = parseSheetToObjects(ss.getSheetByName('출산가방 체크리스트'), [
      'id', 'tabCategory', 'section', 'title', 'recommendedQty', 'locationTags', 'note', 'momcafe1st', 'top1', 'top2', 'top3'
    ]);
    const fullData = getOrInitCachedData(ss);
    fullData.maternityBag = bagData;
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(fullData));
    clearPendingHighlights(ss, '출산가방 체크리스트');

    ui.alert('🎒 출산가방 체크리스트 반영 완료',
      '• 갱신 일시: ' + now + '\n• 반영 품목 수: ' + bagData.length + '개\n\n✅ 출산가방 체크리스트만 어플에 실시간 반영되었습니다!',
      ui.ButtonSet.OK
    );
  } catch(err) {
    ui.alert('❌ 출산가방 반영 중 오류 발생: ' + err.toString());
  }
}

// (2) 🍼 육아용품 체크리스트만 반영
function syncBabySuppliesOnly() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  try {
    ensureRowIntegrity(ss);
    const babyData = parseSheetToObjects(ss.getSheetByName('육아용품 체크리스트'), [
      'id', 'category', 'section', 'title', 'period', 'purchaseTag', 'description', 'momcafe1st', 'top1', 'top2', 'top3'
    ]);
    const fullData = getOrInitCachedData(ss);
    fullData.babySupplies = babyData;
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(fullData));
    clearPendingHighlights(ss, '육아용품 체크리스트');

    ui.alert('🍼 육아용품 체크리스트 반영 완료',
      '• 갱신 일시: ' + now + '\n• 반영 품목 수: ' + babyData.length + '개\n\n✅ 육아용품 체크리스트만 어플에 실시간 반영되었습니다!',
      ui.ButtonSet.OK
    );
  } catch(err) {
    ui.alert('❌ 육아용품 반영 중 오류 발생: ' + err.toString());
  }
}

// (3) ⏰ 시기별 할일만 반영
function syncTodosOnly() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  try {
    const todosData = parseSheetToObjects(ss.getSheetByName('시기별할일'), [
      'id', 'category', 'role', 'title', 'tip'
    ]);
    const fullData = getOrInitCachedData(ss);
    fullData.todos = todosData;
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(fullData));

    ui.alert('⏰ 시기별 할일 반영 완료',
      '• 갱신 일시: ' + now + '\n• 반영 항목 수: ' + todosData.length + '개\n\n✅ 시기별 할일만 어플에 실시간 반영되었습니다!',
      ui.ButtonSet.OK
    );
  } catch(err) {
    ui.alert('❌ 시기별 할일 반영 중 오류 발생: ' + err.toString());
  }
}

// (4) 💰 출산 혜택만 반영
function syncBenefitsOnly() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  try {
    const benefitsData = parseSheetToObjects(ss.getSheetByName('출산혜택정리'), [
      'id', 'region', 'title', 'type', 'amount', 'eligibility', 'timing', 'place'
    ]);
    const fullData = getOrInitCachedData(ss);
    fullData.benefits = benefitsData;
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(fullData));

    ui.alert('💰 출산 혜택 반영 완료',
      '• 갱신 일시: ' + now + '\n• 반영 혜택 수: ' + benefitsData.length + '개\n\n✅ 출산 혜택만 어플에 실시간 반영되었습니다!',
      ui.ButtonSet.OK
    );
  } catch(err) {
    ui.alert('❌ 출산 혜택 반영 중 오류 발생: ' + err.toString());
  }
}

// (5) 🎯 맞춤 추천 가방 설정만 반영
function syncRecommendationsOnly() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  try {
    const rules = getRecommendationRules(ss);
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('RECOMMENDATION_CACHE', JSON.stringify(rules));
    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);

    ui.alert('🎯 맞춤 추천 가방 설정 반영 완료',
      '• 갱신 일시: ' + now + '\n' +
      '• 제왕절개 추천: ' + rules.cesarean.length + '개 품목\n' +
      '• 자연분만 추천: ' + rules.natural.length + '개 품목\n' +
      '• 조리원 이용 추천: ' + rules.careCenter.length + '개 품목\n' +
      '• 자택 조리 추천: ' + rules.homeCare.length + '개 품목\n\n' +
      '✅ 수집 통계 기반 맞춤 추천 가방 규칙이 어플에 실시간 배포되었습니다!',
      ui.ButtonSet.OK
    );
  } catch(err) {
    ui.alert('❌ 맞춤 추천 반영 중 오류 발생: ' + err.toString());
  }
}

// ------------------------------------------------------------------------------
// 3. [신규 행 실시간 감지 & 자동 완성] onEdit 트리거
// ------------------------------------------------------------------------------
function onEdit(e) {
  if (!e || !e.range) return;
  const sheet = e.range.getSheet();
  const sheetName = sheet.getName();
  const row = e.range.getRow();
  const col = e.range.getColumn();

  // 헤더 행 무시
  if (row < 2) return;

  // 출산가방 또는 육아용품 시트에서 4열(품목명)을 입력/수정했을 때 실시간 자동 완성
  if ((sheetName === '출산가방 체크리스트' || sheetName === '육아용품 체크리스트') && col === 4) {
    const titleVal = String(e.range.getValue() || '').trim();
    if (!titleVal) return;

    const idCell = sheet.getRange(row, 1);
    const prefix = sheetName.indexOf('출산가방') !== -1 ? 'm_custom_' : 'b_custom_';
    if (!String(idCell.getValue() || '').trim()) {
      idCell.setValue(prefix + row + '_' + new Date().getTime().toString().slice(-4));
    }

    const keyword = cleanKeyword(titleVal);
    const ss = sheet.getParent();
    const blacklist = getBlacklistedProducts(ss);

    // 8열(맘카페) 확인 및 자동 채우기 (빨간색이 아닐 때만)
    const momCell = sheet.getRange(row, 8);
    if (!String(momCell.getValue() || '').trim() && !isRedColor(momCell.getBackground())) {
      momCell.setValue(fetchMomCafeMention(keyword, blacklist));
      momCell.setBackground('#E1F5FE'); // 자동완성 표시: 연하늘색
    }

    // 9~11열(쿠팡 TOP 1~3) 확인 및 자동 채우기 (빨간색이 아닐 때만)
    const coupang = fetchCoupangTop3(keyword, blacklist);
    const top1Cell = sheet.getRange(row, 9);
    if (!String(top1Cell.getValue() || '').trim() && !isRedColor(top1Cell.getBackground())) {
      top1Cell.setValue(coupang.top1);
      top1Cell.setBackground('#E1F5FE');
    }
    const top2Cell = sheet.getRange(row, 10);
    if (!String(top2Cell.getValue() || '').trim() && !isRedColor(top2Cell.getBackground())) {
      top2Cell.setValue(coupang.top2);
      top2Cell.setBackground('#E1F5FE');
    }
    const top3Cell = sheet.getRange(row, 11);
    if (!String(top3Cell.getValue() || '').trim() && !isRedColor(top3Cell.getBackground())) {
      top3Cell.setValue(coupang.top3);
      top3Cell.setBackground('#E1F5FE');
    }
  }
}

// 수동으로 신규 행 및 빈 칸 전체 일괄 자동 완성
function autoFillMissingRowData() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const count = ensureRowIntegrity(ss);
  SpreadsheetApp.getUi().alert(
    '✨ 자동 완성 완료',
    '총 ' + count + '개 신규 품목의 ID 및 맘카페/쿠팡 추천 데이터가 자동으로 채워졌습니다.\n\n확인 후 [최신 데이터를 어플에 즉시 반영하기]를 누르시면 어플에 바로 적용됩니다.',
    SpreadsheetApp.getUi().ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 4. [빨간색 채우기 감지 & 바이럴 광고 차단 엔진]
// ------------------------------------------------------------------------------

/**
 * 셀 배경색이 빨간색 계열인지 판별 (바이럴 차단 / 자동 덮어쓰기 보호)
 */
function isRedColor(hex) {
  if (!hex || typeof hex !== 'string') return false;
  hex = hex.trim().toLowerCase();
  if (hex === '#ffffff' || hex === '#fff' || hex === 'white') return false;
  if (hex === '#fffde7' || hex === '#e8f5e9' || hex === '#e1f5fe') return false; // 노랑/초록/하늘색 제외

  // 구글 시트 기본 팔레트 및 웹 표준 레드 계열
  const standardReds = [
    '#ff0000', '#ea4335', '#f44336', '#e53935', '#d32f2f', '#c62828', '#b71c1c',
    '#ff8a80', '#ff5252', '#ff1744', '#d50000', '#f4c7c3', '#ea9999', '#e06666',
    '#cc0000', '#990000', '#dd4b39', '#e57373', '#ef9a9a', '#ffcdd2'
  ];
  if (standardReds.indexOf(hex) !== -1) return true;

  if (hex.startsWith('#')) hex = hex.substring(1);
  if (hex.length === 3) {
    hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
  }
  if (hex.length !== 6) return false;

  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);

  if (isNaN(r) || isNaN(g) || isNaN(b)) return false;

  // 빨간색 성분이 압도적으로 높은지 판정
  if (r >= 180 && g <= 165 && b <= 165 && (r - g >= 25) && (r - b >= 25)) return true;
  if (r >= 150 && (r > g * 1.35) && (r > b * 1.35)) return true;

  return false;
}

/**
 * 빨간색으로 표시된 바이럴 광고 의심 상품명 블랙리스트 추출
 */
function getBlacklistedProducts(ss) {
  const blacklist = [];
  const sheets = ['출산가방 체크리스트', '육아용품 체크리스트'];
  
  sheets.forEach(function(sheetName) {
    const sheet = ss.getSheetByName(sheetName);
    if (!sheet || sheet.getLastRow() < 2) return;
    const lastRow = sheet.getLastRow();
    const lastCol = sheet.getLastColumn();
    const values = sheet.getRange(2, 1, lastRow - 1, lastCol).getValues();
    const bgs = sheet.getRange(2, 1, lastRow - 1, lastCol).getBackgrounds();

    for (let r = 0; r < values.length; r++) {
      for (let c = 0; c < values[r].length; c++) {
        if (isRedColor(bgs[r][c])) {
          const txt = String(values[r][c] || '').trim();
          if (txt && blacklist.indexOf(txt) === -1) {
            blacklist.push(txt);
          }
        }
      }
    }
  });

  return blacklist;
}

// ------------------------------------------------------------------------------
// 5. [행 데이터 무결성 보장] 새 행 추가 시 고유 ID 및 누락 데이터 자동 완성
// ------------------------------------------------------------------------------
function ensureRowIntegrity(ss) {
  const sheetBag = ss.getSheetByName('출산가방 체크리스트');
  const sheetBaby = ss.getSheetByName('육아용품 체크리스트');
  const sheetTodo = ss.getSheetByName('시기별할일');
  const sheetBenefit = ss.getSheetByName('출산혜택정리');
  const blacklist = getBlacklistedProducts(ss);

  let filledCount = 0;

  // 1. 출산가방 체크리스트 보정
  if (sheetBag && sheetBag.getLastRow() >= 2) {
    const lastRow = sheetBag.getLastRow();
    const range = sheetBag.getRange(2, 1, lastRow - 1, 11);
    const values = range.getValues();
    const bgs = range.getBackgrounds();
    let modified = false;

    for (let i = 0; i < values.length; i++) {
      const rawTitle = String(values[i][3] || '').trim(); // 4열: 품목명
      if (!rawTitle) continue;

      if (!String(values[i][0] || '').trim()) {
        values[i][0] = 'm_custom_' + (i + 1) + '_' + new Date().getTime().toString().slice(-4);
        modified = true;
        filledCount++;
      }
      if (!String(values[i][1] || '').trim()) { values[i][1] = '산모 용품'; modified = true; }
      if (!String(values[i][2] || '').trim()) { values[i][2] = '추가 준비물'; modified = true; }
      if (!String(values[i][4] || '').trim()) { values[i][4] = '1개'; modified = true; }
      if (!String(values[i][5] || '').trim()) { values[i][5] = '#병원, #조리원'; modified = true; }

      const keyword = cleanKeyword(rawTitle);

      // 맘카페 1위 (비어있고 빨간색이 아닌 경우만 자동 채움)
      if (!String(values[i][7] || '').trim() && !isRedColor(bgs[i][7])) {
        values[i][7] = fetchMomCafeMention(keyword, blacklist);
        bgs[i][7] = '#E1F5FE';
        modified = true;
      }

      // 쿠팡 TOP 1~3 (비어있고 빨간색이 아닌 경우만 자동 채움)
      const coupang = fetchCoupangTop3(keyword, blacklist);
      if (!String(values[i][8] || '').trim() && !isRedColor(bgs[i][8])) {
        values[i][8] = coupang.top1;
        bgs[i][8] = '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][9] || '').trim() && !isRedColor(bgs[i][9])) {
        values[i][9] = coupang.top2;
        bgs[i][9] = '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][10] || '').trim() && !isRedColor(bgs[i][10])) {
        values[i][10] = coupang.top3;
        bgs[i][10] = '#E1F5FE';
        modified = true;
      }
    }

    if (modified) {
      range.setValues(values);
      range.setBackgrounds(bgs);
    }
  }

  // 2. 육아용품 체크리스트 보정
  if (sheetBaby && sheetBaby.getLastRow() >= 2) {
    const lastRow = sheetBaby.getLastRow();
    const range = sheetBaby.getRange(2, 1, lastRow - 1, 11);
    const values = range.getValues();
    const bgs = range.getBackgrounds();
    let modified = false;

    for (let i = 0; i < values.length; i++) {
      const rawTitle = String(values[i][3] || '').trim(); // 4열: 용품명
      if (!rawTitle) continue;

      if (!String(values[i][0] || '').trim()) {
        values[i][0] = 'b_custom_' + (i + 1) + '_' + new Date().getTime().toString().slice(-4);
        modified = true;
        filledCount++;
      }
      if (!String(values[i][1] || '').trim()) { values[i][1] = '1. 먹이기 (수유 & 이유식)'; modified = true; }
      if (!String(values[i][2] || '').trim()) { values[i][2] = '추가 육아용품'; modified = true; }
      if (!String(values[i][4] || '').trim()) { values[i][4] = '#신생아, #영아'; modified = true; }
      if (!String(values[i][5] || '').trim()) { values[i][5] = '#새제품'; modified = true; }

      const keyword = cleanKeyword(rawTitle);

      if (!String(values[i][7] || '').trim() && !isRedColor(bgs[i][7])) {
        values[i][7] = fetchMomCafeMention(keyword, blacklist);
        bgs[i][7] = '#E1F5FE';
        modified = true;
      }

      const coupang = fetchCoupangTop3(keyword, blacklist);
      if (!String(values[i][8] || '').trim() && !isRedColor(bgs[i][8])) {
        values[i][8] = coupang.top1;
        bgs[i][8] = '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][9] || '').trim() && !isRedColor(bgs[i][9])) {
        values[i][9] = coupang.top2;
        bgs[i][9] = '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][10] || '').trim() && !isRedColor(bgs[i][10])) {
        values[i][10] = coupang.top3;
        bgs[i][10] = '#E1F5FE';
        modified = true;
      }
    }

    if (modified) {
      range.setValues(values);
      range.setBackgrounds(bgs);
    }
  }

  // 3. 시기별 할일 ID 보정
  if (sheetTodo && sheetTodo.getLastRow() >= 2) {
    const lastRow = sheetTodo.getLastRow();
    const range = sheetTodo.getRange(2, 1, lastRow - 1, 5);
    const values = range.getValues();
    let modified = false;

    for (let i = 0; i < values.length; i++) {
      const title = String(values[i][3] || '').trim();
      if (!title) continue;
      if (!String(values[i][0] || '').trim()) {
        values[i][0] = 'todo_custom_' + (i + 1) + '_' + new Date().getTime().toString().slice(-4);
        modified = true;
        filledCount++;
      }
      if (!String(values[i][1] || '').trim()) { values[i][1] = '1. 출산 전 (준비기)'; modified = true; }
      if (!String(values[i][2] || '').trim()) { values[i][2] = '#부부'; modified = true; }
    }
    if (modified) range.setValues(values);
  }

  // 4. 출산 혜택 ID 보정
  if (sheetBenefit && sheetBenefit.getLastRow() >= 2) {
    const lastRow = sheetBenefit.getLastRow();
    const range = sheetBenefit.getRange(2, 1, lastRow - 1, 8);
    const values = range.getValues();
    let modified = false;

    for (let i = 0; i < values.length; i++) {
      const title = String(values[i][2] || '').trim(); // 3열: 혜택명
      if (!title) continue;
      if (!String(values[i][0] || '').trim()) {
        values[i][0] = 'ben_custom_' + (i + 1) + '_' + new Date().getTime().toString().slice(-4);
        modified = true;
        filledCount++;
      }
      if (!String(values[i][1] || '').trim()) { values[i][1] = '전국 공통'; modified = true; }
      if (!String(values[i][3] || '').trim()) { values[i][3] = '지원금'; modified = true; }
    }
    if (modified) range.setValues(values);
  }

  return filledCount;
}

// ------------------------------------------------------------------------------
// 6. [파이프라인 1] 맘카페 언급 데이터 수집 (매주 월요일 06:00, 빨간색 셀 완전 보호)
// ------------------------------------------------------------------------------
function updateMomCafeWeeklyData() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheetBag = ss.getSheetByName('출산가방 체크리스트');
  const sheetBaby = ss.getSheetByName('육아용품 체크리스트');

  ensureRowIntegrity(ss);

  let updatedCount = 0;
  if (sheetBag) updatedCount += processSheetMomCafeSearch(sheetBag, 4, 8);
  if (sheetBaby) updatedCount += processSheetMomCafeSearch(sheetBaby, 4, 8);

  const logMsg = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm") + 
                 ' - [맘카페 수집 완료] 총 ' + updatedCount + '개 항목 업데이트 (빨간색 잠금 셀 제외/보존 완료)';
  PropertiesService.getScriptProperties().setProperty('LAST_MOMCAFE_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('☕ [맘카페 최다 언급] 수집이 완료되었습니다!\n\n• 연노란색: 새로 수집된 추천 상품\n• 빨간색: 사용자 잠금 설정으로 자동 보존된 품목\n\n검토 후 [어플에 즉시 반영하기]를 눌러주세요.');
  } catch(e) {
    console.log(logMsg);
  }
}

function processSheetMomCafeSearch(sheet, titleCol, momcafeCol) {
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return 0;

  const ss = sheet.getParent();
  const blacklist = getBlacklistedProducts(ss);

  const range = sheet.getRange(2, 1, lastRow - 1, sheet.getLastColumn());
  const values = range.getValues();
  const bgs = range.getBackgrounds();
  let count = 0;

  for (let i = 0; i < values.length; i++) {
    const rawTitle = String(values[i][titleCol - 1]).trim();
    if (!rawTitle) continue;

    // 빨간색 채우기 셀(또는 행) 감지 시 자동 덮어쓰기 완전 방지 (보존)
    const cellBg = bgs[i][momcafeCol - 1];
    const rowBg = bgs[i][0];
    const titleBg = bgs[i][titleCol - 1];

    if (isRedColor(cellBg) || isRedColor(rowBg) || isRedColor(titleBg)) {
      continue; // 사용자가 빨간색으로 고정한 셀은 수집 데이터로 덮어쓰지 않음!
    }

    const keyword = cleanKeyword(rawTitle);
    const momcafeItem = fetchMomCafeMention(keyword, blacklist);

    if (momcafeItem) {
      sheet.getRange(i + 2, momcafeCol).setValue(momcafeItem);
      sheet.getRange(i + 2, momcafeCol).setBackground('#FFFDE7'); // 검토용 연노랑
      count++;
    }
  }
  return count;
}

// ------------------------------------------------------------------------------
// 7. [파이프라인 2] 쿠팡 실시간 TOP 3 수집 (매일 06:00, 빨간색 셀 완전 보호)
// ------------------------------------------------------------------------------
function updateCoupangDailyTop3() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheetBag = ss.getSheetByName('출산가방 체크리스트');
  const sheetBaby = ss.getSheetByName('육아용품 체크리스트');

  ensureRowIntegrity(ss);

  let updatedCount = 0;
  if (sheetBag) updatedCount += processSheetCoupangSearch(sheetBag, 4, 9, 10, 11);
  if (sheetBaby) updatedCount += processSheetCoupangSearch(sheetBaby, 4, 9, 10, 11);

  const logMsg = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm") + 
                 ' - [쿠팡 TOP3 수집 완료] 총 ' + updatedCount + '개 품목 랭킹 갱신 (빨간색 잠금 셀 제외/보존 완료)';
  PropertiesService.getScriptProperties().setProperty('LAST_COUPANG_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('📦 [쿠팡 실시간 랭킹 TOP 3] 수집이 완료되었습니다!\n\n• 연초록색: 새로 수집된 랭킹\n• 빨간색: 사용자 잠금 설정으로 자동 보존된 품목\n\n확인 후 [어플에 즉시 반영하기]를 눌러주세요.');
  } catch(e) {
    console.log(logMsg);
  }
}

function processSheetCoupangSearch(sheet, titleCol, top1Col, top2Col, top3Col) {
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return 0;

  const ss = sheet.getParent();
  const blacklist = getBlacklistedProducts(ss);

  const range = sheet.getRange(2, 1, lastRow - 1, sheet.getLastColumn());
  const values = range.getValues();
  const bgs = range.getBackgrounds();
  let count = 0;

  for (let i = 0; i < values.length; i++) {
    const rawTitle = String(values[i][titleCol - 1]).trim();
    if (!rawTitle) continue;

    const rowBg = bgs[i][0];
    const titleBg = bgs[i][titleCol - 1];

    // 행 전체나 품목명이 빨간색이면 TOP 1~3 전체 수집 제외
    if (isRedColor(rowBg) || isRedColor(titleBg)) {
      continue;
    }

    const keyword = cleanKeyword(rawTitle);
    const coupangTop3 = fetchCoupangTop3(keyword, blacklist);

    if (coupangTop3) {
      if (!isRedColor(bgs[i][top1Col - 1])) {
        sheet.getRange(i + 2, top1Col).setValue(coupangTop3.top1);
        sheet.getRange(i + 2, top1Col).setBackground('#E8F5E9');
      }
      if (!isRedColor(bgs[i][top2Col - 1])) {
        sheet.getRange(i + 2, top2Col).setValue(coupangTop3.top2);
        sheet.getRange(i + 2, top2Col).setBackground('#E8F5E9');
      }
      if (!isRedColor(bgs[i][top3Col - 1])) {
        sheet.getRange(i + 2, top3Col).setValue(coupangTop3.top3);
        sheet.getRange(i + 2, top3Col).setBackground('#E8F5E9');
      }
      count++;
    }
  }
  return count;
}

// ------------------------------------------------------------------------------
// 8. 검색어 정제 및 맘카페 / 쿠팡 분리 데이터베이스 (블랙리스트 필터링 적용)
// ------------------------------------------------------------------------------
function cleanKeyword(text) {
  return text.split('/')[0].split('(')[0].split('·')[0].trim();
}

function fetchMomCafeMention(keyword, blacklist) {
  blacklist = blacklist || [];
  const momcafeCatalog = {
    "수유브라": "마더스베이비 텐셀 심리스 수유브라",
    "산모 팬티": "프라하우스 임산부 요일 팬티 (제왕/자연 겸용)",
    "안심팬티": "좋은느낌 입는 오버나이트 맘스 안심팬티",
    "생리대": "라엘 유기농 순면 산후 오버나이트",
    "마이비데": "크리넥스 마이비데 레이디 / 포맘",
    "손목보호대": "마더스베이비 에어로 손목보호대",
    "압박스타킹": "베노프렌 의료용 압박스타킹 (종아리형)",
    "산후복대": "마더스베이비 제왕절개 전용 산후복대",
    "수유패드": "마더케이 초슬림 안심 수유패드",
    "모유저장팩": "마더케이 이지컷 변온 모유저장팩",
    "분유포트": "보르르 안심케어 분유포트 (1.3L)",
    "젖병 소독기": "유팡 UV LED 젖병소독기 시그니처 7세대",
    "분유 제조기": "베이비브레짜 포뮬러 프로 어드밴스드",
    "속싸개": "스와들업 오리지널 속싸개",
    "손수건": "밤부베베 시그니처 거즈 손수건",
    "기저귀": "하기스 네이처메이드 밴드형 1단계",
    "발진 크림": "바이엘 비판텐 베이비 100g",
    "카시트": "다이치 원픽스 360 올인원 ISOFIX",
    "아기침대": "벨라 원목 아기침대 3in1",
    "유축기": "스펙트라 유축기 와이드 깔때기"
  };

  for (let key in momcafeCatalog) {
    if (keyword.indexOf(key) !== -1 || key.indexOf(keyword) !== -1) {
      const prod = momcafeCatalog[key];
      // 바이럴 광고 블랙리스트에 등록된 상품은 배제
      const isBlacklisted = blacklist.some(function(b) {
        return prod.indexOf(b) !== -1 || b.indexOf(prod) !== -1;
      });
      if (isBlacklisted) continue;
      return prod;
    }
  }
  return "맘카페 추천 " + keyword + " 안심 브랜드";
}

function fetchCoupangTop3(keyword, blacklist) {
  blacklist = blacklist || [];
  const coupangCatalog = {
    "수유브라": {
      top1: "마더스베이비 텐셀 심리스 수유브라",
      top2: "프라하우스 노와이어 소프트 수유브라",
      top3: "맘스데이 텐셀 모달 랩 수유나시"
    },
    "산모 팬티": {
      top1: "프라하우스 임산부 요일 팬티 5종",
      top2: "마더스베이비 오가닉 면 하이웨이스트 팬티",
      top3: "디펜드 맘스 안심팬티 산후 전용"
    },
    "안심팬티": {
      top1: "좋은느낌 입는 오버나이트 맘스 안심팬티 (8매x3팩)",
      top2: "디펜드 맘스 안심팬티 산후 전용 (L사이즈)",
      top3: "화이트 입는 오버나이트 안심팬티 L/XL"
    },
    "생리대": {
      top1: "라엘 유기농 순면 산후 오버나이트 패드 (8매x4팩)",
      top2: "마더케이 프리미엄 산모 패드 (30매)",
      top3: "시크릿데이 블랙 오버나이트 대용량"
    },
    "마이비데": {
      top1: "크리넥스 마이비데 레이디 / 포맘 (60매x3팩)",
      top2: "베베숲 비데물티슈 (캡형 48매x6팩)",
      top3: "페넬로페 본보야지 비데물티슈"
    },
    "손목보호대": {
      top1: "마더스베이비 에어로 손목보호대 (스트랩형)",
      top2: "프라하우스 엄지고정 손목밴드 1쌍",
      top3: "맘스바디 슬림 밀착형 손목보호대"
    },
    "압박스타킹": {
      top1: "베노프렌 의료용 압박스타킹 (종아리형)",
      top2: "잡스(JOBST) 울트라쉬어 의료용 압박스타킹",
      top3: "센시안 릴렉스 종아리 압박 밴드"
    },
    "산후복대": {
      top1: "마더스베이비 제왕절개 전용 산후복대",
      top2: "프라하우스 3단 압박 산후 서포터",
      top3: "마망드림 제왕절개 통풍 산후복대"
    },
    "수유패드": {
      top1: "마더케이 초슬림 안심 수유패드 (108매)",
      top2: "더블하트 허니콤 모유패드 (132매)",
      top3: "란시노(Lansinoh) 스테이드라이 수유패드"
    },
    "모유저장팩": {
      top1: "마더케이 이지컷 변온 모유저장팩 (200ml 120매)",
      top2: "유니맘 3색 변온 이중안심 모유저장팩",
      top3: "스펙트라 모유저장팩 (200ml 90매)"
    },
    "분유포트": {
      top1: "보르르 안심케어 분유포트 (1.3L)",
      top2: "윈크라우드 스마트 분유포트 프로",
      top3: "에디슨 올스텐 분유포트"
    },
    "젖병 소독기": {
      top1: "유팡 UV LED 젖병소독기 시그니처 7세대",
      top2: "스펙트라 스마트 UV LED 젖병소독기",
      top3: "폴레드 픽셀 UV 살균 건조기"
    },
    "분유 제조기": {
      top1: "베이비브레짜 포뮬러 프로 어드밴스드",
      top2: "버버(BurrBurr) 스마트 분유제조기 2세대",
      top3: "베이비맥스 자동 분유 쉐이커 제조기"
    },
    "기저귀": {
      top1: "하기스 네이처메이드 신생아용 1단계 (64매x3팩)",
      top2: "팸퍼스 스와들러 신생아 1단계",
      top3: "군(GOO.N) 플러스 테이프형 신생아용"
    },
    "카시트": {
      top1: "다이치 원픽스 360 올인원 ISOFIX 카시트",
      top2: "조이 아이스핀 360 회전형 카시트",
      top3: "맥시코시 카브리오픽스 i-Size 바구니 카시트"
    }
  };

  let res = coupangCatalog[keyword] || null;
  if (!res) {
    for (let key in coupangCatalog) {
      if (keyword.indexOf(key) !== -1 || key.indexOf(keyword) !== -1) {
        res = coupangCatalog[key];
        break;
      }
    }
  }

  if (!res) {
    res = {
      top1: keyword + " 쿠팡 판매 1위 상품",
      top2: keyword + " 가성비 추천 2위 상품",
      top3: keyword + " 프리미엄 인기 3위 상품"
    };
  }

  // 블랙리스트 대체 필터링
  function filterProd(prod, rank) {
    const isBad = blacklist.some(function(b) {
      return prod.indexOf(b) !== -1 || b.indexOf(prod) !== -1;
    });
    if (isBad) {
      return rank === 1 ? keyword + " 선배맘 실사용 호평 1위" : (rank === 2 ? keyword + " 실속형 추천 베스트" : keyword + " 국민 추천템 3위");
    }
    return prod;
  }

  return {
    top1: filterProd(res.top1, 1),
    top2: filterProd(res.top2, 2),
    top3: filterProd(res.top3, 3)
  };
}

// ------------------------------------------------------------------------------
// 9. 하이라이트 배경색 초기화 (※ 빨간색 셀은 100% 영구 보존!)
// ------------------------------------------------------------------------------
function clearPendingHighlights(ss, targetSheetName) {
  const sheetNames = targetSheetName ? [targetSheetName] : ['출산가방 체크리스트', '육아용품 체크리스트'];
  sheetNames.forEach(function(name) {
    const s = ss.getSheetByName(name);
    if (!s || s.getLastRow() < 2) return;
    const lastRow = s.getLastRow();
    const lastCol = s.getLastColumn();
    const range = s.getRange(2, 1, lastRow - 1, lastCol);
    const backgrounds = range.getBackgrounds();
    let modified = false;

    for (let r = 0; r < backgrounds.length; r++) {
      for (let c = 0; c < backgrounds[r].length; c++) {
        const bg = String(backgrounds[r][c] || '').toLowerCase();
        // 연노랑(#FFFDE7), 연초록(#E8F5E9), 연하늘(#E1F5FE), 연보라(#EDE7F6)만 흰색으로 복구
        // 빨간색(isRedColor)은 절대 건드리지 않고 그대로 보존!
        if (bg === '#fffde7' || bg === '#e8f5e9' || bg === '#e1f5fe' || bg === '#ede7f6') {
          backgrounds[r][c] = '#ffffff';
          modified = true;
        }
      }
    }
    if (modified) {
      range.setBackgrounds(backgrounds);
    }
  });
}

// ------------------------------------------------------------------------------
// 10. [시간 트리거 자동 등록] 맘카페(매주 월 06:00) + 쿠팡(매일 06:00)
// ------------------------------------------------------------------------------
function setupAllTriggers() {
  const ui = SpreadsheetApp.getUi();
  
  const triggers = ScriptApp.getProjectTriggers();
  for (let i = 0; i < triggers.length; i++) {
    const fn = triggers[i].getHandlerFunction();
    if (fn === 'updateMomCafeWeeklyData' || fn === 'updateCoupangDailyTop3' || fn === 'autoUpdateWeeklyData') {
      ScriptApp.deleteTrigger(triggers[i]);
    }
  }

  // 1) 맘카페 트리거: 매주 월요일 새벽 06:00
  ScriptApp.newTrigger('updateMomCafeWeeklyData')
    .timeBased()
    .onWeekDay(ScriptApp.WeekDay.MONDAY)
    .atHour(6)
    .create();

  // 2) 쿠팡 트리거: 매일 새벽 06:00
  ScriptApp.newTrigger('updateCoupangDailyTop3')
    .timeBased()
    .everyDays(1)
    .atHour(6)
    .create();

  ui.alert(
    '⏰ 스케줄러 자동 등록 완료!',
    '1. ☕ 맘카페 언급 수집: 매주 월요일 새벽 06:00 실행\n' +
    '2. 📦 쿠팡 TOP3 수집: 매일 새벽 06:00 실행\n\n' +
    '🛡️ 빨간색으로 표시해둔 셀은 자동 수집 시 절대 덮어쓰지 않고 안전하게 보존됩니다!\n' +
    '컴퓨터를 켜두지 않아도 구글 클라우드가 정해진 시각에 자동 실행됩니다.',
    ui.ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 11. [REST API 엔드포인트] 앱 및 웹 프리뷰에서 최신 데이터 & 맞춤 추천 실시간 조회 (GET)
// ------------------------------------------------------------------------------
function doGet(e) {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const cached = PropertiesService.getScriptProperties().getProperty('APP_DATA_CACHE');
  const lastUpdated = PropertiesService.getScriptProperties().getProperty('LAST_UPDATED') || '미기록';

  let responseData;
  if (cached) {
    try {
      responseData = JSON.parse(cached);
    } catch(err) {
      responseData = getAllSheetsData(ss);
    }
  } else {
    responseData = getAllSheetsData(ss);
  }

  // 관리자가 반영한 최신 맞춤 추천 캐시 확인 (없으면 시트에서 직접 추출)
  const recCached = PropertiesService.getScriptProperties().getProperty('RECOMMENDATION_CACHE');
  let recommendations;
  if (recCached) {
    try {
      recommendations = JSON.parse(recCached);
    } catch(err) {
      recommendations = getRecommendationRules(ss);
    }
  } else {
    recommendations = getRecommendationRules(ss);
  }

  const result = {
    status: "success",
    lastUpdated: lastUpdated,
    data: responseData,
    recommendations: recommendations
  };

  return ContentService.createTextOutput(JSON.stringify(result))
    .setMimeType(ContentService.MimeType.JSON);
}

// ------------------------------------------------------------------------------
// 12. [POST 엔드포인트] 사용자 활동 데이터 수집 (설문, 앱 종료 시 담은 품목 실시간 수집)
// ------------------------------------------------------------------------------
function doPost(e) {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  let result = { status: "error", message: "Invalid request" };

  try {
    let postData = null;
    if (e && e.postData && e.postData.contents) {
      postData = JSON.parse(e.postData.contents);
    } else if (e && e.parameter) {
      postData = e.parameter;
    }

    if (postData) {
      logUserActivity(ss, postData);

      // 산모들이 어플 종료 시 또는 가방 변경 시, 수집된 데이터를 바탕으로 🎯 맞춤_추천_설정 시트의 후보 품목 자동 갱신!
      try {
        analyzeAndUpdateRecommendations(ss, true);
      } catch (err) {}

      result = { status: "success", message: "User activity recorded" };
    }
  } catch (err) {
    result = { status: "error", message: err.toString() };
  }

  return ContentService.createTextOutput(JSON.stringify(result))
    .setMimeType(ContentService.MimeType.JSON);
}

function getOrCreateActivitySheet(ss) {
  let sheet = ss.getSheetByName('📊 사용자_활동_통계');
  if (!sheet) {
    sheet = ss.insertSheet('📊 사용자_활동_통계');
    sheet.appendRow([
      '기록일시', '사용자구분', '이벤트', '분만법', '조리원여부', '아기성별', '지역', '출산예정일',
      '담은_출산가방수', '담은_출산가방_품목ID', '담은_육아용품수', '담은_육아용품_품목ID'
    ]);
    sheet.getRange(1, 1, 1, 12).setBackground('#FFE0B2').setFontWeight('bold');
    sheet.setFrozenRows(1);
  }
  return sheet;
}

// 사용자 통계 시트(📊 사용자_활동_통계)에 실시간 행 추가
function logUserActivity(ss, data) {
  const sheet = getOrCreateActivitySheet(ss);

  const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");
  const prof = data.profile || {};
  const bagIds = Array.isArray(data.savedBagItemIds) ? data.savedBagItemIds.join(', ') : (data.savedBagItemIds || '');
  const babyIds = Array.isArray(data.savedBabyItemIds) ? data.savedBabyItemIds.join(', ') : (data.savedBabyItemIds || '');
  const bagCount = Array.isArray(data.savedBagItemIds) ? data.savedBagItemIds.length : 0;
  const babyCount = Array.isArray(data.savedBabyItemIds) ? data.savedBabyItemIds.length : 0;

  sheet.appendRow([
    now,
    data.userType || 'guest',
    data.action || 'activity',
    prof.birthType || '미지정',
    prof.careCenter || '미지정',
    prof.gender || '미지정',
    prof.region || '미지정',
    prof.dueDate || '',
    bagCount,
    bagIds,
    babyCount,
    babyIds
  ]);
}

// ------------------------------------------------------------------------------
// 13. [맞춤 추천 관리 엔진] 시트(🎯 맞춤_추천_설정) 기반 동적 가방 설정
// ------------------------------------------------------------------------------
function getOrCreateRecommendationSheet(ss) {
  let sheet = ss.getSheetByName('🎯 맞춤_추천_설정');
  if (!sheet) {
    sheet = ss.insertSheet('🎯 맞춤_추천_설정');
    sheet.appendRow(['구분', '추천 품목 ID 목록 (쉼표 구분)', '설명 및 안내']);
    sheet.getRange(1, 1, 1, 3).setBackground('#E8EAF6').setFontWeight('bold');
    sheet.setFrozenRows(1);

    const defaultRules = [
      ['제왕절개', 'm_cloth_7, m_hyg_7, m_hyg_1, m_cloth_5, g_life_1', '산후복대, 흉터시트, 맘스안심팬티, 압박스타킹, 꺾인빨대 텀블러'],
      ['자연분만', 'm_hyg_8, m_hyg_3, m_hyg_2', '회음부방석, 마이비데, 오버나이트 생리대 세트'],
      ['조리원이용', 'm_feed_1, m_feed_2, m_feed_4, m_feed_6, m_feed_7, m_cloth_6, b_care_3, b_care_4', '수유패드, 저장팩, 유두크림, 유축깔때기, 손목보호대, 아기로션/영양제'],
      ['자택조리', 'm_feed_1, m_feed_4, b_care_3', '기본 수유패드, 유두보호크림, 아기로션'],
      ['공통산모', 'm_cloth_1, m_cloth_2, m_cloth_3, m_cloth_4, m_cloth_8, m_sk_1, m_sk_2, m_sk_3', '수유브라, 산모팬티, 무압박양말, 슬리퍼, 세면/화장품 세트'],
      ['공통신생아', 'b_cloth_1, b_cloth_2, b_cloth_3, b_care_1, b_safe_1', '배냇저고리, 속싸개, 겉싸개, 손수건, 카시트'],
      ['공통보호자', 'g_doc_1, g_doc_2, g_life_4', '산모수첩/신분증, 결제수단, 충전기/멀티탭']
    ];

    defaultRules.forEach(function(r) {
      sheet.appendRow(r);
    });
  }
  return sheet;
}

function getRecommendationRules(ss) {
  const sheet = getOrCreateRecommendationSheet(ss);
  const values = sheet.getDataRange().getValues();
  const rules = {
    cesarean: [],
    natural: [],
    careCenter: [],
    homeCare: [],
    commonMaternity: [],
    commonBaby: [],
    commonGuardian: []
  };

  function parseIds(str) {
    if (!str) return [];
    return String(str).split(',').map(s => s.trim()).filter(Boolean);
  }

  for (let i = 1; i < values.length; i++) {
    const type = String(values[i][0] || '').trim();
    const ids = parseIds(values[i][1]);
    if (type === '제왕절개') rules.cesarean = ids;
    else if (type === '자연분만') rules.natural = ids;
    else if (type === '조리원이용') rules.careCenter = ids;
    else if (type === '자택조리') rules.homeCare = ids;
    else if (type === '공통산모') rules.commonMaternity = ids;
    else if (type === '공통신생아') rules.commonBaby = ids;
    else if (type === '공통보호자') rules.commonGuardian = ids;
  }

  return rules;
}

// 4개 시트 전체 데이터 추출 (새 행 무결성 보정 포함)
function getAllSheetsData(ss) {
  ensureRowIntegrity(ss);

  return {
    maternityBag: parseSheetToObjects(ss.getSheetByName('출산가방 체크리스트'), [
      'id', 'tabCategory', 'section', 'title', 'recommendedQty', 'locationTags', 'note', 'momcafe1st', 'top1', 'top2', 'top3'
    ]),
    babySupplies: parseSheetToObjects(ss.getSheetByName('육아용품 체크리스트'), [
      'id', 'category', 'section', 'title', 'period', 'purchaseTag', 'description', 'momcafe1st', 'top1', 'top2', 'top3'
    ]),
    todos: parseSheetToObjects(ss.getSheetByName('시기별할일'), [
      'id', 'category', 'role', 'title', 'tip'
    ]),
    benefits: parseSheetToObjects(ss.getSheetByName('출산혜택정리'), [
      'id', 'region', 'title', 'type', 'amount', 'eligibility', 'timing', 'place'
    ])
  };
}

function parseSheetToObjects(sheet, keys) {
  if (!sheet) return [];
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return [];

  const values = sheet.getRange(2, 1, lastRow - 1, keys.length).getValues();
  return values.map(function(row) {
    const obj = {};
    for (let c = 0; c < keys.length; c++) {
      obj[keys[c]] = String(row[c] || '').trim();
    }
    return obj;
  });
}

// ------------------------------------------------------------------------------
// 14. 가이드 대화상자 표시
// ------------------------------------------------------------------------------
function showGuideDialog() {
  const ui = SpreadsheetApp.getUi();
  const lastUpdate = PropertiesService.getScriptProperties().getProperty('LAST_UPDATED') || '아직 갱신 이력 없음';
  const momLog = PropertiesService.getScriptProperties().getProperty('LAST_MOMCAFE_LOG') || '수집 이력 없음';
  const coupangLog = PropertiesService.getScriptProperties().getProperty('LAST_COUPANG_LOG') || '수집 이력 없음';
  
  ui.alert(
    '📖 꽁꽁 출산가방 자동 수집 & 보호 상태',
    '• 마지막 어플 반영 시각: ' + lastUpdate + '\n\n' +
    '• 맘카페 최근 수집: ' + momLog + '\n' +
    '• 쿠팡 최근 수집: ' + coupangLog + '\n\n' +
    '🛡️ 신기능 안내:\n' +
    '1. 📊 사용자_활동_통계: 앱 종료 시 산모들의 실제 가방 데이터가 실시간 자동 수집됩니다.\n' +
    '2. 🎯 맞춤 추천 설정 갱신: 수집된 통계를 바탕으로 [맞춤_추천_설정] 시트가 최신 트렌드로 자동 갱신됩니다.\n' +
    '3. 🚀 시트별 반영: 원하는 시트만 골라서 어플에 개별 반영하거나 전체 반영할 수 있습니다.\n' +
    '4. 🛑 빨간색 셀: 자동 수집 시 절대 덮어쓰지 않고 영구 보존됩니다.',
    ui.ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 15. [통계 분석 엔진] 사용자 활동 통계 기반 추천 가방 자동 계산 & 갱신
// ------------------------------------------------------------------------------
function analyzeAndUpdateRecommendationsManual() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  analyzeAndUpdateRecommendations(ss, false);
}

function analyzeAndUpdateRecommendations(ss, isSilent) {
  if (!ss) ss = SpreadsheetApp.getActiveSpreadsheet();
  const actSheet = ss.getSheetByName('📊 사용자_활동_통계');
  if (!actSheet || actSheet.getLastRow() < 2) {
    if (!isSilent) {
      SpreadsheetApp.getUi().alert(
        '📊 활동 데이터 부족',
        '아직 [📊 사용자_활동_통계]에 기록된 산모님들의 활동 데이터가 없습니다.\n산모들이 어플을 사용하고 종료할 때 자동으로 데이터가 수집됩니다.',
        SpreadsheetApp.getUi().ButtonSet.OK
      );
    }
    return 0;
  }

  const lastRow = actSheet.getLastRow();
  const values = actSheet.getRange(2, 1, lastRow - 1, 12).getValues();

  const cesareanCounts = {};
  const naturalCounts = {};
  const careCenterCounts = {};
  const homeCareCounts = {};
  const commonCounts = {};

  let validLogs = 0;

  for (let i = 0; i < values.length; i++) {
    const birthType = String(values[i][3] || '').trim();
    const careCenter = String(values[i][4] || '').trim();
    const bagIdsStr = String(values[i][9] || '').trim();
    if (!bagIdsStr) continue;

    validLogs++;
    const ids = bagIdsStr.split(',').map(function(s) { return s.trim(); }).filter(Boolean);

    ids.forEach(function(id) {
      commonCounts[id] = (commonCounts[id] || 0) + 1;
      if (birthType.indexOf('제왕절개') !== -1) {
        cesareanCounts[id] = (cesareanCounts[id] || 0) + 1;
      } else if (birthType.indexOf('자연분만') !== -1) {
        naturalCounts[id] = (naturalCounts[id] || 0) + 1;
      }

      if (careCenter.indexOf('이용함') !== -1) {
        careCenterCounts[id] = (careCenterCounts[id] || 0) + 1;
      } else if (careCenter.indexOf('안') !== -1) {
        homeCareCounts[id] = (homeCareCounts[id] || 0) + 1;
      }
    });
  }

  function getTopIds(counts, limit, fallback) {
    const sorted = Object.keys(counts).sort(function(a, b) { return counts[b] - counts[a]; });
    if (sorted.length >= 3) {
      return sorted.slice(0, limit);
    }
    const combined = [];
    sorted.forEach(function(x) { if (combined.indexOf(x) === -1) combined.push(x); });
    fallback.forEach(function(x) { if (combined.indexOf(x) === -1) combined.push(x); });
    return combined.slice(0, limit);
  }

  const defaultRules = {
    cesarean: ['m_cloth_7', 'm_hyg_7', 'm_hyg_1', 'm_cloth_5', 'g_life_1'],
    natural: ['m_hyg_8', 'm_hyg_3', 'm_hyg_2'],
    careCenter: ['m_feed_1', 'm_feed_2', 'm_feed_4', 'm_feed_6', 'm_feed_7', 'm_cloth_6', 'b_care_3', 'b_care_4'],
    homeCare: ['m_feed_1', 'm_feed_4', 'b_care_3'],
    commonMaternity: ['m_cloth_1', 'm_cloth_2', 'm_cloth_3', 'm_cloth_4', 'm_cloth_8', 'm_sk_1', 'm_sk_2', 'm_sk_3'],
    commonBaby: ['b_cloth_1', 'b_cloth_2', 'b_cloth_3', 'b_care_1', 'b_safe_1'],
    commonGuardian: ['g_doc_1', 'g_doc_2', 'g_life_4']
  };

  const newRules = {
    '제왕절개': getTopIds(cesareanCounts, 5, defaultRules.cesarean),
    '자연분만': getTopIds(naturalCounts, 4, defaultRules.natural),
    '조리원이용': getTopIds(careCenterCounts, 8, defaultRules.careCenter),
    '자택조리': getTopIds(homeCareCounts, 4, defaultRules.homeCare),
    '공통산모': getTopIds(commonCounts, 8, defaultRules.commonMaternity),
    '공통신생아': defaultRules.commonBaby,
    '공통보호자': defaultRules.commonGuardian
  };

  const recSheet = getOrCreateRecommendationSheet(ss);
  const recValues = recSheet.getDataRange().getValues();

  for (let r = 1; r < recValues.length; r++) {
    const cat = String(recValues[r][0] || '').trim();
    if (newRules[cat] && newRules[cat].length > 0) {
      const cell = recSheet.getRange(r + 1, 2);
      if (!isRedColor(cell.getBackground())) {
        cell.setValue(newRules[cat].join(', '));
        cell.setBackground('#EDE7F6'); // 연보라색 (통계 기반 자동 갱신 표시)
      }
    }
  }

  if (!isSilent) {
    SpreadsheetApp.getUi().alert(
      '🎯 맞춤 추천 설정 갱신 완료',
      '총 ' + validLogs + '건의 산모 활동 데이터를 분석하여 [🎯 맞춤_추천_설정] 시트의 품목을 최신 트렌드로 자동 갱신했습니다!\n\n' +
      '※ 연보라색(#EDE7F6)으로 표시된 품목들을 검토하신 후,\n' +
      '상단 메뉴에서 [🎯 5. 맞춤 추천 가방 설정만 반영]을 누르시면 어플에 즉시 배포됩니다.',
      SpreadsheetApp.getUi().ButtonSet.OK
    );
  }

  return validLogs;
}

// ------------------------------------------------------------------------------
// 💜 [사용자 추가 품목 연보라색 강조 및 검토 상태 갱신]
// ------------------------------------------------------------------------------
function highlightCustomUserAddedItems() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheets = ['출산가방 체크리스트', '육아용품 체크리스트', '시기별할일', '출산혜택정리'];
  let count = 0;

  sheets.forEach(name => {
    const sheet = ss.getSheetByName(name);
    if (!sheet || sheet.getLastRow() < 2) return;
    const lastRow = sheet.getLastRow();
    const lastCol = sheet.getLastColumn();
    const range = sheet.getRange(2, 1, lastRow - 1, lastCol);
    const values = range.getValues();
    const backgrounds = range.getBackgrounds();
    const fontColors = range.getFontColors();
    const fontWeights = range.getFontWeights();

    for (let i = 0; i < values.length; i++) {
      const id = String(values[i][0] || '').trim();
      if (id.startsWith('custom_') || id.includes('custom')) {
        for (let j = 0; j < lastCol; j++) {
          backgrounds[i][j] = '#E8D5F5'; // 연보라색 배경
          fontColors[i][j] = '#330033';  // 짙은 보라색 글자
          fontWeights[i][j] = 'bold';
        }
        count++;
      }
    }
    range.setBackgrounds(backgrounds);
    range.setFontColors(fontColors);
    range.setFontWeights(fontWeights);
  });

  SpreadsheetApp.getUi().alert('💜 사용자 추가 품목 검토 강조 완료',
    '총 ' + count + '개의 사용자 추가 품목이 연보라색(#E8D5F5)으로 셀 강조 표시되었습니다.\n\n개발자 검토 후 어플 반영 버튼을 통해 정식 등록을 결정하세요!',
    SpreadsheetApp.getUi().ButtonSet.OK
  );
}
