package com.example.maternitybag

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Login : NavKey
@Serializable data object OnboardingSurvey : NavKey
@Serializable data object Dashboard : NavKey
@Serializable data object MyBag : NavKey
@Serializable data object MaternityBagChecklist : NavKey
@Serializable data object BabySuppliesChecklist : NavKey
@Serializable data object SavedMaternityBag : NavKey
@Serializable data object TodoChecklist : NavKey
@Serializable data object BenefitsCategory : NavKey
@Serializable data object NationalBenefits : NavKey
@Serializable data object RegionalBenefits : NavKey
@Serializable data object PersonalBenefits : NavKey
@Serializable data object SavedBenefits : NavKey
