package com.example.maternitybag.ui.checklist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maternitybag.R
import com.example.maternitybag.data.MaternityBagItem
import com.example.maternitybag.data.MaternityBagRepository
import com.example.maternitybag.data.RecommendationRepository
import com.example.maternitybag.theme.PeachLight
import com.example.maternitybag.theme.PeachPrimary
import com.example.maternitybag.theme.SoftSurface
import com.example.maternitybag.theme.TextDark
import com.example.maternitybag.theme.TextMuted
import com.example.maternitybag.theme.WarmBackground
import com.example.maternitybag.ui.components.TopSheetRecommendationModal
import kotlinx.coroutines.launch

enum class MaternitySortColumn {
    TITLE, TAG, SAVED
}

/* ========================================================
   1. 출산가방 준비물 체크리스트 화면 (3개 탭 + 추가/삭제버튼 분할)
   1번 탭: 산모 용품
   2번 탭: 신생아 용품
   3번 탭: 보호자 & 공통
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaternityBagChecklistScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isDeleteMode by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("전체") }
    var sortColumn by remember { mutableStateOf<MaternitySortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    val savedIds by MaternityBagRepository.savedItemIds.collectAsState()
    val maternityItems by MaternityBagRepository.maternityItems.collectAsState()
    val babyItems by MaternityBagRepository.babyItems.collectAsState()
    val guardianItems by MaternityBagRepository.guardianItems.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRecItemTitle by remember { mutableStateOf<String?>(null) }

    val tabs = listOf("1. 산모 용품", "2. 신생아 용품", "3. 보호자 & 공통")

    val currentList = when (selectedTabIndex) {
        0 -> maternityItems
        1 -> babyItems
        else -> guardianItems
    }

    val selectedCategoryName = when (selectedTabIndex) {
        0 -> "산모 용품"
        1 -> "신생아 용품"
        else -> "보호자 & 공통"
    }

    // 1. 실시간 검색 및 태그 필터링
    val filteredList = remember(currentList, searchQuery, selectedFilterTag, savedIds) {
        currentList.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                item.title.lowercase().contains(q) ||
                    item.note.lowercase().contains(q) ||
                    item.locationTags.any { it.lowercase().contains(q) }
            }

            val matchesFilter = when (selectedFilterTag) {
                "전체" -> true
                "👜 담김" -> savedIds.contains(item.id)
                "#병원", "#조리원", "#퇴원퇴소" -> item.locationTags.contains(selectedFilterTag)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    // 2. 컬럼별 정렬 (클릭 1회: 오름차순, 2회: 내림차순, 3회: 기본)
    val displayList = remember(filteredList, sortColumn, isSortAscending, savedIds) {
        if (sortColumn == null) {
            filteredList
        } else {
            val comparator = when (sortColumn!!) {
                MaternitySortColumn.TITLE -> compareBy<MaternityBagItem> { it.title }
                MaternitySortColumn.TAG -> compareBy { it.locationTags.joinToString() }
                MaternitySortColumn.SAVED -> compareBy { savedIds.contains(it.id) }
            }
            if (isSortAscending) filteredList.sortedWith(comparator) else filteredList.sortedWith(comparator.reversed())
        }
    }

    val groupedItems = remember(displayList, sortColumn, isSortAscending) {
        if (sortColumn == null) {
            displayList.groupBy { it.section.ifEmpty { "기타 항목" } }
        } else {
            val sortLabel = when (sortColumn!!) {
                MaternitySortColumn.TITLE -> "준비물 이름순 정렬"
                MaternitySortColumn.TAG -> "장소 태그순 정렬"
                MaternitySortColumn.SAVED -> "내 가방 담김순 정렬"
            } + if (isSortAscending) " (오름차순 ▲)" else " (내림차순 ▼)"
            mapOf(sortLabel to displayList)
        }
    }

    fun onHeaderSortClick(column: MaternitySortColumn) {
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

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        containerColor = WarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "출산가방 체크리스트",
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
            // 상단 안내 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PeachLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "출산 준비물 관리 👜",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "실시간 검색, 태그 필터, 헤더 클릭 정렬로 쉽게 찾아보세요.",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_maternity_bag),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // 3개 카테고리 탭 (산모 용품, 신생아 용품, 보호자 & 공통)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = WarmBackground,
                contentColor = PeachPrimary,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = PeachPrimary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) PeachPrimary else TextDark
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 반으로 분할된 준비물 추가 & 삭제 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 좌측: 준비물 추가 버튼
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "준비물 추가",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // 우측: 준비물 삭제 버튼 (삭제 모드 토글)
                Button(
                    onClick = { isDeleteMode = !isDeleteMode },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDeleteMode) Color(0xFFD32F2F) else Color(0xFF757575)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDeleteMode) "삭제 완료 ✓" else "준비물 삭제",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // 1. 실시간 검색창
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                placeholder = { Text("준비물, 태그, 메모 검색...", fontSize = 12.5.sp, color = TextMuted) },
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
                        IconButton(onClick = { searchQuery = "" }) {
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

            // 2. 필터 칩 목록 (전체 | #병원 | #조리원 | #퇴원퇴소 | 👜 담김)
            val filterOptions = listOf("전체", "#병원", "#조리원", "#퇴원퇴소", "👜 담김")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp),
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
                            .clickable { selectedFilterTag = filterTag }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filterTag,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextDark,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 3. 정렬 가능한 준비물 리스트 헤더 (준비물 (수량·메모) | 장소 | 내가방 넣기 또는 삭제) - 모두 가운데 정렬
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDeleteMode) Color(0xFFFFEBEE) else Color(0xFFF1EAE6))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 준비물 (TITLE)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(MaternitySortColumn.TITLE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "준비물" + if (sortColumn == MaternitySortColumn.TITLE) (if (isSortAscending) " ▲" else " ▼") else " ⇅",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == MaternitySortColumn.TITLE) PeachPrimary else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 장소 (TAG)
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(MaternitySortColumn.TAG) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "장소" + if (sortColumn == MaternitySortColumn.TAG) (if (isSortAscending) " ▲" else " ▼") else " ⇅",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == MaternitySortColumn.TAG) PeachPrimary else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 가방담기 (SAVED) 또는 삭제
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            if (!isDeleteMode) onHeaderSortClick(MaternitySortColumn.SAVED)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDeleteMode) "삭제" else ("가방담기" + if (sortColumn == MaternitySortColumn.SAVED) (if (isSortAscending) " ▲" else " ▼") else " ⇅"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeleteMode) Color(0xFFD32F2F) else PeachPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // 준비물 리스트 또는 결과 없음 안내
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "검색 또는 필터 조건에 맞는 항목이 없습니다.",
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedItems.forEach { (sectionName, items) ->
                        item(key = "section_$sectionName") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFF2EF))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "❖ $sectionName (${items.size})",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PeachPrimary
                                )
                            }
                        }

                        items(items, key = { it.id }) { item ->
                            val isSaved = savedIds.contains(item.id)
                            MaternityBagItemRow(
                                item = item,
                                isSaved = isSaved,
                                isDeleteMode = isDeleteMode,
                                onToggleSaved = {
                                    MaternityBagRepository.toggleItemSaved(item.id)
                                    scope.launch {
                                        val msg = if (!isSaved) {
                                            "'${item.title}'이(가) 내 가방에 담겼습니다 👜"
                                        } else {
                                            "'${item.title}'이(가) 내 가방에서 제외되었습니다"
                                        }
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                },
                                onDeleteItem = {
                                    MaternityBagRepository.deleteItem(item.id)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("'${item.title}'이(가) 삭제되었습니다 🗑️")
                                    }
                                },
                                onOpenRecommendation = {
                                    selectedRecItemTitle = item.title
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // 준비물 추가 모달 대화상자
    if (showAddDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var inputQty by remember { mutableStateOf("") }
        var selectedPlaceHospital by remember { mutableStateOf(true) }
        var selectedPlaceCareCenter by remember { mutableStateOf(true) }
        var selectedPlaceLeave by remember { mutableStateOf(false) }
        var inputNote by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "[$selectedCategoryName] 준비물 추가 ✍️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "현재 선택된 [$selectedCategoryName] 탭에 새 준비물을 등록합니다.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("준비물 이름 (예: 애착 인형)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PeachPrimary,
                            focusedLabelColor = PeachPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputQty,
                        onValueChange = { inputQty = it },
                        label = { Text("수량 (예: 1개, 선택사항)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PeachPrimary,
                            focusedLabelColor = PeachPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "장소 태그 선택", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TagToggleButton(
                            text = "#병원",
                            isSelected = selectedPlaceHospital,
                            onToggle = { selectedPlaceHospital = !selectedPlaceHospital }
                        )
                        TagToggleButton(
                            text = "#조리원",
                            isSelected = selectedPlaceCareCenter,
                            onToggle = { selectedPlaceCareCenter = !selectedPlaceCareCenter }
                        )
                        TagToggleButton(
                            text = "#퇴원퇴소",
                            isSelected = selectedPlaceLeave,
                            onToggle = { selectedPlaceLeave = !selectedPlaceLeave }
                        )
                    }

                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("참고사항 메모 (선택사항, 1줄)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PeachPrimary,
                            focusedLabelColor = PeachPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputTitle.isNotBlank()) {
                            val locationTags = mutableListOf<String>()
                            if (selectedPlaceHospital) locationTags.add("#병원")
                            if (selectedPlaceCareCenter) locationTags.add("#조리원")
                            if (selectedPlaceLeave) locationTags.add("#퇴원퇴소")

                            val mergedTitle = if (inputQty.isNotBlank() && !inputTitle.contains(inputQty)) {
                                "${inputTitle.trim()} (${inputQty.trim()})"
                            } else {
                                inputTitle.trim()
                            }

                            MaternityBagRepository.addItemToTab(
                                tabCategory = selectedCategoryName,
                                title = mergedTitle,
                                recommendedQty = "",
                                locationTags = locationTags,
                                priorityTag = "",
                                note = inputNote.trim()
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar("'$mergedTitle'이(가) [$selectedCategoryName]에 추가되었습니다 👜")
                            }
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary)
                ) {
                    Text("확인", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("취소", color = TextMuted)
                }
            }
        )
    }

    // 선배맘 추천 TOP 3 탑시트 모달
    TopSheetRecommendationModal(
        visible = selectedRecItemTitle != null,
        itemTitle = selectedRecItemTitle,
        onDismiss = { selectedRecItemTitle = null }
    )
}

@Composable
fun TagToggleButton(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) PeachPrimary.copy(alpha = 0.15f) else Color(0xFFF0F0F0))
            .border(
                width = 1.dp,
                color = if (isSelected) PeachPrimary else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PeachPrimary else TextMuted
        )
    }
}

/* ========================================================
   준비물 체크리스트 개별 행 아이템
   [준비물 (수량·메모) | 장소 | 내가방 넣기 / 삭제 버튼]
   - 전체 가운데 정렬
   - 모든 텍스트 1줄 (TextOverflow.Ellipsis)
======================================================== */
@Composable
fun MaternityBagItemRow(
    item: MaternityBagItem,
    isSaved: Boolean,
    isDeleteMode: Boolean,
    onToggleSaved: () -> Unit,
    onDeleteItem: () -> Unit,
    onOpenRecommendation: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                if (isDeleteMode) onDeleteItem() else onToggleSaved()
            }),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSaved) Color(0xFFFFF8F6) else SoftSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. 준비물 이름 + 1줄 참고사항 메모 (가운데 정렬)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
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
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 2. 장소 태그 (가운데 정렬)
                Box(
                    modifier = Modifier.width(75.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item.locationTags.forEach { locTag ->
                            LocationTagChip(tag = locTag)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 3. 가방담기 (체크박스) 또는 삭제 버튼 (가운데 정렬)
                Box(
                    modifier = Modifier.width(76.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDeleteMode) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFD32F2F))
                                .clickable { onDeleteItem() }
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "삭제 🗑️",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    } else {
                        Checkbox(
                            checked = isSaved,
                            onCheckedChange = { onToggleSaved() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PeachPrimary,
                                uncheckedColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }
            }

            // 4. 하단: 좌측(맘카페 언급 1위) + 우측(선배맘 추천 TOP 3 칩 버튼)
            if (!isDeleteMode) {
                val momcafeProd = RecommendationRepository.getMomcafeRank1Product(item.title)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 좌측: ♥ 맘카페 언급 1위 [xxx] 배지
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFF0F3))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFFFCCD5),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(onClick = onOpenRecommendation)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "♥", fontSize = 10.sp, color = Color(0xFFFF2E63), fontWeight = FontWeight.Black)
                            Text(
                                text = "맘카페 언급 1위",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD62246)
                            )
                            Text(
                                text = momcafeProd,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E1934),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 우측: 선배맘 추천 TOP 3 칩 버튼
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFF0EC))
                            .border(
                                width = 1.dp,
                                color = PeachPrimary.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(onClick = onOpenRecommendation)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "⭐", fontSize = 10.sp)
                            Text(
                                text = "선배맘 추천 TOP 3",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD3543A)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationTagChip(tag: String) {
    val bg = when (tag) {
        "#병원" -> Color(0xFFE8F0FE)
        "#조리원" -> Color(0xFFE6F4EA)
        "#퇴원퇴소" -> Color(0xFFFEF7E0)
        else -> Color(0xFFF1F3F4)
    }
    val textColor = when (tag) {
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
            text = tag,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
fun SavedMaternityBagScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    com.example.maternitybag.ui.mybag.MyBagScreen(
        onNavigateBack = onNavigateBack,
        initialTab = 1,
        modifier = modifier
    )
}

