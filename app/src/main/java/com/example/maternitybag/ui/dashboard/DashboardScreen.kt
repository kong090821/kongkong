package com.example.maternitybag.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maternitybag.R
import com.example.maternitybag.theme.MaternityBagTheme
import com.example.maternitybag.theme.PeachPrimary
import com.example.maternitybag.theme.SoftSurface
import com.example.maternitybag.theme.TextDark
import com.example.maternitybag.theme.TextMuted
import com.example.maternitybag.theme.WarmBackground
import kotlinx.coroutines.launch

data class MenuItem(
    val id: Int,
    val title: String,
    val description: String,
    val iconRes: Int,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMyBag: () -> Unit,
    onNavigateToMaternityBagChecklist: () -> Unit = {},
    onNavigateToBabySuppliesChecklist: () -> Unit = {},
    onNavigateToTodoChecklist: () -> Unit = {},
    onNavigateToBenefits: () -> Unit,
    onNavigateToSurvey: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by com.example.maternitybag.data.UserProfileRepository.currentProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        MenuItem(
            id = 1,
            title = stringResource(id = R.string.menu_bag_title),
            description = stringResource(id = R.string.menu_bag_desc),
            iconRes = R.drawable.ic_checklist, // 캐리어/체크리스트 아이콘
            accentColor = Color(0xFFFF6F59)
        ),
        MenuItem(
            id = 2,
            title = stringResource(id = R.string.menu_items_title),
            description = stringResource(id = R.string.menu_items_desc),
            iconRes = R.drawable.ic_baby_bottle, // 젖병 아이콘
            accentColor = Color(0xFF43A047)
        ),
        MenuItem(
            id = 3,
            title = stringResource(id = R.string.menu_todo_title),
            description = stringResource(id = R.string.menu_todo_desc),
            iconRes = R.drawable.ic_calendar_todo, // 달력 아이콘
            accentColor = Color(0xFF7E57C2)
        ),
        MenuItem(
            id = 4,
            title = stringResource(id = R.string.menu_benefit_title),
            description = stringResource(id = R.string.menu_benefit_desc),
            iconRes = R.drawable.ic_gift_benefit, // 동전/혜택 아이콘
            accentColor = Color(0xFFFFA000)
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.dashboard_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "로그아웃 / 뒤로가기",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WarmBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                val (dDayText, weeksText) = userProfile.calculateDDayInfo()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F4)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD8CD))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (userProfile.userType == com.example.maternitybag.data.LoginType.GUEST) "👶" else "🌸",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${userProfile.userName} (${userProfile.userType.displayName})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                if (userProfile.userType == com.example.maternitybag.data.LoginType.GUEST) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "비회원(저장안됨)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = onNavigateToSurvey,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = PeachPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCBC)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("설문 수정 ⚙️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 태그 목록
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            val genderText = when (userProfile.gender) {
                                "남아" -> "👦 남아"
                                "여아" -> "👧 여아"
                                else -> "🤍 성별미정"
                            }
                            val careText = if (userProfile.careCenter == "이용함") "🏢 조리원" else "🏡 자택조리"

                            ProfileTagChip(text = if (weeksText.isNotBlank()) "$dDayText ($weeksText)" else dDayText)
                            ProfileTagChip(text = genderText)
                            ProfileTagChip(text = userProfile.birthType)
                            ProfileTagChip(text = careText)
                            ProfileTagChip(text = "📍 ${userProfile.region}")
                        }
                    }
                }
            }

            items(menuItems.size) { index ->
                val item = menuItems[index]
                MenuCardItem(
                    item = item,
                    onClick = {
                        if (item.id == 1) {
                            onNavigateToMaternityBagChecklist()
                        } else if (item.id == 2) {
                            onNavigateToBabySuppliesChecklist()
                        } else if (item.id == 3) {
                            onNavigateToTodoChecklist()
                        } else if (item.id == 4) {
                            onNavigateToBenefits()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "[${item.title}] 화면으로 이동합니다 🌸"
                                )
                            }
                        }
                    }
                )
            }

            // 하단 '내 가방' 특별 버튼
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Button(
                    onClick = onNavigateToMyBag,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PeachPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_maternity_bag),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.btn_my_bag),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${stringResource(id = R.string.btn_my_bag_desc)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MenuCardItem(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 원형 아이콘 배경
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(item.accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // 텍스트 정보
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileTagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaternityBagTheme {
        DashboardScreen(
            onNavigateBack = {},
            onNavigateToMyBag = {},
            onNavigateToMaternityBagChecklist = {},
            onNavigateToBenefits = {}
        )
    }
}
