package com.example.maternitybag.ui.benefits

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maternitybag.R
import com.example.maternitybag.data.BenefitItem
import com.example.maternitybag.data.BenefitsRepository
import com.example.maternitybag.theme.PeachLight
import com.example.maternitybag.theme.PeachPrimary
import com.example.maternitybag.theme.SoftSurface
import com.example.maternitybag.theme.TextDark
import com.example.maternitybag.theme.TextMuted
import com.example.maternitybag.theme.WarmBackground
import kotlinx.coroutines.launch

/* ========================================================
   1. 출산 혜택 정리 메인 카테고리 (전국 공통 vs 지역별 vs 개인별)
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitsCategoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNational: () -> Unit,
    onNavigateToRegional: () -> Unit,
    onNavigateToPersonal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "출산 혜택 정리",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 상단 안내 배너
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "알뜰한 출산 혜택 챙기기 🎁",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "전국 공통 정부 지원금, 거주지별 지원금, 직장·보험 개인별 혜택을 한눈에 확인하세요.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_gift_benefit),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            // 카테고리 1: 전국 공통 혜택
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToNational),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_checklist),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "1. 전국 공통 혜택",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "정부 지원",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "첫만남이용권, 부모급여, 아동수당, 출생신고/건보등록, 전기·가스 감면 등 대한민국 공통 혜택 모음",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // 카테고리 2: 지역별 혜택
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToRegional),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gift_benefit),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "2. 지역별 혜택",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEDE7F6))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "지자체 특화",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF673AB7)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "세종시 출생축하금(120만 원), 서울 산후조리비, 경기·인천 1억+아이드림 등 거주지 시·군·구 추가 지원금 모음",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // 카테고리 3: 개인별 혜택
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToPersonal),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_maternity_bag),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "3. 개인별 혜택",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE3F2FD))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "직장·보험·세제",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "공무원연금공단(100만), 교직원공제회(10만), 맞춤형 복지포인트, 태아/차보험 환급, 연말정산 소득/세액공제 모음",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

enum class BenefitSortColumn {
    TITLE, ELIGIBILITY, SAVED
}

fun filterBenefits(
    list: List<BenefitItem>,
    searchQuery: String,
    filterTag: String,
    savedIds: Set<String>
): List<BenefitItem> {
    return list.filter { item ->
        val matchesSearch = if (searchQuery.isBlank()) {
            true
        } else {
            val q = searchQuery.trim().lowercase()
            item.title.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.eligibility.lowercase().contains(q) ||
                (item.badge?.lowercase()?.contains(q) == true) ||
                item.timing.lowercase().contains(q) ||
                item.place.lowercase().contains(q)
        }

        val matchesFilter = when (filterTag) {
            "전체" -> true
            "👜 담긴 혜택" -> savedIds.contains(item.id)
            "현금 지원" -> item.badge?.contains("현금") == true || item.description.contains("현금") || item.title.contains("급여") || item.title.contains("수당")
            "바우처" -> item.badge?.contains("바우처") == true || item.description.contains("바우처") || item.title.contains("이용권")
            "생활비 감면" -> item.badge?.contains("감면") == true || item.badge?.contains("할인") == true || item.description.contains("감면") || item.description.contains("할인")
            "의료·보육" -> item.badge?.contains("의료") == true || item.badge?.contains("보육") == true || item.description.contains("의료") || item.description.contains("돌봄") || item.description.contains("보육")
            else -> true
        }

        matchesSearch && matchesFilter
    }
}

fun sortBenefits(
    list: List<BenefitItem>,
    sortColumn: BenefitSortColumn?,
    isSortAscending: Boolean,
    savedIds: Set<String>
): List<BenefitItem> {
    if (sortColumn == null) return list
    val comparator = when (sortColumn) {
        BenefitSortColumn.TITLE -> compareBy<BenefitItem> { it.title }
        BenefitSortColumn.ELIGIBILITY -> compareBy { it.eligibility }
        BenefitSortColumn.SAVED -> compareBy { savedIds.contains(it.id) }
    }
    return if (isSortAscending) list.sortedWith(comparator) else list.sortedWith(comparator.reversed())
}

@Composable
fun BenefitSearchAndFilterHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilterTag: String,
    onFilterTagChange: (String) -> Unit,
    sortColumn: BenefitSortColumn?,
    isSortAscending: Boolean,
    onHeaderSortClick: (BenefitSortColumn) -> Unit,
    isDeleteMode: Boolean
) {
    // 1. 실시간 검색창
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp),
        placeholder = { Text("혜택명, 지원 내용, 자격 검색...", fontSize = 12.5.sp, color = TextMuted) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색",
                tint = PeachPrimary,
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "검색어 지우기",
                        tint = TextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PeachPrimary,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )

    // 2. 필터 칩 목록 (전체 | 현금 지원 | 바우처 | 생활비 감면 | 의료·보육 | 👜 담긴 혜택)
    val filterOptions = listOf("전체", "현금 지원", "바우처", "생활비 감면", "의료·보육", "👜 담긴 혜택")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(filterOptions) { filterTag ->
            val isSelected = selectedFilterTag == filterTag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) PeachPrimary else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) PeachPrimary else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onFilterTagChange(filterTag) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filterTag,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextDark
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(3.dp))

    // 3. 정렬 가능한 테이블 헤더
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EAE6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onHeaderSortClick(BenefitSortColumn.TITLE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "혜택 내용" + if (sortColumn == BenefitSortColumn.TITLE) (if (isSortAscending) " ▲" else " ▼") else " ⇅",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (sortColumn == BenefitSortColumn.TITLE) PeachPrimary else TextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onHeaderSortClick(BenefitSortColumn.ELIGIBILITY) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "자격/조건" + if (sortColumn == BenefitSortColumn.ELIGIBILITY) (if (isSortAscending) " ▲" else " ▼") else " ⇅",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (sortColumn == BenefitSortColumn.ELIGIBILITY) PeachPrimary else TextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Box(
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        if (!isDeleteMode) onHeaderSortClick(BenefitSortColumn.SAVED)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isDeleteMode) "삭제" else ("가방담기" + if (sortColumn == BenefitSortColumn.SAVED) (if (isSortAscending) " ▲" else " ▼") else " ⇅"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDeleteMode) Color(0xFFD32F2F) else PeachPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(2.dp))
}

/* ========================================================
   2. 전국 공통 혜택 리스트 화면
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalBenefitsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedIds by BenefitsRepository.savedBenefitIds.collectAsState()
    val completedIds by BenefitsRepository.completedBenefitIds.collectAsState()
    val nationalList by BenefitsRepository.nationalBenefits.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isDeleteMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("전체") }
    var sortColumn by remember { mutableStateOf<BenefitSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    // 1. 실시간 검색 및 태그 필터링
    val filteredList = remember(nationalList, searchQuery, selectedFilterTag, savedIds) {
        filterBenefits(nationalList, searchQuery, selectedFilterTag, savedIds)
    }

    // 2. 컬럼별 정렬
    val displayList = remember(filteredList, sortColumn, isSortAscending, savedIds) {
        sortBenefits(filteredList, sortColumn, isSortAscending, savedIds)
    }

    fun onHeaderSortClick(column: BenefitSortColumn) {
        if (sortColumn != column) {
            sortColumn = column
            isSortAscending = true
        } else if (isSortAscending) {
            isSortAscending = false
        } else {
            sortColumn = null
            isSortAscending = true
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
                        text = "전국 공통 혜택",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            // 상단 추가/삭제 버튼 반반 분할
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "혜택 추가", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { isDeleteMode = !isDeleteMode },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDeleteMode) Color(0xFFFFEBEE) else SoftSurface,
                        contentColor = if (isDeleteMode) Color(0xFFD32F2F) else TextDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDeleteMode) "삭제 완료" else "혜택 삭제",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 검색 & 필터 칩 & 정렬 헤더
            BenefitSearchAndFilterHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedFilterTag = selectedFilterTag,
                onFilterTagChange = { selectedFilterTag = it },
                sortColumn = sortColumn,
                isSortAscending = isSortAscending,
                onHeaderSortClick = ::onHeaderSortClick,
                isDeleteMode = isDeleteMode
            )

            // 혜택 리스트 또는 검색 결과 없음
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "검색 또는 필터 조건에 맞는 혜택이 없습니다.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "검색어를 변경하거나 필터를 '전체'로 재설정해 보세요.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "체크박스를 누르면 '내 가방'에 쏙 담겨요! 👜 (${displayList.size}건)",
                            fontSize = 12.5.sp,
                            color = PeachPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 4.dp)
                        )
                    }

                    items(displayList, key = { it.id }) { benefit ->
                        val isSaved = savedIds.contains(benefit.id)
                        val isCompleted = completedIds.contains(benefit.id)
                        BenefitCardItem(
                            benefit = benefit,
                            isSaved = isSaved,
                            isCompleted = isCompleted,
                            isDeleteMode = isDeleteMode,
                            onDeleteClick = {
                                BenefitsRepository.deleteBenefitItem(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("'${benefit.title}' 혜택이 삭제되었습니다.")
                                }
                            },
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    val msg = if (!isSaved) {
                                        "'${benefit.title}'이(가) 내 가방에 담겼습니다 👜"
                                    } else {
                                        "'${benefit.title}'이(가) 내 가방에서 제외되었습니다"
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    val msg = if (!isCompleted) {
                                        "'${benefit.title}' 할일을 완료했습니다 ✅"
                                    } else {
                                        "'${benefit.title}' 완료를 취소했습니다"
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBenefitDialog(
            type = "national",
            onDismiss = { showAddDialog = false },
            onAdded = {
                scope.launch { snackbarHostState.showSnackbar("새로운 전국 공통 혜택이 추가되었습니다! ✨") }
            }
        )
    }
}

/* ========================================================
   3. 지역별 혜택 리스트 화면 (지역 선택 버튼 + 리스트)
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalBenefitsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val regions = listOf("세종", "서울", "경기", "인천", "부산", "대구", "대전", "광주", "울산", "충북", "충남", "전북", "전남", "경북", "경남", "강원", "제주")
    var selectedRegion by remember { mutableStateOf("세종") }
    val savedIds by BenefitsRepository.savedBenefitIds.collectAsState()
    val completedIds by BenefitsRepository.completedBenefitIds.collectAsState()
    val regionalMap by BenefitsRepository.regionalBenefits.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isDeleteMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("전체") }
    var sortColumn by remember { mutableStateOf<BenefitSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    val currentList = regionalMap[selectedRegion] ?: emptyList()

    // 1. 실시간 검색 및 필터링
    val filteredList = remember(currentList, searchQuery, selectedFilterTag, savedIds) {
        filterBenefits(currentList, searchQuery, selectedFilterTag, savedIds)
    }

    // 2. 컬럼별 정렬
    val displayList = remember(filteredList, sortColumn, isSortAscending, savedIds) {
        sortBenefits(filteredList, sortColumn, isSortAscending, savedIds)
    }

    fun onHeaderSortClick(column: BenefitSortColumn) {
        if (sortColumn != column) {
            sortColumn = column
            isSortAscending = true
        } else if (isSortAscending) {
            isSortAscending = false
        } else {
            sortColumn = null
            isSortAscending = true
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
                        text = "지역별 혜택",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            // 상단 추가/삭제 버튼 반반 분할
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "혜택 추가", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { isDeleteMode = !isDeleteMode },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDeleteMode) Color(0xFFFFEBEE) else SoftSurface,
                        contentColor = if (isDeleteMode) Color(0xFFD32F2F) else TextDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDeleteMode) "삭제 완료" else "혜택 삭제",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 상단 지역 선택 칩 리스트 (가로 스크롤)
            Text(
                text = "지역을 선택해 주세요",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(regions) { region ->
                    val isSelected = (region == selectedRegion)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRegion = region },
                        label = {
                            Text(
                                text = region,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PeachPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SoftSurface,
                            labelColor = TextDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 검색 & 필터 칩 & 정렬 헤더
            BenefitSearchAndFilterHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedFilterTag = selectedFilterTag,
                onFilterTagChange = { selectedFilterTag = it },
                sortColumn = sortColumn,
                isSortAscending = isSortAscending,
                onHeaderSortClick = ::onHeaderSortClick,
                isDeleteMode = isDeleteMode
            )

            // 선택된 지역 혜택 리스트
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentList.isEmpty()) "해당 지역의 상세 혜택 정보가 준비 중입니다." else "검색 또는 필터 조건에 맞는 혜택이 없습니다.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "검색어를 변경하거나 필터를 '전체'로 재설정해 보세요.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "[$selectedRegion] 지역 특화 출산 지원금 및 바우처 (${displayList.size}건)",
                            fontSize = 12.5.sp,
                            color = PeachPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        )
                    }

                    items(displayList, key = { it.id }) { benefit ->
                        val isSaved = savedIds.contains(benefit.id)
                        val isCompleted = completedIds.contains(benefit.id)
                        BenefitCardItem(
                            benefit = benefit,
                            isSaved = isSaved,
                            isCompleted = isCompleted,
                            isDeleteMode = isDeleteMode,
                            onDeleteClick = {
                                BenefitsRepository.deleteBenefitItem(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("'${benefit.title}' 혜택이 삭제되었습니다.")
                                }
                            },
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    val msg = if (!isSaved) {
                                        "'${benefit.title}'이(가) 내 가방에 담겼습니다 👜"
                                    } else {
                                        "'${benefit.title}'이(가) 내 가방에서 제외되었습니다"
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    val msg = if (!isCompleted) {
                                        "'${benefit.title}' 할일을 완료했습니다 ✅"
                                    } else {
                                        "'${benefit.title}' 완료를 취소했습니다"
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBenefitDialog(
            type = "regional",
            defaultRegion = selectedRegion,
            onDismiss = { showAddDialog = false },
            onAdded = {
                scope.launch { snackbarHostState.showSnackbar("새로운 지역 혜택이 추가되었습니다! ✨") }
            }
        )
    }
}

/* ========================================================
   4. 개인별 혜택 리스트 화면 (직장·보험·세제·공무원/교직원 등)
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalBenefitsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedIds by BenefitsRepository.savedBenefitIds.collectAsState()
    val completedIds by BenefitsRepository.completedBenefitIds.collectAsState()
    val personalList by BenefitsRepository.personalBenefits.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isDeleteMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("전체") }
    var sortColumn by remember { mutableStateOf<BenefitSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    // 1. 실시간 검색 및 태그 필터링
    val filteredList = remember(personalList, searchQuery, selectedFilterTag, savedIds) {
        filterBenefits(personalList, searchQuery, selectedFilterTag, savedIds)
    }

    // 2. 컬럼별 정렬
    val displayList = remember(filteredList, sortColumn, isSortAscending, savedIds) {
        sortBenefits(filteredList, sortColumn, isSortAscending, savedIds)
    }

    fun onHeaderSortClick(column: BenefitSortColumn) {
        if (sortColumn != column) {
            sortColumn = column
            isSortAscending = true
        } else if (isSortAscending) {
            isSortAscending = false
        } else {
            sortColumn = null
            isSortAscending = true
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
                        text = "개인별 혜택",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            // 상단 추가/삭제 버튼 반반 분할
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "혜택 추가", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { isDeleteMode = !isDeleteMode },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDeleteMode) Color(0xFFFFEBEE) else SoftSurface,
                        contentColor = if (isDeleteMode) Color(0xFFD32F2F) else TextDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDeleteMode) "삭제 완료" else "혜택 삭제",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 검색 & 필터 칩 & 정렬 헤더
            BenefitSearchAndFilterHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedFilterTag = selectedFilterTag,
                onFilterTagChange = { selectedFilterTag = it },
                sortColumn = sortColumn,
                isSortAscending = isSortAscending,
                onHeaderSortClick = ::onHeaderSortClick,
                isDeleteMode = isDeleteMode
            )

            // 혜택 리스트 또는 검색 결과 없음
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "검색 또는 필터 조건에 맞는 개인별 혜택이 없습니다.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "검색어를 변경하거나 필터를 '전체'로 재설정해 보세요.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "직장, 보험사, 공제회, 연말정산 등 맞춤 혜택 (${displayList.size}건)",
                            fontSize = 12.5.sp,
                            color = PeachPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 4.dp)
                        )
                    }

                    items(displayList, key = { it.id }) { benefit ->
                        val isSaved = savedIds.contains(benefit.id)
                        val isCompleted = completedIds.contains(benefit.id)
                        BenefitCardItem(
                            benefit = benefit,
                            isSaved = isSaved,
                            isCompleted = isCompleted,
                            isDeleteMode = isDeleteMode,
                            onDeleteClick = {
                                BenefitsRepository.deleteBenefitItem(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("'${benefit.title}' 혜택이 삭제되었습니다.")
                                }
                            },
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    val msg = if (!isSaved) {
                                        "'${benefit.title}'이(가) 내 가방에 담겼습니다 👜"
                                    } else {
                                        "'${benefit.title}'이(가) 내 가방에서 제외되었습니다"
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    val msg = if (!isCompleted) {
                                        "'${benefit.title}' 할일을 완료했습니다 ✅"
                                    } else {
                                        "'${benefit.title}' 완료를 취소했습니다"
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBenefitDialog(
            type = "personal",
            onDismiss = { showAddDialog = false },
            onAdded = {
                scope.launch { snackbarHostState.showSnackbar("새로운 개인별 혜택이 추가되었습니다! ✨") }
            }
        )
    }
}

/* ========================================================
   5. 내가 저장한 출산 혜택 보기 화면 (내 가방 연동: 윗칸 / 아랫칸)
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedBenefitsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedIds by BenefitsRepository.savedBenefitIds.collectAsState()
    val completedIds by BenefitsRepository.completedBenefitIds.collectAsState()

    val uncompletedList = BenefitsRepository.getSavedUncompletedBenefits()
    val completedList = BenefitsRepository.getSavedCompletedBenefits()
    val totalSavedCount = uncompletedList.size + completedList.size

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "내가 저장한 출산 혜택",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PeachLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "내 가방에 담긴 혜택 (총 ${totalSavedCount}개) 👜",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "완료 버튼을 누르면 아래 완료 칸으로 이동합니다.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_maternity_bag),
                            contentDescription = null,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            if (totalSavedCount == 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "아직 담아둔 혜택이 없어요! 🌸",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "[4. 출산 혜택 정리]에서 '내 가방' 체크박스를 눌러 나만의 혜택을 쏙 담아보세요.",
                                fontSize = 12.5.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // ================= 윗칸: 내가 선택한 진행 중 리스트 =================
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0EC))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📌 윗칸: 내가 선택한 리스트 (${uncompletedList.size}개)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PeachPrimary
                            )
                        }
                    }
                }

                if (uncompletedList.isEmpty()) {
                    item {
                        Text(
                            text = "진행 중인 혜택이 없거나 모두 완료했습니다! 🎉",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    items(uncompletedList) { benefit ->
                        val isCompleted = completedIds.contains(benefit.id)
                        BenefitCardItem(
                            benefit = benefit,
                            isSaved = true,
                            isCompleted = isCompleted,
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("'${benefit.title}'이(가) 내 가방에서 제외되었습니다")
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (!isCompleted) "'${benefit.title}' 완료! 아래 아랫칸으로 이동했습니다 ✅"
                                        else "'${benefit.title}' 완료 취소. 윗칸으로 복귀했습니다 ⬆️"
                                    )
                                }
                            }
                        )
                    }
                }

                // ================= 아랫칸: 완료 버튼을 누른 리스트 =================
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp, bottom = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅ 아랫칸: 완료된 리스트 (${completedList.size}개)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                if (completedList.isEmpty()) {
                    item {
                        Text(
                            text = "아직 완료된 항목이 없어요. 윗칸에서 [완료] 버튼을 누르면 이 칸으로 내려갑니다! 🍀",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    items(completedList) { benefit ->
                        val isCompleted = completedIds.contains(benefit.id)
                        BenefitCardItem(
                            benefit = benefit,
                            isSaved = true,
                            isCompleted = isCompleted,
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("'${benefit.title}'이(가) 내 가방에서 제외되었습니다")
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (!isCompleted) "'${benefit.title}' 완료! 아래 아랫칸으로 이동했습니다 ✅"
                                        else "'${benefit.title}' 완료 취소. 윗칸으로 복귀했습니다 ⬆️"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/* ========================================================
   공통: 혜택 아이템 카드 (혜택 | 자격 및 조건 | 시기/장소 | 내 가방 체크박스 + 완료 버튼)
======================================================== */
@Composable
fun BenefitCardItem(
    benefit: BenefitItem,
    isSaved: Boolean,
    isCompleted: Boolean,
    onToggleSaved: () -> Unit,
    onToggleCompleted: () -> Unit,
    isDeleteMode: Boolean = false,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                if (isDeleteMode && onDeleteClick != null) {
                    onDeleteClick()
                } else {
                    onToggleSaved()
                }
            }),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF7F9F7) else SoftSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.5.dp else 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 본문 내용 (혜택 타이틀 + 뱃지 + 내용 + 시기/장소 + 자격조건) - 모두 가운데 정렬 & 1줄 제한
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 혜택 타이틀 + 뱃지
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = benefit.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) TextMuted else TextDark,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    benefit.badge?.let { badge ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCompleted) Color(0xFFEEEEEE) else Color(0xFFFFF3E0))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCompleted) TextMuted else Color(0xFFE65100),
                                maxLines = 1
                            )
                        }
                    }
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "완료됨 ✓",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2. 지원 내용/금액
                Text(
                    text = benefit.description,
                    fontSize = 12.sp,
                    color = if (isCompleted) TextMuted.copy(alpha = 0.7f) else Color(0xFF333333),
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 3. ⏱️ 시기 & 📍 장소/방법 (있는 경우 칩 형태로 표시)
                if (benefit.timing.isNotEmpty() || benefit.place.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (benefit.timing.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isCompleted) Color(0xFFF0F0F0) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⏱️ ${benefit.timing}",
                                    fontSize = 11.sp,
                                    color = if (isCompleted) TextMuted else Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                        if (benefit.place.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isCompleted) Color(0xFFF0F0F0) else Color(0xFFE1F5FE))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "📍 ${benefit.place}",
                                    fontSize = 11.sp,
                                    color = if (isCompleted) TextMuted else Color(0xFF0288D1),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 4. 자격 및 조건 (연한 박스)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCompleted) Color(0xFFF2F2F2) else Color(0xFFF7F7F7))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "자격·조건: ",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) TextMuted else PeachPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = benefit.eligibility,
                        fontSize = 11.5.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 우측 컬럼: 내 가방 체크박스 + 담김버튼 바로 아래 완료 버튼 OR 삭제 버튼
            if (isDeleteMode) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFEBEE))
                        .clickable { onDeleteClick?.invoke() }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "삭제 🗑️",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isSaved) "담김 ✓" else "가방담기",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSaved) PeachPrimary else TextMuted
                    )
                    Checkbox(
                        checked = isSaved,
                        onCheckedChange = { onToggleSaved() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PeachPrimary,
                            uncheckedColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // [담김버튼 바로 아래 완료 버튼]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isCompleted) Color(0xFF43A047)
                                else Color(0xFFE0E0E0)
                            )
                            .clickable { onToggleCompleted() }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCompleted) "완료됨 ✓" else "완료",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color.White else Color(0xFF555555)
                        )
                    }
                }
            }
        }
    }
}

/* ========================================================
   다이얼로그: 새로운 혜택 추가
======================================================== */
@Composable
fun AddBenefitDialog(
    type: String, // "national", "regional", "personal"
    defaultRegion: String = "세종",
    onDismiss: () -> Unit,
    onAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eligibility by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("신규 혜택") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "새로운 혜택 추가",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("혜택 이름 (예: 산후조리비 지원)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("혜택 내용 및 금액") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eligibility,
                    onValueChange = { eligibility = it },
                    label = { Text("자격 및 조건") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timing,
                    onValueChange = { timing = it },
                    label = { Text("신청 시기 (예: 출산 후 30일 이내)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("혜택 장소/신청 방법 (예: 행정복지센터)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        BenefitsRepository.addBenefitItem(
                            type = type,
                            title = title,
                            description = description.ifBlank { "사용자 직접 추가 혜택" },
                            eligibility = eligibility.ifBlank { "해당자" },
                            badge = badge,
                            region = if (type == "regional") defaultRegion else null,
                            timing = timing,
                            place = place
                        )
                        onAdded()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary)
            ) {
                Text("추가하기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
