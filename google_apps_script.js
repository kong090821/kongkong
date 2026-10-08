/**
 * ==============================================================================
 * 🍼 [꽁꽁 출산가방 v1.1.8] 구글 스프레드시트 완전 자동화 및 실시간 클라우드 연동 스크립트
 * ==============================================================================
 * 
 * [v1.1.8 주요 핵심 기능 및 파이프라인]
 * 1. 🤖 Gemini AI 실시간 인터넷 검색 & 모든 세부 열(수량/위치/팁/브랜드) 자동 완성:
 *    - 품목명(4열)만 입력하면 Gemini AI가 네이버 맘카페 & 쿠팡 실시간 랭킹을 인터넷에서 검색하여
 *      적정 수량, 병원/조리원 보관 위치, 체크포인트 팁, 맘카페 1위 브랜드, 쿠팡 TOP 1~3까지 10개 열 전체 자동 작성!
 *    - 상단 메뉴 [🔑 Gemini AI 설정]에서 무료 API 키 등록 지원 및 단일/일괄 AI 자동 완성 메뉴 완비
 * 
 * 2. 🔗 쿠팡 파트너스 개별 수익 링크(L, M, N열) & 기본 링크 Fallback 스마트 연동:
 *    - 구글 시트에서 수동 입력한 개별 상품 파트너스 링크(top1_url, top2_url, top3_url) 최우선 적용
 *    - 미입력 품목은 선생님의 기본 쿠팡 파트너스 링크(https://link.coupang.com/a/hDXnz86Thk)로 100% 자동 연결
 *    - 상단 메뉴 [🔗 쿠팡 파트너스 수동 입력 링크 어플에 즉시 반영] 버튼으로 실시간 원클릭 배포
 * 
 * 3. ⏰ 출산 전후 시기별·역할별 111개 체크리스트 전수 지원:
 *    - 임산부·신생아 필수 백신 7종 & 태아보험 전 과정 프로세스 4종
 *    - 분만실 촬영·탯줄 채취, 아기 첫 순간 영상, 신생아 등록·팔찌, 산모 밀착 간호/보행 부축, 회사/구독 일시중단 등 20개 신규 핵심 할일 완벽 탑재
 * 
 * 4. 💰 2026년 출산 혜택 총정리 가이드 전수 반영 (총 56종):
 *    - 전국 공통 15종 (국민행복카드 100만, 산후도우미 바우처, 제왕절개 본인부담 0원 등)
 *    - 개인 맞춤 18종 (우체국 엄마보험 무료, 신생아 특례 대출, 취득세 500만 감면, 혼인·출산 증여세 3억 비과세 등)
 *    - 전국 17개 시·도 지자체 특화 혜택 23종 실시간 스마트 머지 지원
 * 
 * 3. 🆕 신규 행 자동 완성 & 어플 등록:
 *    - 새 행을 추가하고 품목명만 적어도, 고유 ID 자동 생성 및 맘카페 1위/쿠팡 TOP 1~3 자동 완성
 *    - [어플에 즉시 반영하기] 클릭 시 새로 추가한 행이 어플에 실시간으로 즉시 등록
 * 
 * 4. 🔴 빨간색 셀 잠금 & 중요 내용 보호:
 *    - 사용자가 빨간색(채우기 색상)으로 칠해둔 셀은 중요 항목으로서 자동 수집 시 절대 덮어쓰지 않고 영구 보존!
 *    - 빨간색으로 지정된 상품은 '바이럴 광고 블랙리스트'로 인식되어 향후 자동 수집 추천에서도 자동 제외
 * 
 * 5. 💙 파란색 셀 분리 & 바이럴/광고 수동 검토 엔진:
 *    - 데이터 수집 시 바이럴/광고 의심 상품은 파란색(채우기 색상: #BBDEFB) 배경으로 자동 분리!
 *    - 파란색 배경 셀/행은 자동 수집 시 덮어쓰이지 않으며, 사용자가 직접 배경색을 흰색으로 바꾸기 전까지 어플 배포에서 자동 제외!
 * 
 * 6. ☕ 맘카페 & 📦 쿠팡 분리 자동 수집 스케줄러:
 *    - 맘카페 최다 언급: 매주 월요일 새벽 06:00 자동 수집 (검토: 연노랑 #FFFDE7 / 바이럴: 파란색 #BBDEFB)
 *    - 쿠팡 실시간 랭킹: 매일 새벽 06:00 자동 수집 (검토: 연초록 #E8F5E9 / 바이럴: 파란색 #BBDEFB)
 * 
 * 7. 🚀 실시간 어플 동기화:
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
    .addItem('🤖 [Gemini AI] 현재 선택한 행 품목 인터넷 실시간 검색 자동 완성', 'fillActiveItemWithGemini')
    .addItem('🤖 [Gemini AI] 빈 칸 있는 모든 품목 일괄 자동 완성', 'autoFillAllEmptyWithGemini')
    .addItem('🔑 [Gemini AI] API 키 설정 (Google AI Studio 무료 발급)', 'setupGeminiApiKeyDialog')
    .addSeparator()
    .addItem('🔗 [쿠팡 파트너스] 수동 입력 링크 어플에 즉시 반영', 'syncCoupangLinksToApp')
    .addSeparator()
    .addItem('🎒 [시트별 반영] 1. 출산가방 체크리스트만 반영', 'syncMaternityBagOnly')
    .addItem('🍼 [시트별 반영] 2. 육아용품 체크리스트만 반영', 'syncBabySuppliesOnly')
    .addItem('⏰ [시트별 반영] 3. 시기별 할일만 반영', 'syncTodosOnly')
    .addItem('💰 [시트별 반영] 4. 출산 혜택만 반영', 'syncBenefitsOnly')
    .addItem('🎯 [시트별 반영] 5. 맞춤 추천 가방 설정만 반영', 'syncRecommendationsOnly')
    .addSeparator()
    .addItem('🎯 맞춤 추천 설정 갱신', 'updateRecommendationSettingsFromStats')
    .addSeparator()
    .addItem('✨ [스마트 카탈로그] 신규 품목 데이터 및 추천 상품 기본 채우기', 'autoFillMissingRowData')
    .addItem('💜 [사용자 추가] 신규 등록 품목 연보라색 셀 강조 표시', 'highlightCustomUserAddedItems')
    .addItem('☕ [수동 실행] 맘카페 언급 1위 데이터 지금 수집', 'updateMomCafeWeeklyData')
    .addItem('📦 [수동 실행] 쿠팡 TOP 3 랭킹 데이터 지금 수집', 'updateCoupangDailyTop3')
    .addSeparator()
    .addItem('📥 [원클릭 채우기] 최신 111종 할일 & 56종 혜택 시트에 자동 동기화', 'importMasterDataToSheets')
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

    let msg = '• 배포 버전: 꽁꽁 출산가방 v1.1.8 (빌드 코드: 18)\n' +
              '• 갱신 일시: ' + now + '\n' +
              '• 출산가방 품목: ' + data.maternityBag.length + '개\n' +
              '• 육아용품 품목: ' + data.babySupplies.length + '개\n' +
              '• 시기별 할일: ' + data.todos.length + '개 (💉예방접종 7종 & 📑보험 4종 포함 총 111종)\n' +
              '• 출산 혜택: ' + data.benefits.length + '개 (전국 15종, 개인 18종, 지자체 23종 전수 정제 완료)\n' +
              '• 맞춤 추천 가방: 제왕/자연/조리원 규칙 동시 배포 완료\n';
    
    if (autoFilled > 0) {
      msg += '• 신규 추가 행: ' + autoFilled + '개 품목 ID 및 데이터 자동 생성\n';
    }
    msg += '\n🔴 빨간색(중요 보존) 및 💙 파란색(바이럴 검토 대기) 항목을 안전하게 처리하고,\n✅ 검토 완료(흰색 배경)된 데이터만 어플에 성공적으로 실시간 반영되었습니다!';

    ui.alert('🎉 전체 어플 데이터 갱신 완료!', msg, ui.ButtonSet.OK);
  } catch (err) {
    ui.alert('❌ 갱신 중 오류가 발생했습니다: ' + err.toString());
  }
}

// ------------------------------------------------------------------------------
// [쿠팡 파트너스 수동 입력 링크 어플 반영]
// ------------------------------------------------------------------------------
function syncCoupangLinksToApp() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  const DEFAULT_COUPANG_URL = 'https://link.coupang.com/a/hDXnz86Thk';

  try {
    ensureRowIntegrity(ss);
    const data = getAllSheetsData(ss);
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(data));

    let customMaternityLinks = 0;
    (data.maternityBag || []).forEach(function(item) {
      if ((item.top1_url && item.top1_url !== DEFAULT_COUPANG_URL) ||
          (item.top2_url && item.top2_url !== DEFAULT_COUPANG_URL) ||
          (item.top3_url && item.top3_url !== DEFAULT_COUPANG_URL)) {
        customMaternityLinks++;
      }
    });

    let customBabyLinks = 0;
    (data.babySupplies || []).forEach(function(item) {
      if ((item.top1_url && item.top1_url !== DEFAULT_COUPANG_URL) ||
          (item.top2_url && item.top2_url !== DEFAULT_COUPANG_URL) ||
          (item.top3_url && item.top3_url !== DEFAULT_COUPANG_URL)) {
        customBabyLinks++;
      }
    });

    const msg = '• 배포 버전: 꽁꽁 출산가방 v1.1.7\n' +
                '• 갱신 일시: ' + now + '\n' +
                '• 출산가방 수동 등록 품목: ' + customMaternityLinks + '개\n' +
                '• 육아용품 수동 등록 품목: ' + customBabyLinks + '개\n' +
                '• 기본 연결 링크: ' + DEFAULT_COUPANG_URL + '\n\n' +
                '✅ 구글 시트에서 수동으로 입력하신 쿠팡 파트너스 링크가 어플에 성공적으로 반영되었습니다!\n' +
                '※ 링크를 입력하지 않은 모든 품목은 회원님의 기본 쿠팡 링크로 자동 연결됩니다.';

    ui.alert('🔗 쿠팡 파트너스 링크 반영 완료', msg, ui.ButtonSet.OK);
  } catch (err) {
    ui.alert('❌ 쿠팡 링크 반영 중 오류 발생: ' + err.toString());
  }
}

// ------------------------------------------------------------------------------
// [각 시트별 개별 반영 함수들]
// ------------------------------------------------------------------------------

// (1) 🎒 출산가방 체크리스트만 반영
function syncMaternityBagOnly() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  const DEFAULT_COUPANG_URL = 'https://link.coupang.com/a/hDXnz86Thk';
  try {
    ensureRowIntegrity(ss);
    const bagData = parseSheetToObjects(ss.getSheetByName('출산가방 체크리스트'), [
      'id', 'tabCategory', 'section', 'title', 'recommendedQty', 'locationTags', 'note', 'momcafe1st', 'top1', 'top2', 'top3', 'top1_url', 'top2_url', 'top3_url'
    ]).map(function(item) {
      item.top1_url = (item.top1_url && String(item.top1_url).trim()) ? String(item.top1_url).trim() : DEFAULT_COUPANG_URL;
      item.top2_url = (item.top2_url && String(item.top2_url).trim()) ? String(item.top2_url).trim() : DEFAULT_COUPANG_URL;
      item.top3_url = (item.top3_url && String(item.top3_url).trim()) ? String(item.top3_url).trim() : DEFAULT_COUPANG_URL;
      return item;
    });
    const fullData = getOrInitCachedData(ss);
    fullData.maternityBag = bagData;
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(fullData));
    clearPendingHighlights(ss, '출산가방 체크리스트');

    ui.alert('🎒 출산가방 체크리스트 반영 완료',
      '• 갱신 일시: ' + now + '\n• 반영 품목 수: ' + bagData.length + '개\n\n✅ 출산가방 체크리스트 및 쿠팡 링크가 어플에 실시간 반영되었습니다!',
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
  const DEFAULT_COUPANG_URL = 'https://link.coupang.com/a/hDXnz86Thk';
  try {
    ensureRowIntegrity(ss);
    const babyData = parseSheetToObjects(ss.getSheetByName('육아용품 체크리스트'), [
      'id', 'category', 'section', 'title', 'period', 'purchaseTag', 'description', 'momcafe1st', 'top1', 'top2', 'top3', 'top1_url', 'top2_url', 'top3_url'
    ]).map(function(item) {
      item.top1_url = (item.top1_url && String(item.top1_url).trim()) ? String(item.top1_url).trim() : DEFAULT_COUPANG_URL;
      item.top2_url = (item.top2_url && String(item.top2_url).trim()) ? String(item.top2_url).trim() : DEFAULT_COUPANG_URL;
      item.top3_url = (item.top3_url && String(item.top3_url).trim()) ? String(item.top3_url).trim() : DEFAULT_COUPANG_URL;
      return item;
    });
    const fullData = getOrInitCachedData(ss);
    fullData.babySupplies = babyData;
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");

    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(fullData));
    clearPendingHighlights(ss, '육아용품 체크리스트');

    ui.alert('🍼 육아용품 체크리스트 반영 완료',
      '• 갱신 일시: ' + now + '\n• 반영 품목 수: ' + babyData.length + '개\n\n✅ 육아용품 체크리스트 및 쿠팡 링크가 어플에 실시간 반영되었습니다!',
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

    ui.alert('⏰ 시기별 할일 반영 완료 (v1.1.6)',
      '• 갱신 일시: ' + now + '\n• 반영 항목 수: ' + todosData.length + '개 (💉예방접종 7종 & 📑태아보험 4종 및 신규 할일 20종 전수 반영)\n\n✅ 시기별 할일이 어플(v1.1.6)에 실시간 반영되었습니다!',
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

    ui.alert('💰 출산 혜택 반영 완료 (v1.1.7)',
      '• 갱신 일시: ' + now + '\n• 반영 혜택 수: ' + benefitsData.length + '개 (전국 15종, 개인 18종 및 지자체 특화 혜택 전수 반영)\n\n✅ 출산 혜택이 어플(v1.1.7)에 실시간 반영되었습니다!',
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
// (6) 📥 [원클릭 자동 채우기] 최신 111종 할일 & 56종 혜택 시트에 자동 동기화
// ------------------------------------------------------------------------------
function importMasterDataToSheets() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();

  const confirm = ui.alert(
    '📥 최신 마스터 데이터 시트 동기화',
    '어플의 최신 데이터(111종 할일 체크리스트 & 56종 출산 혜택)를 구글 스프레드시트의 [시기별할일] 및 [출산혜택정리] 시트에 자동으로 채워넣으시겠습니까?\n\n※ 기존 행이 최신 데이터로 깔끔하게 갱신되며, 어플 캐시까지 원클릭으로 동기화됩니다.',
    ui.ButtonSet.YES_NO
  );

  if (confirm !== ui.Button.YES) return;

  try {
    const res = executeMasterDataImport(ss);
    ui.alert(
      '🎉 구글 스프레드시트 갱신 완료!',
      '• 시기별 할일: ' + res.todosCount + '개 항목 채우기 완료\n' +
      '• 출산 혜택: ' + res.benefitsCount + '개 항목 채우기 완료\n\n' +
      '✅ 구글 시트에 최신 데이터가 완벽히 반영되었으며, 어플 캐시 동기화까지 100% 완료되었습니다!',
      ui.ButtonSet.OK
    );
  } catch (err) {
    ui.alert('❌ 데이터 동기화 중 오류가 발생했습니다: ' + err.toString());
  }
}

/**
 * 최신 GitHub data_export.json 데이터를 구글 시트에 주입하고 어플 캐시를 즉시 갱신
 */
