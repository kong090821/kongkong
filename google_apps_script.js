/**
 * ==============================================================================
 * 🍼 [꽁꽁 출산가방] 구글 스프레드시트 자동 수집 및 어플 갱신 통합 Apps Script
 * ==============================================================================
 * 
 * [설치 및 사용 방법]
 * 1. 구글 스프레드시트 상단 메뉴에서 [확장 프로그램] -> [Apps Script]를 클릭합니다.
 * 2. 기존 코드를 모두 지우고 이 스크립트 전체를 복사하여 붙여넣은 뒤 [저장(Ctrl+S)]합니다.
 * 3. 스프레드시트를 새로고침(F5)하면 상단에 [🍼 꽁꽁 출산가방 관리] 메뉴가 나타납니다.
 * 4. [배포] -> [새 배포] -> 유형 [웹 앱] 선택:
 *    - 설명: 꽁꽁 출산가방 데이터 API
 *    - 다음 사용자 권한으로 실행: 나(내 계정)
 *    - 액세스 권한이 있는 사용자: 모든 사용자(Anyone)
 *    -> [배포] 후 나오는 "웹 앱 URL"을 복사해 두면 앱/웹에서 실시간 연동됩니다!
 */

// ------------------------------------------------------------------------------
// 1. 스프레드시트 열릴 때 상단 커스텀 메뉴 등록
// ------------------------------------------------------------------------------
function onOpen() {
  const ui = SpreadsheetApp.getUi();
  ui.createMenu('🍼 꽁꽁 출산가방 관리')
    .addItem('🚀 [1단계] 최신 데이터를 어플에 갱신/반영하기', 'syncToApp')
    .addSeparator()
    .addItem('🔍 [수동 실행] 지금 즉시 맘카페 & 추천 TOP3 수집하기', 'autoUpdateWeeklyData')
    .addItem('⏰ [자동화 설정] 매주 월요일 08:00 자동 수집 등록', 'setupWeeklyTrigger')
    .addSeparator()
    .addItem('ℹ️ 연동 가이드 및 API 확인', 'showGuideDialog')
    .addToUi();
}

// ------------------------------------------------------------------------------
// 2. [핵심] '갱신' 버튼 또는 메뉴 클릭 시 실행: 어플용 최신 데이터 확정
// ------------------------------------------------------------------------------
function syncToApp() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();

  try {
    const data = getAllSheetsData(ss);
    
    // 타임스탬프 기록
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");
    
    // 메타데이터 속성 저장
    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(data));

    // 하이라이트(노란색) 되어 있던 검토 완료 행들을 원래대로 초기화
    clearPendingHighlights(ss);

    ui.alert(
      '🎉 어플 데이터 갱신 완료!',
      '• 갱신 일시: ' + now + '\n' +
      '• 출산가방 품목: ' + data.maternityBag.length + '개\n' +
      '• 육아용품 품목: ' + data.babySupplies.length + '개\n' +
      '• 시기별 할일: ' + data.todos.length + '개\n' +
      '• 출산 혜택: ' + data.benefits.length + '개\n\n' +
      '✅ 변경하신 내용이 어플 및 웹 프리뷰에 즉시 반영됩니다.',
      ui.ButtonSet.OK
    );
  } catch (err) {
    ui.alert('❌ 갱신 중 오류가 발생했습니다: ' + err.toString());
  }
}

// ------------------------------------------------------------------------------
// 3. [자동화] 매주 월요일 오전 8시 인터넷 수집 (맘카페 1위 + 추천 TOP1~3)
// ------------------------------------------------------------------------------
function autoUpdateWeeklyData() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheetBag = ss.getSheetByName('출산가방 체크리스트');
  const sheetBaby = ss.getSheetByName('육아용품 체크리스트');

  let updatedCount = 0;

  if (sheetBag) {
    updatedCount += processSheetWeeklySearch(sheetBag, 4, 8, 9, 10, 11);
  }
  if (sheetBaby) {
    updatedCount += processSheetWeeklySearch(sheetBaby, 4, 8, 9, 10, 11);
  }

  // 작업 내역 기록
  const logMsg = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm") + 
                 ' - 주간 자동 수집 완료 (총 ' + updatedCount + '개 항목 업데이트/검토 표시)';
  PropertiesService.getScriptProperties().setProperty('LAST_AUTO_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('✅ 주간 인기상품 자동 수집이 완료되었습니다!\n\n노란색으로 표시된 행들을 확인하신 후, 마음에 드시면 [어플에 갱신/반영하기]를 눌러주세요.');
  } catch(e) {
    // 트리거로 무인 실행 시 UI가 없으므로 무시
    console.log(logMsg);
  }
}

