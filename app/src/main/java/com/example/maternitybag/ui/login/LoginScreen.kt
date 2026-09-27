package com.example.maternitybag.ui.login

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maternitybag.R
import com.example.maternitybag.data.LoginType
import com.example.maternitybag.data.UserProfileRepository
import com.example.maternitybag.theme.BorderLight
import com.example.maternitybag.theme.MaternityBagTheme
import com.example.maternitybag.theme.PeachLight
import com.example.maternitybag.theme.PeachPrimary
import com.example.maternitybag.theme.SoftSurface
import com.example.maternitybag.theme.TextDark
import com.example.maternitybag.theme.TextMuted
import com.example.maternitybag.theme.WarmBackground

@Composable
fun LoginScreen(
    onGuestLoginClick: () -> Unit,
    onSocialLoginClick: (LoginType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 꽁꽁 출산가방 공식 앱 아이콘 (동글동글한 스퀘어클 형태)
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFFFF7F2)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ggong_bag),
                    contentDescription = "꽁꽁 출산가방 앱 아이콘",
                    modifier = Modifier.size(116.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 어플 제목
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PeachPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 부제목 / 슬로건
            Text(
                text = stringResource(id = R.string.app_subtitle),
                fontSize = 13.5.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 로그인 카드 컨테이너
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. 비회원으로 이용하기 버튼 우선 배치
                    Button(
                        onClick = {
                            UserProfileRepository.setLoginType(LoginType.GUEST, "비회원 방문자")
                            onGuestLoginClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFF3EE),
                            contentColor = Color(0xFFD84315)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFFFFCCBC)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = "👶 비회원으로 이용하기  ➔",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 비회원 정보 미저장 안내 문구
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
                        border = BorderStroke(1.dp, Color(0xFFFFE0B2))
                    ) {
                        Text(
                            text = "⚠️ 비회원으로 이용 시 입력하신 정보와 내 가방 내역이 저장되지 않습니다.",
                            fontSize = 11.5.sp,
                            color = Color(0xFFC62828),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 구분선
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                        Text(
                            text = "  또는 1초 간편 회원가입 / 시작  ",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3개 소셜 동그란 로고 버튼 (카카오, 네이버, 구글)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 카카오 (노란색 원형)
                        SocialRoundButton(
                            bgColor = Color(0xFFFEE500),
                            symbolText = "kakao",
                            label = "카카오",
                            textColor = Color(0xFF381E1F),
                            onClick = {
                                UserProfileRepository.setLoginType(LoginType.KAKAO, "카카오 회원")
                                onSocialLoginClick(LoginType.KAKAO)
                            }
                        )

                        Spacer(modifier = Modifier.width(22.dp))

                        // 네이버 (초록색 원형)
                        SocialRoundButton(
                            bgColor = Color(0xFF03C75A),
                            symbolText = "N",
                            label = "네이버",
                            textColor = Color.White,
                            onClick = {
                                UserProfileRepository.setLoginType(LoginType.NAVER, "네이버 회원")
                                onSocialLoginClick(LoginType.NAVER)
                            }
                        )

                        Spacer(modifier = Modifier.width(22.dp))

                        // 구글 (흰색 원형 + 테두리)
                        SocialRoundButton(
                            bgColor = Color.White,
                            symbolText = "G",
                            label = "구글",
                            textColor = Color(0xFF4285F4),
                            borderColor = Color(0xFFDDDDDD),
                            onClick = {
                                UserProfileRepository.setLoginType(LoginType.GOOGLE, "구글 회원")
                                onSocialLoginClick(LoginType.GOOGLE)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "카카오 · 네이버 · 구글로 1초 가입 및 맞춤 저장 🔒",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "이용약관   |   개인정보처리방침   |   고객지원",
                fontSize = 11.5.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SocialRoundButton(
    bgColor: Color,
    symbolText: String,
    label: String,
    textColor: Color,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = bgColor,
            shadowElevation = 3.dp,
            border = borderColor?.let { BorderStroke(1.dp, it) }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (symbolText == "kakao") "톡" else symbolText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextDark,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaternityBagTheme {
        LoginScreen(
            onGuestLoginClick = {},
            onSocialLoginClick = {}
        )
    }
}