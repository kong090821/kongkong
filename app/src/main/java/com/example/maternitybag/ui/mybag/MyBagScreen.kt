package com.example.maternitybag.ui.mybag

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maternitybag.data.BabySuppliesRepository
import com.example.maternitybag.data.BabySupplyItem
import com.example.maternitybag.data.BenefitItem
import com.example.maternitybag.data.BenefitsRepository
import com.example.maternitybag.data.MaternityBagItem
import com.example.maternitybag.data.MaternityBagRepository
import com.example.maternitybag.data.TodoItem
import com.example.maternitybag.data.TodoRepository
import com.example.maternitybag.theme.MaternityBagTheme
import com.example.maternitybag.theme.PeachPrimary
import com.example.maternitybag.theme.SoftSurface
import com.example.maternitybag.theme.TextDark
import com.example.maternitybag.theme.TextMuted
import com.example.maternitybag.theme.WarmBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBagScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSavedMaternityBag: () -> Unit = {},
    onNavigateToSavedBenefits: () -> Unit = {},
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // null = 1~4번 메인 버튼 화면, 1 = 출산가방, 2 = 육아용품, 3 = 시기별 할일, 4 = 출산 혜택
    var selectedFeature by remember { mutableStateOf<Int?>(null) }

    // 1. 출산가방 데이터
    val matItems by MaternityBagRepository.maternityItems.collectAsState()
    val babyMatItems by MaternityBagRepository.babyItems.collectAsState()
    val guardianMatItems by MaternityBagRepository.guardianItems.collectAsState()
    val savedMatIds by MaternityBagRepository.savedItemIds.collectAsState()
    val compMatIds by MaternityBagRepository.completedItemIds.collectAsState()

    val allMatItems = remember(matItems, babyMatItems, guardianMatItems) {
        matItems + babyMatItems + guardianMatItems
    }
    val savedMatList = remember(allMatItems, savedMatIds) {
        allMatItems.filter { savedMatIds.contains(it.id) }
    }
    val compMatList = remember(savedMatList, compMatIds) {
        savedMatList.filter { compMatIds.contains(it.id) }
    }
    val uncompMatList = remember(savedMatList, compMatIds) {
        savedMatList.filter { !compMatIds.contains(it.id) }
    }
    val matPercent = if (savedMatList.isNotEmpty()) (compMatList.size * 100) / savedMatList.size else 0

    // 2. 육아용품 데이터
    val feedItems by BabySuppliesRepository.feedItems.collectAsState()
    val sleepItems by BabySuppliesRepository.sleepItems.collectAsState()
    val careItems by BabySuppliesRepository.careItems.collectAsState()
    val outItems by BabySuppliesRepository.outItems.collectAsState()
    val savedBabyIds by BabySuppliesRepository.savedItemIds.collectAsState()
    val compBabyIds by BabySuppliesRepository.completedItemIds.collectAsState()

    val allBabyItems = remember(feedItems, sleepItems, careItems, outItems) {
        feedItems + sleepItems + careItems + outItems
    }
    val savedBabyList = remember(allBabyItems, savedBabyIds) {
        allBabyItems.filter { savedBabyIds.contains(it.id) }
    }
    val compBabyList = remember(savedBabyList, compBabyIds) {
        savedBabyList.filter { compBabyIds.contains(it.id) }
    }
    val uncompBabyList = remember(savedBabyList, compBabyIds) {
        savedBabyList.filter { !compBabyIds.contains(it.id) }
    }
    val babyPercent = if (savedBabyList.isNotEmpty()) (compBabyList.size * 100) / savedBabyList.size else 0

    // 3. 시기별 할일 데이터
    val todoItems by TodoRepository.todoItems.collectAsState()
    val savedTodoIds by TodoRepository.savedTodoIds.collectAsState()
    val compTodoIds by TodoRepository.completedTodoIds.collectAsState()

    val savedTodoList = remember(todoItems, savedTodoIds) {
        todoItems.filter { savedTodoIds.contains(it.id) }
    }
    val compTodoList = remember(savedTodoList, compTodoIds) {
        savedTodoList.filter { compTodoIds.contains(it.id) }
    }
    val uncompTodoList = remember(savedTodoList, compTodoIds) {
        savedTodoList.filter { !compTodoIds.contains(it.id) }
    }
    val todoPercent = if (savedTodoList.isNotEmpty()) (compTodoList.size * 100) / savedTodoList.size else 0

    // 4. 출산 혜택 데이터
    val natBenefits by BenefitsRepository.nationalBenefits.collectAsState()
    val perBenefits by BenefitsRepository.personalBenefits.collectAsState()
    val regBenefits by BenefitsRepository.regionalBenefits.collectAsState()
    val savedBenefitIds by BenefitsRepository.savedBenefitIds.collectAsState()
    val compBenefitIds by BenefitsRepository.completedBenefitIds.collectAsState()

    val allBenefits = remember(natBenefits, perBenefits, regBenefits) {
        (natBenefits + perBenefits + regBenefits.values.flatten()).distinctBy { it.id }
    }
    val savedBenefitList = remember(allBenefits, savedBenefitIds) {
        allBenefits.filter { savedBenefitIds.contains(it.id) }
    }
    val compBenefitList = remember(savedBenefitList, compBenefitIds) {
        savedBenefitList.filter { compBenefitIds.contains(it.id) }
    }
    val uncompBenefitList = remember(savedBenefitList, compBenefitIds) {
        savedBenefitList.filter { !compBenefitIds.contains(it.id) }
    }
    val benefitPercent = if (savedBenefitList.isNotEmpty()) (compBenefitList.size * 100) / savedBenefitList.size else 0

    // 화면 제목 및 뒤로가기 액션
    val screenTitle = when (selectedFeature) {
        1 -> "1. 출산가방 준비물"
        2 -> "2. 육아용품 리스트"
        3 -> "3. 시기별 할일 리스트"
        4 -> "4. 출산 혜택 리스트"
        else -> "내 가방 👜"
    }

    val onBackAction = {
        if (selectedFeature != null) {
            selectedFeature = null
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackAction) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // [상태 1] 1~4번 메인 버튼 화면 (선택된 기능 없음)
            if (selectedFeature == null) {
                MyBagMenuButtonsView(
                    savedMatCount = savedMatList.size,
                    compMatCount = compMatList.size,
                    matPercent = matPercent,
                    savedBabyCount = savedBabyList.size,
                    compBabyCount = compBabyList.size,
                    babyPercent = babyPercent,
                    savedTodoCount = savedTodoList.size,
                    compTodoCount = compTodoList.size,
                    todoPercent = todoPercent,
                    savedBenCount = savedBenefitList.size,
                    compBenCount = compBenefitList.size,
                    benPercent = benefitPercent,
                    onSelectFeature = { selectedFeature = it }
                )
            } else {
                // [상태 2] 선택된 1~4번 기능의 상세 화면
                // 상단 네비게이션: 1~4번 목록 복귀 버튼 + 4개 빠른 전환 탭
                FeatureTopNavBar(
                    selectedFeature = selectedFeature ?: 1,
                    onBackToMenu = { selectedFeature = null },
                    onSelectFeature = { selectedFeature = it }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (selectedFeature) {
                        1 -> {
                            // 1. 출산가방 준비물
                            item {
                                FeatureProgressBox(
                                    featureName = "출산가방 준비물",
                                    icon = "👜",
                                    savedCount = savedMatList.size,
                                    compCount = compMatList.size,
                                    percent = matPercent,
                                    themeColor = Color(0xFFFF6F59),
                                    gradientEnd = Color(0xFFFF8A75)
                                )
                            }
                            renderMaternityBagSection(
                                savedList = savedMatList,
                                uncompList = uncompMatList,
                                compList = compMatList,
                                onToggleCompleted = { item ->
                                    val isComp = compMatIds.contains(item.id)
                                    MaternityBagRepository.toggleItemCompleted(item.id)
                                    scope.launch {
                                        val msg = if (!isComp) "✅ '${item.title}' 준비 완료! 맨 아래로 이동했습니다." else "'${item.title}' 미완료로 변경"
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        2 -> {
                            // 2. 육아용품 리스트
                            item {
                                FeatureProgressBox(
                                    featureName = "육아용품",
                                    icon = "🍼",
                                    savedCount = savedBabyList.size,
                                    compCount = compBabyList.size,
                                    percent = babyPercent,
                                    themeColor = Color(0xFF43A047),
                                    gradientEnd = Color(0xFF66BB6A)
                                )
                            }
                            renderBabySuppliesSection(
                                savedList = savedBabyList,
                                uncompList = uncompBabyList,
                                compList = compBabyList,
                                onToggleCompleted = { item ->
                                    val isComp = compBabyIds.contains(item.id)
                                    BabySuppliesRepository.toggleItemCompleted(item.id)
                                    scope.launch {
                                        val msg = if (!isComp) "✅ '${item.title}' 준비 완료! 맨 아래로 이동했습니다." else "'${item.title}' 미완료로 변경"
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        3 -> {
                            // 3. 시기별 할일 리스트
                            item {
                                FeatureProgressBox(
                                    featureName = "시기별 할일",
                                    icon = "📅",
                                    savedCount = savedTodoList.size,
                                    compCount = compTodoList.size,
                                    percent = todoPercent,
                                    themeColor = Color(0xFF7E57C2),
                                    gradientEnd = Color(0xFF9575CD)
                                )
                            }
                            renderTodoSection(
                                savedList = savedTodoList,
                                uncompList = uncompTodoList,
                                compList = compTodoList,
                                onToggleCompleted = { item ->
                                    val isComp = compTodoIds.contains(item.id)
                                    TodoRepository.toggleTodoCompleted(item.id)
                                    scope.launch {
                                        val msg = if (!isComp) "✅ '${item.title}' 실천 완료! 맨 아래로 이동했습니다." else "'${item.title}' 미완료로 변경"
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        4 -> {
                            // 4. 출산 혜택 리스트
                            item {
                                FeatureProgressBox(
                                    featureName = "출산 혜택",
                                    icon = "🎁",
                                    savedCount = savedBenefitList.size,
                                    compCount = compBenefitList.size,
                                    percent = benefitPercent,
                                    themeColor = Color(0xFFFFA000),
                                    gradientEnd = Color(0xFFFFB300)
                                )
                            }
                            renderBenefitsSection(
                                savedList = savedBenefitList,
                                uncompList = uncompBenefitList,
                                compList = compBenefitList,
                                onToggleCompleted = { item ->
                                    val isComp = compBenefitIds.contains(item.id)
                                    BenefitsRepository.toggleBenefitCompleted(item.id)
                                    scope.launch {
                                        val msg = if (!isComp) "✅ '${item.title}' 신청 완료! 맨 아래로 이동했습니다." else "'${item.title}' 미완료로 변경"
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/* ========================================================
   1~4번 메인 버튼 화면 컴포넌트
======================================================== */
@Composable
private fun MyBagMenuButtonsView(
    savedMatCount: Int, compMatCount: Int, matPercent: Int,
    savedBabyCount: Int, compBabyCount: Int, babyPercent: Int,
    savedTodoCount: Int, compTodoCount: Int, todoPercent: Int,
    savedBenCount: Int, compBenCount: Int, benPercent: Int,
    onSelectFeature: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 가이드 박스
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F6)),
            border = BorderStroke(1.dp, Color(0xFFFFE4DD))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "확인할 가방 번호를 눌러주세요 🌸",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "버튼을 누르면 해당 항목들의 전체 준비율과 담긴 목록이 나타납니다.",
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 1번 버튼: 출산가방 준비물
        FeatureMenuCardButton(
            num = 1,
            icon = "👜",
            title = "1. 출산가방 준비물",
            desc = "병원 및 조리원 입원 필수 준비물",
            badgeText = "${savedMatCount}개 담김 (${compMatCount}개 완료, ${matPercent}%)",
            themeColor = Color(0xFFFF6F59),
            badgeBg = Color(0xFFFFF0EC),
            borderColor = Color(0xFFFFD6CC),
            onClick = { onSelectFeature(1) }
        )

        // 2번 버튼: 육아용품 리스트
        FeatureMenuCardButton(
            num = 2,
            icon = "🍼",
            title = "2. 육아용품 리스트",
            desc = "수유·수면·위생·외출 신생아 필수품",
            badgeText = "${savedBabyCount}개 담김 (${compBabyCount}개 완료, ${babyPercent}%)",
            themeColor = Color(0xFF43A047),
            badgeBg = Color(0xFFE8F5E9),
            borderColor = Color(0xFFC8E6C9),
            onClick = { onSelectFeature(2) }
        )

        // 3번 버튼: 시기별 할일 리스트
        FeatureMenuCardButton(
            num = 3,
            icon = "📅",
            title = "3. 시기별 할일 리스트",
            desc = "출산 전부터 병원·조리원·가정 할일",
            badgeText = "${savedTodoCount}개 담김 (${compTodoCount}개 완료, ${todoPercent}%)",
            themeColor = Color(0xFF7E57C2),
            badgeBg = Color(0xFFEDE7F6),
            borderColor = Color(0xFFD1C4E9),
            onClick = { onSelectFeature(3) }
        )

        // 4번 버튼: 출산 혜택 리스트
        FeatureMenuCardButton(
            num = 4,
            icon = "🎁",
            title = "4. 출산 혜택 리스트",
            desc = "전국·지역·개인 맞춤 정부 지원금 모음",
            badgeText = "${savedBenCount}개 담김 (${compBenCount}개 완료, ${benPercent}%)",
            themeColor = Color(0xFFFFA000),
            badgeBg = Color(0xFFFFF3E0),
            borderColor = Color(0xFFFFE0B2),
            onClick = { onSelectFeature(4) }
        )
    }
}

@Composable
private fun FeatureMenuCardButton(
    num: Int,
    icon: String,
    title: String,
    desc: String,
    badgeText: String,
    themeColor: Color,
    badgeBg: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 원형 번호 배지
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(themeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = num.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 이모지 아이콘
            Text(
                text = icon,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 본문
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }
            }

            // 화살표
            Text(
                text = "➔",
                fontSize = 15.sp,
                color = TextMuted,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

/* ========================================================
   상세 뷰 상단 네비게이션: [◀ 1~4번 목록] + 4개 빠른 탭
======================================================== */
@Composable
private fun FeatureTopNavBar(
    selectedFeature: Int,
    onBackToMenu: () -> Unit,
    onSelectFeature: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1~4번 목록 돌아가기 버튼
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F3F4))
                .clickable { onBackToMenu() }
                .padding(horizontal = 10.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "◀ 1~4번 목록",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C4043)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 4개 기능 탭
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Triple(1, "1. 출산가방", Color(0xFFFF6F59)),
                Triple(2, "2. 육아용품", Color(0xFF43A047)),
                Triple(3, "3. 시기별 할일", Color(0xFF7E57C2)),
                Triple(4, "4. 출산 혜택", Color(0xFFFFA000))
            )

            tabs.forEach { (idx, title, col) ->
                val isActive = selectedFeature == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) col else SoftSurface)
                        .clickable { onSelectFeature(idx) }
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color.White else TextMuted
                    )
                }
            }
        }
    }
}

/* ========================================================
   사진과 똑같은 박스: 버튼 색상과 같은 바탕색의 준비율 카드
======================================================== */
@Composable
private fun FeatureProgressBox(
    featureName: String,
    icon: String,
    savedCount: Int,
    compCount: Int,
    percent: Int,
    themeColor: Color,
    gradientEnd: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(themeColor, gradientEnd)),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 반투명 흰색 필 배지
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.28f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$icon 내가 담은 $featureName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 볼드 흰색 타이틀
                Text(
                    text = "총 ${savedCount}개 담김 (${compCount}개 완료, ${percent}%)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 반투명 트랙 위 흰색 프로그레스 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                ) {
                    val progressFraction = if (savedCount > 0) compCount.toFloat() / savedCount else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressFraction.coerceIn(0f, 1f))
                            .height(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 하단 설명 및 퍼센트
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "완료된 항목을 체크하면 취소선과 함께 가장 아래로 이동합니다 💪",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$percent%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/* ========================================================
   1. 출산가방 준비물 섹션
======================================================== */
private fun LazyListScope.renderMaternityBagSection(
    savedList: List<MaternityBagItem>,
    uncompList: List<MaternityBagItem>,
    compList: List<MaternityBagItem>,
    onToggleCompleted: (MaternityBagItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "담긴 출산가방 준비물이 없습니다.\n[출산가방 체크리스트]에서 '가방담기'를 눌러보세요!")
        }
        return
    }

    // 테이블 헤더 (목차)
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EAE6))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "준비물",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "장소",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(70.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "완료",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6F59),
                    modifier = Modifier.width(76.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // 미완료 항목
    items(uncompList, key = { "mat_${it.id}" }) { item ->
        MaternityItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // 완료 항목 (가장 아랫칸으로 이동)
    if (compList.isNotEmpty()) {
        item {
            CompletedDivider(count = compList.size, themeColor = Color(0xFFFF6F59))
        }
        items(compList, key = { "mat_comp_${it.id}" }) { item ->
            MaternityItemRow(
                item = item,
                isCompleted = true,
                onToggleCompleted = { onToggleCompleted(item) }
            )
        }
    }
}

@Composable
private fun MaternityItemRow(
    item: MaternityBagItem,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCompleted() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF9F9F9) else SoftSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Bold,
                    color = if (isCompleted) TextMuted else TextDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.note,
                        fontSize = 10.5.sp,
                        color = TextMuted,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier.width(70.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.locationTags.forEach { locTag ->
                        val bg = when (locTag) {
                            "#병원" -> Color(0xFFE8F0FE)
                            "#조리원" -> Color(0xFFE6F4EA)
                            "#퇴원퇴소" -> Color(0xFFFEF7E0)
                            else -> Color(0xFFF1F3F4)
                        }
                        val tc = when (locTag) {
                            "#병원" -> Color(0xFF1967D2)
                            "#조리원" -> Color(0xFF137333)
                            "#퇴원퇴소" -> Color(0xFFB06000)
                            else -> Color(0xFF5F6368)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(bg)
                                .padding(horizontal = 3.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = locTag,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tc,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.width(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompleted() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PeachPrimary,
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
            }
        }
    }
}

/* ========================================================
   2. 육아용품 리스트 섹션
======================================================== */
private fun LazyListScope.renderBabySuppliesSection(
    savedList: List<BabySupplyItem>,
    uncompList: List<BabySupplyItem>,
    compList: List<BabySupplyItem>,
    onToggleCompleted: (BabySupplyItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "담긴 육아용품이 없습니다.\n[육아용품 체크리스트]에서 '가방담기'를 눌러보세요!")
        }
        return
    }

    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EAE6))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "준비물",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "시기",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(56.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "구입경로",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(65.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "완료",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF43A047),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // 미완료 항목
    items(uncompList, key = { "baby_${it.id}" }) { item ->
        BabySupplyItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // 완료 항목 (가장 아랫칸으로 이동)
    if (compList.isNotEmpty()) {
        item {
            CompletedDivider(count = compList.size, themeColor = Color(0xFF43A047))
        }
        items(compList, key = { "baby_comp_${it.id}" }) { item ->
            BabySupplyItemRow(
                item = item,
                isCompleted = true,
                onToggleCompleted = { onToggleCompleted(item) }
            )
        }
    }
}

@Composable
private fun BabySupplyItemRow(
    item: BabySupplyItem,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCompleted() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF9F9F9) else SoftSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Bold,
                    color = if (isCompleted) TextMuted else TextDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.note,
                        fontSize = 10.5.sp,
                        color = TextMuted,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier.width(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.periodTags.joinToString(", "),
                    fontSize = 10.5.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier.width(65.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (item.purchaseTag == "#당근") Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.purchaseTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.purchaseTag == "#당근") Color(0xFFE65100) else Color(0xFF2E7D32)
                    )
                }
            }

            Box(
                modifier = Modifier.width(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompleted() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF43A047),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
            }
        }
    }
}

/* ========================================================
   3. 시기별 할일 리스트 섹션
======================================================== */
private fun LazyListScope.renderTodoSection(
    savedList: List<TodoItem>,
    uncompList: List<TodoItem>,
    compList: List<TodoItem>,
    onToggleCompleted: (TodoItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "담긴 시기별 할일이 없습니다.\n[시기별 할일 체크리스트]에서 '가방담기'를 눌러보세요!")
        }
        return
    }

    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EAE6))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "해야할일",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "누가?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(65.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "완료",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7E57C2),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // 미완료 항목
    items(uncompList, key = { "todo_${it.id}" }) { item ->
        TodoItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // 완료 항목 (가장 아랫칸으로 이동)
    if (compList.isNotEmpty()) {
        item {
            CompletedDivider(count = compList.size, themeColor = Color(0xFF7E57C2))
        }
        items(compList, key = { "todo_comp_${it.id}" }) { item ->
            TodoItemRow(
                item = item,
                isCompleted = true,
                onToggleCompleted = { onToggleCompleted(item) }
            )
        }
    }
}