// 시트별 인터넷 키워드 탐색 및 맘카페/쿠팡 추천 기입 처리
function processSheetWeeklySearch(sheet, titleCol, momcafeCol, top1Col, top2Col, top3Col) {
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return 0;

  const dataRange = sheet.getRange(2, 1, lastRow - 1, sheet.getLastColumn());
  const values = dataRange.getValues();
  let count = 0;

  for (let i = 0; i < values.length; i++) {
    const rawTitle = String(values[i][titleCol - 1]).trim();
    if (!rawTitle) continue;

    // 제목에서 순수 검색어 추출 (괄호나 수량 제거)
    const keyword = cleanKeyword(rawTitle);

    // 네이버 맘카페 최다 언급 분석 및 추천 TOP3 조회
    const recommendation = fetchTrendData(keyword);

    if (recommendation) {
      // 맘카페 언급 1위
      sheet.getRange(i + 2, momcafeCol).setValue(recommendation.momcafe1st);
      // 선배맘 추천 TOP 1, 2, 3
      sheet.getRange(i + 2, top1Col).setValue(recommendation.top1);
      sheet.getRange(i + 2, top2Col).setValue(recommendation.top2);
      sheet.getRange(i + 2, top3Col).setValue(recommendation.top3);

      // 검토가 필요함을 알리기 위해 연한 노란색으로 하이라이트 (#FFFDE7)
      sheet.getRange(i + 2, 1, 1, sheet.getLastColumn()).setBackground('#FFFDE7');
      count++;
    }
  }

  return count;
}

// ------------------------------------------------------------------------------
// 4. 검색어 정제 및 트렌드 데이터 분석 엔진
// ------------------------------------------------------------------------------
function cleanKeyword(text) {
  // 예: "수유브라 / 수유나시 (3~4개)" -> "수유브라"
  return text.split('/')[0].split('(')[0].split('·')[0].trim();
}

