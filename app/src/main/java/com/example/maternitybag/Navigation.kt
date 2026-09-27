package com.example.maternitybag

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.maternitybag.ui.benefits.BenefitsCategoryScreen
import com.example.maternitybag.ui.benefits.NationalBenefitsScreen
import com.example.maternitybag.ui.benefits.PersonalBenefitsScreen
import com.example.maternitybag.ui.benefits.RegionalBenefitsScreen
import com.example.maternitybag.ui.benefits.SavedBenefitsScreen
import com.example.maternitybag.ui.checklist.BabySuppliesChecklistScreen
import com.example.maternitybag.ui.checklist.MaternityBagChecklistScreen
import com.example.maternitybag.ui.checklist.SavedMaternityBagScreen
import com.example.maternitybag.ui.checklist.TodoChecklistScreen
import com.example.maternitybag.ui.dashboard.DashboardScreen
import com.example.maternitybag.ui.login.LoginScreen
import com.example.maternitybag.ui.mybag.MyBagScreen

import com.example.maternitybag.ui.onboarding.OnboardingSurveyScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Login)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Login> {
          LoginScreen(
            onGuestLoginClick = { backStack.add(OnboardingSurvey) },
            onSocialLoginClick = { backStack.add(OnboardingSurvey) },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<OnboardingSurvey> {
          OnboardingSurveyScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            onCompleteSurvey = { backStack.add(Dashboard) },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<Dashboard> {
          DashboardScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            onNavigateToMyBag = { backStack.add(MyBag) },
            onNavigateToMaternityBagChecklist = { backStack.add(MaternityBagChecklist) },
            onNavigateToBabySuppliesChecklist = { backStack.add(BabySuppliesChecklist) },
            onNavigateToTodoChecklist = { backStack.add(TodoChecklist) },
            onNavigateToBenefits = { backStack.add(BenefitsCategory) },
            onNavigateToSurvey = { backStack.add(OnboardingSurvey) },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<MyBag> {
          MyBagScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            onNavigateToSavedMaternityBag = { backStack.add(SavedMaternityBag) },
            onNavigateToSavedBenefits = { backStack.add(SavedBenefits) },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<MaternityBagChecklist> {
          MaternityBagChecklistScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<BabySuppliesChecklist> {
          BabySuppliesChecklistScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<TodoChecklist> {
          TodoChecklistScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<SavedMaternityBag> {
          SavedMaternityBagScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<BenefitsCategory> {
          BenefitsCategoryScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            onNavigateToNational = { backStack.add(NationalBenefits) },
            onNavigateToRegional = { backStack.add(RegionalBenefits) },
            onNavigateToPersonal = { backStack.add(PersonalBenefits) },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<NationalBenefits> {
          NationalBenefitsScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<RegionalBenefits> {
          RegionalBenefitsScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<PersonalBenefits> {
          PersonalBenefitsScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
        entry<SavedBenefits> {
          SavedBenefitsScreen(
            onNavigateBack = { backStack.removeLastOrNull() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
      },
  )
}
