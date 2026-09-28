package com.kongkong.babybag.ui.benefits

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
import com.kongkong.babybag.R
import com.kongkong.babybag.data.BenefitItem
import com.kongkong.babybag.data.BenefitsRepository
import com.kongkong.babybag.theme.PeachLight
import com.kongkong.babybag.theme.PeachPrimary
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground
import kotlinx.coroutines.launch

/* ========================================================
   1. Ï∂úÏÇ∞ ?úÌÉù ?ïÎ¶¨ Î©îÏù∏ Ïπ¥ÌÖåÍ≥†Î¶¨ (?ÑÍµ≠ Í≥µÌÜµ vs ÏßÄ??≥Ñ vs Í∞úÏù∏Î≥?
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
                        text = "Ï∂úÏÇ∞ ?úÌÉù ?ïÎ¶¨",
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
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ?ÅÎã® ?àÎÇ¥ Î∞∞ÎÑà
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
                            text = "?åÎú∞??Ï∂úÏÇ∞ ?úÌÉù Ï±ôÍ∏∞Í∏??éÅ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "?ÑÍµ≠ Í≥µÌÜµ ?ïÎ? ÏßÄ?êÍ∏à, Í±∞Ï£ºÏßÄÎ≥?ÏßÄ?êÍ∏à, ÏßÅÏû•¬∑Î≥¥Ìóò Í∞úÏù∏Î≥??úÌÉù???úÎàà???ïÏù∏?òÏÑ∏??",
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

            // Ïπ¥ÌÖåÍ≥†Î¶¨ 1: ?ÑÍµ≠ Í≥µÌÜµ ?úÌÉù
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
                                text = "1. ?ÑÍµ≠ Í≥µÌÜµ ?úÌÉù",
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
                                    text = "?ïÎ? ÏßÄ??,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ï≤´Îßå?®Ïù¥?©Í∂å, Î∂ÄÎ™®Í∏â?? ?ÑÎèô?òÎãπ, Ï∂úÏÉù?†Í≥†/Í±¥Î≥¥?±Î°ù, ?ÑÍ∏∞¬∑Í∞Ä??Í∞êÎ©¥ ???Ä?úÎ?Íµ?Í≥µÌÜµ ?úÌÉù Î™®Ïùå",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Ïπ¥ÌÖåÍ≥†Î¶¨ 2: ÏßÄ??≥Ñ ?úÌÉù
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
                                text = "2. ÏßÄ??≥Ñ ?úÌÉù",
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
                                    text = "ÏßÄ?êÏ≤¥ ?πÌôî",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF673AB7)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "?∏Ï¢Ö??Ï∂úÏÉùÏ∂ïÌïòÍ∏?120Îß???, ?úÏö∏ ?∞ÌõÑÏ°∞Î¶¨Îπ? Í≤ΩÍ∏∞¬∑?∏Ï≤ú 1???ÑÏù¥?úÎ¶º ??Í±∞Ï£ºÏßÄ ?ú¬∑Íµ∞¬∑Íµ?Ï∂îÍ? ÏßÄ?êÍ∏à Î™®Ïùå",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Ïπ¥ÌÖåÍ≥†Î¶¨ 3: Í∞úÏù∏Î≥??úÌÉù
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
                                text = "3. Í∞úÏù∏Î≥??úÌÉù",
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
                                    text = "ÏßÅÏû•¬∑Î≥¥Ìóò¬∑?∏Ï†ú",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Í≥µÎ¨¥?êÏó∞Í∏àÍ≥µ??100Îß?, ÍµêÏßÅ?êÍ≥µ?úÌöå(10Îß?, ÎßûÏ∂§??Î≥µÏ??¨Ïù∏?? ?úÏïÑ/Ï∞®Î≥¥???òÍ∏â, ?∞Îßê?ïÏÇ∞ ?åÎìù/?∏Ïï°Í≥µÏ†ú Î™®Ïùå",
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
            "?ÑÏ≤¥" -> true
            "?ëú ?¥Í∏¥ ?úÌÉù" -> savedIds.contains(item.id)
            "?ÑÍ∏à ÏßÄ?? -> item.badge?.contains("?ÑÍ∏à") == true || item.description.contains("?ÑÍ∏à") || item.title.contains("Í∏âÏó¨") || item.title.contains("?òÎãπ")
            "Î∞îÏö∞Ï≤? -> item.badge?.contains("Î∞îÏö∞Ï≤?) == true || item.description.contains("Î∞îÏö∞Ï≤?) || item.title.contains("?¥Ïö©Í∂?)
            "?ùÌôúÎπ?Í∞êÎ©¥" -> item.badge?.contains("Í∞êÎ©¥") == true || item.badge?.contains("?†Ïù∏") == true || item.description.contains("Í∞êÎ©¥") || item.description.contains("?†Ïù∏")
            "?òÎ£å¬∑Î≥¥Ïú°" -> item.badge?.contains("?òÎ£å") == true || item.badge?.contains("Î≥¥Ïú°") == true || item.description.contains("?òÎ£å") || item.description.contains("?åÎ¥Ñ") || item.description.contains("Î≥¥Ïú°")
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
    // 1. ?§ÏãúÍ∞?Í≤Ä?âÏ∞Ω
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp),
        placeholder = { Text("?úÌÉùÎ™? ÏßÄ???¥Ïö©, ?êÍ≤© Í≤Ä??..", fontSize = 12.5.sp, color = TextMuted) },
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
                IconButton(onClick = { onSearchQueryChange("") }) {
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

    // 2. ?ÑÌÑ∞ Ïπ?Î™©Î°ù (?ÑÏ≤¥ | ?ÑÍ∏à ÏßÄ??| Î∞îÏö∞Ï≤?| ?ùÌôúÎπ?Í∞êÎ©¥ | ?òÎ£å¬∑Î≥¥Ïú° | ?ëú ?¥Í∏¥ ?úÌÉù)
    val filterOptions = listOf("?ÑÏ≤¥", "?ÑÍ∏à ÏßÄ??, "Î∞îÏö∞Ï≤?, "?ùÌôúÎπ?Í∞êÎ©¥", "?òÎ£å¬∑Î≥¥Ïú°", "?ëú ?¥Í∏¥ ?úÌÉù")
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

    // 3. ?ïÎ†¨ Í∞Ä?•Ìïú ?åÏù¥Î∏??§Îçî
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
                    text = "?úÌÉù ?¥Ïö©" + if (sortColumn == BenefitSortColumn.TITLE) (if (isSortAscending) " ?? else " ??) else " ??,
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
                    text = "?êÍ≤©/Ï°∞Í±¥" + if (sortColumn == BenefitSortColumn.ELIGIBILITY) (if (isSortAscending) " ?? else " ??) else " ??,
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
                    text = if (isDeleteMode) "??†ú" else ("Í∞ÄÎ∞©Îã¥Í∏? + if (sortColumn == BenefitSortColumn.SAVED) (if (isSortAscending) " ?? else " ??) else " ??),
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
   2. ?ÑÍµ≠ Í≥µÌÜµ ?úÌÉù Î¶¨Ïä§???îÎ©¥
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
    var selectedFilterTag by remember { mutableStateOf("?ÑÏ≤¥") }
    var sortColumn by remember { mutableStateOf<BenefitSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    // 1. ?§ÏãúÍ∞?Í≤Ä??Î∞??úÍ∑∏ ?ÑÌÑ∞Îß?
    val filteredList = remember(nationalList, searchQuery, selectedFilterTag, savedIds) {
        filterBenefits(nationalList, searchQuery, selectedFilterTag, savedIds)
    }

    // 2. Ïª¨ÎüºÎ≥??ïÎ†¨
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
                        text = "?ÑÍµ≠ Í≥µÌÜµ ?úÌÉù",
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
            // ?ÅÎã® Ï∂îÍ?/??†ú Î≤ÑÌäº Î∞òÎ∞ò Î∂ÑÌï†
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
                    Text(text = "?úÌÉù Ï∂îÍ?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                        text = if (isDeleteMode) "??†ú ?ÑÎ£å" else "?úÌÉù ??†ú",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Í≤Ä??& ?ÑÌÑ∞ Ïπ?& ?ïÎ†¨ ?§Îçî
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

            // ?úÌÉù Î¶¨Ïä§???êÎäî Í≤Ä??Í≤∞Í≥º ?ÜÏùå
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Í≤Ä???êÎäî ?ÑÌÑ∞ Ï°∞Í±¥??ÎßûÎäî ?úÌÉù???ÜÏäµ?àÎã§.",
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Ï≤¥ÌÅ¨Î∞ïÏä§Î•??ÑÎ•¥Î©?'??Í∞ÄÎ∞??????¥Í≤®?? ?ëú (${displayList.size}Í±?",
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
                                    snackbarHostState.showSnackbar("'${benefit.title}' ?úÌÉù????†ú?òÏóà?µÎãà??")
                                }
                            },
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    val msg = if (!isSaved) {
                                        "'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê ?¥Í≤º?µÎãà???ëú"
                                    } else {
                                        "'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê???úÏô∏?òÏóà?µÎãà??
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    val msg = if (!isCompleted) {
                                        "'${benefit.title}' ?†Ïùº???ÑÎ£å?àÏäµ?àÎã§ ??
                                    } else {
                                        "'${benefit.title}' ?ÑÎ£åÎ•?Ï∑®ÏÜå?àÏäµ?àÎã§"
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
                scope.launch { snackbarHostState.showSnackbar("?àÎ°ú???ÑÍµ≠ Í≥µÌÜµ ?úÌÉù??Ï∂îÍ??òÏóà?µÎãà?? ??) }
            }
        )
    }
}

/* ========================================================
   3. ÏßÄ??≥Ñ ?úÌÉù Î¶¨Ïä§???îÎ©¥ (ÏßÄ???†ÌÉù Î≤ÑÌäº + Î¶¨Ïä§??
======================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalBenefitsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val regions = listOf("?∏Ï¢Ö", "?úÏö∏", "Í≤ΩÍ∏∞", "?∏Ï≤ú", "Î∂Ä??, "?ÄÍµ?, "?Ä??, "Í¥ëÏ£º", "?∏ÏÇ∞", "Ï∂©Î∂Å", "Ï∂©ÎÇ®", "?ÑÎ∂Å", "?ÑÎÇ®", "Í≤ΩÎ∂Å", "Í≤ΩÎÇ®", "Í∞ïÏõê", "?úÏ£º")
    var selectedRegion by remember { mutableStateOf("?∏Ï¢Ö") }
    val savedIds by BenefitsRepository.savedBenefitIds.collectAsState()
    val completedIds by BenefitsRepository.completedBenefitIds.collectAsState()
    val regionalMap by BenefitsRepository.regionalBenefits.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isDeleteMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("?ÑÏ≤¥") }
    var sortColumn by remember { mutableStateOf<BenefitSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    val currentList = regionalMap[selectedRegion] ?: emptyList()

    // 1. ?§ÏãúÍ∞?Í≤Ä??Î∞??ÑÌÑ∞Îß?
    val filteredList = remember(currentList, searchQuery, selectedFilterTag, savedIds) {
        filterBenefits(currentList, searchQuery, selectedFilterTag, savedIds)
    }

    // 2. Ïª¨ÎüºÎ≥??ïÎ†¨
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
                        text = "ÏßÄ??≥Ñ ?úÌÉù",
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
            // ?ÅÎã® Ï∂îÍ?/??†ú Î≤ÑÌäº Î∞òÎ∞ò Î∂ÑÌï†
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
                    Text(text = "?úÌÉù Ï∂îÍ?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                        text = if (isDeleteMode) "??†ú ?ÑÎ£å" else "?úÌÉù ??†ú",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ?ÅÎã® ÏßÄ???†ÌÉù Ïπ?Î¶¨Ïä§??(Í∞ÄÎ°??§ÌÅ¨Î°?
            Text(
                text = "ÏßÄ??ùÑ ?†ÌÉù??Ï£ºÏÑ∏??,
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

            // Í≤Ä??& ?ÑÌÑ∞ Ïπ?& ?ïÎ†¨ ?§Îçî
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

            // ?†ÌÉù??ÏßÄ???úÌÉù Î¶¨Ïä§??
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentList.isEmpty()) "?¥Îãπ ÏßÄ??ùò ?ÅÏÑ∏ ?úÌÉù ?ïÎ≥¥Í∞Ä Ï§ÄÎπ?Ï§ëÏûÖ?àÎã§." else "Í≤Ä???êÎäî ?ÑÌÑ∞ Ï°∞Í±¥??ÎßûÎäî ?úÌÉù???ÜÏäµ?àÎã§.",
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "[$selectedRegion] ÏßÄ???πÌôî Ï∂úÏÇ∞ ÏßÄ?êÍ∏à Î∞?Î∞îÏö∞Ï≤?(${displayList.size}Í±?",
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
                                    snackbarHostState.showSnackbar("'${benefit.title}' ?úÌÉù????†ú?òÏóà?µÎãà??")
                                }
                            },
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    val msg = if (!isSaved) {
                                        "'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê ?¥Í≤º?µÎãà???ëú"
                                    } else {
                                        "'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê???úÏô∏?òÏóà?µÎãà??
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    val msg = if (!isCompleted) {
                                        "'${benefit.title}' ?†Ïùº???ÑÎ£å?àÏäµ?àÎã§ ??
                                    } else {
                                        "'${benefit.title}' ?ÑÎ£åÎ•?Ï∑®ÏÜå?àÏäµ?àÎã§"
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
                scope.launch { snackbarHostState.showSnackbar("?àÎ°ú??ÏßÄ???úÌÉù??Ï∂îÍ??òÏóà?µÎãà?? ??) }
            }
        )
    }
}

/* ========================================================
   4. Í∞úÏù∏Î≥??úÌÉù Î¶¨Ïä§???îÎ©¥ (ÏßÅÏû•¬∑Î≥¥Ìóò¬∑?∏Ï†ú¬∑Í≥µÎ¨¥??ÍµêÏßÅ????
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
    var selectedFilterTag by remember { mutableStateOf("?ÑÏ≤¥") }
    var sortColumn by remember { mutableStateOf<BenefitSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    // 1. ?§ÏãúÍ∞?Í≤Ä??Î∞??úÍ∑∏ ?ÑÌÑ∞Îß?
    val filteredList = remember(personalList, searchQuery, selectedFilterTag, savedIds) {
        filterBenefits(personalList, searchQuery, selectedFilterTag, savedIds)
    }

    // 2. Ïª¨ÎüºÎ≥??ïÎ†¨
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
                        text = "Í∞úÏù∏Î≥??úÌÉù",
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
            // ?ÅÎã® Ï∂îÍ?/??†ú Î≤ÑÌäº Î∞òÎ∞ò Î∂ÑÌï†
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
                    Text(text = "?úÌÉù Ï∂îÍ?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                        text = if (isDeleteMode) "??†ú ?ÑÎ£å" else "?úÌÉù ??†ú",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Í≤Ä??& ?ÑÌÑ∞ Ïπ?& ?ïÎ†¨ ?§Îçî
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

            // ?úÌÉù Î¶¨Ïä§???êÎäî Í≤Ä??Í≤∞Í≥º ?ÜÏùå
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Í≤Ä???êÎäî ?ÑÌÑ∞ Ï°∞Í±¥??ÎßûÎäî Í∞úÏù∏Î≥??úÌÉù???ÜÏäµ?àÎã§.",
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "ÏßÅÏû•, Î≥¥Ìóò?? Í≥µÏ†ú?? ?∞Îßê?ïÏÇ∞ ??ÎßûÏ∂§ ?úÌÉù (${displayList.size}Í±?",
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
                                    snackbarHostState.showSnackbar("'${benefit.title}' ?úÌÉù????†ú?òÏóà?µÎãà??")
                                }
                            },
                            onToggleSaved = {
                                BenefitsRepository.toggleBenefitSaved(benefit.id)
                                scope.launch {
                                    val msg = if (!isSaved) {
                                        "'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê ?¥Í≤º?µÎãà???ëú"
                                    } else {
                                        "'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê???úÏô∏?òÏóà?µÎãà??
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    val msg = if (!isCompleted) {
                                        "'${benefit.title}' ?†Ïùº???ÑÎ£å?àÏäµ?àÎã§ ??
                                    } else {
                                        "'${benefit.title}' ?ÑÎ£åÎ•?Ï∑®ÏÜå?àÏäµ?àÎã§"
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
                scope.launch { snackbarHostState.showSnackbar("?àÎ°ú??Í∞úÏù∏Î≥??úÌÉù??Ï∂îÍ??òÏóà?µÎãà?? ??) }
            }
        )
    }
}

/* ========================================================
   5. ?¥Í? ?Ä?•Ìïú Ï∂úÏÇ∞ ?úÌÉù Î≥¥Í∏∞ ?îÎ©¥ (??Í∞ÄÎ∞??∞Îèô: ?óÏπ∏ / ?ÑÎû´Ïπ?
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
                        text = "?¥Í? ?Ä?•Ìïú Ï∂úÏÇ∞ ?úÌÉù",
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
                                text = "??Í∞ÄÎ∞©Ïóê ?¥Í∏¥ ?úÌÉù (Ï¥?${totalSavedCount}Í∞? ?ëú",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "?ÑÎ£å Î≤ÑÌäº???ÑÎ•¥Î©??ÑÎûò ?ÑÎ£å Ïπ∏ÏúºÎ°??¥Îèô?©Îãà??",
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
                                text = "?ÑÏßÅ ?¥ÏïÑ???úÌÉù???ÜÏñ¥?? ?å∏",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "[4. Ï∂úÏÇ∞ ?úÌÉù ?ïÎ¶¨]?êÏÑú '??Í∞ÄÎ∞? Ï≤¥ÌÅ¨Î∞ïÏä§Î•??åÎü¨ ?òÎßå???úÌÉù?????¥ÏïÑÎ≥¥ÏÑ∏??",
                                fontSize = 12.5.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // ================= ?óÏπ∏: ?¥Í? ?†ÌÉù??ÏßÑÌñâ Ï§?Î¶¨Ïä§??=================
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
                                text = "?ìå ?óÏπ∏: ?¥Í? ?†ÌÉù??Î¶¨Ïä§??(${uncompletedList.size}Í∞?",
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
                            text = "ÏßÑÌñâ Ï§ëÏù∏ ?úÌÉù???ÜÍ±∞??Î™®Îëê ?ÑÎ£å?àÏäµ?àÎã§! ?éâ",
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
                                    snackbarHostState.showSnackbar("'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê???úÏô∏?òÏóà?µÎãà??)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (!isCompleted) "'${benefit.title}' ?ÑÎ£å! ?ÑÎûò ?ÑÎû´Ïπ∏ÏúºÎ°??¥Îèô?àÏäµ?àÎã§ ??
                                        else "'${benefit.title}' ?ÑÎ£å Ï∑®ÏÜå. ?óÏπ∏?ºÎ°ú Î≥µÍ??àÏäµ?àÎã§ ‚¨ÜÔ∏è"
                                    )
                                }
                            }
                        )
                    }
                }

                // ================= ?ÑÎû´Ïπ? ?ÑÎ£å Î≤ÑÌäº???ÑÎ•∏ Î¶¨Ïä§??=================
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
                                text = "???ÑÎû´Ïπ? ?ÑÎ£å??Î¶¨Ïä§??(${completedList.size}Í∞?",
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
                            text = "?ÑÏßÅ ?ÑÎ£å????™©???ÜÏñ¥?? ?óÏπ∏?êÏÑú [?ÑÎ£å] Î≤ÑÌäº???ÑÎ•¥Î©???Ïπ∏ÏúºÎ°??¥Î†§Í∞ëÎãà?? ??",
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
                                    snackbarHostState.showSnackbar("'${benefit.title}'??Í∞Ä) ??Í∞ÄÎ∞©Ïóê???úÏô∏?òÏóà?µÎãà??)
                                }
                            },
                            onToggleCompleted = {
                                BenefitsRepository.toggleBenefitCompleted(benefit.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (!isCompleted) "'${benefit.title}' ?ÑÎ£å! ?ÑÎûò ?ÑÎû´Ïπ∏ÏúºÎ°??¥Îèô?àÏäµ?àÎã§ ??
                                        else "'${benefit.title}' ?ÑÎ£å Ï∑®ÏÜå. ?óÏπ∏?ºÎ°ú Î≥µÍ??àÏäµ?àÎã§ ‚¨ÜÔ∏è"
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
   Í≥µÌÜµ: ?úÌÉù ?ÑÏù¥??Ïπ¥Îìú (?úÌÉù | ?êÍ≤© Î∞?Ï°∞Í±¥ | ?úÍ∏∞/?•ÏÜå | ??Í∞ÄÎ∞?Ï≤¥ÌÅ¨Î∞ïÏä§ + ?ÑÎ£å Î≤ÑÌäº)
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
            // Î≥∏Î¨∏ ?¥Ïö© (?úÌÉù ?Ä?¥Ì? + Î±ÉÏ? + ?¥Ïö© + ?úÍ∏∞/?•ÏÜå + ?êÍ≤©Ï°∞Í±¥) - Î™®Îëê Í∞Ä?¥Îç∞ ?ïÎ†¨ & 1Ï§??úÌïú
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. ?úÌÉù ?Ä?¥Ì? + Î±ÉÏ?
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
                                text = "?ÑÎ£å????,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2. ÏßÄ???¥Ïö©/Í∏àÏï°
                Text(
                    text = benefit.description,
                    fontSize = 12.sp,
                    color = if (isCompleted) TextMuted.copy(alpha = 0.7f) else Color(0xFF333333),
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 3. ?±Ô∏è ?úÍ∏∞ & ?ìç ?•ÏÜå/Î∞©Î≤ï (?àÎäî Í≤ΩÏö∞ Ïπ??ïÌÉúÎ°??úÏãú)
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
                                    text = "?±Ô∏è ${benefit.timing}",
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
                                    text = "?ìç ${benefit.place}",
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

                // 4. ?êÍ≤© Î∞?Ï°∞Í±¥ (?∞Ìïú Î∞ïÏä§)
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
                        text = "?êÍ≤©¬∑Ï°∞Í±¥: ",
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

            // ?∞Ï∏° Ïª¨Îüº: ??Í∞ÄÎ∞?Ï≤¥ÌÅ¨Î∞ïÏä§ + ?¥Í?Î≤ÑÌäº Î∞îÎ°ú ?ÑÎûò ?ÑÎ£å Î≤ÑÌäº OR ??†ú Î≤ÑÌäº
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
                        text = "??†ú ?óëÔ∏?,
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
                        text = if (isSaved) "?¥Í? ?? else "Í∞ÄÎ∞©Îã¥Í∏?,
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

                    // [?¥Í?Î≤ÑÌäº Î∞îÎ°ú ?ÑÎûò ?ÑÎ£å Î≤ÑÌäº]
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
                            text = if (isCompleted) "?ÑÎ£å???? else "?ÑÎ£å",
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
   ?§Ïù¥?ºÎ°úÍ∑? ?àÎ°ú???úÌÉù Ï∂îÍ?
======================================================== */
@Composable
fun AddBenefitDialog(
    type: String, // "national", "regional", "personal"
    defaultRegion: String = "?∏Ï¢Ö",
    onDismiss: () -> Unit,
    onAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eligibility by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("?†Í∑ú ?úÌÉù") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "?àÎ°ú???úÌÉù Ï∂îÍ?",
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
                    label = { Text("?úÌÉù ?¥Î¶Ñ (?? ?∞ÌõÑÏ°∞Î¶¨Îπ?ÏßÄ??") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("?úÌÉù ?¥Ïö© Î∞?Í∏àÏï°") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eligibility,
                    onValueChange = { eligibility = it },
                    label = { Text("?êÍ≤© Î∞?Ï°∞Í±¥") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timing,
                    onValueChange = { timing = it },
                    label = { Text("?†Ï≤≠ ?úÍ∏∞ (?? Ï∂úÏÇ∞ ??30???¥ÎÇ¥)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("?úÌÉù ?•ÏÜå/?†Ï≤≠ Î∞©Î≤ï (?? ?âÏ†ïÎ≥µÏ??ºÌÑ∞)") },
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
                            description = description.ifBlank { "?¨Ïö©??ÏßÅÏ†ë Ï∂îÍ? ?úÌÉù" },
                            eligibility = eligibility.ifBlank { "?¥Îãπ?? },
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
                Text("Ï∂îÍ??òÍ∏∞")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ï∑®ÏÜå")
            }
        }
    )
}