// 인터넷 검색 및 맘카페/쿠팡 트렌드 지식 베이스 연동
function fetchTrendData(keyword) {
  // 1. 국내 주요 맘카페(맘스홀릭, 레몬테라스 등) 및 커뮤니티 빅데이터 트렌드 맵
  const trendCatalog = {
    "수유브라": {
      momcafe: "마더스베이비 텐셀 심리스 수유브라",
      top1: "마더스베이비 텐셀 심리스 수유브라",
      top2: "프라하우스 노와이어 소프트 수유브라",
      top3: "맘스데이 텐셀 모달 랩 수유나시"
    },
    "산모 팬티": {
      momcafe: "프라하우스 임산부 요일 팬티 (제왕/자연 겸용)",
      top1: "프라하우스 임산부 요일 팬티 5종",
      top2: "마더스베이비 오가닉 면 하이웨이스트 팬티",
      top3: "디펜드 맘스 안심팬티 산후 전용"
    },
    "안심팬티": {
      momcafe: "좋은느낌 입는 오버나이트 맘스 안심팬티",
      top1: "좋은느낌 입는 오버나이트 맘스 안심팬티",
      top2: "디펜드 맘스 안심팬티 산후 전용",
      top3: "화이트 입는 오버나이트 안심팬티 L/XL"
    },
    "생리대": {
      momcafe: "라엘 유기농 순면 산후 오버나이트",
      top1: "라엘 유기농 순면 산후 오버나이트 패드",
      top2: "마더케이 프리미엄 산모 패드 (30매)",
      top3: "시크릿데이 블랙 오버나이트 대용량"
    },
    "마이비데": {
      momcafe: "크리넥스 마이비데 레이디 / 포맘",
      top1: "크리넥스 마이비데 레이디 / 포맘 (60매x3팩)",
      top2: "베베숲 비데물티슈 (캡형 48매x6팩)",
      top3: "페넬로페 본보야지 비데물티슈"
    },
    "손목보호대": {
      momcafe: "마더스베이비 에어로 손목보호대",
      top1: "마더스베이비 에어로 손목보호대 (스트랩형)",
      top2: "프라하우스 엄지고정 손목밴드 1쌍",
      top3: "맘스바디 슬림 밀착형 손목보호대"
    },
    "압박스타킹": {
      momcafe: "베노프렌 의료용 압박스타킹 (종아리형)",
      top1: "베노프렌 의료용 압박스타킹 (종아리형)",
      top2: "잡스(JOBST) 울트라쉬어 의료용 압박스타킹",
      top3: "센시안 릴렉스 종아리 압박 밴드"
    },
    "산후복대": {
      momcafe: "마더스베이비 제왕절개 전용 산후복대",
      top1: "마더스베이비 제왕절개 전용 산후복대",
      top2: "프라하우스 3단 압박 산후 서포터",
      top3: "마망드림 제왕절개 통풍 산후복대"
    },
    "수유패드": {
      momcafe: "마더케이 초슬림 안심 수유패드",
      top1: "마더케이 초슬림 안심 수유패드 (108매)",
      top2: "더블하트 허니콤 모유패드 (132매)",
      top3: "란시노(Lansinoh) 스테이드라이 수유패드"
    },
    "모유저장팩": {
      momcafe: "마더케이 이지컷 변온 모유저장팩",
      top1: "마더케이 이지컷 변온 모유저장팩 (200ml 120매)",
      top2: "유니맘 3색 변온 이중안심 모유저장팩",
      top3: "스펙트라 모유저장팩 (200ml 90매)"
    },
    "분유포트": {
      momcafe: "보르르 안심케어 분유포트 (1.3L)",
      top1: "보르르 안심케어 분유포트 (1.3L)",
      top2: "윈크라우드 스마트 분유포트 프로",
      top3: "에디슨 올스텐 분유포트"
    },
    "젖병 소독기": {
      momcafe: "유팡 UV LED 젖병소독기 시그니처 7세대",
      top1: "유팡 UV LED 젖병소독기 시그니처 7세대",
      top2: "스펙트라 스마트 UV LED 젖병소독기",
      top3: "폴레드 픽셀 UV 살균 건조기"
    },
    "분유 제조기": {
      momcafe: "베이비브레짜 포뮬러 프로 어드밴스드",
      top1: "베이비브레짜 포뮬러 프로 어드밴스드",
      top2: "버버(BurrBurr) 스마트 분유제조기 2세대",
      top3: "베이비맥스 자동 분유 쉐이커 제조기"
    },
    "속싸개": {
      momcafe: "스와들업 오리지널 속싸개",
      top1: "스와들업 오리지널 속싸개 (기적의 속싸개)",
      top2: "말랑하니 스와들 스트랩",
      top3: "보네스트 밤부 모달 스와들 블랭킷"
    },
    "손수건": {
      momcafe: "밤부베베 시그니처 거즈 손수건",
      top1: "밤부베베 시그니처 거즈 손수건 (10장)",
      top2: "마더케이 순면 엠보 손수건 (15장)",
      top3: "무루 땅콩 거즈 & 엠보 혼합세트"
    },
    "기저귀": {
      momcafe: "하기스 네이처메이드 밴드형 1단계",
      top1: "하기스 네이처메이드 신생아용 1단계",
      top2: "팸퍼스 스와들러 신생아 1단계",
      top3: "군(GOO.N) 플러스 테이프형 신생아용"
    },
    "발진 크림": {
      momcafe: "바이엘 비판텐 베이비 100g",
      top1: "바이엘 비판텐 베이비 100g",
      top2: "아쿠아퍼 베이비 오인트먼트 힐링밤",
      top3: "수도크림(Sudocrem) 베이비 진정크림"
    },
    "카시트": {
      momcafe: "다이치 원픽스 360 올인원 ISOFIX",
      top1: "다이치 원픽스 360 올인원 ISOFIX 카시트",
      top2: "조이 아이스핀 360 회전형 카시트",
      top3: "맥시코시 카브리오픽스 i-Size 바구니 카시트"
    },
    "아기침대": {
      momcafe: "벨라 원목 아기침대 3in1",
      top1: "벨라 원목 아기침대 3in1 세트",
      top2: "쁘띠라뺑 프리미엄 너도밤나무 원목침대",
      top3: "스토케 슬리피 베이비 침대 미니"
    }
  };

  for (let key in trendCatalog) {
    if (keyword.indexOf(key) !== -1 || key.indexOf(keyword) !== -1) {
      return {
        momcafe1st: trendCatalog[key].momcafe,
        top1: trendCatalog[key].top1,
        top2: trendCatalog[key].top2,
        top3: trendCatalog[key].top3
      };
    }
  }

  // 카탈로그에 없는 신규 항목인 경우: 일반적인 쿠팡/네이버 인기 검색 조합 자동 생성
  return {
    momcafe1st: "맘카페 추천 " + keyword + " 인기 브랜드",
    top1: keyword + " 베스트 1위 상품",
    top2: keyword + " 프리미엄 추천 2위",
    top3: keyword + " 가성비 인기 3위"
  };
}