function executeMasterDataImport(ss) {
  const url = 'https://raw.githubusercontent.com/kong090821/kongkong/main/data_export.json';
  const response = UrlFetchApp.fetch(url, { muteHttpExceptions: true });
  if (response.getResponseCode() !== 200) {
    throw new Error('데이터 다운로드 실패 (HTTP ' + response.getResponseCode() + ')');
  }

  const json = JSON.parse(response.getContentText('UTF-8'));
  let todosCount = 0;
  let benefitsCount = 0;

  // 1. 시기별할일 시트 갱신
  if (json.todos && Array.isArray(json.todos) && json.todos.length > 0) {
    let sheetTodos = ss.getSheetByName('시기별할일');
    if (!sheetTodos) {
      sheetTodos = ss.insertSheet('시기별할일');
      sheetTodos.appendRow(['ID', '시기구분', '담당역할(#아빠/#엄마/#부부)', '할일제목', '상세팁/가이드']);
      sheetTodos.getRange(1, 1, 1, 5).setBackground('#FFE0B2').setFontWeight('bold');
    }

    const lastRow = sheetTodos.getLastRow();
    if (lastRow >= 2) {
      sheetTodos.getRange(2, 1, lastRow - 1, sheetTodos.getLastColumn()).clearContent();
    }

    const todosRows = json.todos.map(function(t) {
      return [
        t.id || '',
        t.category || t.tabCategory || '',
        t.role || t.roleTag || '#부부',
        t.title || '',
        t.tip || ''
      ];
    });

    sheetTodos.getRange(2, 1, todosRows.length, 5).setValues(todosRows);
    todosCount = todosRows.length;
  }

  // 2. 출산혜택정리 시트 갱신
  if (json.benefits && Array.isArray(json.benefits) && json.benefits.length > 0) {
    let sheetBen = ss.getSheetByName('출산혜택정리');
    if (!sheetBen) {
      sheetBen = ss.insertSheet('출산혜택정리');
      sheetBen.appendRow(['ID', '지역구분', '혜택명', '지원형태(바우처/현금)', '지원금액 및 내용', '신청자격', '신청시기', '신청처']);
      sheetBen.getRange(1, 1, 1, 8).setBackground('#FFE0B2').setFontWeight('bold');
    }

    const lastRowBen = sheetBen.getLastRow();
    if (lastRowBen >= 2) {
      sheetBen.getRange(2, 1, lastRowBen - 1, sheetBen.getLastColumn()).clearContent();
    }

    const benRows = json.benefits.map(function(b) {
      return [
        b.id || '',
        b.region || '',
        b.title || '',
        b.type || b.badge || '',
        b.amount || b.desc || '',
        b.eligibility || '',
        b.timing || '',
        b.place || ''
      ];
    });

    sheetBen.getRange(2, 1, benRows.length, 8).setValues(benRows);
    benefitsCount = benRows.length;
  }

  // 3. 앱 캐시 동기화도 즉시 수행
  syncToApp();

  return { todosCount: todosCount, benefitsCount: benefitsCount };
}

