package com.example.maternitybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MaternityBagItem(
    val id: String,
    val title: String,
    val recommendedQty: String = "",
    val tabCategory: String, // "산모 용품", "신생아 용품", "보호자 & 공통"
    val section: String = "", // e.g. "의류 & 착용품", "위생 & 산후 회복 용품", etc.
    val locationTags: List<String>, // e.g. listOf("#병원", "#조리원"), listOf("#퇴원퇴소")
    val priorityTag: String = "", // "#필수", "#선택" (하위호환)
    val note: String = "", // e.g. "(임산부 전용/제왕절개용)"
    val isCustom: Boolean = false
)

object MaternityBagRepository {

    // 1번 탭: 산모 용품 (총 30개 항목 전수 등록)
    private val defaultMaternityItems = listOf(
        // [의류 & 착용품]
        MaternityBagItem("m_cloth_1", "수유브라 / 수유나시 (3~4개)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_cloth_2", "산모 팬티 (3~4개)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원"), note = "(임산부 전용/제왕절개용)"),
        MaternityBagItem("m_cloth_3", "편한 산모 바지 (3~4벌)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_cloth_4", "무압박 양말 (4~6켤레)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_cloth_5", "압박스타킹 (1~2개)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_cloth_6", "손목보호대 (1~2개)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_cloth_7", "산후복대 (1개)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원"), note = "(제왕절개 시 병원 구매 가능)"),
        MaternityBagItem("m_cloth_8", "산모용 슬리퍼 (1켤레)", "", "산모 용품", "의류 & 착용품", listOf("#병원", "#조리원"), note = "(미끄럼 방지 폭신한 것)"),
        MaternityBagItem("m_cloth_9", "외출복 / 가디건 (1벌)", "", "산모 용품", "의류 & 착용품", listOf("#퇴원퇴소"), note = "(퇴원 및 이동 시 착용)"),

        // [위생 & 산후 회복 용품]
        MaternityBagItem("m_hyg_1", "입는 안심팬티 (2~3팩)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_hyg_2", "산모 패드·생리대 (1세트)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_hyg_3", "마이비데 / 비데물티슈 (3개)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_hyg_4", "바디샤워 티슈 (1~3개)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_hyg_5", "위생 티슈·물티슈 (1~2개)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_hyg_6", "가슴 쿨링팩·카보크림 (필요시)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원"), note = "(울혈·울유 대비)"),
        MaternityBagItem("m_hyg_7", "흉터관리 겔·시트 (필요시)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원"), note = "(제왕절개용)"),
        MaternityBagItem("m_hyg_8", "회음부 방석 / 좌욕판 (1개)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원"), note = "(자연분만용 / 병원 구비 확인)"),
        MaternityBagItem("m_hyg_9", "치질연고 / 비판텐 (필요시)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_hyg_10", "다리 공기압 마사지기 (1개)", "", "산모 용품", "위생 & 산후 회복 용품", listOf("#병원", "#조리원"), note = "(붓기 관리용)"),

        // [수유 용품]
        MaternityBagItem("m_feed_1", "수유패드 (1~2팩)", "", "산모 용품", "수유 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_feed_2", "모유저장팩 (1~2팩)", "", "산모 용품", "수유 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_feed_3", "수유 티슈 (필요시)", "", "산모 용품", "수유 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_feed_4", "유두보호크림 (1~2개)", "", "산모 용품", "수유 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_feed_5", "유두보호기 (1~2개)", "", "산모 용품", "수유 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_feed_6", "유축 깔때기 & 클립 (1~2개)", "", "산모 용품", "수유 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("m_feed_7", "젖병 세제 & 젖병 솔 (1세트)", "", "산모 용품", "수유 용품", listOf("#조리원")),

        // [개인 위생 & 스킨케어]
        MaternityBagItem("m_sk_1", "산모 세면도구 세트 (1세트)", "", "산모 용품", "개인 위생 & 스킨케어", listOf("#병원", "#조리원"), note = "(샴푸·바디워시·칫솔치약 등)"),
        MaternityBagItem("m_sk_2", "기초화장·보습 세트 (1세트)", "", "산모 용품", "개인 위생 & 스킨케어", listOf("#병원", "#조리원"), note = "(스킨·로션·립밤·핸드크림)"),
        MaternityBagItem("m_sk_3", "헤어 케어 도구 세트 (1세트)", "", "산모 용품", "개인 위생 & 스킨케어", listOf("#병원", "#조리원"), note = "(드라이기·머리빗·머리끈 등)"),
        MaternityBagItem("m_sk_4", "드라이샴푸 (1개)", "", "산모 용품", "개인 위생 & 스킨케어", listOf("#병원"), note = "(입원 중 샤워 불가 시 추천)")
    )

    // 2번 탭: 신생아 용품 (총 11개 항목 전수 등록)
    private val defaultBabyItems = listOf(
        // [의류 & 착용품 (퇴원 및 모자동실용)]
        MaternityBagItem("b_cloth_1", "배냇저고리 / 배냇수트 (1~2개)", "", "신생아 용품", "의류 & 착용품 (퇴원 및 모자동실용)", listOf("#퇴원퇴소"), note = "(세탁 완료 필수)"),
        MaternityBagItem("b_cloth_2", "속싸개 (1~2개)", "", "신생아 용품", "의류 & 착용품 (퇴원 및 모자동실용)", listOf("#퇴원퇴소")),
        MaternityBagItem("b_cloth_3", "겉싸개 (1개)", "", "신생아 용품", "의류 & 착용품 (퇴원 및 모자동실용)", listOf("#퇴원퇴소")),
        MaternityBagItem("b_cloth_4", "신생아 모자·싸개류 (각 1개)", "", "신생아 용품", "의류 & 착용품 (퇴원 및 모자동실용)", listOf("#퇴원퇴소"), note = "(손발싸개·모자·양말)"),

        // [위생 & 케어 용품]
        MaternityBagItem("b_care_1", "가재 손수건 (10장 이상)", "", "신생아 용품", "위생 & 케어 용품", listOf("#병원", "#조리원"), note = "(거즈·엠보 세탁 완료)"),
        MaternityBagItem("b_care_2", "아기 물티슈 (1~2통)", "", "신생아 용품", "위생 & 케어 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("b_care_3", "아기 로션 / 아기 크림 (1개)", "", "신생아 용품", "위생 & 케어 용품", listOf("#조리원")),
        MaternityBagItem("b_care_4", "아기 영양제 (1개)", "", "신생아 용품", "위생 & 케어 용품", listOf("#병원", "#조리원"), note = "(비타민D·유산균)"),
        MaternityBagItem("b_care_5", "신생아 손톱깎이 세트 (1세트)", "", "신생아 용품", "위생 & 케어 용품", listOf("#조리원")),
        MaternityBagItem("b_care_6", "모자동실 촬영 소품 (필요시)", "", "신생아 용품", "위생 & 케어 용품", listOf("#조리원"), note = "(디데이달력·초점책·인형)"),

        // [이동 및 안전 용품]
        MaternityBagItem("b_safe_1", "신생아 카시트 (1개)", "", "신생아 용품", "이동 및 안전 용품", listOf("#퇴원퇴소"), note = "(자차 이동 시 법적 필수 품목)")
    )

    // 3번 탭: 보호자 & 공통 (총 15개 항목 전수 등록)
    private val defaultGuardianItems = listOf(
        // [필수 서류 & 자금]
        MaternityBagItem("g_doc_1", "산모수첩 & 신분증 (1세트)", "", "보호자 & 공통", "필수 서류 & 자금", listOf("#병원")),
        MaternityBagItem("g_doc_2", "결제 수단 (카드/현금)", "", "보호자 & 공통", "필수 서류 & 자금", listOf("#병원", "#조리원")),

        // [보호자(남편) 전용 용품]
        MaternityBagItem("g_dad_1", "남편 세면도구 & 여벌옷 (1세트)", "", "보호자 & 공통", "보호자(남편) 전용 용품", listOf("#병원", "#조리원")),
        MaternityBagItem("g_dad_2", "남편용 침구류 (1세트)", "", "보호자 & 공통", "보호자(남편) 전용 용품", listOf("#병원"), note = "(병원 제공 여부 확인 필요)"),
        MaternityBagItem("g_dad_3", "남편용 슬리퍼 (1켤레)", "", "보호자 & 공통", "보호자(남편) 전용 용품", listOf("#병원", "#조리원")),

        // [공통 생활용품 & 전자기기]
        MaternityBagItem("g_life_1", "텀블러 & 빨대 세트 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원"), note = "(누워서 마시는 꺾인 빨대)"),
        MaternityBagItem("g_life_2", "종이컵 & 보리차/붓기차 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원")),
        MaternityBagItem("g_life_3", "일회용 수세미 & 수저 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원"), note = "(텀블러 세척 및 식사 보조용)"),
        MaternityBagItem("g_life_4", "충전기 & 멀티탭 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원")),
        MaternityBagItem("g_life_5", "거치대 & 미니선풍기 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원")),
        MaternityBagItem("g_life_6", "미니 가습기 (1개)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원"), note = "(건조한 병실 대비)"),
        MaternityBagItem("g_life_7", "필기도구 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원"), note = "(가위·네임펜·일반펜 / 모유기록용)"),
        MaternityBagItem("g_life_8", "산모 영양제 (1세트)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원"), note = "(철분·칼슘·마그네슘 등)"),
        MaternityBagItem("g_life_9", "보호자 마스크 (1팩)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#병원", "#조리원")),
        MaternityBagItem("g_life_10", "접이식 보조가방 / 에코백 (1~2개)", "", "보호자 & 공통", "공통 생활용품 & 전자기기", listOf("#퇴원퇴소"), note = "(퇴원 선물 및 늘어난 짐 수거용)")
    )

    private val _maternityItems = MutableStateFlow(defaultMaternityItems)
    val maternityItems: StateFlow<List<MaternityBagItem>> = _maternityItems.asStateFlow()

    private val _babyItems = MutableStateFlow(defaultBabyItems)
    val babyItems: StateFlow<List<MaternityBagItem>> = _babyItems.asStateFlow()

    private val _guardianItems = MutableStateFlow(defaultGuardianItems)
    val guardianItems: StateFlow<List<MaternityBagItem>> = _guardianItems.asStateFlow()

    // 초기 시각적 만족감을 위한 담김 아이템 설정 (산모 용품 15개 담김 중 12개 완료 -> 80%)
    private val initialSavedIds = setOf(
        "m_cloth_1", "m_cloth_2", "m_cloth_3", "m_cloth_4", "m_cloth_5",
        "m_cloth_6", "m_cloth_7", "m_cloth_8", "m_hyg_1", "m_hyg_2",
        "m_hyg_3", "m_hyg_5", "m_feed_1", "m_feed_2", "m_sk_1"
    )

    private val initialCompletedIds = setOf(
        "m_cloth_1", "m_cloth_2", "m_cloth_3", "m_cloth_4", "m_cloth_5",
        "m_cloth_6", "m_cloth_7", "m_cloth_8", "m_hyg_1", "m_hyg_2",
        "m_hyg_3", "m_hyg_5"
    )

    private val _savedItemIds = MutableStateFlow<Set<String>>(initialSavedIds)
    val savedItemIds: StateFlow<Set<String>> = _savedItemIds.asStateFlow()

    private val _completedItemIds = MutableStateFlow<Set<String>>(initialCompletedIds)
    val completedItemIds: StateFlow<Set<String>> = _completedItemIds.asStateFlow()

    // 준비물 추가 (원하는 카테고리 탭에 추가)
    fun addItemToTab(
        tabCategory: String,
        title: String,
        recommendedQty: String = "",
        locationTags: List<String> = listOf("#병원", "#조리원"),
        priorityTag: String = "",
        note: String = ""
    ) {
        val newId = "custom_${System.currentTimeMillis()}"
        val fullTitle = if (recommendedQty.isNotBlank() && !title.contains("(")) "$title ($recommendedQty)" else title
        val newItem = MaternityBagItem(
            id = newId,
            title = fullTitle,
            recommendedQty = recommendedQty,
            tabCategory = tabCategory,
            section = "추가된 준비물",
            locationTags = locationTags.ifEmpty { listOf("#병원") },
            priorityTag = priorityTag,
            note = note,
            isCustom = true
        )
        when (tabCategory) {
            "산모 용품" -> _maternityItems.update { it + newItem }
            "신생아 용품" -> _babyItems.update { it + newItem }
            "보호자 & 공통" -> _guardianItems.update { it + newItem }
            else -> _maternityItems.update { it + newItem }
        }
        // 자동 내 가방 담기
        toggleItemSaved(newId)
    }

    // 준비물 삭제
    fun deleteItem(itemId: String) {
        _maternityItems.update { list -> list.filterNot { it.id == itemId } }
        _babyItems.update { list -> list.filterNot { it.id == itemId } }
        _guardianItems.update { list -> list.filterNot { it.id == itemId } }
        _savedItemIds.update { it - itemId }
        _completedItemIds.update { it - itemId }
    }

    fun setSavedItems(itemIds: Set<String>) {
        _savedItemIds.value = itemIds
        _completedItemIds.value = emptySet()
    }

    fun toggleItemSaved(itemId: String) {
        _savedItemIds.update { current ->
            if (current.contains(itemId)) {
                _completedItemIds.update { it - itemId }
                current - itemId
            } else {
                current + itemId
            }
        }
    }

    fun isItemSaved(itemId: String): Boolean {
        return _savedItemIds.value.contains(itemId)
    }

    fun toggleItemCompleted(itemId: String) {
        _completedItemIds.update { current ->
            if (current.contains(itemId)) {
                current - itemId
            } else {
                current + itemId
            }
        }
    }

    fun isItemCompleted(itemId: String): Boolean {
        return _completedItemIds.value.contains(itemId)
    }

    fun getAllItems(): List<MaternityBagItem> {
        return _maternityItems.value + _babyItems.value + _guardianItems.value
    }

    fun getItemsForTab(tabIndex: Int): List<MaternityBagItem> {
        return when (tabIndex) {
            0 -> _maternityItems.value
            1 -> _babyItems.value
            else -> _guardianItems.value
        }
    }

    fun getSavedItems(tabCategoryFilter: String? = null): List<MaternityBagItem> {
        val ids = _savedItemIds.value
        val all = getAllItems().filter { ids.contains(it.id) }
        return if (tabCategoryFilter == null || tabCategoryFilter == "전체") {
            all
        } else {
            all.filter { it.tabCategory == tabCategoryFilter }
        }
    }

    fun getSavedUncompletedItems(tabCategoryFilter: String? = null): List<MaternityBagItem> {
        val saved = getSavedItems(tabCategoryFilter)
        val completed = _completedItemIds.value
        return saved.filter { !completed.contains(it.id) }
    }

    fun getSavedCompletedItems(tabCategoryFilter: String? = null): List<MaternityBagItem> {
        val saved = getSavedItems(tabCategoryFilter)
        val completed = _completedItemIds.value
        return saved.filter { completed.contains(it.id) }
    }

    fun getCategoryProgress(tabCategoryName: String): Pair<Int, Int> {
        val savedInCat = getSavedItems(tabCategoryName)
        val completedInCat = getSavedCompletedItems(tabCategoryName)
        return Pair(completedInCat.size, savedInCat.size)
    }
}
