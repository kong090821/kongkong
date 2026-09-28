package com.kongkong.babybag.ui.checklist

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
import com.kongkong.babybag.R
import com.kongkong.babybag.data.MaternityBagItem
import com.kongkong.babybag.data.MaternityBagRepository
import com.kongkong.babybag.data.RecommendationRepository
import com.kongkong.babybag.theme.PeachLight
import com.kongkong.babybag.theme.PeachPrimary
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground
import com.kongkong.babybag.ui.components.TopSheetRecommendationModal
import kotlinx.coroutines.launch

enum class MaternitySortColumn {
    TITLE, TAG, SAVED
}

/* ========================================================
   1. Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º Ï≤¥ÌÅ¨Î¶¨Ïä§???îÎ©¥ (3Í∞???+ Ï∂îÍ?/??†úÎ≤ÑÌäº Î∂ÑÌï†)
   1Î≤??? ?∞Î™® ?©Ìíà
   2Î≤??? ?†ÏÉù???©Ìíà
   3Î≤??? Î≥¥Ìò∏??& Í≥µÌÜµ
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
    var selectedFilterTag by remember { mutableStateOf("?ÑÏ≤¥") }
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

    val tabs = listOf("1. ?∞Î™® ?©Ìíà", "2. ?†ÏÉù???©Ìíà", "3. Î≥¥Ìò∏??& Í≥µÌÜµ")

    val currentList = when (selectedTabIndex) {
        0 -> maternityItems
        1 -> babyItems
        else -> guardianItems
    }

    val selectedCategoryName = when (selectedTabIndex) {
        0 -> "?∞Î™® ?©Ìíà"
        1 -> "?†ÏÉù???©Ìíà"
        else -> "Î≥¥Ìò∏??& Í≥µÌÜµ"
    }

    // 1. ?§ÏãúÍ∞?Í≤Ä??Î∞??úÍ∑∏ ?ÑÌÑ∞Îß?
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
                "?ÑÏ≤¥" -> true
                "?ëú ?¥Í?" -> savedIds.contains(item.id)
                "#Î≥ëÏõê", "#Ï°∞Î¶¨??, "#?¥Ïõê?¥ÏÜå" -> item.locationTags.contains(selectedFilterTag)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    // 2. Ïª¨ÎüºÎ≥??ïÎ†¨ (?¥Î¶≠ 1?? ?§Î¶ÑÏ∞®Ïàú, 2?? ?¥Î¶ºÏ∞®Ïàú, 3?? Í∏∞Î≥∏)
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
            displayList.groupBy { it.section.ifEmpty { "Í∏∞Ì? ??™©" } }
        } else {
            val sortLabel = when (sortColumn!!) {
                MaternitySortColumn.TITLE -> "Ï§ÄÎπÑÎ¨º ?¥Î¶Ñ???ïÎ†¨"
                MaternitySortColumn.TAG -> "?•ÏÜå ?úÍ∑∏???ïÎ†¨"
                MaternitySortColumn.SAVED -> "??Í∞ÄÎ∞??¥Í????ïÎ†¨"
            } + if (isSortAscending) " (?§Î¶ÑÏ∞®Ïàú ??" else " (?¥Î¶ºÏ∞®Ïàú ??"
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
                        text = "Ï∂úÏÇ∞Í∞ÄÎ∞?Ï≤¥ÌÅ¨Î¶¨Ïä§??,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
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
        ) {
            // ?ÅÎã® ?àÎÇ¥ Ïπ¥Îìú
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
                            text = "Ï∂úÏÇ∞ Ï§ÄÎπÑÎ¨º Í¥ÄÎ¶??ëú",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "?§ÏãúÍ∞?Í≤Ä?? ?úÍ∑∏ ?ÑÌÑ∞, ?§Îçî ?¥Î¶≠ ?ïÎ†¨Î°??ΩÍ≤å Ï∞æÏïÑÎ≥¥ÏÑ∏??",
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

            // 3Í∞?Ïπ¥ÌÖåÍ≥†Î¶¨ ??(?∞Î™® ?©Ìíà, ?†ÏÉù???©Ìíà, Î≥¥Ìò∏??& Í≥µÌÜµ)
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

            // Î∞òÏúºÎ°?Î∂ÑÌï†??Ï§ÄÎπÑÎ¨º Ï∂îÍ? & ??†ú Î≤ÑÌäº
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ï¢åÏ∏°: Ï§ÄÎπÑÎ¨º Ï∂îÍ? Î≤ÑÌäº
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
                        text = "Ï§ÄÎπÑÎ¨º Ï∂îÍ?",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // ?∞Ï∏°: Ï§ÄÎπÑÎ¨º ??†ú Î≤ÑÌäº (??†ú Î™®Îìú ?†Í?)
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
                        text = if (isDeleteMode) "??†ú ?ÑÎ£å ?? else "Ï§ÄÎπÑÎ¨º ??†ú",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // 1. ?§ÏãúÍ∞?Í≤Ä?âÏ∞Ω
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                placeholder = { Text("Ï§ÄÎπÑÎ¨º, ?úÍ∑∏, Î©îÎ™® Í≤Ä??..", fontSize = 12.5.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Í≤Ä??,
                        tint = PeachPrimary,
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
                    focusedBorderColor = PeachPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 2. ?ÑÌÑ∞ Ïπ?Î™©Î°ù (?ÑÏ≤¥ | #Î≥ëÏõê | #Ï°∞Î¶¨??| #?¥Ïõê?¥ÏÜå | ?ëú ?¥Í?)
            val filterOptions = listOf("?ÑÏ≤¥", "#Î≥ëÏõê", "#Ï°∞Î¶¨??, "#?¥Ïõê?¥ÏÜå", "?ëú ?¥Í?")
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

            // 3. ?ïÎ†¨ Í∞Ä?•Ìïú Ï§ÄÎπÑÎ¨º Î¶¨Ïä§???§Îçî (Ï§ÄÎπÑÎ¨º (?òÎüâ¬∑Î©îÎ™®) | ?•ÏÜå | ?¥Í?Î∞??£Í∏∞ ?êÎäî ??†ú) - Î™®Îëê Í∞Ä?¥Îç∞ ?ïÎ†¨
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDeleteMode) Color(0xFFFFEBEE) else Color(0xFFF1EAE6))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ï§ÄÎπÑÎ¨º (TITLE)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(MaternitySortColumn.TITLE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ï§ÄÎπÑÎ¨º" + if (sortColumn == MaternitySortColumn.TITLE) (if (isSortAscending) " ?? else " ??) else " ??,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == MaternitySortColumn.TITLE) PeachPrimary else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // ?•ÏÜå (TAG)
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onHeaderSortClick(MaternitySortColumn.TAG) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "?•ÏÜå" + if (sortColumn == MaternitySortColumn.TAG) (if (isSortAscending) " ?? else " ??) else " ??,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sortColumn == MaternitySortColumn.TAG) PeachPrimary else TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Í∞ÄÎ∞©Îã¥Í∏?(SAVED) ?êÎäî ??†ú
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
                        text = if (isDeleteMode) "??†ú" else ("Í∞ÄÎ∞©Îã¥Í∏? + if (sortColumn == MaternitySortColumn.SAVED) (if (isSortAscending) " ?? else " ??) else " ??),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeleteMode) Color(0xFFD32F2F) else PeachPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Ï§ÄÎπÑÎ¨º Î¶¨Ïä§???êÎäî Í≤∞Í≥º ?ÜÏùå ?àÎÇ¥
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Í≤Ä???êÎäî ?ÑÌÑ∞ Ï°∞Í±¥??ÎßûÎäî ??™©???ÜÏäµ?àÎã§.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Í≤Ä?âÏñ¥Î•?Î≥ÄÍ≤ΩÌïòÍ±∞ÎÇò ?ÑÌÑ∞Î•?'?ÑÏ≤¥'Î°??¨ÏÑ§?ïÌï¥ Î≥¥ÏÑ∏??",
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
                                    text = "??$sectionName (${items.size})",
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
                                            "'${item.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê ?¥Í≤º?µÎãà???ëú"
                                        } else {
                                            "'${item.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê???úÏô∏?òÏóà?µÎãà??
                                        }
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                },
                                onDeleteItem = {
                                    MaternityBagRepository.deleteItem(item.id)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("'${item.title}'??Í∞Ä) ??†ú?òÏóà?µÎãà???óëÔ∏?)
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

    // Ï§ÄÎπÑÎ¨º Ï∂îÍ? Î™®Îã¨ ?Ä?îÏÉÅ??
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
                    text = "[$selectedCategoryName] Ï§ÄÎπÑÎ¨º Ï∂îÍ? ?çÔ∏è",
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
                        text = "?ÑÏû¨ ?†ÌÉù??[$selectedCategoryName] ??óê ??Ï§ÄÎπÑÎ¨º???±Î°ù?©Îãà??",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Ï§ÄÎπÑÎ¨º ?¥Î¶Ñ (?? ?†Ï∞© ?∏Ìòï)") },
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
                        label = { Text("?òÎüâ (?? 1Í∞? ?†ÌÉù?¨Ìï≠)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PeachPrimary,
                            focusedLabelColor = PeachPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "?•ÏÜå ?úÍ∑∏ ?†ÌÉù", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TagToggleButton(
                            text = "#Î≥ëÏõê",
                            isSelected = selectedPlaceHospital,
                            onToggle = { selectedPlaceHospital = !selectedPlaceHospital }
                        )
                        TagToggleButton(
                            text = "#Ï°∞Î¶¨??,
                            isSelected = selectedPlaceCareCenter,
                            onToggle = { selectedPlaceCareCenter = !selectedPlaceCareCenter }
                        )
                        TagToggleButton(
                            text = "#?¥Ïõê?¥ÏÜå",
                            isSelected = selectedPlaceLeave,
                            onToggle = { selectedPlaceLeave = !selectedPlaceLeave }
                        )
                    }

                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Ï∞∏Í≥†?¨Ìï≠ Î©îÎ™® (?†ÌÉù?¨Ìï≠, 1Ï§?") },
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
                            if (selectedPlaceHospital) locationTags.add("#Î≥ëÏõê")
                            if (selectedPlaceCareCenter) locationTags.add("#Ï°∞Î¶¨??)
                            if (selectedPlaceLeave) locationTags.add("#?¥Ïõê?¥ÏÜå")

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
                                snackbarHostState.showSnackbar("'$mergedTitle'??Í∞Ä) [$selectedCategoryName]??Ï∂îÍ??òÏóà?µÎãà???ëú")
                            }
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary)
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
   Ï§ÄÎπÑÎ¨º Ï≤¥ÌÅ¨Î¶¨Ïä§??Í∞úÎ≥Ñ ???ÑÏù¥??
   [Ï§ÄÎπÑÎ¨º (?òÎüâ¬∑Î©îÎ™®) | ?•ÏÜå | ?¥Í?Î∞??£Í∏∞ / ??†ú Î≤ÑÌäº]
   - ?ÑÏ≤¥ Í∞Ä?¥Îç∞ ?ïÎ†¨
   - Î™®Îì† ?çÏä§??1Ï§?(TextOverflow.Ellipsis)
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
                // 1. Ï§ÄÎπÑÎ¨º ?¥Î¶Ñ + 1Ï§?Ï∞∏Í≥†?¨Ìï≠ Î©îÎ™® (Í∞Ä?¥Îç∞ ?ïÎ†¨)
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

                // 2. ?•ÏÜå ?úÍ∑∏ (Í∞Ä?¥Îç∞ ?ïÎ†¨)
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

                // 3. Í∞ÄÎ∞©Îã¥Í∏?(Ï≤¥ÌÅ¨Î∞ïÏä§) ?êÎäî ??†ú Î≤ÑÌäº (Í∞Ä?¥Îç∞ ?ïÎ†¨)
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
                                checkedColor = PeachPrimary,
                                uncheckedColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }
            }

            // 4. ?òÎã®: Ï¢åÏ∏°(ÎßòÏπ¥???∏Í∏â 1?? + ?∞Ï∏°(?†Î∞∞Îß?Ï∂îÏ≤ú TOP 3 Ïπ?Î≤ÑÌäº)
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
fun LocationTagChip(tag: String) {
    val bg = when (tag) {
        "#Î≥ëÏõê" -> Color(0xFFE8F0FE)
        "#Ï°∞Î¶¨?? -> Color(0xFFE6F4EA)
        "#?¥Ïõê?¥ÏÜå" -> Color(0xFFFEF7E0)
        else -> Color(0xFFF1F3F4)
    }
    val textColor = when (tag) {
        "#Î≥ëÏõê" -> Color(0xFF1967D2)
        "#Ï°∞Î¶¨?? -> Color(0xFF137333)
        "#?¥Ïõê?¥ÏÜå" -> Color(0xFFB06000)
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
    com.kongkong.babybag.ui.mybag.MyBagScreen(
        onNavigateBack = onNavigateBack,
        initialTab = 1,
        modifier = modifier
    )
}