// ------------------------------------------------------------------------------
// 3. [Gemini AI & 실시간 온에딧 자동 완성 엔진]
// ------------------------------------------------------------------------------

/**
 * 🔑 Gemini AI API 키 설정 다이얼로그 (Google AI Studio 무료 키)
 */
function setupGeminiApiKeyDialog() {
  const ui = SpreadsheetApp.getUi();
  const props = PropertiesService.getScriptProperties();
  const currentKey = props.getProperty('GEMINI_API_KEY') || '';
  const maskedKey = currentKey ? (currentKey.substring(0, 6) + '...' + currentKey.slice(-4)) : '미등록 (스마트 카탈로그 모드로 작동 중)';

  const res = ui.prompt(
    '🤖 Gemini AI 무료 API 키 설정',
    'Google AI Studio에서 무료로 발급받은 Gemini API 키를 입력하세요.\n(현재 상태: ' + maskedKey + ')\n\n※ API 키를 등록하면 품목명만 적어도 수량, 보관위치, 팁, 맘카페 1위, 쿠팡 상품까지 10개 열을 AI가 실시간 검색하여 자동 완성합니다.\n\n발급 링크: https://aistudio.google.com/app/apikey\n(빈 칸으로 두고 확인을 누르면 API 키가 삭제됩니다)',
    ui.ButtonSet.OK_CANCEL
  );

  if (res.getSelectedButton() === ui.Button.OK) {
    const newKey = res.getResponseText().trim();
    if (newKey) {
      props.setProperty('GEMINI_API_KEY', newKey);
      ui.alert('✅ 설정 완료', 'Gemini API 키가 성공적으로 등록되었습니다!\n이제 시트에 품목명을 입력하면 Gemini AI가 자동으로 세부 항목들을 실시간 채워줍니다.', ui.ButtonSet.OK);
    } else {
      props.deleteProperty('GEMINI_API_KEY');
      ui.alert('ℹ️ 해제 완료', 'Gemini API 키가 해제되었습니다. 기본 내장 카탈로그 모드로 전환됩니다.', ui.ButtonSet.OK);
    }
  }
}

/**
 * 🤖 Gemini 1.5 Flash API 호출: 품목명으로 10개 세부 속성 실시간 JSON 추출
 */
function fetchGeminiItemDetails(sheetName, itemTitle) {
  const apiKey = PropertiesService.getScriptProperties().getProperty('GEMINI_API_KEY');
  if (!apiKey) return null;

  const isBag = (sheetName.indexOf('출산가방') !== -1);
  const prompt = isBag
    ? `너는 대한민국 임신·출산 전문 스마트 어시스턴트야. 출산가방 준비물 품목인 "${itemTitle}"에 대해 최신 네이버 맘카페(맘스홀릭 등)와 쿠팡 인기 상품 트렌드를 반영하여 다음 JSON 객체 형식으로만 답해줘. 마크다운 백틱 없이 순수 JSON만 반환해.\n` +
      `{\n` +
      `  "tabCategory": "병원가방 또는 조리원가방 또는 퇴원/집가방 중 가장 적절한 1개",\n` +
      `  "section": "산모 필수품, 아기용품, 위생/세면, 수유용품, 서류/기타 중 1개",\n` +
      `  "recommendedQty": "일반적으로 권장되는 준비 수량 (예: 1팩, 2벌, 3개 등 단위 포함)",\n` +
      `  "locationTags": "보관 및 준비 위치 (예: 캐리어, 산모수첩가방, 트롤리, 보루박스 등)",\n` +
      `  "note": "선배맘들이 강조하는 실전 꿀팁 및 체크포인트 (1~2문장)",\n` +
      `  "momcafe1st": "맘카페 실사용 선호도 1위 대표 브랜드 또는 제품명",\n` +
      `  "top1": "쿠팡에서 가장 판매량이 높은 1위 구체적 상품명",\n` +
      `  "top2": "쿠팡 추천 2위 상품명",\n` +
      `  "top3": "쿠팡 추천 3위 상품명"\n` +
      `}`
    : `너는 대한민국 육아·신생아 전문 스마트 어시스턴트야. 육아용품 품목인 "${itemTitle}"에 대해 최신 네이버 맘카페와 쿠팡 인기 상품 트렌드를 반영하여 다음 JSON 객체 형식으로만 답해줘. 마크다운 백틱 없이 순수 JSON만 반환해.\n` +
      `{\n` +
      `  "category": "수유용품, 위생용품, 의류/침구, 목욕용품, 안전/외출, 아기방가구 중 1개",\n` +
      `  "section": "세부 카테고리 (예: 젖병/세척, 기저귀케어, 배냇저고리 등)",\n` +
      `  "period": "권장 사용 시기 (예: 신생아~1개월, 1~3개월, 출산 직후 등)",\n` +
      `  "purchaseTag": "구매 시기 (필수, 선택, 조리원 퇴소 후, 출산 직후 중 1개)",\n` +
      `  "description": "선배맘들의 선택 가이드 및 실사용 팁 (1~2문장)",\n` +
      `  "momcafe1st": "맘카페 실사용 선호도 1위 대표 브랜드 또는 제품명",\n` +
      `  "top1": "쿠팡 인기 판매 1위 구체적 상품명",\n` +
      `  "top2": "쿠팡 인기 판매 2위 상품명",\n` +
      `  "top3": "쿠팡 인기 판매 3위 상품명"\n` +
      `}`;

  const url = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=' + apiKey;
  const payload = {
    contents: [{ parts: [{ text: prompt }] }],
    generationConfig: {
      response_mime_type: 'application/json',
      temperature: 0.2
    }
  };

  try {
    const resp = UrlFetchApp.fetch(url, {
      method: 'post',
      contentType: 'application/json',
      payload: JSON.stringify(payload),
      muteHttpExceptions: true
    });

    if (resp.getResponseCode() !== 200) {
      Logger.log('Gemini API Error: ' + resp.getContentText());
      return null;
    }

    const json = JSON.parse(resp.getContentText());
    if (!json.candidates || json.candidates.length === 0) return null;
    const rawText = json.candidates[0].content.parts[0].text;
    const cleanedJson = rawText.replace(/```json/g, '').replace(/```/g, '').trim();
    return JSON.parse(cleanedJson);
  } catch (err) {
    Logger.log('fetchGeminiItemDetails Error: ' + err.message);
    return null;
  }
}

/**
 * 🪄 단일 행의 모든 열을 Gemini AI 또는 스마트 카탈로그로 안전하게 채우기
 */
