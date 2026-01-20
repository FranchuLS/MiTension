package com.fxn.mitension.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import com.fxn.mitension.R
import kotlin.math.roundToInt

@Composable
fun SplashComponent() {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.95f) }

    val infiniteTransition = rememberInfiniteTransition(label = "luz")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "desplazamiento_luz"
    )
    LaunchedEffect(Unit) {
        // Aparición muy suave en lugar de carrera diagonal
        alphaAnim.animateTo(1f, animationSpec = tween(1200))
        scaleAnim.animateTo(1f, animationSpec = spring(Spring.DampingRatioLowBouncy))
    }

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF5D8AA8), // Azul inicial
            Color(0xFFBB86FC), // Morado (el haz de luz)
            Color(0xFF5D8AA8)  // Vuelve al azul
        ),
        start = IntOffset(laserOffset.roundToInt(), laserOffset.roundToInt()).toOffset(),
        end = IntOffset(laserOffset.roundToInt() + 300, laserOffset.roundToInt() + 300).toOffset()
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.aminhatension_icon),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FyX=N",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 30.sp,
                    brush = brush
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 10.sp,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            )
        }
    }
}