package com.kongkong.babybag.ui.login

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
import com.kongkong.babybag.R
import com.kongkong.babybag.data.LoginType
import com.kongkong.babybag.data.UserProfileRepository
import com.kongkong.babybag.theme.BorderLight
import com.kongkong.babybag.theme.MaternityBagTheme
import com.kongkong.babybag.theme.PeachLight
import com.kongkong.babybag.theme.PeachPrimary
import com.kongkong.babybag.theme.SoftSurface
import com.kongkong.babybag.theme.TextDark
import com.kongkong.babybag.theme.TextMuted
import com.kongkong.babybag.theme.WarmBackground

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

            // ê½ê½ ì¶œì‚°ê°€ë°?ê³µì‹ ???„ì´ì½?(?™ê??™ê????¤í€˜ì–´???•íƒœ)
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFFFF7F2)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ggong_bag),
                    contentDescription = "ê½ê½ ì¶œì‚°ê°€ë°????„ì´ì½?,
                    modifier = Modifier.size(116.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ?´í”Œ ?œëª©
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PeachPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ë¶€?œëª© / ?¬ë¡œê±?
            Text(
                text = stringResource(id = R.string.app_subtitle),
                fontSize = 13.5.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // ë¡œê·¸??ì¹´ë“œ ì»¨í…Œ?´ë„ˆ
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
                    // 1. ë¹„íšŒ?ìœ¼ë¡??´ìš©?˜ê¸° ë²„íŠ¼ ?°ì„  ë°°ì¹˜
                    Button(
                        onClick = {
                            UserProfileRepository.setLoginType(LoginType.GUEST, "ë¹„íšŒ??ë°©ë¬¸??)
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
                            text = "?‘¶ ë¹„íšŒ?ìœ¼ë¡??´ìš©?˜ê¸°  ??,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ë¹„íšŒ???•ë³´ ë¯¸ì????ˆë‚´ ë¬¸êµ¬
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
                        border = BorderStroke(1.dp, Color(0xFFFFE0B2))
                    ) {
                        Text(
                            text = "? ï¸ ë¹„íšŒ?ìœ¼ë¡??´ìš© ???…ë ¥?˜ì‹  ?•ë³´?€ ??ê°€ë°??´ì—­???€?¥ë˜ì§€ ?ŠìŠµ?ˆë‹¤.",
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

                    // êµ¬ë¶„??
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                        Text(
                            text = "  ?ëŠ” 1ì´?ê°„íŽ¸ ?Œì›ê°€??/ ?œìž‘  ",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3ê°??Œì…œ ?™ê·¸?€ ë¡œê³  ë²„íŠ¼ (ì¹´ì¹´?? ?¤ì´ë²? êµ¬ê?)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ì¹´ì¹´??(?¸ë????í˜•)
                        SocialRoundButton(
                            bgColor = Color(0xFFFEE500),
                            symbolText = "kakao",
                            label = "ì¹´ì¹´??,
                            textColor = Color(0xFF381E1F),
                            onClick = {
                                UserProfileRepository.setLoginType(LoginType.KAKAO, "ì¹´ì¹´???Œì›")
                                onSocialLoginClick(LoginType.KAKAO)
                            }
                        )

                        Spacer(modifier = Modifier.width(22.dp))

                        // ?¤ì´ë²?(ì´ˆë¡???í˜•)
                        SocialRoundButton(
                            bgColor = Color(0xFF03C75A),
                            symbolText = "N",
                            label = "?¤ì´ë²?,
                            textColor = Color.White,
                            onClick = {
                                UserProfileRepository.setLoginType(LoginType.NAVER, "?¤ì´ë²??Œì›")
                                onSocialLoginClick(LoginType.NAVER)
                            }
                        )

                        Spacer(modifier = Modifier.width(22.dp))

                        // êµ¬ê? (?°ìƒ‰ ?í˜• + ?Œë‘ë¦?
                        SocialRoundButton(
                            bgColor = Color.White,
                            symbolText = "G",
                            label = "êµ¬ê?",
                            textColor = Color(0xFF4285F4),
                            borderColor = Color(0xFFDDDDDD),
                            onClick = {
                                UserProfileRepository.setLoginType(LoginType.GOOGLE, "êµ¬ê? ?Œì›")
                                onSocialLoginClick(LoginType.GOOGLE)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ì¹´ì¹´??Â· ?¤ì´ë²?Â· êµ¬ê?ë¡?1ì´?ê°€??ë°?ë§žì¶¤ ?€???”’",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "?´ìš©?½ê?   |   ê°œì¸?•ë³´ì²˜ë¦¬ë°©ì¹¨   |   ê³ ê°ì§€??,
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
                    text = if (symbolText == "kakao") "?? else symbolText,
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