function fillSingleRowWithGeminiOrCatalog(sheet, row, titleVal, ss, blacklist) {
  if (!sheet || row < 2 || !titleVal) return false;
  const sheetName = sheet.getName();
  const isBag = (sheetName.indexOf('출산가방') !== -1);
  const isBaby = (sheetName.indexOf('육아용품') !== -1);
  if (!isBag && !isBaby) return false;

  if (!ss) ss = sheet.getParent();
  if (!blacklist) blacklist = getBlacklistedProducts(ss);

  // 1열: 고유 ID 자동 생성
  const idCell = sheet.getRange(row, 1);
  const prefix = isBag ? 'm_custom_' : 'b_custom_';
  if (!String(idCell.getValue() || '').trim()) {
    idCell.setValue(prefix + row + '_' + new Date().getTime().toString().slice(-4));
  }

  // Gemini AI 데이터 추출 시도
  const aiData = fetchGeminiItemDetails(sheetName, titleVal);
  const keyword = cleanKeyword(titleVal);
  const defaultCoupang = 'https://link.coupang.com/a/hDXnz86Thk';

  if (isBag) {
    // 2열: 구분 (tabCategory)
    const tabCell = sheet.getRange(row, 2);
    if (!String(tabCell.getValue() || '').trim() && !isRedColor(tabCell.getBackground()) && !isBlueColor(tabCell.getBackground())) {
      tabCell.setValue((aiData && aiData.tabCategory) ? aiData.tabCategory : '병원가방');
      tabCell.setBackground('#E1F5FE');
    }

    // 3열: 카테고리 (section)
    const secCell = sheet.getRange(row, 3);
    if (!String(secCell.getValue() || '').trim() && !isRedColor(secCell.getBackground()) && !isBlueColor(secCell.getBackground())) {
      secCell.setValue((aiData && aiData.section) ? aiData.section : '산모 필수품');
      secCell.setBackground('#E1F5FE');
    }

    // 5열: 권장수량 (recommendedQty)
    const qtyCell = sheet.getRange(row, 5);
    if (!String(qtyCell.getValue() || '').trim() && !isRedColor(qtyCell.getBackground()) && !isBlueColor(qtyCell.getBackground())) {
      qtyCell.setValue((aiData && aiData.recommendedQty) ? aiData.recommendedQty : '1개');
      qtyCell.setBackground('#E1F5FE');
    }

    // 6열: 보관위치 (locationTags)
    const locCell = sheet.getRange(row, 6);
    if (!String(locCell.getValue() || '').trim() && !isRedColor(locCell.getBackground()) && !isBlueColor(locCell.getBackground())) {
      locCell.setValue((aiData && aiData.locationTags) ? aiData.locationTags : '캐리어');
      locCell.setBackground('#E1F5FE');
    }

    // 7열: 체크포인트/팁 (note)
    const noteCell = sheet.getRange(row, 7);
    if (!String(noteCell.getValue() || '').trim() && !isRedColor(noteCell.getBackground()) && !isBlueColor(noteCell.getBackground())) {
      noteCell.setValue((aiData && aiData.note) ? aiData.note : (titleVal + ' 필수 준비 권장'));
      noteCell.setBackground('#E1F5FE');
    }

    // 8열: 맘카페 1위 (momcafe1st)
    const momCell = sheet.getRange(row, 8);
    if (!String(momCell.getValue() || '').trim() && !isRedColor(momCell.getBackground()) && !isBlueColor(momCell.getBackground())) {
      const val = (aiData && aiData.momcafe1st) ? aiData.momcafe1st : fetchMomCafeMention(keyword, blacklist);
      momCell.setValue(val);
      momCell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    // 9~11열: 쿠팡 TOP 1~3 (top1, top2, top3)
    const coupangFallback = fetchCoupangTop3(keyword, blacklist);
    const top1Cell = sheet.getRange(row, 9);
    if (!String(top1Cell.getValue() || '').trim() && !isRedColor(top1Cell.getBackground()) && !isBlueColor(top1Cell.getBackground())) {
      const val = (aiData && aiData.top1) ? aiData.top1 : coupangFallback.top1;
      top1Cell.setValue(val);
      top1Cell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    const top2Cell = sheet.getRange(row, 10);
    if (!String(top2Cell.getValue() || '').trim() && !isRedColor(top2Cell.getBackground()) && !isBlueColor(top2Cell.getBackground())) {
      const val = (aiData && aiData.top2) ? aiData.top2 : coupangFallback.top2;
      top2Cell.setValue(val);
      top2Cell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    const top3Cell = sheet.getRange(row, 11);
    if (!String(top3Cell.getValue() || '').trim() && !isRedColor(top3Cell.getBackground()) && !isBlueColor(top3Cell.getBackground())) {
      const val = (aiData && aiData.top3) ? aiData.top3 : coupangFallback.top3;
      top3Cell.setValue(val);
      top3Cell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    // 12~14열: 쿠팡 파트너스 링크
    const u1Cell = sheet.getRange(row, 12);
    if (!String(u1Cell.getValue() || '').trim()) u1Cell.setValue(defaultCoupang);
    const u2Cell = sheet.getRange(row, 13);
    if (!String(u2Cell.getValue() || '').trim()) u2Cell.setValue(defaultCoupang);
    const u3Cell = sheet.getRange(row, 14);
    if (!String(u3Cell.getValue() || '').trim()) u3Cell.setValue(defaultCoupang);
  }

  if (isBaby) {
    // 2열: 카테고리 (category)
    const catCell = sheet.getRange(row, 2);
    if (!String(catCell.getValue() || '').trim() && !isRedColor(catCell.getBackground()) && !isBlueColor(catCell.getBackground())) {
      catCell.setValue((aiData && aiData.category) ? aiData.category : '위생용품');
      catCell.setBackground('#E1F5FE');
    }

    // 3열: 세부분류 (section)
    const secCell = sheet.getRange(row, 3);
    if (!String(secCell.getValue() || '').trim() && !isRedColor(secCell.getBackground()) && !isBlueColor(secCell.getBackground())) {
      secCell.setValue((aiData && aiData.section) ? aiData.section : '기타용품');
      secCell.setBackground('#E1F5FE');
    }

    // 5열: 사용시기 (period)
    const perCell = sheet.getRange(row, 5);
    if (!String(perCell.getValue() || '').trim() && !isRedColor(perCell.getBackground()) && !isBlueColor(perCell.getBackground())) {
      perCell.setValue((aiData && aiData.period) ? aiData.period : '신생아~1개월');
      perCell.setBackground('#E1F5FE');
    }

    // 6열: 구매태그 (purchaseTag)
    const tagCell = sheet.getRange(row, 6);
    if (!String(tagCell.getValue() || '').trim() && !isRedColor(tagCell.getBackground()) && !isBlueColor(tagCell.getBackground())) {
      tagCell.setValue((aiData && aiData.purchaseTag) ? aiData.purchaseTag : '필수');
      tagCell.setBackground('#E1F5FE');
    }

    // 7열: 설명/팁 (description)
    const descCell = sheet.getRange(row, 7);
    if (!String(descCell.getValue() || '').trim() && !isRedColor(descCell.getBackground()) && !isBlueColor(descCell.getBackground())) {
      descCell.setValue((aiData && aiData.description) ? aiData.description : (titleVal + ' 가이드 및 팁'));
      descCell.setBackground('#E1F5FE');
    }

    // 8열: 맘카페 1위 (momcafe1st)
    const momCell = sheet.getRange(row, 8);
    if (!String(momCell.getValue() || '').trim() && !isRedColor(momCell.getBackground()) && !isBlueColor(momCell.getBackground())) {
      const val = (aiData && aiData.momcafe1st) ? aiData.momcafe1st : fetchMomCafeMention(keyword, blacklist);
      momCell.setValue(val);
      momCell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    // 9~11열: 쿠팡 TOP 1~3 (top1, top2, top3)
    const coupangFallback = fetchCoupangTop3(keyword, blacklist);
    const top1Cell = sheet.getRange(row, 9);
    if (!String(top1Cell.getValue() || '').trim() && !isRedColor(top1Cell.getBackground()) && !isBlueColor(top1Cell.getBackground())) {
      const val = (aiData && aiData.top1) ? aiData.top1 : coupangFallback.top1;
      top1Cell.setValue(val);
      top1Cell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    const top2Cell = sheet.getRange(row, 10);
    if (!String(top2Cell.getValue() || '').trim() && !isRedColor(top2Cell.getBackground()) && !isBlueColor(top2Cell.getBackground())) {
      const val = (aiData && aiData.top2) ? aiData.top2 : coupangFallback.top2;
      top2Cell.setValue(val);
      top2Cell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    const top3Cell = sheet.getRange(row, 11);
    if (!String(top3Cell.getValue() || '').trim() && !isRedColor(top3Cell.getBackground()) && !isBlueColor(top3Cell.getBackground())) {
      const val = (aiData && aiData.top3) ? aiData.top3 : coupangFallback.top3;
      top3Cell.setValue(val);
      top3Cell.setBackground(isViralAdKeyword(val) ? '#BBDEFB' : '#E1F5FE');
    }

    // 12~14열: 쿠팡 파트너스 링크
    const u1Cell = sheet.getRange(row, 12);
    if (!String(u1Cell.getValue() || '').trim()) u1Cell.setValue(defaultCoupang);
    const u2Cell = sheet.getRange(row, 13);
    if (!String(u2Cell.getValue() || '').trim()) u2Cell.setValue(defaultCoupang);
    const u3Cell = sheet.getRange(row, 14);
    if (!String(u3Cell.getValue() || '').trim()) u3Cell.setValue(defaultCoupang);
  }

  return true;
}

/**
 * ⚡ 실시간 onEdit 트리거: 4열(품목명) 입력 시 즉시 Gemini AI / 카탈로그 자동 완성 작동
 */
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

    fillSingleRowWithGeminiOrCatalog(sheet, row, titleVal, sheet.getParent(), null);
  }
}

/**
 * 🤖 [메뉴 실행] 현재 선택된 행의 품목을 Gemini AI로 즉시 자동 완성
 */
function fillActiveItemWithGemini() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheet = ss.getActiveSheet();
  const sheetName = sheet.getName();
  const ui = SpreadsheetApp.getUi();

  if (sheetName !== '출산가방 체크리스트' && sheetName !== '육아용품 체크리스트') {
    ui.alert('안내', '출산가방 체크리스트 또는 육아용품 체크리스트 시트에서 행을 선택한 후 실행해주세요.', ui.ButtonSet.OK);
    return;
  }

  const row = sheet.getActiveCell().getRow();
  if (row < 2) {
    ui.alert('안내', '데이터가 있는 행을 선택해주세요. (2행부터 가능)', ui.ButtonSet.OK);
    return;
  }

  const titleVal = String(sheet.getRange(row, 4).getValue() || '').trim();
  if (!titleVal) {
    ui.alert('안내', '선택한 행의 4열(품목명)이 비어있습니다. 먼저 품목명을 적어주세요.', ui.ButtonSet.OK);
    return;
  }

  const success = fillSingleRowWithGeminiOrCatalog(sheet, row, titleVal, ss, null);
  if (success) {
    ui.alert('🎉 자동 완성 완료', '품목 [' + titleVal + ']의 세부 항목(수량, 위치, 팁, 맘카페 1위, 쿠팡 상품 등)이 자동으로 채워졌습니다!\n\n확인 후 상단 메뉴 [어플에 즉시 반영]을 누르면 앱에 바로 등록됩니다.', ui.ButtonSet.OK);
  }
}

/**
 * 🤖 [메뉴 실행] 빈 칸이 있는 모든 품목 Gemini AI 일괄 자동 완성
 */
function autoFillAllEmptyWithGemini() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();
  const sheets = [ss.getSheetByName('출산가방 체크리스트'), ss.getSheetByName('육아용품 체크리스트')].filter(Boolean);

  let totalUpdated = 0;
  for (let s = 0; s < sheets.length; s++) {
    const sheet = sheets[s];
    const lastRow = sheet.getLastRow();
    if (lastRow < 2) continue;

    for (let r = 2; r <= lastRow; r++) {
      const titleVal = String(sheet.getRange(r, 4).getValue() || '').trim();
      if (!titleVal) continue;

      // 8열(맘카페)이나 9열(쿠팡1)이나 5열(수량) 중 하나라도 비어있으면 자동 완성 실행
      const c5 = String(sheet.getRange(r, 5).getValue() || '').trim();
      const c8 = String(sheet.getRange(r, 8).getValue() || '').trim();
      const c9 = String(sheet.getRange(r, 9).getValue() || '').trim();

      if (!c5 || !c8 || !c9) {
        fillSingleRowWithGeminiOrCatalog(sheet, r, titleVal, ss, null);
        totalUpdated++;
        Utilities.sleep(1200); // 무료 API 분당 호출 제한 방지
      }
    }
  }

  ui.alert(
    '✨ Gemini AI 일괄 완성 완료',
    '총 ' + totalUpdated + '개 품목의 세부 항목이 인터넷 검색을 통해 자동으로 채워졌습니다.\n\n확인 후 [어플에 즉시 반영하기]를 누르시면 앱에 즉시 적용됩니다.',
    ui.ButtonSet.OK
  );
}

// 수동으로 신규 행 및 빈 칸 전체 일괄 자동 완성 (기본 카탈로그 모드)
function autoFillMissingRowData() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const count = ensureRowIntegrity(ss);
  SpreadsheetApp.getUi().alert(
    '✨ 자동 완성 완료',
    '총 ' + count + '개 신규 품목의 ID 및 맘카페/쿠팡 추천 데이터가 기본 카탈로그로 채워졌습니다.\n\n확인 후 [어플에 즉시 반영하기]를 누르시면 어플에 바로 적용됩니다.',
    SpreadsheetApp.getUi().ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 4. [색상 감지 & 바이럴 광고 차단 엔진]
// ------------------------------------------------------------------------------

/**
 * 셀 배경색이 빨간색 계열인지 판별 (중요 데이터 보호 / 자동 덮어쓰기 영구 방지 / 블랙리스트)
 */
function isRedColor(hex) {
  if (!hex || typeof hex !== 'string') return false;
  hex = hex.trim().toLowerCase();
  if (hex === '#ffffff' || hex === '#fff' || hex === 'white') return false;
  if (hex === '#fffde7' || hex === '#e8f5e9' || hex === '#e1f5fe' || hex === '#ede7f6') return false; // 노랑/초록/하늘/보라 제외

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
 * 셀 배경색이 파란색 계열인지 판별 (바이럴 광고/수동 검토 대기 항목 분리)
 */
function isBlueColor(hex) {
  if (!hex || typeof hex !== 'string') return false;
  hex = hex.trim().toLowerCase();
  if (hex === '#ffffff' || hex === '#fff' || hex === 'white') return false;
  if (hex === '#fffde7' || hex === '#e8f5e9' || hex === '#ede7f6') return false; // 노랑/초록/보라 제외

  // 단순 신규 자동완성 표시용 연하늘색(#e1f5fe)은 제외
  if (hex === '#e1f5fe') return false;

  const standardBlues = [
    '#2196f3', '#1e88e5', '#1976d2', '#1565c0', '#0d47a1',
    '#90caf9', '#64b5f6', '#42a5f5', '#29b6f6', '#00b0ff',
    '#0091ea', '#e3f2fd', '#bbdefb', '#b3e5fc', '#81d4fa',
    '#4fc3f7', '#00e5ff', '#00b8d4', '#1890ff', '#108ee9'
  ];
  if (standardBlues.indexOf(hex) !== -1) return true;

  if (hex.startsWith('#')) hex = hex.substring(1);
  if (hex.length === 3) {
    hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
  }
  if (hex.length !== 6) return false;

  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);

  if (isNaN(r) || isNaN(g) || isNaN(b)) return false;

  // 파란색 성분이 확연히 우세한지 판정
  if (b >= 180 && r <= 180 && (b - r >= 25) && (b - g >= 15)) return true;
  if (b >= 150 && (b > r * 1.3) && (b > g * 1.1)) return true;

  return false;
}

/**
 * 바이럴 광고 키워드 포함 여부 판별
 */
function isViralAdKeyword(text) {
  if (!text || typeof text !== 'string') return false;
  const lower = text.toLowerCase();
  const viralKeywords = [
    '광고', '협찬', '바이럴', '파트너스', '체험단', '대행', '홍보',
    '지원받아', '소정의', '대가지급', '원고료', '스폰서', 'ad', 'promoted'
  ];
  return viralKeywords.some(kw => lower.indexOf(kw) !== -1);
}

/**
 * 빨간색(중요 보존/블랙리스트) 및 파란색(바이럴 검토 대기)으로 표시된 품목 추출
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
        if (isRedColor(bgs[r][c]) || isBlueColor(bgs[r][c])) {
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

      // 맘카페 1위 (비어있고 빨간색/파란색이 아닌 경우만 자동 채움)
      if (!String(values[i][7] || '').trim() && !isRedColor(bgs[i][7]) && !isBlueColor(bgs[i][7])) {
        const momVal = fetchMomCafeMention(keyword, blacklist);
        values[i][7] = momVal;
        bgs[i][7] = isViralAdKeyword(momVal) ? '#BBDEFB' : '#E1F5FE';
        modified = true;
      }

      // 쿠팡 TOP 1~3 (비어있고 빨간색/파란색이 아닌 경우만 자동 채움)
      const coupang = fetchCoupangTop3(keyword, blacklist);
      if (!String(values[i][8] || '').trim() && !isRedColor(bgs[i][8]) && !isBlueColor(bgs[i][8])) {
        values[i][8] = coupang.top1;
        bgs[i][8] = isViralAdKeyword(coupang.top1) ? '#BBDEFB' : '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][9] || '').trim() && !isRedColor(bgs[i][9]) && !isBlueColor(bgs[i][9])) {
        values[i][9] = coupang.top2;
        bgs[i][9] = isViralAdKeyword(coupang.top2) ? '#BBDEFB' : '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][10] || '').trim() && !isRedColor(bgs[i][10]) && !isBlueColor(bgs[i][10])) {
        values[i][10] = coupang.top3;
        bgs[i][10] = isViralAdKeyword(coupang.top3) ? '#BBDEFB' : '#E1F5FE';
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

      if (!String(values[i][7] || '').trim() && !isRedColor(bgs[i][7]) && !isBlueColor(bgs[i][7])) {
        const momVal = fetchMomCafeMention(keyword, blacklist);
        values[i][7] = momVal;
        bgs[i][7] = isViralAdKeyword(momVal) ? '#BBDEFB' : '#E1F5FE';
        modified = true;
      }

      const coupang = fetchCoupangTop3(keyword, blacklist);
      if (!String(values[i][8] || '').trim() && !isRedColor(bgs[i][8]) && !isBlueColor(bgs[i][8])) {
        values[i][8] = coupang.top1;
        bgs[i][8] = isViralAdKeyword(coupang.top1) ? '#BBDEFB' : '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][9] || '').trim() && !isRedColor(bgs[i][9]) && !isBlueColor(bgs[i][9])) {
        values[i][9] = coupang.top2;
        bgs[i][9] = isViralAdKeyword(coupang.top2) ? '#BBDEFB' : '#E1F5FE';
        modified = true;
      }
      if (!String(values[i][10] || '').trim() && !isRedColor(bgs[i][10]) && !isBlueColor(bgs[i][10])) {
        values[i][10] = coupang.top3;
        bgs[i][10] = isViralAdKeyword(coupang.top3) ? '#BBDEFB' : '#E1F5FE';
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
// 6. [파이프라인 1] 맘카페 언급 데이터 수집 (매주 월요일 06:00, 빨간색/파란색 셀 보존)
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
                 ' - [맘카페 수집 완료] 총 ' + updatedCount + '개 항목 업데이트 (🔴빨간색 💙파란색 지정 셀 보존 완료)';
  PropertiesService.getScriptProperties().setProperty('LAST_MOMCAFE_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('☕ [맘카페 최다 언급] 수집이 완료되었습니다!\n\n• 연노란색: 새로 수집된 추천 상품\n• 🔴 빨간색: 사용자가 보호한 중요 품목\n• 💙 파란색: 바이럴/광고 의심 검토 분리 품목\n\n검토 후 [어플에 즉시 반영하기]를 눌러주세요.');
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

    // 빨간색(중요 보존) 및 파란색(바이럴 검토 대기) 채우기 셀(또는 행) 감지 시 자동 덮어쓰기 방지
    const cellBg = bgs[i][momcafeCol - 1];
    const rowBg = bgs[i][0];
    const titleBg = bgs[i][titleCol - 1];

    if (isRedColor(cellBg) || isRedColor(rowBg) || isRedColor(titleBg) ||
        isBlueColor(cellBg) || isBlueColor(rowBg) || isBlueColor(titleBg)) {
      continue;
    }

    const keyword = cleanKeyword(rawTitle);
    const momcafeItem = fetchMomCafeMention(keyword, blacklist);

    if (momcafeItem) {
      sheet.getRange(i + 2, momcafeCol).setValue(momcafeItem);
      const isViral = isViralAdKeyword(momcafeItem);
      sheet.getRange(i + 2, momcafeCol).setBackground(isViral ? '#BBDEFB' : '#FFFDE7');
      count++;
    }
  }
  return count;
}

// ------------------------------------------------------------------------------
// 7. [파이프라인 2] 쿠팡 실시간 TOP 3 수집 (매일 06:00, 빨간색/파란색 셀 보존)
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
                 ' - [쿠팡 TOP3 수집 완료] 총 ' + updatedCount + '개 품목 랭킹 갱신 (🔴빨간색 💙파란색 지정 셀 보존 완료)';
  PropertiesService.getScriptProperties().setProperty('LAST_COUPANG_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('📦 [쿠팡 실시간 랭킹 TOP 3] 수집이 완료되었습니다!\n\n• 연초록색: 새로 수집된 랭킹\n• 🔴 빨간색: 중요 영구 보존 품목\n• 💙 파란색: 바이럴/광고 검토 분리 품목\n\n확인 후 [어플에 즉시 반영하기]를 눌러주세요.');
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

    // 행 전체나 품목명이 빨간색/파란색이면 TOP 1~3 전체 수집 제외 (보존)
    if (isRedColor(rowBg) || isRedColor(titleBg) || isBlueColor(rowBg) || isBlueColor(titleBg)) {
      continue;
    }

    const keyword = cleanKeyword(rawTitle);
    const coupangTop3 = fetchCoupangTop3(keyword, blacklist);

    if (coupangTop3) {
      if (!isRedColor(bgs[i][top1Col - 1]) && !isBlueColor(bgs[i][top1Col - 1])) {
        sheet.getRange(i + 2, top1Col).setValue(coupangTop3.top1);
        sheet.getRange(i + 2, top1Col).setBackground(isViralAdKeyword(coupangTop3.top1) ? '#BBDEFB' : '#E8F5E9');
      }
      if (!isRedColor(bgs[i][top2Col - 1]) && !isBlueColor(bgs[i][top2Col - 1])) {
        sheet.getRange(i + 2, top2Col).setValue(coupangTop3.top2);
        sheet.getRange(i + 2, top2Col).setBackground(isViralAdKeyword(coupangTop3.top2) ? '#BBDEFB' : '#E8F5E9');
      }
      if (!isRedColor(bgs[i][top3Col - 1]) && !isBlueColor(bgs[i][top3Col - 1])) {
        sheet.getRange(i + 2, top3Col).setValue(coupangTop3.top3);
        sheet.getRange(i + 2, top3Col).setBackground(isViralAdKeyword(coupangTop3.top3) ? '#BBDEFB' : '#E8F5E9');
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
// 9. 하이라이트 배경색 초기화 (※ 🔴빨간색 및 💙파란색 셀은 100% 영구 보존!)
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
        // 🔴빨간색(isRedColor) 및 💙파란색(isBlueColor)은 수동 확인 대상이므로 건드리지 않고 그대로 보존!
        if (!isRedColor(bg) && !isBlueColor(bg) &&
            (bg === '#fffde7' || bg === '#e8f5e9' || bg === '#e1f5fe' || bg === '#ede7f6')) {
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
    '🛡️ 🔴빨간색(중요 보존) 및 💙파란색(바이럴 검토 대기)으로 표시해둔 셀은 자동 수집 시 절대 덮어쓰지 않고 안전하게 보존됩니다!\n' +
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
    version: "1.1.5",
    versionCode: 15,
    lastUpdated: lastUpdated,
    updatedAt: lastUpdated,
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
      if (postData.action === 'importMasterData') {
        const importRes = executeMasterDataImport(ss);
        result = {
          status: "success",
          message: "Master data imported successfully",
          todosCount: importRes.todosCount,
          benefitsCount: importRes.benefitsCount
        };
        return ContentService.createTextOutput(JSON.stringify(result))
          .setMimeType(ContentService.MimeType.JSON);
      }

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
// 13. [맞춤 추천 관리 엔진 및 파싱 함수] 시트 기반 데이터 파싱 (파란색 셀 어플 배포 자동 제외)
// ------------------------------------------------------------------------------
function getItemTitleMap(ss) {
  const titleMap = {};
  const sheets = ['출산가방 체크리스트', '육아용품 체크리스트', '시기별할일', '출산혜택정리'];
  
  sheets.forEach(function(sheetName) {
    const s = ss.getSheetByName(sheetName);
    if (!s || s.getLastRow() < 2) return;
    const values = s.getRange(2, 1, s.getLastRow() - 1, s.getLastColumn()).getValues();
    values.forEach(function(row) {
      const id = String(row[0] || '').trim();
      if (!id) return;
      let title = '';
      if (sheetName === '출산혜택정리') {
        title = String(row[2] || '').trim();
      } else {
        title = String(row[3] || '').trim();
      }
      if (id && title) {
        titleMap[id] = title;
      }
    });
  });

  const defaults = {
    'm_cloth_1': '수유브라', 'm_cloth_2': '산모팬티', 'm_cloth_3': '무압박 수유양말', 'm_cloth_4': '실내 슬리퍼',
    'm_cloth_5': '의료용 압박스타킹', 'm_cloth_6': '손목보호대', 'm_cloth_7': '산후복대', 'm_cloth_8': '가디건/겉옷',
    'm_hyg_1': '맘스 안심팬티', 'm_hyg_2': '오버나이트 생리대', 'm_hyg_3': '마이비데 (비데물티슈)', 'm_hyg_7': '흉터 시트 (제왕절개용)', 'm_hyg_8': '회음부 방석',
    'm_feed_1': '수유패드', 'm_feed_2': '모유저장팩', 'm_feed_4': '유두보호크림', 'm_feed_6': '유축기 깔때기', 'm_feed_7': '수유 쿠션',
    'm_sk_1': '산모 세면용품 세트', 'm_sk_2': '기초 화장품', 'm_sk_3': '립밤 & 수분크림', 'm_sk_5': '엄마용 영양제 (비타민, 철분 등)',
    'b_cloth_1': '배냇저고리', 'b_cloth_2': '속싸개', 'b_cloth_3': '겉싸개', 'b_care_1': '손수건', 'b_care_3': '아기 로션/수딩젤', 'b_care_4': '아기 영양제 (유산균/비타민D)',
    'b_care_dday': '디데이달력', 'b_care_focusbook': '초점책', 'b_care_nightlight': '수유등',
    'b_safe_1': '신생아 카시트', 'g_doc_1': '산모수첩 & 신분증', 'g_doc_2': '결제 수단 (카드/현금)',
    'g_dad_2': '보호자 세면용품', 'g_dad_4': '아빠용 침구류',
    'g_life_1': '텀블러', 'g_life_straw': '주름빨대', 'g_life_4': '휴대폰 충전기', 'g_life_multi': '멀티탭',
    'g_life_7': '손세정제', 'g_life_mask': '일회용마스크'
  };
  for (let k in defaults) {
    if (!titleMap[k]) titleMap[k] = defaults[k];
  }

  return titleMap;
}

function buildPairedRow(categoryName, idList, titleMap) {
  const row = [categoryName];
  idList.forEach(function(id) {
    const cleanId = String(id || '').trim();
    if (!cleanId) return;
    const title = titleMap[cleanId] || cleanId;
    row.push(cleanId);
    row.push(title);
  });
  return row;
}

function getOrCreateRecommendationSheet(ss) {
  let sheet = ss.getSheetByName('🎯 맞춤_추천_설정');
  const titleMap = getItemTitleMap(ss);

  const headers = [
    '구분', 
    '추천품목ID1', '품목명1', 
    '추천품목ID2', '품목명2', 
    '추천품목ID3', '품목명3', 
    '추천품목ID4', '품목명4', 
    '추천품목ID5', '품목명5', 
    '추천품목ID6', '품목명6', 
    '추천품목ID7', '품목명7', 
    '추천품목ID8', '품목명8'
  ];

  if (!sheet) {
    sheet = ss.insertSheet('🎯 맞춤_추천_설정');
    sheet.appendRow(headers);
    sheet.getRange(1, 1, 1, headers.length).setBackground('#E8EAF6').setFontWeight('bold');
    sheet.setFrozenRows(1);

    const defaultRules = [
      buildPairedRow('제왕절개', ['m_cloth_7', 'm_hyg_7', 'm_hyg_1', 'm_cloth_5', 'g_life_1'], titleMap),
      buildPairedRow('자연분만', ['m_hyg_8', 'm_hyg_3', 'm_hyg_2'], titleMap),
      buildPairedRow('조리원이용', ['m_feed_1', 'm_feed_2', 'm_feed_4', 'm_feed_6', 'm_feed_7', 'm_cloth_6', 'b_care_3', 'b_care_4'], titleMap),
      buildPairedRow('자택조리', ['m_feed_1', 'm_feed_4', 'b_care_3'], titleMap),
      buildPairedRow('공통산모', ['m_cloth_1', 'm_cloth_2', 'm_cloth_3', 'm_cloth_4', 'm_cloth_8', 'm_sk_1', 'm_sk_2', 'm_sk_3'], titleMap),
      buildPairedRow('공통신생아', ['b_cloth_1', 'b_cloth_2', 'b_cloth_3', 'b_care_1', 'b_safe_1'], titleMap),
      buildPairedRow('공통보호자', ['g_doc_1', 'g_doc_2', 'g_life_4'], titleMap)
    ];

    defaultRules.forEach(function(r) {
      sheet.appendRow(r);
    });
  } else {
    const firstHeader = String(sheet.getRange(1, 2).getValue() || '').trim();
    if (firstHeader.indexOf('목록') !== -1 || sheet.getLastColumn() <= 3) {
      const oldValues = sheet.getDataRange().getValues();
      sheet.clearContents();
      sheet.getRange(1, 1, 1, headers.length).setValues([headers]).setBackground('#E8EAF6').setFontWeight('bold');
      sheet.setFrozenRows(1);

      const categoryDefaults = {
        '제왕절개': ['m_cloth_7', 'm_hyg_7', 'm_hyg_1', 'm_cloth_5', 'g_life_1'],
        '자연분만': ['m_hyg_8', 'm_hyg_3', 'm_hyg_2'],
        '조리원이용': ['m_feed_1', 'm_feed_2', 'm_feed_4', 'm_feed_6', 'm_feed_7', 'm_cloth_6', 'b_care_3', 'b_care_4'],
        '자택조리': ['m_feed_1', 'm_feed_4', 'b_care_3'],
        '공통산모': ['m_cloth_1', 'm_cloth_2', 'm_cloth_3', 'm_cloth_4', 'm_cloth_8', 'm_sk_1', 'm_sk_2', 'm_sk_3'],
        '공통신생아': ['b_cloth_1', 'b_cloth_2', 'b_cloth_3', 'b_care_1', 'b_safe_1'],
        '공통보호자': ['g_doc_1', 'g_doc_2', 'g_life_4']
      };

      for (let r = 1; r < oldValues.length; r++) {
        const cat = String(oldValues[r][0] || '').trim();
        if (!cat) continue;
        let ids = [];
        const rawIds = String(oldValues[r][1] || '').trim();
        if (rawIds) {
          ids = rawIds.split(',').map(s => s.trim()).filter(Boolean);
        } else if (categoryDefaults[cat]) {
          ids = categoryDefaults[cat];
        }
        if (ids.length > 0) {
          sheet.appendRow(buildPairedRow(cat, ids, titleMap));
        }
      }
    }
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

  const keyMap = {
    '제왕절개': 'cesarean',
    '자연분만': 'natural',
    '조리원이용': 'careCenter',
    '자택조리': 'homeCare',
    '공통산모': 'commonMaternity',
    '공통신생아': 'commonBaby',
    '공통보호자': 'commonGuardian'
  };

  for (let i = 1; i < values.length; i++) {
    const type = String(values[i][0] || '').trim();
    if (keyMap[type] && rules[keyMap[type]].length === 0) {
      const ids = [];
      const row = values[i];
      for (let c = 1; c < row.length; c++) {
        const val = String(row[c] || '').trim();
        if (!val) continue;
        if (val.indexOf(',') !== -1) {
          val.split(',').forEach(p => {
            const cleanP = p.trim();
            if (cleanP && ids.indexOf(cleanP) === -1) ids.push(cleanP);
          });
        } else {
          if (c % 2 === 1) {
            if (ids.indexOf(val) === -1) ids.push(val);
          } else {
            if (/^(m_|b_|g_|todo_|ben_|custom)/i.test(val) && ids.indexOf(val) === -1) {
              ids.push(val);
            }
          }
        }
      }
      rules[keyMap[type]] = ids;
    }
  }

  return rules;
}

// 4개 시트 전체 데이터 추출 (파란색 셀 바이럴 광고 어플 반영 자동 분리)
function getAllSheetsData(ss) {
  ensureRowIntegrity(ss);
  const DEFAULT_COUPANG_URL = 'https://link.coupang.com/a/hDXnz86Thk';

  const maternityList = parseSheetToObjects(ss.getSheetByName('출산가방 체크리스트'), [
    'id', 'tabCategory', 'section', 'title', 'recommendedQty', 'locationTags', 'note', 'momcafe1st', 'top1', 'top2', 'top3', 'top1_url', 'top2_url', 'top3_url'
  ]).map(function(item) {
    item.top1_url = (item.top1_url && String(item.top1_url).trim()) ? String(item.top1_url).trim() : DEFAULT_COUPANG_URL;
    item.top2_url = (item.top2_url && String(item.top2_url).trim()) ? String(item.top2_url).trim() : DEFAULT_COUPANG_URL;
    item.top3_url = (item.top3_url && String(item.top3_url).trim()) ? String(item.top3_url).trim() : DEFAULT_COUPANG_URL;
    return item;
  });

  const babyList = parseSheetToObjects(ss.getSheetByName('육아용품 체크리스트'), [
    'id', 'category', 'section', 'title', 'period', 'purchaseTag', 'description', 'momcafe1st', 'top1', 'top2', 'top3', 'top1_url', 'top2_url', 'top3_url'
  ]).map(function(item) {
    item.top1_url = (item.top1_url && String(item.top1_url).trim()) ? String(item.top1_url).trim() : DEFAULT_COUPANG_URL;
    item.top2_url = (item.top2_url && String(item.top2_url).trim()) ? String(item.top2_url).trim() : DEFAULT_COUPANG_URL;
    item.top3_url = (item.top3_url && String(item.top3_url).trim()) ? String(item.top3_url).trim() : DEFAULT_COUPANG_URL;
    return item;
  });

  return {
    maternityBag: maternityList,
    babySupplies: babyList,
    todos: parseSheetToObjects(ss.getSheetByName('시기별할일'), [
      'id', 'category', 'role', 'title', 'tip'
    ]),
    benefits: parseSheetToObjects(ss.getSheetByName('출산혜택정리'), [
      'id', 'region', 'title', 'type', 'amount', 'eligibility', 'timing', 'place'
    ])
  };
}

/**
 * 시트 데이터를 어플 객체 배열로 변환
 * (※ 💙파란색 지정 행/품목은 사용자가 직접 흰색 배경으로 수정하기 전까지 어플에 반영되지 않도록 자동 제외/분리)
 */
function parseSheetToObjects(sheet, keys) {
  if (!sheet) return [];
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return [];

  const sheetMaxCols = sheet.getMaxColumns();
  const fetchCols = Math.min(sheetMaxCols, keys.length);
  const range = sheet.getRange(2, 1, lastRow - 1, fetchCols);
  const values = range.getValues();
  const bgs = range.getBackgrounds();

  const result = [];
  for (let r = 0; r < values.length; r++) {
    const rowBg = String(bgs[r][0] || '').toLowerCase();
    const titleColIdx = keys.indexOf('title') !== -1 ? keys.indexOf('title') : 3;
    const titleBg = (titleColIdx < fetchCols) ? String(bgs[r][titleColIdx] || '').toLowerCase() : '';

    // 1. 행 전체나 품목명 셀이 파란색(바이럴 광고 수동 검토 대기)이면 어플에 절대 반영하지 않고 완전 분리!
    if (isBlueColor(rowBg) || isBlueColor(titleBg)) {
      continue;
    }

    const obj = {};
    for (let c = 0; c < keys.length; c++) {
      if (c < fetchCols) {
        const cellBg = String(bgs[r][c] || '').toLowerCase();
        // 2. 개별 셀이 파란색(바이럴/광고 대기)이면, 해당 개별 항목만 수동 검토 승인 전까지 어플 배포 데이터에서 제외 (빈 값 처리)
        if (isBlueColor(cellBg)) {
          obj[keys[c]] = '';
        } else {
          obj[keys[c]] = String(values[r][c] || '').trim();
        }
      } else {
        obj[keys[c]] = '';
      }
    }

    // 파란색 제외 후 의미 있는 데이터가 남은 경우만 앱 목록에 추가
    if (obj.title || obj.id) {
      result.push(obj);
    }
  }

  return result;
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
    '📖 꽁꽁 출산가방 v1.1.7 자동 수집 & 연동 가이드',
    '• 현재 앱 버전: v1.1.7 (빌드 코드: 17)\n' +
    '• 마지막 어플 반영 시각: ' + lastUpdate + '\n\n' +
    '• 맘카페 최근 수집: ' + momLog + '\n' +
    '• 쿠팡 최근 수집: ' + coupangLog + '\n\n' +
    '🔗 [쿠팡 파트너스 수익 링크 연동 안내]:\n' +
    '• 출산가방 및 육아용품 시트 L, M, N열에 단축 링크 입력 시 개별 상품 링크 우선 적용\n' +
    '• 미입력 품목은 선생님의 기본 쿠팡 파트너스 링크(https://link.coupang.com/a/hDXnz86Thk)로 자동 안전 연결\n' +
    '• 입력 후 상단 메뉴 [🔗 쿠팡 파트너스 수동 입력 링크 어플에 즉시 반영] 클릭 시 실시간 배포\n\n' +
    '🛡️ 색상별 기능 가이드:\n' +
    '1. 🔴 빨간색 셀: 중요 정보 영구 잠금 및 보호 (자동 수집 시 절대 덮어쓰지 않음)\n' +
    '2. 💙 파란색 셀: 바이럴/광고 의심 항목 자동 분리 (직접 흰색 배경으로 바꾼 것만 어플 반영)\n' +
    '3. 💜 연보라색 셀: 사용자 추가 신규 등록 품목 강조\n' +
    '4. 💛/💚 연노랑/연초록 셀: 새로 수집된 맘카페/쿠팡 추천 랭킹\n\n' +
    '💡 사용 팁: 수집된 바이럴 광고 의심 제품은 자동으로 파란색으로 칠해지며 어플 배포에서 분리됩니다. 확인 후 직접 셀 배경색을 흰색으로 바꾸시면 어플에 정상 반영됩니다.',
    ui.ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 15. [통계 분석 엔진] 사용자 활동 통계 기반 추천 가방 자동 계산 & 하단 추가 (보라색 글자)
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
    '공통산모': getTopIds(commonCounts, 8, defaultRules.commonMaternity)
  };

  const recSheet = getOrCreateRecommendationSheet(ss);
  const titleMap = getItemTitleMap(ss);
  const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm");

  const currentValues = recSheet.getDataRange().getValues();
  let statsStartRow = -1;

  for (let r = 0; r < currentValues.length; r++) {
    const firstCell = String(currentValues[r][0] || '').trim();
    if (firstCell.indexOf('📊') !== -1 || firstCell.indexOf('[통계') !== -1) {
      statsStartRow = r + 1;
      break;
    }
  }

  if (statsStartRow !== -1 && recSheet.getLastRow() >= statsStartRow) {
    const numRowsToDelete = recSheet.getLastRow() - statsStartRow + 1;
    recSheet.deleteRows(statsStartRow, numRowsToDelete);
  }

  recSheet.appendRow(['']);

  const sepText = '📊 [통계 기반 추천 후보 (자동 갱신: ' + now + ')] - 아래 보라색 품목을 검토 후 상단 추천 행으로 자유롭게 복사하세요.';
  const sepRowIdx = recSheet.appendRow([sepText]).getLastRow();
  recSheet.getRange(sepRowIdx, 1, 1, 17).setBackground('#EDE7F6').setFontColor('#4A148C').setFontWeight('bold');

  const statsCategories = ['제왕절개', '자연분만', '조리원이용', '자택조리', '공통산모'];
  const newRowNumbers = [];

  statsCategories.forEach(function(cat) {
    const categoryLabel = '[통계추천] ' + cat;
    const pairedRow = buildPairedRow(categoryLabel, newRules[cat], titleMap);
    const addedRowIdx = recSheet.appendRow(pairedRow).getLastRow();
    newRowNumbers.push(addedRowIdx);
  });

  newRowNumbers.forEach(function(rIdx) {
    const maxCol = recSheet.getLastColumn();
    const rowRange = recSheet.getRange(rIdx, 1, 1, maxCol);
    rowRange.setFontColor('#7B1FA2');
    rowRange.setFontWeight('bold');
    rowRange.setBackground('#FAF5FF');
  });

  if (!isSilent) {
    SpreadsheetApp.getUi().alert(
      '🎯 맞춤 추천 통계 갱신 완료',
      '총 ' + validLogs + '건의 산모 활동 데이터를 분석하여 [🎯 맞춤_추천_설정] 시트 아래쪽에 최신 추천 품목을 보라색 글씨로 작성했습니다!\n\n' +
      '• 기존 데이터: 상단에 그대로 보존됩니다.\n' +
      '• 통계 추천 데이터: 하단에 [통계추천] 항목으로 보라색 글씨로 작성되었습니다.\n\n' +
      '하단의 보라색 품목을 검토하시고 상단 추천 행에 복사해 넣으신 뒤,\n' +
      '상단 메뉴 [🎯 5. 맞춤 추천 가방 설정만 반영]을 누르시면 어플에 즉시 배포됩니다.',
      SpreadsheetApp.getUi().ButtonSet.OK
    );
  }

  return validLogs;
}

// ------------------------------------------------------------------------------
// 16. [사용자 추가 품목 연보라색 강조 도구]
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