@Composable
private fun TodoItemRow(
    item: TodoItem,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit
) {
    val (roleBg, roleColor) = when (item.roleTag) {
        "#아빠" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        "#엄마" -> Pair(Color(0xFFFFF0EC), Color(0xFFFF6F59))
        else -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCompleted() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF9F9F9) else SoftSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Bold,
                    color = if (isCompleted) TextMuted else TextDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.tip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.tip,
                        fontSize = 10.5.sp,
                        color = TextMuted,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .width(65.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(roleBg)
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.roleTag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor
                )
            }

            Box(
                modifier = Modifier.width(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompleted() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF7E57C2),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
            }
        }
    }
}

/* ========================================================
   4. 출산 혜택 리스트 섹션
======================================================== */
private fun LazyListScope.renderBenefitsSection(
    savedList: List<BenefitItem>,
    uncompList: List<BenefitItem>,
    compList: List<BenefitItem>,
    onToggleCompleted: (BenefitItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "담긴 출산 혜택이 없습니다.\n[출산 혜택 정리]에서 '가방담기'를 눌러보세요!")
        }
        return
    }

    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EAE6))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "혜택 내용",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "자격/조건",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "완료",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // 미완료 항목
    items(uncompList, key = { "ben_${it.id}" }) { item ->
        BenefitItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // 완료 항목 (가장 아랫칸으로 이동)
    if (compList.isNotEmpty()) {
        item {
            CompletedDivider(count = compList.size, themeColor = Color(0xFFFFA000))
        }
        items(compList, key = { "ben_comp_${it.id}" }) { item ->
            BenefitItemRow(
                item = item,
                isCompleted = true,
                onToggleCompleted = { onToggleCompleted(item) }
            )
        }
    }
}

@Composable
private fun BenefitItemRow(
    item: BenefitItem,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCompleted() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF9F9F9) else SoftSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Bold,
                    color = if (isCompleted) TextMuted else TextDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        fontSize = 10.5.sp,
                        color = TextMuted,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.eligibility,
                    fontSize = 11.sp,
                    color = if (isCompleted) TextMuted else TextDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier.width(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompleted() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFFA000),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
            }
        }
    }
}

/* ========================================================
   공통 UI 컴포넌트
======================================================== */
@Composable
private fun CompletedDivider(count: Int, themeColor: Color = Color(0xFF43A047)) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "─── 준비 완료 (${count}개) ───",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = themeColor
        )
    }
}

@Composable
private fun EmptySectionCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyBagScreenPreview() {
    MaternityBagTheme {
        MyBagScreen(onNavigateBack = {})
    }
}
