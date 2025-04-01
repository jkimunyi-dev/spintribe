package com.android.onboardingscreen.screens.onBoarding

import com.android.onboardingscreen.R

data class OnBoardingItem(
    val image: Int,
    val title: Int,
    val description: Int
) {
    companion object {
        fun get(): List<OnBoardingItem> {
            return listOf(
                OnBoardingItem(
                    R.drawable.onboarding1,
                    R.string.onBoardingTitle1,
                    R.string.onBoardingText1
                ),
                OnBoardingItem(
                    R.drawable.onboarding2,
                    R.string.onBoardingTitle2,
                    R.string.onBoardingText2
                ),
                OnBoardingItem(
                    R.drawable.onboarding3,
                    R.string.onBoardingTitle3,
                    R.string.onBoardingText3
                ),
                OnBoardingItem(
                    R.drawable.onboarding4,
                    R.string.onBoardingTitle4,
                    R.string.onBoardingText4
                )
            )
        }
    }
}