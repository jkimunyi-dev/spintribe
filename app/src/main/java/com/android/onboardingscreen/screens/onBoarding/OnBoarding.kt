package com.android.onboardingscreen.screens.onBoarding

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.onboardingscreen.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.android.onboardingscreen.data.DataStoreManager

val InstrumentSans = FontFamily(
    Font(R.font.instrument_sans_semibold, FontWeight.SemiBold)
)

@Composable
fun OnBoarding(
    onNavigateToAuth: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val items = OnBoardingItem.get()
    val state = rememberPagerState(pageCount = { items.size })
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    LaunchedEffect(Unit) {
        Log.d("Navigation", "OnBoarding screen composed")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image with Pager
        HorizontalPager(
            state = state,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = items[page].image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(items[state.currentPage].title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = InstrumentSans,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(items[state.currentPage].description),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = InstrumentSans,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    ),
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                BottomSection(
                    size = items.size,
                    index = state.currentPage,
                    isLastPage = state.currentPage == items.size - 1,
                    onNextClicked = {
                        if (state.currentPage + 1 < items.size) {
                            scope.launch {
                                state.animateScrollToPage(state.currentPage + 1)
                            }
                        } else {
                            scope.launch {
                                dataStoreManager.setOnboardingCompleted()
                                onNavigateToAuth()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomSection(
    size: Int,
    index: Int,
    isLastPage: Boolean,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(size) {
                Indicator(isSelected = it == index)
            }
        }

        // Next/Get Started button
        Button(
            onClick = onNextClicked,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = if (isLastPage) "Get Started" else "Next",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun RowScope.Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = ""
    )

    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = if (isSelected) 1f else 0.5f))
    )
}
