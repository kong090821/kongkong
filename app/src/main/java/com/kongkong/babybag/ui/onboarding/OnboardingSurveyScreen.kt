package com.kongkong.babybag.ui.onboarding

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kongkong.babybag.data.UserProfile
import com.kongkong.babybag.data.UserProfileRepository
import com.kongkong.babybag.theme.BorderLight
import com.kongkong.babybag.theme.PeachLight
import com.kongkong.babybag.theme.PeachPrimary
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingSurveyScreen(
    onNavigateBack: () -> Unit,
    onCompleteSurvey: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProfile by UserProfileRepository.currentProfile.collectAsState()

    var dueDate by remember { mutableStateOf(currentProfile.dueDate.ifBlank { "2026-10-15" }) }
    var selectedGender by remember { mutableStateOf(currentProfile.gender) }
    var selectedRegion by remember { mutableStateOf(currentProfile.region) }
    var selectedBirthType by remember { mutableStateOf(currentProfile.birthType) }
    var selectedCareCenter by remember { mutableStateOf(currentProfile.careCenter) }

    val scrollState = rememberScrollState()

    val regions = listOf(
        "서울", "경기", "인천", "부산", "충북", "대구", "대전", "광주",
        "세종", "울산", "강원", "충남", "전북", "전남", "경북", "경남", "제주"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "맞춤 온보딩 설문",
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
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 상단 환영 배너
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0EC))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "반가워요, ! ??",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PeachPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "5가지 질문에 답해주시면 딱 맞는 출산가방 준비물과 지자체 혜택을 자동으로 챙겨드려요.",
                        fontSize = 12.5.sp,
                        color = TextMuted,
                        lineHeight = 17.sp
                    )
                }
            }

            // 1. 출산 예정일
            SurveySectionCard(title = "?? 1. 출산 예정일") {
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("YYYY-MM-DD 형식 (예: 2026-10-15)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PeachPrimary,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFAFAFA)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                val (dDayText, weeksText) = UserProfile(dueDate = dueDate).calculateDDayInfo()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PeachLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (weeksText.isNotBlank()) " ()" else dDayText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PeachPrimary
                    )
                }
            }

            // 2. 아이 성별
            SurveySectionCard(title = "?? 2. 아이 성별") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("남아" to "?? 남아", "여아" to "?? 여아", "모름" to "?? 아직 몰라요").forEach { (key, label) ->
                        SurveyOptionChip(
                            label = label,
                            isSelected = selectedGender == key,
                            onClick = { selectedGender = key },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. 거주지역
            SurveySectionCard(title = "?? 3. 거주지역 (지자체 혜택 연동)") {
                Text(
                    text = "* 선택하신 지역의 특화 출산 지원금 및 바우처가 자동 담깁니다.",
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    regions.forEach { reg ->
                        val isSelected = selectedRegion == reg
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PeachPrimary else Color(0xFFF7F2EF))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PeachPrimary else BorderLight,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedRegion = reg }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = reg,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextDark
                            )
                        }
                    }
                }
            }

            // 4. 분만 방법
            SurveySectionCard(title = "?? 4. 분만 방법 (맞춤 준비물 연동)") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("자연분만" to "?? 자연분만", "제왕절개" to "?? 제왕절개", "미정" to "?? 아직 미정").forEach { (key, label) ->
                        SurveyOptionChip(
                            label = label,
                            isSelected = selectedBirthType == key,
                            onClick = { selectedBirthType = key },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                val hint = when (selectedBirthType) {
                    "제왕절개" -> "* 제왕절개: 산후복대, 흉터관리 겔, 구부러지는 빨대, 안심팬티 자동 담기"
                    "자연분만" -> "* 자연분만: 회음부 방석, 좌욕기판, 마이비데 등 산후회복템 자동 담기"
                    else -> "* 미정: 주요 회복용품 공통 준비 지원"
                }
                Text(text = hint, fontSize = 11.5.sp, color = Color(0xFFE65100))
            }

            // 5. 조리원 이용 여부
            SurveySectionCard(title = "?? 5. 산후조리원 이용 여부") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("이용함" to "?? 조리원 입소", "이용안함" to "?? 자택 산후조리").forEach { (key, label) ->
                        SurveyOptionChip(
                            label = label,
                            isSelected = selectedCareCenter == key,
                            onClick = { selectedCareCenter = key },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                val hint = if (selectedCareCenter == "이용함") {
                    "* 조리원 이용: 유축 깔때기, 모유저장팩, 젖병세제 등 조리원 생활템 자동 담기"
                } else {
                    "* 자택 조리: 산후도우미 바우처 및 가정 케어 중심 안내"
                }
                Text(text = hint, fontSize = 11.5.sp, color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 완료 버튼
            Button(
                onClick = {
                    val updated = currentProfile.copy(
                        dueDate = dueDate,
                        gender = selectedGender,
                        region = selectedRegion,
                        birthType = selectedBirthType,
                        careCenter = selectedCareCenter
                    )
                    UserProfileRepository.updateProfile(updated)
                    onCompleteSurvey()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PeachPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "? 나만의 맞춤 출산가방 & 혜택 자동 생성 ?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SurveySectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun SurveyOptionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PeachPrimary else Color(0xFFF9F6F4))
            .border(
                width = 1.5.dp,
                color = if (isSelected) PeachPrimary else BorderLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextDark
        )
    }
}