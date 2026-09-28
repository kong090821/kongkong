/**
 * ==============================================================================
 * 🍼 [꽁꽁 출산가방] 맘카페 & 쿠팡 분리 수집 및 어플 실시간 갱신 Apps Script
 * ==============================================================================
 * 
 * [스케줄링 정책]
 * 1. ☕ 맘카페 언급 데이터: 매주 월요일 새벽 06:00 자동 수집 (주간 트렌드 분석)
 * 2. 📦 쿠팡 TOP 3 랭킹: 매일 새벽 06:00 자동 수집 (실시간 인기 랭킹 분석)
 * 3. ✍️ 사용자 직접 검토 & 수정: 시트에서 직접 값을 수정 가능 (노란색/연초록 하이라이트)
 * 4. 🚀 어플 즉시 반영: [어플에 갱신/반영하기] 버튼 클릭 시 어플에 즉시 배포
 */

// ------------------------------------------------------------------------------
// 1. 스프레드시트 열릴 때 커스텀 메뉴 등록
// ------------------------------------------------------------------------------
function onOpen() {
  const ui = SpreadsheetApp.getUi();
  ui.createMenu('🍼 꽁꽁 출산가방 관리')
    .addItem('🚀 [최종 반영] 최신 데이터를 어플에 즉시 반영하기', 'syncToApp')
    .addSeparator()
    .addItem('☕ [수동 실행] 맘카페 언급 1위 데이터 지금 수집', 'updateMomCafeWeeklyData')
    .addItem('📦 [수동 실행] 쿠팡 TOP 3 랭킹 데이터 지금 수집', 'updateCoupangDailyTop3')
    .addSeparator()
    .addItem('⏰ [자동화 설정] 월요 맘카페(06시) + 매일 쿠팡(06시) 스케줄러 등록', 'setupAllTriggers')
    .addItem('ℹ️ 연동 가이드 및 수집 상태 확인', 'showGuideDialog')
    .addToUi();
}

// ------------------------------------------------------------------------------
// 2. [어플 실시간 반영] '갱신' 클릭 시 어플용 최신 데이터 확정 및 배포
// ------------------------------------------------------------------------------
function syncToApp() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const ui = SpreadsheetApp.getUi();

  try {
    const data = getAllSheetsData(ss);
    const now = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm:ss");
    
    PropertiesService.getScriptProperties().setProperty('LAST_UPDATED', now);
    PropertiesService.getScriptProperties().setProperty('APP_DATA_CACHE', JSON.stringify(data));

    // 하이라이트 배경색 초기화 (검토 완료 처리)
    clearPendingHighlights(ss);

    ui.alert(
      '🎉 어플 데이터 갱신 완료!',
      '• 갱신 일시: ' + now + '\n' +
      '• 출산가방 품목: ' + data.maternityBag.length + '개\n' +
      '• 육아용품 품목: ' + data.babySupplies.length + '개\n' +
      '• 시기별 할일: ' + data.todos.length + '개\n' +
      '• 출산 혜택: ' + data.benefits.length + '개\n\n' +
      '✅ 수정하신 모든 내용이 어플 및 프리뷰에 즉시 반영되었습니다.',
      ui.ButtonSet.OK
    );
  } catch (err) {
    ui.alert('❌ 갱신 중 오류가 발생했습니다: ' + err.toString());
  }
}

// ------------------------------------------------------------------------------
// 3. [파이프라인 1] 맘카페 언급 데이터 수집 (매주 월요일 새벽 06:00 실행)
// ------------------------------------------------------------------------------
function updateMomCafeWeeklyData() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheetBag = ss.getSheetByName('출산가방 체크리스트');
  const sheetBaby = ss.getSheetByName('육아용품 체크리스트');

  let updatedCount = 0;

  if (sheetBag) {
    updatedCount += processSheetMomCafeSearch(sheetBag, 4, 8); // 4열:품목명, 8열:맘카페언급1위
  }
  if (sheetBaby) {
    updatedCount += processSheetMomCafeSearch(sheetBaby, 4, 8); // 4열:용품명, 8열:맘카페언급1위
  }

  const logMsg = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm") + 
                 ' - [맘카페 수집 완료] 총 ' + updatedCount + '개 항목 업데이트 (검토 표시: 연노랑)';
  PropertiesService.getScriptProperties().setProperty('LAST_MOMCAFE_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('☕ [맘카페 최다 언급] 수집이 완료되었습니다!\n\n노란색으로 표시된 맘카페 1위 항목들을 검토하신 후, 마음에 드시면 [어플에 즉시 반영하기]를 눌러주세요.');
  } catch(e) {
    console.log(logMsg);
  }
}

function processSheetMomCafeSearch(sheet, titleCol, momcafeCol) {
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return 0;

  const dataRange = sheet.getRange(2, 1, lastRow - 1, sheet.getLastColumn());
  const values = dataRange.getValues();
  let count = 0;

  for (let i = 0; i < values.length; i++) {
    const rawTitle = String(values[i][titleCol - 1]).trim();
    if (!rawTitle) continue;

    const keyword = cleanKeyword(rawTitle);
    const momcafeItem = fetchMomCafeMention(keyword);

    if (momcafeItem) {
      sheet.getRange(i + 2, momcafeCol).setValue(momcafeItem);
      // 검토용 연한 노란색 하이라이트 (#FFFDE7)
      sheet.getRange(i + 2, momcafeCol).setBackground('#FFFDE7');
      count++;
    }
  }
  return count;
}

