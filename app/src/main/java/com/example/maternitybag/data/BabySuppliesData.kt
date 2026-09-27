package com.example.maternitybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BabySupplyItem(
    val id: String,
    val title: String,
    val periodTags: List<String>, // e.g. listOf("#신생아", "#영아"), listOf("#전체")
    val purchaseTag: String, // "#당근", "#새제품"
    val note: String = "",
    val section: String = "", // Subcategory
    val tabCategory: String = "", // "1. 먹이기 (수유 & 이유식)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)", etc.
    val isCustom: Boolean = false
)

object BabySuppliesRepository {

    // 1번 탭: 먹이기 (수유 & 이유식) (18개 항목)
    private val tab1FeedItems = listOf(
        // 분유 수유 & 세척·소독
        BabySupplyItem("bs_f_1", "분유 제조기", listOf("#신생아", "#영아"), "#당근", "(사용 기간이 짧아 중고 수요/공급 활발)", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_2", "분유 교반기 (분유 쉐이커)", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_3", "분유포트", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_4", "휴대용 분유포트", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_5", "젖병 세척기", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_6", "젖병 소독기", listOf("#전체"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_7", "젖병 건조대 / 집게 / 브러쉬", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_8", "소형 젖병 (150ml 내외)", listOf("#신생아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_9", "중대형 젖병 (240ml 내외)", listOf("#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_10", "신생아용 젖꼭지 (SS 사이즈)", listOf("#신생아"), "#당근", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_11", "노리개 젖꼭지 (쪽쪽이)", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_12", "쪽쪽이 클립 / 보관케이스", listOf("#신생아", "#영아"), "#새제품", "", "분유 수유 & 세척·소독", "1. 먹이기 (수유 & 이유식)"),

        // 수유 보조 & 엄마 케어
        BabySupplyItem("bs_f_13", "수유 시트", listOf("#신생아", "#영아"), "#당근", "", "수유 보조 & 엄마 케어", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_14", "유두 보호 크림", listOf("#신생아"), "#새제품", "", "수유 보조 & 엄마 케어", "1. 먹이기 (수유 & 이유식)"),

        // 이유식 & 성장 식사
        BabySupplyItem("bs_f_15", "유아용 하이체어", listOf("#영아", "#유아"), "#새제품", "(백화점 구매 추천)", "이유식 & 성장 식사", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_16", "이유식 턱받이", listOf("#영아", "#유아"), "#새제품", "(선물/새제품)", "이유식 & 성장 식사", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_17", "이유식 식기", listOf("#영아", "#유아"), "#새제품", "", "이유식 & 성장 식사", "1. 먹이기 (수유 & 이유식)"),
        BabySupplyItem("bs_f_18", "이유식 스푼 / 큐브 용기", listOf("#영아"), "#새제품", "", "이유식 & 성장 식사", "1. 먹이기 (수유 & 이유식)")
    )

    // 2번 탭: 재우기 & 쉬기 (수면 & 공간·놀이) (24개 항목)
    private val tab2SleepItems = listOf(
        // 수면 & 가구·섬유
        BabySupplyItem("bs_s_1", "원목/신생아 아기침대", listOf("#신생아"), "#당근", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_2", "아기침대용 방수 쿨매트", listOf("#신생아", "#영아"), "#새제품", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_3", "침대용 방수패드", listOf("#전체"), "#새제품", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_4", "두상 관리 베개", listOf("#신생아", "#영아"), "#새제품", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_5", "옆잠 방지 베개", listOf("#신생아", "#영아"), "#당근", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_6", "아기 놀이 매트 (폴더매트/바닥매트)", listOf("#전체"), "#새제품", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_7", "암막커튼", listOf("#전체"), "#새제품", "", "수면 & 가구·섬유", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),

        // 수면 보조 & 가전
        BabySupplyItem("bs_s_8", "백색소음기", listOf("#신생아", "#영아"), "#새제품", "", "수면 보조 & 가전", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_9", "베이비 홈캠 + 거치대 + 메모리카드", listOf("#전체"), "#새제품", "", "수면 보조 & 가전", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_10", "가습기", listOf("#전체"), "#새제품", "", "수면 보조 & 가전", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_11", "공기청정기", listOf("#전체"), "#새제품", "", "수면 보조 & 가전", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_s_12", "아이방 온습도계", listOf("#전체"), "#새제품", "", "수면 보조 & 가전", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),

        // 놀이 & 발달 (감각·운동)
        BabySupplyItem("bs_p_1", "신생아 바운서", listOf("#신생아", "#영아"), "#당근", "(스토케 뉴본은 백화점/새제품 추천)", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_2", "타이니/회전 모빌", listOf("#신생아", "#영아"), "#당근", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_3", "병풍 장난감", listOf("#신생아", "#영아"), "#새제품", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_4", "초점책 (흑백, 컬러)", listOf("#신생아", "#영아"), "#당근", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_5", "아기 체육관", listOf("#신생아", "#영아"), "#당근", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_6", "에듀테이블", listOf("#영아", "#유아"), "#당근", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_7", "사운드북", listOf("#전체"), "#새제품", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_8", "춤추는/움직이는 사운드 토이 (꼬꼬맘 등)", listOf("#영아", "#유아"), "#새제품", "(카카오 선물하기 등)", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_9", "고리 장난감 / 실리콘 볼", listOf("#신생아", "#영아"), "#새제품", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_10", "치발기", listOf("#신생아", "#영아"), "#새제품", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_11", "애착인형", listOf("#전체"), "#새제품", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)"),
        BabySupplyItem("bs_p_12", "D-Day 달력", listOf("#신생아", "#영아"), "#새제품", "", "놀이 & 발달 (감각·운동)", "2. 재우기 & 쉬기 (수면 & 공간·놀이)")
    )

    // 3번 탭: 씻기 & 케어 (위생·건강 & 의류) (28개 항목)
    private val tab3CareItems = listOf(
        // 기저귀 교체 & 위생 가구
        BabySupplyItem("bs_c_1", "기저귀 갈이대", listOf("#신생아", "#영아"), "#당근", "", "기저귀 교체 & 위생 가구", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_2", "기저귀 갈이대용 방수매트", listOf("#신생아", "#영아"), "#새제품", "", "기저귀 교체 & 위생 가구", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_3", "기저귀 전용 매직 쓰레기통", listOf("#전체"), "#새제품", "", "기저귀 교체 & 위생 가구", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_4", "이동식 트롤리 (수납장)", listOf("#전체"), "#당근", "", "기저귀 교체 & 위생 가구", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_5", "신생아 기저귀 (NB / 1단계)", listOf("#신생아"), "#새제품", "", "기저귀 교체 & 위생 가구", "3. 씻기 & 케어 (위생·건강 & 의류)"),

        // 목욕 & 세면
        BabySupplyItem("bs_c_6", "아기 목욕 욕조", listOf("#전체"), "#새제품", "(욕조 본체는 새제품, 보조 받침 등은 당근 활용)", "목욕 & 세면", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_7", "아기 세면대 수전 및 필터", listOf("#전체"), "#새제품", "", "목욕 & 세면", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_8", "아기 비데", listOf("#신생아", "#영아"), "#새제품", "", "목욕 & 세면", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_9", "탕온도계", listOf("#신생아", "#영아"), "#새제품", "", "목욕 & 세면", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_10", "신생아 올인원 바스앤샴푸", listOf("#전체"), "#당근", "(작성해주신 목록 반영)", "목욕 & 세면", "3. 씻기 & 케어 (위생·건강 & 의류)"),

        // 건강 & 위생 케어
        BabySupplyItem("bs_c_11", "귀체온계", listOf("#전체"), "#새제품", "(쿠팡/공식몰 새제품 권장)", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_12", "전동 콧물 흡입기", listOf("#전체"), "#새제품", "", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_13", "네일트리머 (전동 손톱갈이) 및 손톱깎이 세트", listOf("#전체"), "#새제품", "", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_14", "아기 면봉", listOf("#전체"), "#당근", "(작성해주신 목록 반영)", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_15", "구강 청결 티슈 / 신생아 멸균 아기거즈", listOf("#신생아", "#영아"), "#새제품", "", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_16", "멸균 알코올 솜 (탯줄 소독용)", listOf("#신생아"), "#새제품", "", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_17", "산후 회복/발진 연고 (비판텐 등)", listOf("#전체"), "#새제품", "(대형약국/해외직구 구매 추천)", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_18", "아기 로션 / 크림", listOf("#전체"), "#새제품", "", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_19", "아기 비타민D / 유산균 (병원/약국 구매)", listOf("#전체"), "#새제품", "", "건강 & 위생 케어", "3. 씻기 & 케어 (위생·건강 & 의류)"),

        // 의류 & 섬유·세탁
        BabySupplyItem("bs_c_20", "배냇저고리", listOf("#신생아"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_21", "바디수트", listOf("#전체"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_22", "속속싸개 / 수면조끼 (스와들/스트랩)", listOf("#신생아", "#영아"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_23", "속싸개", listOf("#신생아"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_24", "손싸개, 발싸개, 아기 모자", listOf("#신생아"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_25", "거즈 / 엠보 손수건, 천기저귀", listOf("#전체"), "#새제품", "(베이비페어 구매 추천)", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_26", "침받이용 턱받이", listOf("#영아"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_27", "아기 옷걸이", listOf("#전체"), "#당근", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)"),
        BabySupplyItem("bs_c_28", "아기 전용 세탁세제 / 1종 주방세제", listOf("#전체"), "#새제품", "", "의류 & 섬유·세탁", "3. 씻기 & 케어 (위생·건강 & 의류)")
    )

    // 4번 탭: 외출 & 이동 (9개 항목)
    private val tab4OutItems = listOf(
        // 유모차 & 이동 수단
        BabySupplyItem("bs_o_1", "디럭스 / 절충형 유모차", listOf("#신생아", "#영아"), "#당근", "(사용 기간이 짧아 당근 거래 활발)", "유모차 & 이동 수단", "4. 외출 & 이동"),
        BabySupplyItem("bs_o_2", "휴대용 유모차", listOf("#영아", "#유아"), "#새제품", "(백화점/공식몰 추천)", "유모차 & 이동 수단", "4. 외출 & 이동"),
        BabySupplyItem("bs_o_3", "유모차 라이너 / 통풍시트", listOf("#전체"), "#새제품", "", "유모차 & 이동 수단", "4. 외출 & 이동"),

        // 카시트
        BabySupplyItem("bs_o_4", "바구니 카시트", listOf("#신생아"), "#당근", "", "카시트", "4. 외출 & 이동"),
        BabySupplyItem("bs_o_5", "회전형/일반 자동차 카시트", listOf("#전체"), "#새제품", "(안전상 이유로 신품 권장)", "카시트", "4. 외출 & 이동"),

        // 아기띠 & 외출 보조
        BabySupplyItem("bs_o_6", "신생아 아기띠", listOf("#신생아", "#영아"), "#당근", "", "아기띠 & 외출 보조", "4. 외출 & 이동"),
        BabySupplyItem("bs_o_7", "올인원/힙시트 아기띠", listOf("#영아", "#유아"), "#새제품", "", "아기띠 & 외출 보조", "4. 외출 & 이동"),
        BabySupplyItem("bs_o_8", "휴대용 손선풍기", listOf("#전체"), "#새제품", "", "아기띠 & 외출 보조", "4. 외출 & 이동"),
        BabySupplyItem("bs_o_9", "기저귀 가방 (백팩/숄더백)", listOf("#전체"), "#새제품", "", "아기띠 & 외출 보조", "4. 외출 & 이동")
    )

    private val _feedItems = MutableStateFlow(tab1FeedItems)
    val feedItems: StateFlow<List<BabySupplyItem>> = _feedItems.asStateFlow()

    private val _sleepItems = MutableStateFlow(tab2SleepItems)
    val sleepItems: StateFlow<List<BabySupplyItem>> = _sleepItems.asStateFlow()

    private val _careItems = MutableStateFlow(tab3CareItems)
    val careItems: StateFlow<List<BabySupplyItem>> = _careItems.asStateFlow()

    private val _outItems = MutableStateFlow(tab4OutItems)
    val outItems: StateFlow<List<BabySupplyItem>> = _outItems.asStateFlow()

    private val initialSavedIds = setOf("bs_f_1", "bs_f_3", "bs_s_1", "bs_c_1", "bs_o_1")
    private val _savedItemIds = MutableStateFlow<Set<String>>(initialSavedIds)
    val savedItemIds: StateFlow<Set<String>> = _savedItemIds.asStateFlow()

    private val _completedItemIds = MutableStateFlow<Set<String>>(setOf("bs_f_1"))
    val completedItemIds: StateFlow<Set<String>> = _completedItemIds.asStateFlow()

    fun setSavedItemIds(ids: Set<String>) {
        _savedItemIds.value = ids
    }

    fun toggleItemSaved(id: String) {
        _savedItemIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun setCompletedItemIds(ids: Set<String>) {
        _completedItemIds.value = ids
    }

    fun toggleItemCompleted(id: String) {
        _completedItemIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun isItemCompleted(id: String): Boolean {
        return _completedItemIds.value.contains(id)
    }

    fun deleteItem(id: String) {
        _feedItems.update { list -> list.filterNot { it.id == id } }
        _sleepItems.update { list -> list.filterNot { it.id == id } }
        _careItems.update { list -> list.filterNot { it.id == id } }
        _outItems.update { list -> list.filterNot { it.id == id } }
        _savedItemIds.update { current -> current - id }
        _completedItemIds.update { current -> current - id }
    }

    fun addItemToTab(
        tabCategory: String,
        title: String,
        periodTags: List<String> = listOf("#신생아"),
        purchaseTag: String = "#새제품",
        note: String = ""
    ) {
        val newId = "custom_baby_${System.currentTimeMillis()}"
        val newItem = BabySupplyItem(
            id = newId,
            title = title,
            periodTags = periodTags,
            purchaseTag = purchaseTag,
            note = note,
            section = "나만의 추가 용품",
            tabCategory = tabCategory,
            isCustom = true
        )
        when (tabCategory) {
            "1. 먹이기 (수유 & 이유식)", "1. 먹이기" -> _feedItems.update { it + newItem }
            "2. 재우기 & 쉬기 (수면 & 공간·놀이)", "2. 재우기 & 쉬기" -> _sleepItems.update { it + newItem }
            "3. 씻기 & 케어 (위생·건강 & 의류)", "3. 씻기 & 케어" -> _careItems.update { it + newItem }
            else -> _outItems.update { it + newItem }
        }
        _savedItemIds.update { it + newId }
    }
}
