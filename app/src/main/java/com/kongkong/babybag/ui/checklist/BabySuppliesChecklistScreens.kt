package com.kongkong.babybag.ui.checklist

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kongkong.babybag.data.BabySupplyItem
import com.kongkong.babybag.data.BabySuppliesRepository
import com.kongkong.babybag.data.RecommendationRepository
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground
import com.kongkong.babybag.ui.components.TopSheetRecommendationModal
import kotlinx.coroutines.launch

enum class BabySupplySortColumn {
    TITLE, PERIOD, PURCHASE, SAVED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabySuppliesChecklistScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isDeleteMode by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("?ÑÏ≤¥") }
    var sortColumn by remember { mutableStateOf<BabySupplySortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    val savedIds by BabySuppliesRepository.savedItemIds.collectAsState()
    val feedItems by BabySuppliesRepository.feedItems.collectAsState()
    val sleepItems by BabySuppliesRepository.sleepItems.collectAsState()
    val careItems by BabySuppliesRepository.careItems.collectAsState()
    val outItems by BabySuppliesRepository.outItems.collectAsState()

    val currentItems = when (selectedTabIndex) {
        0 -> feedItems
        1 -> sleepItems
        2 -> careItems
        else -> outItems
    }

    val tabs = listOf("1. Î®πÏù¥Í∏?, "2. ?¨Ïö∞Í∏?& ?¨Í∏∞", "3. ?ªÍ∏∞ & ÏºÄ??, "4. ?∏Ï∂ú & ?¥Îèô")
    val tabCategoryNames = listOf(
        "1. Î®πÏù¥Í∏?(?òÏú† & ?¥Ïú†??",
        "2. ?¨Ïö∞Í∏?& ?¨Í∏∞ (?òÎ©¥ & Í≥µÍ∞Ñ¬∑?Ä??",
        "3. ?ªÍ∏∞ & ÏºÄ??(?ÑÏÉù¬∑Í±¥Í∞ï & ?òÎ•ò)",
        "4. ?∏Ï∂ú & ?¥Îèô"
    )

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRecItemTitle by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 1. ?§ÏãúÍ∞?Í≤Ä??Î∞??úÍ∑∏ ?ÑÌÑ∞Îß?
    val filteredItems = remember(currentItems, searchQuery, selectedFilterTag, savedIds) {
        currentItems.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                item.title.lowercase().contains(q) ||
                    item.note.lowercase().contains(q) ||
                    item.purchaseTag.lowercase().contains(q) ||
                    item.periodTags.any { it.lowercase().contains(q) }
            }

            val matchesFilter = when (selectedFilterTag) {
                "?ÑÏ≤¥" -> true
                "?ëú Ï∞úÌïúÍ≤? -> savedIds.contains(item.id)
                "#?πÍ∑º", "#?àÏ†ú?? -> item.purchaseTag == selectedFilterTag
                "#?†ÏÉù??, "#?ÅÏïÑ", "#?†ÏïÑ" -> item.periodTags.contains(selectedFilterTag)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    // 2. Ïª¨Îüº ?ïÎ†¨
    val displayItems = remember(filteredItems, sortColumn, isSortAscending, savedIds) {
        if (sortColumn == null) {
            filteredItems
        } else {
            val comparator = when (sortColumn!!) {
                BabySupplySortColumn.TITLE -> compareBy<BabySupplyItem> { it.title }
                BabySupplySortColumn.PERIOD -> compareBy { it.periodTags.joinToString() }
                BabySupplySortColumn.PURCHASE -> compareBy { it.purchaseTag }
                BabySupplySortColumn.SAVED -> compareBy { savedIds.contains(it.id) }
            }
            if (isSortAscending) filteredItems.sortedWith(comparator) else filteredItems.sortedWith(comparator.reversed())
        }
    }

    val grouped = remember(displayItems, sortColumn, isSortAscending) {
        if (sortColumn == null) {
            displayItems.groupBy { it.section.ifBlank { "?ºÎ∞ò ?©Ìíà" } }
        } else {
            val sortLabel = when (sortColumn!!) {
                BabySupplySortColumn.TITLE -> "Ï§ÄÎπÑÎ¨º ?¥Î¶Ñ???ïÎ†¨"
                BabySupplySortColumn.PERIOD -> "?¨Ïö© ?úÍ∏∞???ïÎ†¨"
                BabySupplySortColumn.PURCHASE -> "Íµ¨ÏûÖ Í≤ΩÎ°ú???ïÎ†¨"
                BabySupplySortColumn.SAVED -> "??Í∞ÄÎ∞?Ï∞úÌïú???ïÎ†¨"
            } + if (isSortAscending) " (?§Î¶ÑÏ∞®Ïàú ??" else " (?¥Î¶ºÏ∞®Ïàú ??"
            mapOf(sortLabel to displayItems)
        }
    }

    fun onHeaderSortClick(column: BabySupplySortColumn) {
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
                        text = "?°ÏïÑ?©Ìíà Ï≤¥ÌÅ¨Î¶¨Ïä§??,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "?§Î°úÍ∞ÄÍ∏?,
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
                .padding(horizontal = 14.dp)
        ) {
            // 4Í∞???
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = WarmBackground,
                contentColor = Color(0xFF43A047),
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF43A047),
                            height = 3.dp
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
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color(0xFF43A047) else TextDark
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ?ÅÎã® Î∂ÑÌï† Î≤ÑÌäº: [+ ?©Ìíà Ï∂îÍ?] | [?óëÔ∏??©Ìíà ??†ú]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "+ ?©Ìíà Ï∂îÍ?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = { isDeleteMode = !isDeleteMode },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDeleteMode) Color(0xFFFFEBEE) else SoftSurface,
                        contentColor = if (isDeleteMode) Color(0xFFD32F2F) else TextDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isDeleteMode) Color(0xFFD32F2F) else Color(0xFFE0E0E0)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isDeleteMode) Color(0xFFD32F2F) else TextDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDeleteMode) "??†ú ?ÑÎ£å ?? else "?óëÔ∏??©Ìíà ??†ú",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 1. ?§ÏãúÍ∞?Í≤Ä?âÏ∞Ω
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                placeholder = { Text("?©ÌíàÎ™? ?úÍ∑∏, Î©îÎ™® Í≤Ä??..", fontSize = 12.5.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Í≤Ä??,
                        tint = Color(0xFF43A047),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Í≤Ä?âÏñ¥ ÏßÄ?∞Í∏∞",
                                tint = TextMuted,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF43A047),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 2. ?ÑÌÑ∞ Ïπ?Î™©Î°ù (?ÑÏ≤¥ | #?πÍ∑º | #?àÏ†ú??| #?†ÏÉù??| #?ÅÏïÑ | #?†ÏïÑ | ?ëú Ï∞úÌïúÍ≤?
            val filterOptions = listOf("?ÑÏ≤¥", "#?πÍ∑º", "#?àÏ†ú??, "#?†ÏÉù??, "#?ÅÏïÑ", "#?†ÏïÑ", "?ëú Ï∞úÌïúÍ≤?)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filterOptions) { filterTag ->
                    val isSelected = selectedFilterTag == filterTag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF43A047) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF43A047) else Color(0xFFE0E0E0),
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
                            color = if (isSelected) Color.White else TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. ?ïÎ†¨ Í∞Ä?•Ìïú ?åÏù¥Î∏??§Îçî: [Ï§ÄÎπÑÎ¨º | ?úÍ∏∞ | Íµ¨ÏûÖÍ≤ΩÎ°ú | Í∞ÄÎ∞©Îã¥Í∏?
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ï§ÄÎπÑÎ¨º (TITLE)
                Box(
                    modifier = Modifier
                        .weight(1.8f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(BabySupplySortColumn.TITLE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ï§ÄÎπÑÎ¨º" + if (sortColumn == BabySupplySortColumn.TITLE) (if (isSortAscending) " ?? else " ??) else " ??,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == BabySupplySortColumn.TITLE) Color(0xFF2E7D32) else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // ?úÍ∏∞ (PERIOD)
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(BabySupplySortColumn.PERIOD) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "?úÍ∏∞" + if (sortColumn == BabySupplySortColumn.PERIOD) (if (isSortAscending) " ?? else " ??) else " ??,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == BabySupplySortColumn.PERIOD) Color(0xFF2E7D32) else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Íµ¨ÏûÖÍ≤ΩÎ°ú (PURCHASE)
                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(BabySupplySortColumn.PURCHASE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Íµ¨ÏûÖÍ≤ΩÎ°ú" + if (sortColumn == BabySupplySortColumn.PURCHASE) (if (isSortAscending) " ?? else " ??) else " ??,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == BabySupplySortColumn.PURCHASE) Color(0xFF2E7D32) else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Í∞ÄÎ∞©Îã¥Í∏?(SAVED)
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            if (!isDeleteMode) onHeaderSortClick(BabySupplySortColumn.SAVED)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDeleteMode) "??†ú" else ("Í∞ÄÎ∞©Îã¥Í∏? + if (sortColumn == BabySupplySortColumn.SAVED) (if (isSortAscending) " ?? else " ??) else " ??),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeleteMode) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Î™©Î°ù ?úÏãú (?åÎ∂ÑÎ•??πÏÖòÎ≥?Íµ¨Î∂Ñ ?êÎäî Í≤∞Í≥º ?ÜÏùå ?àÎÇ¥)
            if (displayItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Í≤Ä???êÎäî ?ÑÌÑ∞ Ï°∞Í±¥??ÎßûÎäî ?°ÏïÑ?©Ìíà???ÜÏäµ?àÎã§.", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Í≤Ä?âÏñ¥Î•?Î≥ÄÍ≤ΩÌïòÍ±∞ÎÇò ?ÑÌÑ∞Î•?'?ÑÏ≤¥'Î°??¨ÏÑ§?ïÌï¥ Î≥¥ÏÑ∏??", fontSize = 12.sp, color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    grouped.forEach { (sectionName, items) ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "??$sectionName (${items.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        items(items, key = { it.id }) { item ->
                            val isSaved = savedIds.contains(item.id)
                            BabySupplyItemRow(
                                item = item,
                                isSaved = isSaved,
                                isDeleteMode = isDeleteMode,
                                onToggleSaved = {
                                    BabySuppliesRepository.toggleItemSaved(item.id)
                                    val msg = if (isSaved) "'${item.title}' ??Í∞ÄÎ∞??úÏô∏" else "?ëú '${item.title}' Ï∞úÌïú ?°ÏïÑ?©Ìíà ?¥Í?!"
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                },
                                onDeleteItem = {
                                    BabySuppliesRepository.deleteItem(item.id)
                                    scope.launch { snackbarHostState.showSnackbar("'${item.title}' ??†ú???óëÔ∏?) }
                                },
                                onOpenRecommendation = {
                                    selectedRecItemTitle = item.title
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ?©Ìíà Ï∂îÍ? Î™®Îã¨ ?§Ïù¥?ºÎ°úÍ∑?
    if (showAddDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var selectedPurchase by remember { mutableStateOf("#?àÏ†ú??) }
        var selectedNewborn by remember { mutableStateOf(true) }
        var selectedInfant by remember { mutableStateOf(false) }
        var selectedToddler by remember { mutableStateOf(false) }
        var selectedAll by remember { mutableStateOf(false) }
        var inputNote by remember { mutableStateOf("") }

        val selectedCategoryName = tabCategoryNames[selectedTabIndex]

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "[$selectedCategoryName] ?©Ìíà Ï∂îÍ? ?çÔ∏è",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "?†ÌÉù????óê ???°ÏïÑ?©Ìíà???±Î°ù?©Îãà??",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("?©Ìíà ?¥Î¶Ñ (?? Î∂ÑÏú† ?êÏù¥Ïª?") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF43A047),
                            focusedLabelColor = Color(0xFF43A047)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "?úÍ∏∞ ?úÍ∑∏ ?†ÌÉù", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TagToggleButton(text = "#?†ÏÉù??, isSelected = selectedNewborn, onToggle = { selectedNewborn = !selectedNewborn })
                        TagToggleButton(text = "#?ÅÏïÑ", isSelected = selectedInfant, onToggle = { selectedInfant = !selectedInfant })
                        TagToggleButton(text = "#?†ÏïÑ", isSelected = selectedToddler, onToggle = { selectedToddler = !selectedToddler })
                        TagToggleButton(text = "#?ÑÏ≤¥", isSelected = selectedAll, onToggle = { selectedAll = !selectedAll })
                    }

                    Text(text = "Íµ¨ÏûÖÍ≤ΩÎ°ú ?†ÌÉù", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedPurchase = "#?πÍ∑º" }) {
                            RadioButton(
                                selected = selectedPurchase == "#?πÍ∑º",
                                onClick = { selectedPurchase = "#?πÍ∑º" },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE65100))
                            )
                            Text("#?πÍ∑º (Ï§ëÍ≥†)", fontSize = 13.sp, color = TextDark)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedPurchase = "#?àÏ†ú?? }) {
                            RadioButton(
                                selected = selectedPurchase == "#?àÏ†ú??,
                                onClick = { selectedPurchase = "#?àÏ†ú?? },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF43A047))
                            )
                            Text("#?àÏ†ú??, fontSize = 13.sp, color = TextDark)
                        }
                    }

                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Î©îÎ™® (?†ÌÉù?¨Ìï≠)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF43A047),
                            focusedLabelColor = Color(0xFF43A047)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputTitle.isNotBlank()) {
                            val periodTags = mutableListOf<String>()
                            if (selectedNewborn) periodTags.add("#?†ÏÉù??)
                            if (selectedInfant) periodTags.add("#?ÅÏïÑ")
                            if (selectedToddler) periodTags.add("#?†ÏïÑ")
                            if (selectedAll || periodTags.isEmpty()) periodTags.add("#?ÑÏ≤¥")

                            BabySuppliesRepository.addItemToTab(
                                tabCategory = selectedCategoryName,
                                title = inputTitle.trim(),
                                periodTags = periodTags,
                                purchaseTag = selectedPurchase,
                                note = inputNote.trim()
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar("'${inputTitle.trim()}'??Í∞Ä) ?±Î°ù?òÏóà?µÎãà???çº")
                            }
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("?ïÏù∏", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Ï∑®ÏÜå", color = TextMuted)
                }
            }
        )
    }

    // ?†Î∞∞Îß?Ï∂îÏ≤ú TOP 3 ?ëÏãú??Î™®Îã¨
    TopSheetRecommendationModal(
        visible = selectedRecItemTitle != null,
        itemTitle = selectedRecItemTitle,
        onDismiss = { selectedRecItemTitle = null }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BabySupplyItemRow(
    item: BabySupplyItem,
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
            containerColor = if (isSaved) Color(0xFFF1F8E9) else SoftSurface
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
                // 1. Ï§ÄÎπÑÎ¨º ?¥Î¶Ñ + Î©îÎ™® (Í∞Ä?¥Îç∞ ?ïÎ†¨)
                Column(
                    modifier = Modifier.weight(1.8f),
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

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ?úÍ∏∞ ?úÍ∑∏ (?ÑÏïÑ???∏Î°ú Î∞∞Ïπò)
                Column(
                    modifier = Modifier.width(56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item.periodTags.forEach { tag ->
                        PeriodTagChip(tag = tag)
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. Íµ¨ÏûÖÍ≤ΩÎ°ú ?úÍ∑∏
                Box(
                    modifier = Modifier.width(65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PurchaseTagChip(tag = item.purchaseTag)
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 4. Í∞ÄÎ∞©Îã¥Í∏?(Ï≤¥ÌÅ¨Î∞ïÏä§) ?êÎäî ??†ú Î≤ÑÌäº
                Box(
                    modifier = Modifier.width(72.dp),
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
                                text = "??†ú ?óëÔ∏?,
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
                                checkedColor = Color(0xFF43A047),
                                uncheckedColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }
            }

            // 5. ?òÎã®: Ï¢åÏ∏°(ÎßòÏπ¥???∏Í∏â 1?? + ?∞Ï∏°(?†Î∞∞Îß?Ï∂îÏ≤ú TOP 3 Ïπ?Î≤ÑÌäº)
            if (!isDeleteMode) {
                val momcafeProd = RecommendationRepository.getMomcafeRank1Product(item.title)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ï¢åÏ∏°: ??ÎßòÏπ¥???∏Í∏â 1??[xxx] Î∞∞Ï?
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
                            Text(text = "??, fontSize = 10.sp, color = Color(0xFFFF2E63), fontWeight = FontWeight.Black)
                            Text(
                                text = "ÎßòÏπ¥???∏Í∏â 1??,
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

                    // ?∞Ï∏°: ?†Î∞∞Îß?Ï∂îÏ≤ú TOP 3 Ïπ?Î≤ÑÌäº
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFF0EC))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFEB6E55).copy(alpha = 0.45f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(onClick = onOpenRecommendation)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "‚≠?, fontSize = 10.sp)
                            Text(
                                text = "?†Î∞∞Îß?Ï∂îÏ≤ú TOP 3",
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
fun PeriodTagChip(tag: String) {
    val bg = when (tag) {
        "#?†ÏÉù?? -> Color(0xFFEDE7F6)
        "#?ÅÏïÑ" -> Color(0xFFE3F2FD)
        "#?†ÏïÑ" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }
    val textColor = when (tag) {
        "#?†ÏÉù?? -> Color(0xFF673AB7)
        "#?ÅÏïÑ" -> Color(0xFF1976D2)
        "#?†ÏïÑ" -> Color(0xFFE65100)
        else -> Color(0xFF616161)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun PurchaseTagChip(tag: String) {
    val isCarrot = tag == "#?πÍ∑º"
    val bg = if (isCarrot) Color(0xFFFFE0B2) else Color(0xFFE8F5E9)
    val textColor = if (isCarrot) Color(0xFFE65100) else Color(0xFF2E7D32)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