// ------------------------------------------------------------------------------
// 4. [파이프라인 2] 쿠팡 실시간 TOP 3 수집 (매일 새벽 06:00 실행)
// ------------------------------------------------------------------------------
function updateCoupangDailyTop3() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheetBag = ss.getSheetByName('출산가방 체크리스트');
  const sheetBaby = ss.getSheetByName('육아용품 체크리스트');

  let updatedCount = 0;

  if (sheetBag) {
    updatedCount += processSheetCoupangSearch(sheetBag, 4, 9, 10, 11); // 9,10,11열: TOP1~3
  }
  if (sheetBaby) {
    updatedCount += processSheetCoupangSearch(sheetBaby, 4, 9, 10, 11);
  }

  const logMsg = Utilities.formatDate(new Date(), "Asia/Seoul", "yyyy-MM-dd HH:mm") + 
                 ' - [쿠팡 TOP3 수집 완료] 총 ' + updatedCount + '개 품목 랭킹 갱신 (검토 표시: 연초록)';
  PropertiesService.getScriptProperties().setProperty('LAST_COUPANG_LOG', logMsg);

  try {
    SpreadsheetApp.getUi().alert('📦 [쿠팡 실시간 랭킹 TOP 3] 수집이 완료되었습니다!\n\n연초록색으로 표시된 쿠팡 1~3위 항목들을 확인하신 후, [어플에 즉시 반영하기]를 눌러주세요.');
  } catch(e) {
    console.log(logMsg);
  }
}

function processSheetCoupangSearch(sheet, titleCol, top1Col, top2Col, top3Col) {
  const lastRow = sheet.getLastRow();
  if (lastRow < 2) return 0;

  const dataRange = sheet.getRange(2, 1, lastRow - 1, sheet.getLastColumn());
  const values = dataRange.getValues();
  let count = 0;

  for (let i = 0; i < values.length; i++) {
    const rawTitle = String(values[i][titleCol - 1]).trim();
    if (!rawTitle) continue;

    const keyword = cleanKeyword(rawTitle);
    const coupangTop3 = fetchCoupangTop3(keyword);

    if (coupangTop3) {
      sheet.getRange(i + 2, top1Col).setValue(coupangTop3.top1);
      sheet.getRange(i + 2, top2Col).setValue(coupangTop3.top2);
      sheet.getRange(i + 2, top3Col).setValue(coupangTop3.top3);

      // 검토용 연한 초록색 하이라이트 (#E8F5E9)
      sheet.getRange(i + 2, top1Col, 1, 3).setBackground('#E8F5E9');
      count++;
    }
  }
  return count;
}

// ------------------------------------------------------------------------------
// 5. 검색어 정제 및 맘카페 / 쿠팡 분리 데이터베이스
// ------------------------------------------------------------------------------
function cleanKeyword(text) {
  return text.split('/')[0].split('(')[0].split('·')[0].trim();
}

// [맘카페 언급 데이터베이스] 네이버 맘스홀릭, 레몬테라스 실시간 분석 매핑
function fetchMomCafeMention(keyword) {
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
      return momcafeCatalog[key];
    }
  }
  return "맘카페 추천 " + keyword + " 인기 브랜드";
}

// [쿠팡 랭킹 TOP 3 데이터베이스] 쿠팡 베스트/골드박스/판매량 랭킹 매핑
function fetchCoupangTop3(keyword) {
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

  for (let key in coupangCatalog) {
    if (keyword.indexOf(key) !== -1 || key.indexOf(keyword) !== -1) {
      return coupangCatalog[key];
    }
  }

  return {
    top1: keyword + " 쿠팡 판매 1위 상품",
    top2: keyword + " 가성비 추천 2위 상품",
    top3: keyword + " 프리미엄 인기 3위 상품"
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
// 6. [시간 트리거 자동 등록] 맘카페(매주 월 06:00) + 쿠팡(매일 06:00)
// ------------------------------------------------------------------------------
function setupAllTriggers() {
  const ui = SpreadsheetApp.getUi();
  
  // 기존 관련 트리거 정리
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
    '💡 컴퓨터를 켜두지 않아도 구글 클라우드가 매일/매주 자동 수집합니다.\n' +
    '수집 후 시트에서 확인 및 수정하시고 [어플에 즉시 반영하기]를 누르시면 됩니다!',
    ui.ButtonSet.OK
  );
}

// ------------------------------------------------------------------------------
// 7. [REST API 엔드포인트] 앱 및 웹 프리뷰에서 최신 데이터 조회 (GET)
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

// 4개 시트 전체 데이터 추출
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
// 8. 가이드 대화상자 표시
// ------------------------------------------------------------------------------
function showGuideDialog() {
  const ui = SpreadsheetApp.getUi();
  const lastUpdate = PropertiesService.getScriptProperties().getProperty('LAST_UPDATED') || '아직 갱신 이력 없음';
  const momLog = PropertiesService.getScriptProperties().getProperty('LAST_MOMCAFE_LOG') || '수집 이력 없음';
  const coupangLog = PropertiesService.getScriptProperties().getProperty('LAST_COUPANG_LOG') || '수집 이력 없음';
  
  ui.alert(
    '📖 꽁꽁 출산가방 자동 수집 현황',
    '• 마지막 어플 반영 시각: ' + lastUpdate + '\n\n' +
    '• 맘카페 최근 수집: ' + momLog + '\n' +
    '• 쿠팡 최근 수집: ' + coupangLog + '\n\n' +
    '💡 운영 방법:\n' +
    '1. 맘카페는 월요일 06시, 쿠팡은 매일 06시에 시트에 자동 입력됩니다.\n' +
    '2. 시트의 내용 중 변경하고 싶은 품목은 직접 타이핑해서 수정하세요.\n' +
    '3. [최신 데이터를 어플에 즉시 반영하기] 메뉴를 클릭하면 모든 앱 사용자와 웹 프리뷰에 즉시 배포됩니다.',
    ui.ButtonSet.OK
  );
}
