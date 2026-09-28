package com.kongkong.babybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

enum class LoginType(val displayName: String) {
    GUEST("비회원"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글")
}

data class UserProfile(
    val userType: LoginType = LoginType.GUEST,
    val userName: String = "예비 부모님",
    val dueDate: String = "2026-10-15",
    val gender: String = "남아",
    val region: String = "서울",
    val birthType: String = "자연분만",
    val careCenter: String = "이용함"
) {
    fun calculateDDayInfo(): Pair<String, String> {
        if (dueDate.isBlank()) return Pair("D-Day 미설정", "")
        return try {
            val target = LocalDate.parse(dueDate)
            val today = LocalDate.now()
            val diff = ChronoUnit.DAYS.between(today, target)
            val dDayText = when {
                diff > 0 -> "D-"
                diff == 0L -> "D-Day"
                else -> "D+"
            }
            val passedDays = 280 - diff
            val weeksText = if (passedDays in 1..294) {
                "임신 주차"
            } else ""
            Pair(dDayText, weeksText)
        } catch (e: Exception) {
            Pair("D-Day", "")
        }
    }
}

object UserProfileRepository {
    private val _currentProfile = MutableStateFlow(UserProfile())
    val currentProfile: StateFlow<UserProfile> = _currentProfile.asStateFlow()

    fun updateProfile(profile: UserProfile) {
        _currentProfile.value = profile
        applySmartRecommendations(profile)
    }

    fun setLoginType(type: LoginType, name: String) {
        _currentProfile.value = _currentProfile.value.copy(
            userType = type,
            userName = name
        )
    }

    fun applySmartRecommendations(profile: UserProfile) {
        val bagIds = mutableSetOf(
            "m_cloth_1", "m_cloth_2", "m_cloth_3", "m_cloth_4", "m_cloth_8",
            "m_sk_1", "m_sk_2", "m_sk_3",
            "b_cloth_1", "b_cloth_2", "b_cloth_3", "b_care_1", "b_safe_1",
            "g_doc_1", "g_doc_2", "g_dad_1", "g_life_4", "g_life_8"
        )

        when (profile.birthType) {
            "제왕절개" -> {
                bagIds.add("m_cloth_7")
                bagIds.add("m_hyg_7")
                bagIds.add("m_hyg_1")
                bagIds.add("m_cloth_5")
                bagIds.add("g_life_1")
            }
            "자연분만" -> {
                bagIds.add("m_hyg_8")
                bagIds.add("m_hyg_3")
                bagIds.add("m_hyg_2")
            }
            else -> {
                bagIds.add("m_cloth_7")
                bagIds.add("m_hyg_8")
                bagIds.add("m_hyg_1")
                bagIds.add("m_hyg_3")
            }
        }

        if (profile.careCenter == "이용함") {
            bagIds.add("m_feed_1")
            bagIds.add("m_feed_2")
            bagIds.add("m_feed_4")
            bagIds.add("m_feed_6")
            bagIds.add("m_feed_7")
            bagIds.add("m_cloth_6")
            bagIds.add("b_care_3")
            bagIds.add("b_care_4")
        } else {
            bagIds.add("m_feed_1")
            bagIds.add("m_feed_4")
            bagIds.add("b_care_3")
        }

        MaternityBagRepository.setSavedItems(bagIds)

        val benefitIds = mutableSetOf(
            "nat_1", "nat_2", "nat_3", "nat_6", "nat_7"
        )
        val regMap = BenefitsRepository.regionalBenefits.value
        val regionList = regMap[profile.region] ?: emptyList()
        regionList.forEach { benefitIds.add(it.id) }
        BenefitsRepository.setSavedBenefitIds(benefitIds)

        val babyIds = setOf("bs_f_6", "bs_s_1", "bs_s_12", "bs_c_1", "bs_c_5", "bs_c_6")
        BabySuppliesRepository.setSavedItemIds(babyIds)

        val todoIds = mutableSetOf("todo_b_17", "todo_b_18")
        if (profile.careCenter == "이용함") {
            todoIds.add("todo_b_3")
        }
        TodoRepository.setCompletedTodoIds(todoIds)
    }
}