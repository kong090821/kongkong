package com.example.maternitybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BenefitItem(
    val id: String,
    val title: String,
    val description: String,
    val eligibility: String,
    val region: String? = null, // null이면 전국 공통 / 개인별
    val isNational: Boolean = (region == null),
    val badge: String? = null,
    val timing: String = "",
    val place: String = ""
)

object BenefitsRepository {

    // 1. 전국 공통 혜택 목록
    private val defaultNationalBenefits = listOf(
        BenefitItem(
            id = "nat_1",
            title = "첫만남이용권 (200만 원 / 300만 원)",
            description = "첫째아 200만 원, 둘째아 이상 300만 원 국민행복카드 바우처 포인트 지급",
            eligibility = "대한민국 모든 출생아 (출생 후 1년 이내 신청)",
            badge = "바우처",
            timing = "출산 후",
            place = "행정복지센터 / 복지로"
        ),
        BenefitItem(
            id = "nat_2",
            title = "부모급여 (만 0세 100만 원 / 만 1세 50만 원)",
            description = "만 0세 월 100만 원, 만 1세 월 50만 원 매월 25일 현금 지급",
            eligibility = "만 0~1세 영아 양육 가정",
            badge = "현금 지원",
            timing = "출산 후 매월",
            place = "행정복지센터 / 복지로"
        ),
        BenefitItem(
            id = "nat_3",
            title = "아동수당 (월 10만 원)",
            description = "만 8세 미만(0~95개월) 모든 아동 매월 10만 원 현금 지급 (60일 이내 신청 시 소급)",
            eligibility = "만 8세 미만 모든 아동",
            badge = "현금 지원",
            timing = "60일 이내 신청",
            place = "행정복지센터 / 복지로"
        ),
        BenefitItem(
            id = "nat_4",
            title = "출생신고 및 건강보험 피보험자 등록",
            description = "법적 신분 등록 및 건강보험 피보험자/피부양자 자동 연동",
            eligibility = "모든 출생아 (미신고 시 과태료 발생)",
            badge = "행정 수속",
            timing = "출생 후 30일 이내",
            place = "행정복지센터 / 대법원 전자의무"
        ),
        BenefitItem(
            id = "nat_5",
            title = "출생증명서 발급",
            description = "출생신고, 태아보험 청구 및 회사 제출용 출생증명서 2~3부 수령",
            eligibility = "모든 출산 산모",
            badge = "서류 수속",
            timing = "출생 즉시",
            place = "분만 병원"
        ),
        BenefitItem(
            id = "nat_6",
            title = "전기·가스·수도 요금 감면 신청",
            description = "출산 가구 전기요금 30% 감면 (월 1.6만 원 한도, 3년간), 가스/수도 요금 감면",
            eligibility = "출산 후 3년 이내 가구",
            badge = "생활비 감면",
            timing = "출산 후 즉시",
            place = "인터넷 (한전 123/정부24)"
        ),
        BenefitItem(
            id = "nat_7",
            title = "어린이집 입소 대기 신청",
            description = "출생 직후 임신육아종합포털 아이사랑에서 원하는 어린이집 입소 대기 등록",
            eligibility = "모든 영유아",
            badge = "보육 수속",
            timing = "출산 후 즉시",
            place = "아이사랑 포털 (인터넷)"
        ),
        BenefitItem(
            id = "nat_8",
            title = "맘편한 KTX & SRT 임산부·다자녀 할인",
            description = "맘편한 KTX(일반실 가격으로 특실 이용), SRT 맘편한 30% 할인",
            eligibility = "임산부 및 출산 후 1년 이내 산모",
            badge = "교통 할인",
            timing = "임신 중 ~ 출산 후",
            place = "코레일 / SR (정부24)"
        ),
        BenefitItem(
            id = "nat_9",
            title = "신생아 무료 예방접종 (BCG, B형간염 등)",
            description = "BCG(피내용), B형간염 1~3차 등 영유아 국가 필수 예방접종 18종 전액 무료",
            eligibility = "모든 신생아 및 영유아",
            badge = "의료 지원",
            timing = "출생 직후 ~ 4주 이내",
            place = "분만 병원 / 보건소"
        ),
        BenefitItem(
            id = "nat_10",
            title = "영유아 건강검진 (총 8회 무료)",
            description = "생후 14일부터 71개월까지 성장·발달 단계별 무료 검진 및 구강검진",
            eligibility = "모든 영유아",
            badge = "의료 검진",
            timing = "생후 14일~71개월",
            place = "지정 소아청소년과"
        )
    )

