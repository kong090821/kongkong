package com.example.maternitybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TodoItem(
    val id: String,
    val title: String,
    val tip: String,
    val roleTag: String, // #아빠, #엄마, #부부
    val category: String // "1. 출산 전 (준비기)", "2. 병원 (입원 및 분만기)", "3. 조리원 (회복 및 행정 처리기)", "4. 가정 (집 복귀 및 초기 육아기)"
)

object TodoRepository {

    private val defaultTodoItems = listOf(
        // 1번 탭: 1. 출산 전 (준비기)
        TodoItem("todo_b_1", "집안 전체 대청소 실시", "청소기로 바닥 및 침대 먼지 제거", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_2", "필요없는 가전가구 숨기기", "동선 확보 및 아기 안전 대비", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_3", "커텐 먼지 털기 및 세탁", "아기 호흡기 보호를 위한 사전 청소", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_4", "천장 및 벽 먼지 제거", "상부 구역 먼지 제거", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_5", "각종 가구위 쌓인 먼지 제거", "손이 닿지 않는 상단 먼지 닦기", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_6", "창문 및 창틀 청소", "창문, 베란다문, 현관중문 및 틀 청소", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_7", "바닥 스팀 청소 및 매트 깔기", "위생 소독 및 층간소음/안전 대비", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_8", "자동차 실내외 세차", "내부 스팀 손세차 실시", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_9", "소파 및 침대 청소", "매트리스/소파 딥클리닝", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_10", "유아차 세탁업체 예약", "출산 전 미리 세탁 완료", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_11", "화장실 청소", "유리, 변기, 줄눈, 세면대, 욕조 청소", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_12", "아기비데 설치", "세면대 수전 교체 및 아기비데 설치", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_13", "샤워기 필터 교체", "집안 전체 필터 새것으로 교체", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_14", "워시타워, 식세기 통살균세척", "내부 세척, 세제통 및 오수필터 세척", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_15", "아기 용품 소독 세척", "가습기, 분유 용품 등 사전 소독", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_16", "에어컨 필터 청소", "먼지 제거 및 곰팡이 방지", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_17", "아기 옷, 턱받이 선세탁", "아기 전용 세제로 세탁 후 지퍼백 보관", "#부부", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_18", "아기 손수건 선세탁", "거즈/엠보 구분하여 세탁 및 건조", "#부부", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_19", "실내 온도 세팅", "22~24°C 유지 환경 조성", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_20", "실내 습도 세팅", "40~60% 유지 (가습기 점검)", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_21", "트립트랩 뉴본 설치", "식탁 옆 사전 세팅", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_22", "홈캠 설치", "카메라 각도 및 Wi-Fi 연결 점검", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_23", "맘마존 설치", "유팡, 젖병 열탕, 분유포트 세척 및 배치", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_24", "출산 가방 최종 패킹", "병원/조리원 서류 및 산모/아기 용품 챙기기", "#엄마", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_25", "정부지원 산후도우미 바우처 신청", "출산 예정일 40일 전부터 보건소/복지로 신청", "#부부", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_26", "분만 병원 동선 및 야간 주차 파악", "야간/공휴일 응급실 진입 동선 미리 숙지", "#아빠", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_27", "비상 연락망 공유", "양가, 병원, 조리원, 도우미 업체 연락처 저장", "#부부", "1. 출산 전 (준비기)"),
        TodoItem("todo_b_28", "산모 젤네일 제거 및 왁싱", "수술 모니터링용 손발톱 정돈", "#엄마", "1. 출산 전 (준비기)"),

        // 2번 탭: 2. 병원 (입원 및 분만기)
        TodoItem("todo_h_1", "아기 이름 후보 결정", "출생신고를 위한 이름 미리 결정", "#부부", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_2", "양가 부모님 출산 소식 전달", "산모/아기 건강 상태 및 일정 공유", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_3", "산후조리원 입소 전화", "출산 순서 및 유형에 따른 입소 일정 확정", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_4", "산후도우미 업체 전화", "조리원 퇴소일 전달 및 시작일 조율", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_5", "압박스타킹 착용", "산모 다리 부기 방지 및 혈전 예방", "#엄마", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_6", "항생제 발진 수시 체크", "비판텐 도포 등 피부 상태 확인", "#엄마", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_7", "모유 차 요청", "병원 수유 지원 서비스 확인", "#엄마", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_8", "신생아실 영양제 전달", "준비한 유산균, 비타민 D 전달", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_9", "출생증명서 발급", "원본 2~3부 수령 (병원 퇴원 전)", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_10", "병원비 내역서 출력", "회사 제출 및 연말정산용 서류 확보", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_11", "보험 청구용 서류 수령", "진료비 영수증 및 세부 내역서 발급", "#아빠", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_12", "산후 검진 예약", "실밥 제거 및 산모 건강 체크 일정 확정", "#엄마", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_13", "신생아 예방접종 확인", "BCG 경피용/피내용 등 접종 안내 확인", "#부부", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_14", "병원 내 무료 바우처 신청", "병원 비치 바우처 직접 신청", "#엄마", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_15", "수술부위/회음부 케어 연고 확인", "수간호사에게 소독 및 연고 사용법 숙지", "#엄마", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_16", "선천성 대사이상 검사 동의서 작성", "병원 제출용 기본/확장 검사 선택", "#부부", "2. 병원 (입원 및 분만기)"),
        TodoItem("todo_h_17", "병원비/수술비 결제", "지역화폐 가능 여부 확인 및 결제", "#아빠", "2. 병원 (입원 및 분만기)"),

        // 3번 탭: 3. 조리원 (회복 및 행정 처리기)
        TodoItem("todo_c_1", "출생 신고 수", "정부24 온라인 또는 주민센터 방문", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_2", "첫만남이용권 신청", "200만 원 포인트 (출생신고 시 통합 신청)", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_3", "아동수당 신청", "매월 지급 신청", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_4", "부모급여 신청", "매월 지급 신청", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_5", "산후조리비 지원 신청", "지역별 지원금(수원페이 등) 신청", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_6", "전기요금 감면 신청", "국번 없이 123 전화하여 30% 할인 신청", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_7", "아파트 관리소 고지", "전기료 할인 신청 내역 관리소 전달", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_8", "태아보험 명의 변경", "아기 정보(이름/주민번호) 보험사 등록", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_9", "자동차 보험 자녀 특약 신청", "자녀 등록 시 보험료 할인 혜택 확인", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_10", "건강보험 피부양자 확인", "아기 피부양자 등록 여부 체크", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_11", "어린이집 입소 대기 등록", "아이사랑 앱 설치 후 대기 신청", "#부부", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_12", "영유아 건강검진(1차) 예약", "생후 14~35일 사이 일정 똑닥/전화 예약", "#부부", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_13", "아기 황달 수치 모니터링", "신생아실 수시 확인 요청 (10 이하 유지)", "#엄마", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_14", "배꼽 탈락 및 소독 확인", "조리원 간호사에게 배꼽 상태 확인", "#엄마", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_15", "산모 개인 위생 관리", "양말 착용 및 수유 전 손 소독 철저", "#엄마", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_16", "조리원 퇴소 시간 확정", "퇴소일 오전 시간 최종 확인", "#엄마", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_17", "차량 내부 세차", "아기 탑승 전 차량 실내 청결 확보", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_18", "바구니 카시트 설치 점검", "차량 내 안전한 카시트 장착 확인", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_19", "아기 침대 세팅", "누빔 패드 및 침구류 최종 정돈", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_20", "기저귀 갈이대 소모품 보충", "기저귀, 물티슈, 소독제 등 배치", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_21", "홈캠 설치 및 작동 테스트", "아기 방 모니터링 환경 구축", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_22", "분유 포트 식초 소독", "포트 내부 세척 및 위생 관리", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_23", "분유물 끓이기", "퇴소 1시간 전 물 끓여서 식혀놓기", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_24", "젖병 및 젖꼭지 최종 소독", "아기가 바로 사용할 젖병 소독", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_25", "KTX/다자녀 할인 신청", "해당 시 코레일 앱에 다자녀 등록", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_26", "산후도우미 관리사님 사전 연락", "시작 2~3일 전 특이사항 및 준비물 조율", "#부부", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_27", "탯줄 소독용 알코올 솜 구비", "퇴소 후 사용할 멸균 알코올 솜 준비", "#아빠", "3. 조리원 (회복 및 행정 처리기)"),
        TodoItem("todo_c_28", "조리원 퇴소 교육 이수", "목욕법, 분유량, 아기 변 상태 확인법 이수", "#부부", "3. 조리원 (회복 및 행정 처리기)"),

        // 4번 탭: 4. 가정 (집 복귀 및 초기 육아기)
        TodoItem("todo_home_1", "아기 도장 제작", "탯줄 탈락 후 기념 도장 제작", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_2", "아기 첫 통장 개설", "도장, 등본 지참하여 은행 방문", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_3", "금융 바우처 등록", "은행별 아기 행복 바우처 혜택 신청", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_4", "BCG(결핵) 예방접종 실시", "예약한 소아과/보건소 방문 접종", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_5", "B형 간염 2차 예방접종", "생후 1개월 차 소아과 방문 접종", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_6", "접종열 발생 시 대응", "38도 이상 시 해열제 복용 및 열관리", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_7", "태열 발생 시 온도 하향", "실내 온도 20~22°C로 즉시 조절", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_8", "두피 각질 케어", "오일로 30분 불린 후 전용 샴푸 세정", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_9", "발달 놀이 제공", "초점책, 모빌, 아기체육관 시기별 활용", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_10", "산모 산후 검진 (4~6주)", "오로 상태 및 자궁/수술부위 초음파 확인", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_11", "영유아 건강검진 1차 실시", "지정 소아과 방문하여 신체 계측 진행", "#부부", "4. 가정 (집 복귀 및 초기 육아기)"),
        TodoItem("todo_home_12", "산후도우미 관리사님 맞이", "집안 물품 위치 및 원하는 케어방식 공유", "#부부", "4. 가정 (집 복귀 및 초기 육아기)")
    )

    private val _todoItems = MutableStateFlow(defaultTodoItems)
    val todoItems: StateFlow<List<TodoItem>> = _todoItems.asStateFlow()

    private val initialSavedIds = setOf("todo_b_1", "todo_b_2", "todo_b_17", "todo_b_18", "todo_b_23", "todo_b_24", "todo_b_25")
    private val _savedTodoIds = MutableStateFlow<Set<String>>(initialSavedIds)
    val savedTodoIds: StateFlow<Set<String>> = _savedTodoIds.asStateFlow()

    private val _completedTodoIds = MutableStateFlow<Set<String>>(setOf("todo_b_17", "todo_b_18"))
    val completedTodoIds: StateFlow<Set<String>> = _completedTodoIds.asStateFlow()

    fun setSavedTodoIds(ids: Set<String>) {
        _savedTodoIds.value = ids
    }

    fun toggleTodoSaved(id: String) {
        _savedTodoIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun isTodoSaved(id: String): Boolean {
        return _savedTodoIds.value.contains(id)
    }

    fun setCompletedTodoIds(ids: Set<String>) {
        _completedTodoIds.value = ids
    }

    fun toggleTodoCompleted(id: String) {
        _completedTodoIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun deleteTodoItem(id: String) {
        _todoItems.update { list -> list.filterNot { it.id == id } }
        _savedTodoIds.update { set -> set - id }
        _completedTodoIds.update { set -> set - id }
    }

    fun addTodoItem(title: String, tip: String, roleTag: String, category: String) {
        val newId = "custom_todo_${System.currentTimeMillis()}"
        val newItem = TodoItem(
            id = newId,
            title = title,
            tip = tip,
            roleTag = roleTag,
            category = category
        )
        _todoItems.update { it + newItem }
    }
}
