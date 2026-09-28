package com.kongkong.babybag.ui.checklist

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kongkong.babybag.data.TodoItem
import com.kongkong.babybag.data.TodoRepository
import com.kongkong.babybag.theme.PeachPrimary
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground
import kotlinx.coroutines.launch

enum class TodoSortColumn {
    TITLE, TIP, ROLE, SAVED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoChecklistScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "1. Ï∂úÏÇ∞ ??(Ï§ÄÎπÑÍ∏∞)",
        "2. Î≥ëÏõê (?ÖÏõê/Î∂ÑÎßå)",
        "3. Ï°∞Î¶¨??(?åÎ≥µ/?âÏ†ï)",
        "4. Í∞Ä??(Ï¥àÍ∏∞ ?°ÏïÑ)"
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isDeleteMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("?ÑÏ≤¥") }
    var sortColumn by remember { mutableStateOf<TodoSortColumn?>(null) }
    var isSortAscending by remember { mutableStateOf(true) }

    val todoItems by TodoRepository.todoItems.collectAsState()
    val savedTodoIds by TodoRepository.savedTodoIds.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val activeCategoryName = when (selectedTabIndex) {
        0 -> "1. Ï∂úÏÇ∞ ??(Ï§ÄÎπÑÍ∏∞)"
        1 -> "2. Î≥ëÏõê (?ÖÏõê Î∞?Î∂ÑÎßåÍ∏?"
        2 -> "3. Ï°∞Î¶¨??(?åÎ≥µ Î∞??âÏ†ï Ï≤òÎ¶¨Í∏?"
        else -> "4. Í∞Ä??(Ïß?Î≥µÍ? Î∞?Ï¥àÍ∏∞ ?°ÏïÑÍ∏?"
    }

    val currentItems = todoItems.filter { it.category == activeCategoryName }

    // 1. ?§ÏãúÍ∞?Í≤Ä??Î∞??úÍ∑∏ ?ÑÌÑ∞Îß?
    val filteredItems = remember(currentItems, searchQuery, selectedFilterTag, savedTodoIds) {
        currentItems.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                item.title.lowercase().contains(q) ||
                    item.tip.lowercase().contains(q) ||
                    item.roleTag.lowercase().contains(q)
            }

            val matchesFilter = when (selectedFilterTag) {
                "?ÑÏ≤¥" -> true
                "#?ÑÎπ†", "#?ÑÎßà", "#Î∂ÄÎ∂Ä" -> item.roleTag == selectedFilterTag
                "?ëú ?¥Í∏¥Í≤? -> savedTodoIds.contains(item.id)
                "‚¨??àÎã¥Í∏¥Í≤É" -> !savedTodoIds.contains(item.id)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    // 2. Ïª¨ÎüºÎ≥??ïÎ†¨
    val displayItems = remember(filteredItems, sortColumn, isSortAscending, savedTodoIds) {
        if (sortColumn == null) {
            filteredItems
        } else {
            val comparator = when (sortColumn!!) {
                TodoSortColumn.TITLE -> compareBy<TodoItem> { it.title }
                TodoSortColumn.TIP -> compareBy { it.tip }
                TodoSortColumn.ROLE -> compareBy { it.roleTag }
                TodoSortColumn.SAVED -> compareBy { savedTodoIds.contains(it.id) }
            }
            if (isSortAscending) filteredItems.sortedWith(comparator) else filteredItems.sortedWith(comparator.reversed())
        }
    }

    fun onHeaderSortClick(column: TodoSortColumn) {
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
                        text = "?úÍ∏∞Î≥??†Ïùº Ï≤¥ÌÅ¨Î¶¨Ïä§??,
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
            // 4Í∞???
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
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) PeachPrimary else TextMuted
                            )
                        }
                    )
                }
            }

            // ?ÅÎã® Î∂ÑÌï† Î≤ÑÌäº
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "?†Ïùº Ï∂îÍ?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                        text = if (isDeleteMode) "??†ú ?ÑÎ£å" else "?†Ïùº ??†ú",
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
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                placeholder = { Text("?†Ïùº, ?? ?¥Îãπ??Í≤Ä??..", fontSize = 12.5.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Í≤Ä??,
                        tint = Color(0xFF7E57C2),
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
                    focusedBorderColor = Color(0xFF7E57C2),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 2. ?ÑÌÑ∞ Ïπ?Î™©Î°ù (?ÑÏ≤¥ | #?ÑÎπ† | #?ÑÎßà | #Î∂ÄÎ∂Ä | ?ëú ?¥Í∏¥Í≤?| ‚¨??àÎã¥Í∏¥Í≤É)
            val filterOptions = listOf("?ÑÏ≤¥", "#?ÑÎπ†", "#?ÑÎßà", "#Î∂ÄÎ∂Ä", "?ëú ?¥Í∏¥Í≤?, "‚¨??àÎã¥Í∏¥Í≤É")
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
                            .background(if (isSelected) Color(0xFF7E57C2) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF7E57C2) else Color(0xFFE0E0E0),
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
                    // ?¥Ïïº?†Ïùº (TITLE)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onHeaderSortClick(TodoSortColumn.TITLE) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?¥Ïïº?†Ïùº" + if (sortColumn == TodoSortColumn.TITLE) (if (isSortAscending) " ?? else " ??) else " ??,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sortColumn == TodoSortColumn.TITLE) Color(0xFF7E57C2) else TextDark,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // ?ÑÍ?? (ROLE)
                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onHeaderSortClick(TodoSortColumn.ROLE) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?ÑÍ??" + if (sortColumn == TodoSortColumn.ROLE) (if (isSortAscending) " ?? else " ??) else " ??,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sortColumn == TodoSortColumn.ROLE) Color(0xFF7E57C2) else TextDark,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Í∞ÄÎ∞©Îã¥Í∏?(SAVED)
                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                if (!isDeleteMode) onHeaderSortClick(TodoSortColumn.SAVED)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDeleteMode) "??†ú" else ("Í∞ÄÎ∞©Îã¥Í∏? + if (sortColumn == TodoSortColumn.SAVED) (if (isSortAscending) " ?? else " ??) else " ??),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDeleteMode) Color(0xFFD32F2F) else Color(0xFF7E57C2),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Î¶¨Ïä§???êÎäî Í≤∞Í≥º ?ÜÏùå ?àÎÇ¥
            if (displayItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Í≤Ä???êÎäî ?ÑÌÑ∞ Ï°∞Í±¥??ÎßûÎäî ?†Ïùº???ÜÏäµ?àÎã§.", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Í≤Ä?âÏñ¥Î•?Î≥ÄÍ≤ΩÌïòÍ±∞ÎÇò ?ÑÌÑ∞Î•?'?ÑÏ≤¥'Î°??¨ÏÑ§?ïÌï¥ Î≥¥ÏÑ∏??", fontSize = 12.sp, color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(displayItems, key = { it.id }) { item ->
                        val isSaved = savedTodoIds.contains(item.id)

                        val (roleBg, roleColor) = when (item.roleTag) {
                            "#?ÑÎπ†" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
                            "#?ÑÎßà" -> Pair(Color(0xFFFFF0EC), Color(0xFFFF6F59))
                            else -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isDeleteMode) {
                                        TodoRepository.deleteTodoItem(item.id)
                                        scope.launch { snackbarHostState.showSnackbar("'${item.title}' ?†Ïùº ??†ú???óëÔ∏?) }
                                    } else {
                                        TodoRepository.toggleTodoSaved(item.id)
                                        scope.launch {
                                            val msg = if (!isSaved) "?ëú '${item.title}' ??Í∞ÄÎ∞©Ïóê ?¥Ïïò?µÎãà??" else "'${item.title}' ??Í∞ÄÎ∞©Ïóê??Î∫êÏäµ?àÎã§."
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSaved) Color(0xFFF3E5F5) else SoftSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. ?¥Ïïº?†Ïùº (?óÏ§Ñ: ?úÎ™©, ?ÑÎû´Ï§? ??
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
                                    if (item.tip.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.tip,
                                            fontSize = 10.5.sp,
                                            color = TextMuted,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .width(55.dp)
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
                                    modifier = Modifier.width(55.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDeleteMode) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFFFEBEE))
                                                .clickable {
                                                    TodoRepository.deleteTodoItem(item.id)
                                                    scope.launch { snackbarHostState.showSnackbar("'${item.title}' ??†ú???óëÔ∏?) }
                                                }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "??†ú", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                        }
                                    } else {
                                        Checkbox(
                                            checked = isSaved,
                                            onCheckedChange = {
                                                TodoRepository.toggleTodoSaved(item.id)
                                                scope.launch {
                                                    val msg = if (!isSaved) "?ëú '${item.title}' ??Í∞ÄÎ∞©Ïóê ?¥Ïïò?µÎãà??" else "'${item.title}' ??Í∞ÄÎ∞©Ïóê??Î∫êÏäµ?àÎã§."
                                                    snackbarHostState.showSnackbar(msg)
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF7E57C2),
                                                uncheckedColor = Color(0xFFCCCCCC)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var tip by remember { mutableStateOf("") }
        var roleTag by remember { mutableStateOf("#?ÑÎπ†") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "[$activeCategoryName] ?†Ïùº Ï∂îÍ?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("?¥Ïïº?†Ïùº (?? ÏßëÏïà ?ÑÏ≤¥ ?ÄÏ≤?Üå)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tip,
                        onValueChange = { tip = it },
                        label = { Text("??/ Î©îÎ™® (?? Î∞îÎã• Î®ºÏ? ?úÍ±∞)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "?¥Îãπ??", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        listOf("#?ÑÎπ†", "#?ÑÎßà", "#Î∂ÄÎ∂Ä").forEach { tag ->
                            OutlinedButton(
                                onClick = { roleTag = tag },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (roleTag == tag) PeachPrimary else SoftSurface,
                                    contentColor = if (roleTag == tag) Color.White else TextDark
                                )
                            ) {
                                Text(text = tag, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            TodoRepository.addTodoItem(
                                title = title,
                                tip = tip.ifBlank { "Ï∞∏Í≥† Î©îÎ™®" },
                                roleTag = roleTag,
                                category = activeCategoryName
                            )
                            showAddDialog = false
                            scope.launch { snackbarHostState.showSnackbar(" ?àÎ°ú???†Ïùº??Ï∂îÍ??òÏóà?µÎãà?? ?ìÖ") }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2))
                ) {
                    Text("Ï∂îÍ??òÍ∏∞")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Ï∑®ÏÜå")
                }
            }
        )
    }
}