    // 2. 대한민국 17개 지자체 전체 지역별 혜택 목록
    private val defaultRegionalBenefits = mapOf(
        "세종" to listOf(
            BenefitItem(
                id = "reg_sj_1",
                title = "세종시 출생축하금 (120만 원)",
                description = "세종특별자치시 출생아 1인당 120만 원 지역화폐/현금 지급",
                eligibility = "세종시 거주 출산 가정 (출생 후 6개월 이내 신청)",
                badge = "세종시 특화",
                timing = "6개월 이내",
                place = "세종시 읍·면·동 행정복지센터"
            ),
            BenefitItem(
                id = "reg_sj_2",
                title = "세종시 산모·신생아 건강관리사 지원금 & 농산물",
                description = "산후조도우미 본인부담금 추가 지원 및 48만 원 상당 친환경 농산물 꾸러미 지원",
                eligibility = "세종시 거주 산모",
                badge = "세종시 지원",
                timing = "출산 전 40일 ~ 출산 후 30일",
                place = "세종시 보건소 / 맘편한 임신"
            )
        ),
        "서울" to listOf(
            BenefitItem(
                id = "reg_seoul_1",
                title = "서울형 산후조리경비 지원 (100만 원)",
                description = "출생아 1인당 100만 원 바우처 (산후조리원, 의약품, 운동 등 이용 가능)",
                eligibility = "서울시 90일 이상 거주 출산 가정",
                badge = "서울시 특화",
                timing = "출산 후 60일 이내",
                place = "서울엄마아빠택시 / 몽땅정보만능키"
            ),
            BenefitItem(
                id = "reg_seoul_2",
                title = "서울 엄마아빠 택시 & 임산부 교통비 (70만 원)",
                description = "영아 1인당 10만 원 택시 포인트 + 임산부 교통비 70만 원 지원",
                eligibility = "서울시 거주 임산부 및 영아 가구",
                badge = "교통 지원",
                timing = "임신 중 ~ 출산 후",
                place = "정부24 / 몽땅정보만능키"
            )
        ),
        "경기" to listOf(
            BenefitItem(
                id = "reg_gg_1",
                title = "경기도 산후조리비 지원 (50만 원)",
                description = "출생아 1인당 50만 원 경기지역화폐 지급",
                eligibility = "경기도 거주 출산 가정",
                badge = "경기도 지원",
                timing = "출산 후",
                place = "경기도 시·군 행정복지센터"
            ),
            BenefitItem(
                id = "reg_gg_2",
                title = "경기도 임산부 친환경 농산물 꾸러미 (48만 원)",
                description = "연간 48만 원 상당 친환경 농산물 자부담 20% 지원",
                eligibility = "경기도 거주 임산부/산모",
                badge = "농산물 지원",
                timing = "임신 중 ~ 출산 후 1년",
                place = "임산부 친환경농산물 쇼핑몰"
            )
        ),
        "인천" to listOf(
            BenefitItem(
                id = "reg_ic_1",
                title = "인천 1억 플러스 아이드림 지원",
                description = "천사지원금(1~7세 연 120만 원) 및 아이 꿈 수당 등 인천시 양육 패키지 지원",
                eligibility = "인천시 거주 출산 가정",
                badge = "인천시 특화",
                timing = "출산 후",
                place = "인천시 / 행정복지센터"
            ),
            BenefitItem(
                id = "reg_ic_2",
                title = "인천시 산후조리비 & 임산부 교통비 지원 (50만 원)",
                description = "산후조리 비용 및 이동 교통비 50만 원 바우처 지급",
                eligibility = "인천시 거주 임산부",
                badge = "인천시 지원",
                timing = "출산 전후",
                place = "인천시 보건소"
            )
        ),
        "부산" to listOf(
            BenefitItem(
                id = "reg_bs_1",
                title = "부산시 출산축하금 (첫째 20만 / 둘째 이상 100만 원)",
                description = "부산광역시 출산축하금 + 구·군별 추가 축하금 지원",
                eligibility = "부산시 거주 출산 가정",
                badge = "부산시 지원",
                timing = "출생 후 1년 이내",
                place = "부산시 읍·면·동 행정복지센터"
            ),
            BenefitItem(
                id = "reg_bs_2",
                title = "부산시 산후조리비 지원 (50만 원)",
                description = "동백전 지역화폐 50만 원 상당 산후조리비 지원",
                eligibility = "부산시 거주 산모",
                badge = "부산시 특화",
                timing = "출산 후",
                place = "부산시 보건소 / 읍면동"
            )
        ),
        "대구" to listOf(
            BenefitItem(
                id = "reg_dg_1",
                title = "대구시 출생축하금 (둘째 100만 / 셋째 이상 200만 원)",
                description = "다자녀 출생아 대상 현금 출생축하금 지원",
                eligibility = "대구시 거주 출산 가정",
                badge = "대구시 지원",
                timing = "출생 후 1년 이내",
                place = "대구시 행정복지센터"
            )
        ),
        "대전" to listOf(
            BenefitItem(
                id = "reg_dj_1",
                title = "대전 대전맘 출산장려금 (30만~80만 원)",
                description = "대전시 기본 30만 원 + 구별 추가 축하금 (30~50만 원 추가)",
                eligibility = "대전시 거주 출산 가정",
                badge = "대전시 지원",
                timing = "출생 후 6개월 이내",
                place = "대전시 행정복지센터"
            )
        ),
        "광주" to listOf(
            BenefitItem(
                id = "reg_gj_1",
                title = "광주형 출생축하금 (100만 원)",
                description = "광주광역시 출생아 1인당 100만 원 현금 지급",
                eligibility = "광주시 거주 출산 가정",
                badge = "광주시 지원",
                timing = "출생 후 3개월 이내",
                place = "광주시 행정복지센터"
            )
        ),
        "울산" to listOf(
            BenefitItem(
                id = "reg_us_1",
                title = "울산시 출산축하금 (첫째 50만 / 둘째 100만 / 셋째 200만 원)",
                description = "자녀 순위별 차등 출산축하금 현금 지급",
                eligibility = "울산시 거주 출산 가정",
                badge = "울산시 지원",
                timing = "출생 후 1년 이내",
                place = "울산시 행정복지센터"
            )
        ),
        "충북" to listOf(
            BenefitItem(
                id = "reg_cb_1",
                title = "충북 출산육아수당 (5년간 총 1,000만 원)",
                description = "1세 300만, 2세 200만, 3~5세 각 170만 원 총 1천만 원 분할 지급",
                eligibility = "충청북도 거주 출산 가구",
                badge = "충북 특화",
                timing = "출생 후 5년간",
                place = "충북 시·군 행정복지센터"
            )
        ),
        "충남" to listOf(
            BenefitItem(
                id = "reg_cn_1",
                title = "충남 행복키움수당 (월 10만 원)",
                description = "12~36개월 아동 대상 매월 10만 원 현금 추가 지원",
                eligibility = "충청남도 거주 영유아",
                badge = "충남 지원",
                timing = "매월 25일",
                place = "충남 시·군 행정복지센터"
            )
        ),
        "전북" to listOf(
            BenefitItem(
                id = "reg_jb_1",
                title = "전북 출산축하금 (첫째 30~100만 / 셋째 이상 500만 원)",
                description = "시·군별 차등 지급되는 대형 출생축하금 지원",
                eligibility = "전북 거주 출산 가정",
                badge = "전북 지원",
                timing = "출생 후 1년 이내",
                place = "전북 시·군 행정복지센터"
            )
        ),
        "전남" to listOf(
            BenefitItem(
                id = "reg_jn_1",
                title = "전남 출생기본수당 (1~18세 매월 20만 원)",
                description = "18세까지 매월 20만 원 총 4,320만 원 장기 육아수당 지급",
                eligibility = "전라남도 거주 아동",
                badge = "전남 파격지원",
                timing = "매월 지급",
                place = "전남 시·군 행정복지센터"
            )
        ),
        "경북" to listOf(
            BenefitItem(
                id = "reg_gb_1",
                title = "경북 저출생 극복 출산축하금 (100만~500만 원)",
                description = "시·군별 첫째 100만 원부터 셋째 이상 500만 원 이상 지원",
                eligibility = "경상북도 거주 출산 가정",
                badge = "경북 지원",
                timing = "출생 후 1년 이내",
                place = "경북 시·군 행정복지센터"
            )
        ),
        "경남" to listOf(
            BenefitItem(
                id = "reg_gn_1",
                title = "경남 출산축하금 & 산후조리비 (50만~300만 원)",
                description = "시·군별 출산축하금 및 산후조리비 지원금 파격 혜택",
                eligibility = "경상남도 거주 출산 가정",
                badge = "경남 지원",
                timing = "출산 후",
                place = "경남 시·군 행정복지센터"
            )
        ),
        "강원" to listOf(
            BenefitItem(
                id = "reg_gw_1",
                title = "강원 육아기본수당 (만 0~5세 월 30만~50만 원)",
                description = "정부 부모급여와 연계하여 만 5세까지 매월 육아수당 현금 지급",
                eligibility = "강원특별자치도 거주 가구",
                badge = "강원 특화",
                timing = "매월 25일",
                place = "강원 시·군 행정복지센터"
            )
        ),
        "제주" to listOf(
            BenefitItem(
                id = "reg_jj_1",
                title = "제주 출산복지수당 (첫째 50만 / 둘째 이상 200만 원)",
                description = "둘째 이상 연 50만 원씩 4년간 총 200만 원 분할 지급",
                eligibility = "제주특별자치도 거주 가정",
                badge = "제주 특화",
                timing = "출생 후 4년간",
                place = "제주 읍·면·동 주민센터"
            ),
            BenefitItem(
                id = "reg_jj_2",
                title = "제주 임산부 행복택시 지원 (연 12만 원)",
                description = "임산부 병원 진료 및 이동용 행복택시 12만 원 이용권 지원",
                eligibility = "제주 거주 임산부",
                badge = "교통 지원",
                timing = "임신 중",
                place = "제주도 보건소"
            )
        )
    )