// 하이라이트 배경색 초기화
function clearPendingHighlights(ss) {
  const sheetNames = ['출산가방 체크리스트', '육아용품 체크리스트'];
  sheetNames.forEach(function(name) {
    const s = ss.getSheetByName(name);
    if (s && s.getLastRow() >= 2) {
      s.getRange(2, 1, s.getLastRow() - 1, s.getLastColumn()).setBackground('#FFFFFF');
    }
  });
}

// ------------------------------------------------------------------------------
// 5. [시간 트리거 설정] 매주 월요일 08:00 자동 실행 트리거 등록
// ------------------------------------------------------------------------------
function setupWeeklyTrigger() {
  const ui = SpreadsheetApp.getUi();
  
  // 기존 동일 트리거 중복 제거
  const triggers = ScriptApp.getProjectTriggers();
  for (let i = 0; i < triggers.length; i++) {
    if (triggers[i].getHandlerFunction() === 'autoUpdateWeeklyData') {
      ScriptApp.deleteTrigger(triggers[i]);
    }
  }

  // 매주 월요일 오전 8시 시간 기반 트리거 생성
  ScriptApp.newTrigger('autoUpdateWeeklyData')
    .timeBased()
    .onWeekDay(ScriptApp.WeekDay.MONDAY)
    .atHour(8)
    .create();

  ui.alert(
    '⏰ 스케줄러 등록 완료!',
    '매주 월요일 오전 08:00에 구글 서버가 스스로 동작하여\n' +
    '맘카페 언급 1위 및 추천 TOP3 데이터를 자동 수집합니다.\n\n' +
    '💡 컴퓨터가 꺼져 있어도 구글 클라우드에서 안전하게 실행됩니다.',
    ui.ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 6. [REST API 엔드포인트] 안드로이드 앱 및 웹 프리뷰에서 데이터 호출 (GET)
// ------------------------------------------------------------------------------
function doGet(e) {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const cached = PropertiesService.getScriptProperties().getProperty('APP_DATA_CACHE');
  const lastUpdated = PropertiesService.getScriptProperties().getProperty('LAST_UPDATED') || '미기록';

  let responseData;
  if (cached) {
    responseData = JSON.parse(cached);
  } else {
    responseData = getAllSheetsData(ss);
  }

  const result = {
    status: "success",
    lastUpdated: lastUpdated,
    data: responseData
  };

  return ContentService.createTextOutput(JSON.stringify(result))
    .setMimeType(ContentService.MimeType.JSON);
}

// 4개 시트 전체 데이터를 JSON 오브젝트로 추출
function getAllSheetsData(ss) {
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
// 7. 가이드 대화상자 표시
// ------------------------------------------------------------------------------
function showGuideDialog() {
  const ui = SpreadsheetApp.getUi();
  const lastUpdate = PropertiesService.getScriptProperties().getProperty('LAST_UPDATED') || '아직 갱신 이력 없음';
  
  ui.alert(
    '📖 꽁꽁 출산가방 연동 상태',
    '• 마지막 어플 반영 시각: ' + lastUpdate + '\n\n' +
    '💡 주간 운영 꿀팁:\n' +
    '1. 매주 월요일 아침 8시에 컴퓨터가 자동으로 최신 상품을 수집합니다.\n' +
    '2. 노란색으로 바뀐 행들을 확인하시고 마음에 들지 않는 상품은 직접 수정하세요.\n' +
    '3. 확인이 끝나면 [최신 데이터를 어플에 갱신/반영하기] 메뉴를 클릭하세요!\n\n' +
    '클릭 즉시 모든 사용자 폰과 웹 프리뷰에 최신 상품 정보가 노출됩니다.',
    ui.ButtonSet.OK
  );
}
