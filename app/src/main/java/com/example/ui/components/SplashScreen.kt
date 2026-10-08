package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onContinue: () -> Unit
) {
    // iOS Motion Animation states
    val iconScale = remember { Animatable(0.4f) }
    val iconAlpha = remember { Animatable(0f) }
    val headerAlpha = remember { Animatable(0f) }
    val headerOffsetY = remember { Animatable(30f) }
    val subAlpha = remember { Animatable(0f) }
    val subOffsetY = remember { Animatable(20f) }
    val buttonAlpha = remember { Animatable(0f) }

    // Pulsing subtle glow transition
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(Unit) {
        // Step 1: Icon spring zoom-in (Apple Spring animation)
        iconAlpha.animateTo(1f, animationSpec = tween(400))
        iconScale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Step 2: "Nahid Bro" Header reveal
        headerAlpha.animateTo(1f, animationSpec = tween(350))
        headerOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        // Step 3: Subtitle "RIP Exam OMR Check" reveal
        subAlpha.animateTo(1f, animationSpec = tween(400))
        subOffsetY.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))

        // Step 4: Show Get Started button
        buttonAlpha.animateTo(1f, animationSpec = tween(300))

        // Auto transition after 2.6 seconds if not clicked
        delay(2600)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Icon with Apple-style halo glow
            Box(contentAlignment = Alignment.Center) {
                // Outer glowing halo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    CyanNeon.copy(alpha = 0.25f),
                                    PurpleNeon.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // App Icon Box
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(iconScale.value)
                        .alpha(iconAlpha.value)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F0F14), Color(0xFF1E1E28))
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(CyanNeon, PurpleNeon)),
                            RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "OMR Scanner",
                        tint = CyanNeon,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Header: "Nahid Bro"
            Text(
                text = "Nahid Bro",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = TextWhite,
                modifier = Modifier
                    .alpha(headerAlpha.value)
                    .testTag("splash_header")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle: "RIP Exam OMR Check"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(subAlpha.value)
                    .testTag("splash_subtitle")
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(PinkNeon)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RIP OMR Check",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanNeon,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Computer Vision Bubble & Roll No Engine",
                fontSize = 12.sp,
                color = TextMuted,
                letterSpacing = 0.5.sp,
                modifier = Modifier.alpha(subAlpha.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Apple iOS Style Pill Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(54.dp)
                    .alpha(buttonAlpha.value)
                    .testTag("splash_continue_button"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanNeon,
                    contentColor = AmoledBlack
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter Scanner",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