    // 3. 개인별 혜택 목록 (직장, 공무원, 교직원, 보험, 연말정산 등)
    private val defaultPersonalBenefits = listOf(
        BenefitItem(
            id = "per_1",
            title = "공무원연금공단 출산축하금 (100만 원)",
            description = "공무원 맞춤형 복지 / 공무원연금공단 출산축하금 첫째 100만 원 지급",
            eligibility = "공무원 및 교직원",
            badge = "공무원 혜택",
            timing = "출산 후",
            place = "공무원연금공단 / 기관 복지팀"
        ),
        BenefitItem(
            id = "per_2",
            title = "교직원공제회 출산축하금 (10만 원 + 축하선물)",
            description = "한국교직원공제회 회원 출산 축하금 10만 원 및 신생아 축하용품 선물 지급",
            eligibility = "한국교직원공제회 회원",
            badge = "교직원 혜택",
            timing = "출산 후",
            place = "교직원공제회 홈페이지"
        ),
        BenefitItem(
            id = "per_3",
            title = "공무원 복지포털 출산 가족점수 (자녀 당 10만x2 등)",
            description = "자녀 출산 시 맞춤형 복지포인트 출산 축하 점수 및 가족 점수 추가 부여",
            eligibility = "공무원 및 맞춤형 복지 적용 대상자",
            badge = "복지포인트",
            timing = "출산 후",
            place = "공무원 맞춤형 복지포털"
        ),
        BenefitItem(
            id = "per_4",
            title = "공무원/직장 가족수당 신청 (월 3만~11만 원)",
            description = "가족수당 지급 (첫째 자녀 월 3만, 둘째 월 7만, 셋째 이상 월 11만 원)",
            eligibility = "공무원, 교직원 및 해당 직장인",
            badge = "가족수당",
            timing = "출산 후 급여반영",
            place = "학교/기관 행정실 / 인사과"
        ),
        BenefitItem(
            id = "per_5",
            title = "배우자 출산휴가 (10일 유급휴가) & 육아휴직 급여",
            description = "남편 배우자 출산휴가 10일(유급) 및 6+6 부모육아휴직제 (최대 월 450만 원)",
            eligibility = "근로자 및 공무원/교직원",
            badge = "휴가·휴직",
            timing = "출산 시기 ~ 휴직 시",
            place = "고용24 / 소속 기관 인사과"
        ),
        BenefitItem(
            id = "per_6",
            title = "태아보험 수급 및 계약 조정 (입원·수술비 청구)",
            description = "제왕절개 수술비, 신생아 입원 일당 청구 + 출생 후 피보험자 명의 변경/선납료 환급",
            eligibility = "태아보험 가입자",
            badge = "보험 혜택",
            timing = "수술/출생 후",
            place = "가입 보험사 (앱/콜센터)"
        ),
        BenefitItem(
            id = "per_7",
            title = "자동차보험 태아/자녀 할인 환급",
            description = "태아 확인서 또는 출생증명서 제출 시 자동차보험료 3~15% 환급 (태아 할인 특약)",
            eligibility = "자동차보험 가입자",
            badge = "보험 환급",
            timing = "태아확인 시 / 출생 후",
            place = "가입 자동차보험사"
        ),
        BenefitItem(
            id = "per_8",
            title = "공무원 단체보험 / 실손보험 병원 진료비 신청",
            description = "임신·출산 수술비, 누적 병원 진료비 실손보험 및 단체보험 청구",
            eligibility = "공무원 단체보험 / 개인 실손 가입자",
            badge = "보험 청구",
            timing = "수술/출산 후",
            place = "공무원연금공단 / 보험사"
        ),
        BenefitItem(
            id = "per_9",
            title = "연말정산 - 출산·입양 세액공제 (30만~70만 원)",
            description = "해당 연도 출생아 첫째 30만 원, 둘째 50만 원, 셋째 이상 70만 원 세액공제",
            eligibility = "근로소득자 및 종합소득자",
            badge = "세액공제",
            timing = "연말정산 시기",
            place = "국세청 홈택스"
        ),
        BenefitItem(
            id = "per_10",
            title = "연말정산 - 산후조리원비 의료비 세액공제 (최대 200만 원)",
            description = "산후조리원 결제금액 출생아당 200만 원 한도 의료비 세액공제 (총급여 7천만 원 이하)",
            eligibility = "총급여 7,000만 원 이하 근로자",
            badge = "의료비 공제",
            timing = "조리원비 결제 시",
            place = "국세청 홈택스 / 조리원 영수증"
        ),
        BenefitItem(
            id = "per_11",
            title = "연말정산 - 신생아 인적공제 (부양가족 150만 원)",
            description = "출생아를 부양가족 기본공제 대상자로 등록하여 소득공제 적용",
            eligibility = "근로소득자",
            badge = "인적공제",
            timing = "연말정산 시기",
            place = "국세청 홈택스"
        ),
        BenefitItem(
            id = "per_12",
            title = "트리니움 / 이용 병원 후기 작성 혜택",
            description = "트리니움 여성병원 등 이용 산부인과/조리원 후기 작성 시 상품권 및 기프트 제공",
            eligibility = "해당 병원/조리원 이용자",
            badge = "병원 혜택",
            timing = "퇴원/퇴소 후",
            place = "병원/조리원 공식 카페·블로그"
        )
    )

