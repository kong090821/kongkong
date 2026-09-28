package com.kongkong.babybag.ui.mybag

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
import com.kongkong.babybag.data.BabySuppliesRepository
import com.kongkong.babybag.data.BabySupplyItem
import com.kongkong.babybag.data.BenefitItem
import com.kongkong.babybag.data.BenefitsRepository
import com.kongkong.babybag.data.MaternityBagItem
import com.kongkong.babybag.data.MaternityBagRepository
import com.kongkong.babybag.data.TodoItem
import com.kongkong.babybag.data.TodoRepository
import com.kongkong.babybag.theme.MaternityBagTheme
import com.kongkong.babybag.theme.PeachPrimary
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground
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

    // null = 1~4Î≤?Î©îÏù∏ Î≤ÑÌäº ?îÎ©¥, 1 = Ï∂úÏÇ∞Í∞ÄÎ∞? 2 = ?°ÏïÑ?©Ìíà, 3 = ?úÍ∏∞Î≥??†Ïùº, 4 = Ï∂úÏÇ∞ ?úÌÉù
    var selectedFeature by remember { mutableStateOf<Int?>(null) }

    // 1. Ï∂úÏÇ∞Í∞ÄÎ∞??∞Ïù¥??
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

    // 2. ?°ÏïÑ?©Ìíà ?∞Ïù¥??
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

    // 3. ?úÍ∏∞Î≥??†Ïùº ?∞Ïù¥??
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

    // 4. Ï∂úÏÇ∞ ?úÌÉù ?∞Ïù¥??
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

    // ?îÎ©¥ ?úÎ™© Î∞??§Î°úÍ∞ÄÍ∏??°ÏÖò
    val screenTitle = when (selectedFeature) {
        1 -> "1. Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º"
        2 -> "2. ?°ÏïÑ?©Ìíà Î¶¨Ïä§??
        3 -> "3. ?úÍ∏∞Î≥??†Ïùº Î¶¨Ïä§??
        4 -> "4. Ï∂úÏÇ∞ ?úÌÉù Î¶¨Ïä§??
        else -> "??Í∞ÄÎ∞??ëú"
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
            // [?ÅÌÉú 1] 1~4Î≤?Î©îÏù∏ Î≤ÑÌäº ?îÎ©¥ (?†ÌÉù??Í∏∞Îä• ?ÜÏùå)
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
                // [?ÅÌÉú 2] ?†ÌÉù??1~4Î≤?Í∏∞Îä•???ÅÏÑ∏ ?îÎ©¥
                // ?ÅÎã® ?§ÎπÑÍ≤åÏù¥?? 1~4Î≤?Î™©Î°ù Î≥µÍ? Î≤ÑÌäº + 4Í∞?Îπ†Î•∏ ?ÑÌôò ??
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
                            // 1. Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º
                            item {
                                FeatureProgressBox(
                                    featureName = "Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º",
                                    icon = "?ëú",
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
                                        val msg = if (!isComp) "??'${item.title}' Ï§ÄÎπ??ÑÎ£å! Îß??ÑÎûòÎ°??¥Îèô?àÏäµ?àÎã§." else "'${item.title}' ÎØ∏ÏôÑÎ£åÎ°ú Î≥ÄÍ≤?
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        2 -> {
                            // 2. ?°ÏïÑ?©Ìíà Î¶¨Ïä§??
                            item {
                                FeatureProgressBox(
                                    featureName = "?°ÏïÑ?©Ìíà",
                                    icon = "?çº",
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
                                        val msg = if (!isComp) "??'${item.title}' Ï§ÄÎπ??ÑÎ£å! Îß??ÑÎûòÎ°??¥Îèô?àÏäµ?àÎã§." else "'${item.title}' ÎØ∏ÏôÑÎ£åÎ°ú Î≥ÄÍ≤?
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        3 -> {
                            // 3. ?úÍ∏∞Î≥??†Ïùº Î¶¨Ïä§??
                            item {
                                FeatureProgressBox(
                                    featureName = "?úÍ∏∞Î≥??†Ïùº",
                                    icon = "?ìÖ",
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
                                        val msg = if (!isComp) "??'${item.title}' ?§Ï≤ú ?ÑÎ£å! Îß??ÑÎûòÎ°??¥Îèô?àÏäµ?àÎã§." else "'${item.title}' ÎØ∏ÏôÑÎ£åÎ°ú Î≥ÄÍ≤?
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                        4 -> {
                            // 4. Ï∂úÏÇ∞ ?úÌÉù Î¶¨Ïä§??
                            item {
                                FeatureProgressBox(
                                    featureName = "Ï∂úÏÇ∞ ?úÌÉù",
                                    icon = "?éÅ",
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
                                        val msg = if (!isComp) "??'${item.title}' ?†Ï≤≠ ?ÑÎ£å! Îß??ÑÎûòÎ°??¥Îèô?àÏäµ?àÎã§." else "'${item.title}' ÎØ∏ÏôÑÎ£åÎ°ú Î≥ÄÍ≤?
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
   1~4Î≤?Î©îÏù∏ Î≤ÑÌäº ?îÎ©¥ Ïª¥Ìè¨?åÌä∏
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
        // Í∞Ä?¥Îìú Î∞ïÏä§
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
                    text = "?ïÏù∏??Í∞ÄÎ∞?Î≤àÌò∏Î•??åÎü¨Ï£ºÏÑ∏???å∏",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Î≤ÑÌäº???ÑÎ•¥Î©??¥Îãπ ??™©?§Ïùò ?ÑÏ≤¥ Ï§ÄÎπÑÏú®Í≥??¥Í∏¥ Î™©Î°ù???òÌ??©Îãà??",
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 1Î≤?Î≤ÑÌäº: Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º
        FeatureMenuCardButton(
            num = 1,
            icon = "?ëú",
            title = "1. Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º",
            desc = "Î≥ëÏõê Î∞?Ï°∞Î¶¨???ÖÏõê ?ÑÏàò Ï§ÄÎπÑÎ¨º",
            badgeText = "${savedMatCount}Í∞??¥Í? (${compMatCount}Í∞??ÑÎ£å, ${matPercent}%)",
            themeColor = Color(0xFFFF6F59),
            badgeBg = Color(0xFFFFF0EC),
            borderColor = Color(0xFFFFD6CC),
            onClick = { onSelectFeature(1) }
        )

        // 2Î≤?Î≤ÑÌäº: ?°ÏïÑ?©Ìíà Î¶¨Ïä§??
        FeatureMenuCardButton(
            num = 2,
            icon = "?çº",
            title = "2. ?°ÏïÑ?©Ìíà Î¶¨Ïä§??,
            desc = "?òÏú†¬∑?òÎ©¥¬∑?ÑÏÉù¬∑?∏Ï∂ú ?†ÏÉù???ÑÏàò??,
            badgeText = "${savedBabyCount}Í∞??¥Í? (${compBabyCount}Í∞??ÑÎ£å, ${babyPercent}%)",
            themeColor = Color(0xFF43A047),
            badgeBg = Color(0xFFE8F5E9),
            borderColor = Color(0xFFC8E6C9),
            onClick = { onSelectFeature(2) }
        )

        // 3Î≤?Î≤ÑÌäº: ?úÍ∏∞Î≥??†Ïùº Î¶¨Ïä§??
        FeatureMenuCardButton(
            num = 3,
            icon = "?ìÖ",
            title = "3. ?úÍ∏∞Î≥??†Ïùº Î¶¨Ïä§??,
            desc = "Ï∂úÏÇ∞ ?ÑÎ???Î≥ëÏõê¬∑Ï°∞Î¶¨?ê¬∑Í????†Ïùº",
            badgeText = "${savedTodoCount}Í∞??¥Í? (${compTodoCount}Í∞??ÑÎ£å, ${todoPercent}%)",
            themeColor = Color(0xFF7E57C2),
            badgeBg = Color(0xFFEDE7F6),
            borderColor = Color(0xFFD1C4E9),
            onClick = { onSelectFeature(3) }
        )

        // 4Î≤?Î≤ÑÌäº: Ï∂úÏÇ∞ ?úÌÉù Î¶¨Ïä§??
        FeatureMenuCardButton(
            num = 4,
            icon = "?éÅ",
            title = "4. Ï∂úÏÇ∞ ?úÌÉù Î¶¨Ïä§??,
            desc = "?ÑÍµ≠¬∑ÏßÄ??∑Í∞ú??ÎßûÏ∂§ ?ïÎ? ÏßÄ?êÍ∏à Î™®Ïùå",
            badgeText = "${savedBenCount}Í∞??¥Í? (${compBenCount}Í∞??ÑÎ£å, ${benPercent}%)",
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
            // ?êÌòï Î≤àÌò∏ Î∞∞Ï?
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

            // ?¥Î™®ÏßÄ ?ÑÏù¥ÏΩ?
            Text(
                text = icon,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Î≥∏Î¨∏
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

            // ?îÏÇ¥??
            Text(
                text = "??,
                fontSize = 15.sp,
                color = TextMuted,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

/* ========================================================
   ?ÅÏÑ∏ Î∑??ÅÎã® ?§ÎπÑÍ≤åÏù¥?? [?Ä 1~4Î≤?Î™©Î°ù] + 4Í∞?Îπ†Î•∏ ??
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
        // 1~4Î≤?Î™©Î°ù ?åÏïÑÍ∞ÄÍ∏?Î≤ÑÌäº
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F3F4))
                .clickable { onBackToMenu() }
                .padding(horizontal = 10.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?Ä 1~4Î≤?Î™©Î°ù",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C4043)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 4Í∞?Í∏∞Îä• ??
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Triple(1, "1. Ï∂úÏÇ∞Í∞ÄÎ∞?, Color(0xFFFF6F59)),
                Triple(2, "2. ?°ÏïÑ?©Ìíà", Color(0xFF43A047)),
                Triple(3, "3. ?úÍ∏∞Î≥??†Ïùº", Color(0xFF7E57C2)),
                Triple(4, "4. Ï∂úÏÇ∞ ?úÌÉù", Color(0xFFFFA000))
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
   ?¨ÏßÑÍ≥??ëÍ∞ô?Ä Î∞ïÏä§: Î≤ÑÌäº ?âÏÉÅÍ≥?Í∞ôÏ? Î∞îÌÉï?âÏùò Ï§ÄÎπÑÏú® Ïπ¥Îìú
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
                // Î∞òÌà¨Î™??∞ÏÉâ ??Î∞∞Ï?
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.28f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$icon ?¥Í? ?¥Ï? $featureName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Î≥ºÎìú ?∞ÏÉâ ?Ä?¥Ì?
                Text(
                    text = "Ï¥?${savedCount}Í∞??¥Í? (${compCount}Í∞??ÑÎ£å, ${percent}%)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Î∞òÌà¨Î™??∏Îûô ???∞ÏÉâ ?ÑÎ°úÍ∑∏Î†à??Î∞?
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

                // ?òÎã® ?§Î™Ö Î∞??ºÏÑº??
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "?ÑÎ£å????™©??Ï≤¥ÌÅ¨?òÎ©¥ Ï∑®ÏÜå?†Í≥º ?®Íªò Í∞Ä???ÑÎûòÎ°??¥Îèô?©Îãà???í™",
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
   1. Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º ?πÏÖò
======================================================== */
private fun LazyListScope.renderMaternityBagSection(
    savedList: List<MaternityBagItem>,
    uncompList: List<MaternityBagItem>,
    compList: List<MaternityBagItem>,
    onToggleCompleted: (MaternityBagItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "?¥Í∏¥ Ï∂úÏÇ∞Í∞ÄÎ∞?Ï§ÄÎπÑÎ¨º???ÜÏäµ?àÎã§.\n[Ï∂úÏÇ∞Í∞ÄÎ∞?Ï≤¥ÌÅ¨Î¶¨Ïä§???êÏÑú 'Í∞ÄÎ∞©Îã¥Í∏?Î•??åÎü¨Î≥¥ÏÑ∏??")
        }
        return
    }

    // ?åÏù¥Î∏??§Îçî (Î™©Ï∞®)
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
                    text = "Ï§ÄÎπÑÎ¨º",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?•ÏÜå",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(70.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?ÑÎ£å",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6F59),
                    modifier = Modifier.width(76.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ÎØ∏ÏôÑÎ£???™©
    items(uncompList, key = { "mat_${it.id}" }) { item ->
        MaternityItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // ?ÑÎ£å ??™© (Í∞Ä???ÑÎû´Ïπ∏ÏúºÎ°??¥Îèô)
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
                            "#Î≥ëÏõê" -> Color(0xFFE8F0FE)
                            "#Ï°∞Î¶¨?? -> Color(0xFFE6F4EA)
                            "#?¥Ïõê?¥ÏÜå" -> Color(0xFFFEF7E0)
                            else -> Color(0xFFF1F3F4)
                        }
                        val tc = when (locTag) {
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
   2. ?°ÏïÑ?©Ìíà Î¶¨Ïä§???πÏÖò
======================================================== */
private fun LazyListScope.renderBabySuppliesSection(
    savedList: List<BabySupplyItem>,
    uncompList: List<BabySupplyItem>,
    compList: List<BabySupplyItem>,
    onToggleCompleted: (BabySupplyItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "?¥Í∏¥ ?°ÏïÑ?©Ìíà???ÜÏäµ?àÎã§.\n[?°ÏïÑ?©Ìíà Ï≤¥ÌÅ¨Î¶¨Ïä§???êÏÑú 'Í∞ÄÎ∞©Îã¥Í∏?Î•??åÎü¨Î≥¥ÏÑ∏??")
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
                    text = "Ï§ÄÎπÑÎ¨º",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?úÍ∏∞",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(56.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Íµ¨ÏûÖÍ≤ΩÎ°ú",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(65.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?ÑÎ£å",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF43A047),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ÎØ∏ÏôÑÎ£???™©
    items(uncompList, key = { "baby_${it.id}" }) { item ->
        BabySupplyItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // ?ÑÎ£å ??™© (Í∞Ä???ÑÎû´Ïπ∏ÏúºÎ°??¥Îèô)
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
                        .background(if (item.purchaseTag == "#?πÍ∑º") Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.purchaseTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.purchaseTag == "#?πÍ∑º") Color(0xFFE65100) else Color(0xFF2E7D32)
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
   3. ?úÍ∏∞Î≥??†Ïùº Î¶¨Ïä§???πÏÖò
======================================================== */
private fun LazyListScope.renderTodoSection(
    savedList: List<TodoItem>,
    uncompList: List<TodoItem>,
    compList: List<TodoItem>,
    onToggleCompleted: (TodoItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "?¥Í∏¥ ?úÍ∏∞Î≥??†Ïùº???ÜÏäµ?àÎã§.\n[?úÍ∏∞Î≥??†Ïùº Ï≤¥ÌÅ¨Î¶¨Ïä§???êÏÑú 'Í∞ÄÎ∞©Îã¥Í∏?Î•??åÎü¨Î≥¥ÏÑ∏??")
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
                    text = "?¥Ïïº?†Ïùº",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?ÑÍ??",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(65.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?ÑÎ£å",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7E57C2),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ÎØ∏ÏôÑÎ£???™©
    items(uncompList, key = { "todo_${it.id}" }) { item ->
        TodoItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // ?ÑÎ£å ??™© (Í∞Ä???ÑÎû´Ïπ∏ÏúºÎ°??¥Îèô)
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
        "#?ÑÎπ†" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        "#?ÑÎßà" -> Pair(Color(0xFFFFF0EC), Color(0xFFFF6F59))
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
   4. Ï∂úÏÇ∞ ?úÌÉù Î¶¨Ïä§???πÏÖò
======================================================== */
private fun LazyListScope.renderBenefitsSection(
    savedList: List<BenefitItem>,
    uncompList: List<BenefitItem>,
    compList: List<BenefitItem>,
    onToggleCompleted: (BenefitItem) -> Unit
) {
    if (savedList.isEmpty()) {
        item {
            EmptySectionCard(text = "?¥Í∏¥ Ï∂úÏÇ∞ ?úÌÉù???ÜÏäµ?àÎã§.\n[Ï∂úÏÇ∞ ?úÌÉù ?ïÎ¶¨]?êÏÑú 'Í∞ÄÎ∞©Îã¥Í∏?Î•??åÎü¨Î≥¥ÏÑ∏??")
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
                    text = "?úÌÉù ?¥Ïö©",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?êÍ≤©/Ï°∞Í±¥",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "?ÑÎ£å",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ÎØ∏ÏôÑÎ£???™©
    items(uncompList, key = { "ben_${it.id}" }) { item ->
        BenefitItemRow(
            item = item,
            isCompleted = false,
            onToggleCompleted = { onToggleCompleted(item) }
        )
    }

    // ?ÑÎ£å ??™© (Í∞Ä???ÑÎû´Ïπ∏ÏúºÎ°??¥Îèô)
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
   Í≥µÌÜµ UI Ïª¥Ìè¨?åÌä∏
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
            text = "?Ä?Ä?Ä Ï§ÄÎπ??ÑÎ£å (${count}Í∞? ?Ä?Ä?Ä",
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
