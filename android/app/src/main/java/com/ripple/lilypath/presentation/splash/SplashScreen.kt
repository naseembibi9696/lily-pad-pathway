package com.ripple.lilypath.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ripple.lilypath.R
import com.ripple.lilypath.core.di.AppViewModelFactory
import com.ripple.lilypath.core.theme.LotusAccent
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.PondSecondary
import com.ripple.lilypath.core.theme.SplashOverlay
import com.ripple.lilypath.core.ui.RippleBackground
import com.ripple.lilypath.core.ui.ScreenBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }
    SplashContent()
}

@Composable
private fun SplashContent() {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        revealed = true
    }
    val logoScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.82f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "logoAlpha"
    )
    val titleOffset by animateFloatAsState(
        targetValue = if (revealed) 0f else 16f,
        animationSpec = tween(durationMillis = 500, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "titleOffset"
    )
    val transition = rememberInfiniteTransition(label = "splashLoop")
    val bob by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBob"
    )
    val drift by transition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lotusDrift"
    )

    ScreenBackground(overlay = SplashOverlay) {
        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 28.dp, bottom = 64.dp)
                .offset(x = drift.dp)
                .size(56.dp)
                .alpha(0.5f),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                RippleBackground(
                    modifier = Modifier.size(300.dp),
                    color = PondSecondary,
                    rings = 3,
                    minRadius = 70.dp,
                    maxRadius = 150.dp,
                    durationMillis = 2600
                )
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(LotusAccent.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                )
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .size(168.dp)
                        .offset(y = bob.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFFF3FBF8),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = titleOffset.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = PondSecondary.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp),
                color = LotusAccent,
                trackColor = PondPrimary.copy(alpha = 0.2f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = PondSecondary.copy(alpha = 0.85f)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(220.dp)
                .alpha(0.12f)
                .background(
                    brush = Brush.radialGradient(
                        listOf(PondSecondary, Color.Transparent)
                    ),
                    shape = RoundedCornerShape(110.dp)
                )
        )
    }
}
