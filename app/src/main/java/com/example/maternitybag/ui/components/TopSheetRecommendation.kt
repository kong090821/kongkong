package com.example.maternitybag.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maternitybag.data.ProductRecommendation
import com.example.maternitybag.data.RecommendationRepository
import com.example.maternitybag.theme.PeachPrimary
import com.example.maternitybag.theme.SoftSurface
import com.example.maternitybag.theme.TextDark
import com.example.maternitybag.theme.TextMuted

@Composable
fun TopSheetRecommendationModal(
    visible: Boolean,
    itemTitle: String?,
    onDismiss: () -> Unit
) {
    if (!visible || itemTitle == null) return

    val cleanTitle = remember(itemTitle) {
        itemTitle.replace(Regex("\\(.*?\\)"), "").trim()
    }

    val recommendations = remember(itemTitle) {
        RecommendationRepository.getTop3Recommendations(itemTitle)
    }

    val momcafeTop1 = remember(itemTitle) {
        RecommendationRepository.getMomcafeRank1Product(itemTitle)
    }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.52f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Prevent dismiss when clicking inside sheet */ }
                    ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // 1. 헤더 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF9F7))
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 36.dp)
                        ) {
                            Text(
                                text = "👶 $cleanTitle 구매 가이드",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "쿠팡 판매 인기 TOP 3 제품이에요",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // 상단 주간 크롤러 요약 바
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF2F4))
                                    .border(1.dp, Color(0xFFFFD8DF), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(text = "♥", fontSize = 11.sp, color = Color(0xFFFF2E63), fontWeight = FontWeight.Black)
                                    Text(text = "맘카페 언급 1위", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD62246))
                                    Text(
                                        text = momcafeTop1,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D2522),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.White.copy(alpha = 0.75f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "매주 자동 갱신 🔄",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF8E7D7A)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFE6E3))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Color(0xFF666666),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // 2. 1위~3위 추천 카드 리스트
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        recommendations.forEach { item ->
                            RecommendationCard(
                                item = item,
                                onOpenUrl = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                }
                            )
                        }
                    }

                    // 3. 하단 안내 및 수수료 표기
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "해당 링크를 통해 구매 시 앱 운영에 소정의 수수료가 지급될 수 있습니다.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFDDDDDD))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    item: ProductRecommendation,
    onOpenUrl: () -> Unit
) {
    val (badgeBg, badgeColor, badgeBorder) = when (item.rank) {
        1 -> Triple(Color(0xFFFFF8E1), Color(0xFFD79A00), Color(0xFFFFE082))
        2 -> Triple(Color(0xFFF1F3F4), Color(0xFF5F6368), Color(0xFFDADCE0))
        else -> Triple(Color(0xFFFBE9E7), Color(0xFFD84315), Color(0xFFFFCCBC))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        border = BorderStroke(1.5.dp, Color(0xFFF0E6E2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 상단: 랭킹 뱃지 + 브랜드/상품명
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.rankBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.brandAndName,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 중단: 1줄 핵심 특징 요약
            Text(
                text = item.featureSummary,
                fontSize = 11.5.sp,
                color = Color(0xFF555555),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 하단: [좌측 맘카페 언급 1위/만족도 배지 | 우측 최저가 확인 > 버튼]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 좌측: 맘카페 언급 1위 배지 또는 만족도 배지
                if (item.momcafeTag.isNotBlank()) {
                    if (item.rank == 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFFFFF0F3))
                                .border(1.dp, Color(0xFFFFCCD5), RoundedCornerShape(7.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(text = "♥", fontSize = 11.sp, color = Color(0xFFFF2E63), fontWeight = FontWeight.Black)
                                Text(
                                    text = item.momcafeTag.removePrefix("♥"),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9E1934),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (item.rank == 2) Color(0xFFF1F3F4) else Color(0xFFFBE9E7))
                                .border(1.dp, if (item.rank == 2) Color(0xFFDADCE0) else Color(0xFFFFCCBC), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.momcafeTag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.rank == 2) Color(0xFF5F6368) else Color(0xFFD84315)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onOpenUrl,
                    colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = "최저가 확인 >",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