    private val _nationalBenefits = MutableStateFlow(defaultNationalBenefits)
    val nationalBenefits: StateFlow<List<BenefitItem>> = _nationalBenefits.asStateFlow()

    private val _regionalBenefits = MutableStateFlow(defaultRegionalBenefits)
    val regionalBenefits: StateFlow<Map<String, List<BenefitItem>>> = _regionalBenefits.asStateFlow()

    private val _personalBenefits = MutableStateFlow(defaultPersonalBenefits)
    val personalBenefits: StateFlow<List<BenefitItem>> = _personalBenefits.asStateFlow()

    private val _savedBenefitIds = MutableStateFlow<Set<String>>(emptySet())
    val savedBenefitIds: StateFlow<Set<String>> = _savedBenefitIds.asStateFlow()

    private val _completedBenefitIds = MutableStateFlow<Set<String>>(emptySet())
    val completedBenefitIds: StateFlow<Set<String>> = _completedBenefitIds.asStateFlow()

    fun setSavedBenefitIds(ids: Set<String>) {
        _savedBenefitIds.value = ids
    }

    fun toggleBenefitSaved(id: String) {
        _savedBenefitIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun setCompletedBenefitIds(ids: Set<String>) {
        _completedBenefitIds.value = ids
    }

    fun toggleBenefitCompleted(id: String) {
        _completedBenefitIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun isBenefitCompleted(id: String): Boolean {
        return _completedBenefitIds.value.contains(id)
    }

    fun deleteBenefitItem(id: String) {
        _nationalBenefits.update { list -> list.filterNot { it.id == id } }
        _personalBenefits.update { list -> list.filterNot { it.id == id } }
        _regionalBenefits.update { map ->
            map.mapValues { entry -> entry.value.filterNot { it.id == id } }
        }
        _savedBenefitIds.update { set -> set - id }
        _completedBenefitIds.update { set -> set - id }
    }

    fun addBenefitItem(
        type: String, // "national", "regional", "personal"
        title: String,
        description: String,
        eligibility: String,
        badge: String = "신규 혜택",
        region: String? = null,
        timing: String = "",
        place: String = ""
    ) {
        val newId = "custom_benefit_${System.currentTimeMillis()}"
        val newItem = BenefitItem(
            id = newId,
            title = title,
            description = description,
            eligibility = eligibility,
            region = region,
            badge = badge,
            timing = timing,
            place = place
        )
        when (type) {
            "national" -> _nationalBenefits.update { it + newItem }
            "personal" -> _personalBenefits.update { it + newItem }
            "regional" -> {
                val r = region ?: "세종"
                _regionalBenefits.update { map ->
                    val list = map[r] ?: emptyList()
                    map + (r to (list + newItem))
                }
            }
        }
        _savedBenefitIds.update { it + newId }
    }
}
