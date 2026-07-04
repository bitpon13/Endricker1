package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.BrazilBlue
import com.example.ui.theme.BrazilGreen
import com.example.ui.theme.BrazilYellow
import com.example.ui.theme.SophisticatedBg
import com.example.ui.theme.SophisticatedOutline
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import kotlinx.coroutines.launch

@Composable
fun ClickerScreen(
    viewModel: ClickerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val particles by viewModel.particles.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedBg)
    ) {
        // 1. Custom Header (Matching HTML classes p-6 flex justify-between items-center bg-[#1C1B1F])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SophisticatedBg)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CARREIRA PROFISSIONAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedPrimary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Endrick Clicker",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
            }
            // Real-time GPS Badge (Matching HTML bg-[#49454F] px-3 py-1.5 rounded-full)
            Row(
                modifier = Modifier
                    .background(SophisticatedOutline, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GPS: ${formatCompact(state.goalsPerSecond)}/s",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SophisticatedTextPrimary
                )
            }
        }

        // 2. Goal Counter Section (Matching HTML flex flex-col items-center justify-center py-4)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = formatGoalsFormatted(state.totalGoals),
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-1.5).sp,
                modifier = Modifier.testTag("goals_counter")
            )
            Text(
                text = "GOLS NA SELEÇÃO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SophisticatedSecondary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // 3. Main Clicker Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Glow behind player avatar (Matching HTML relative-glow-blur)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer(alpha = 0.12f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(SophisticatedPrimary, Color.Transparent)
                        )
                    )
            )

            // Clicker scale logic
            var targetScale by remember { mutableStateOf(1f) }
            val animatedScale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "click_scale"
            )

            // The Player Avatar Frame (Matching HTML border-4 border-[#49454F] p-2 bg-[#2B2930])
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer(
                        scaleX = animatedScale,
                        scaleY = animatedScale
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                targetScale = 0.92f
                                tryAwaitRelease()
                                targetScale = 1.0f
                            },
                            onTap = { offset ->
                                viewModel.clickEndrick(
                                    x = offset.x + (240.dp.toPx() / 2) - 120.dp.toPx(),
                                    y = offset.y + (240.dp.toPx() / 2) - 120.dp.toPx()
                                )
                            }
                        )
                    }
                    .testTag("endrick_clicker")
            ) {
                // Circle outer border and container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, SophisticatedOutline, CircleShape)
                        .padding(8.dp)
                        .background(SophisticatedSurface, CircleShape)
                        .clip(CircleShape)
                ) {
                    // Image with background gradient (Matching HTML bg-gradient-to-b from-[#49454F] to-[#1C1B1F])
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(SophisticatedOutline, SophisticatedBg)
                                )
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_endrick),
                            contentDescription = "Clique no Endrick",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Custom Realistic Brazilian Flag Badge Overlay on bottom-right of avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, SophisticatedBg, CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Brazil Flag inner layout
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF009739), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Yellow rhombus (rotated square)
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .rotate(45f)
                                .background(Color(0xFFFEDD00)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Blue sphere inside
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color(0xFF012169), CircleShape)
                            )
                        }
                    }
                }
            }

            // Floating Click particles (+1 with soccer ball rising)
            Box(modifier = Modifier.fillMaxSize()) {
                particles.forEach { particle ->
                    key(particle.id) {
                        FloatingParticle(particle = particle)
                    }
                }
            }
        }

        // 4. Upgrades Section (Matching HTML bg-[#2B2930] rounded-t-[32px] p-6 pb-8 shadow-2xl)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
            color = SophisticatedSurface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Header (Matching HTML flex justify-between items-center mb-4)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MELHORIAS DISPONÍVEIS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Zerar Carreira",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedSecondary,
                        modifier = Modifier
                            .clickable { showResetDialog = true }
                            .padding(4.dp)
                    )
                }

                // Scrollable Upgrades list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.upgrades) { upgrade ->
                        val qty = viewModel.getUpgradeQty(upgrade.id, state)
                        val cost = upgrade.getCurrentCost(qty)
                        val canAfford = state.totalGoals >= cost

                        // Upgrade Card (Matching HTML bg-[#1C1B1F] p-3 rounded-2xl border border-[#49454F])
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = SophisticatedBg.copy(alpha = if (canAfford) 1.0f else 0.6f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (canAfford) SophisticatedOutline else SophisticatedOutline.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = canAfford) {
                                    viewModel.buyUpgrade(upgrade)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Upgrade Icon Badge (Matching HTML rounded-xl bg-[#D0BCFF] flex items-center justify-center text-2xl text-[#381E72])
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = if (canAfford) SophisticatedPrimary else SophisticatedSecondary.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = upgrade.icon,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Name and Description
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = upgrade.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SophisticatedTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (qty > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(SophisticatedOutline, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "Lvl $qty",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SophisticatedPrimary
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (upgrade.clickIncrease > 0) {
                                        "+${formatCompact(upgrade.clickIncrease)} por clique"
                                    } else {
                                        "+${formatCompact(upgrade.passiveIncrease)} por segundo"
                                    },
                                    fontSize = 11.sp,
                                    color = SophisticatedTextSecondary,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }

                            // Price and currency (Matching HTML text-right text-xs font-bold text-[#D0BCFF])
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = formatGoalsCompact(cost),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (canAfford) SophisticatedPrimary else SophisticatedSecondary
                                )
                                Text(
                                    text = "Gols",
                                    fontSize = 10.sp,
                                    color = SophisticatedTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Bottom Navigation (Matching HTML border-t border-[#49454F] h-16 bg-[#1C1B1F])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(SophisticatedBg)
                .border(width = 1.dp, color = SophisticatedOutline, shape = RoundedCornerShape(0.dp))
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Active "Jogo"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 30.dp)
                        .background(SophisticatedOutline, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚽", fontSize = 16.sp)
                }
                Text(
                    text = "Jogo",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Tab 2: Locked "Ranking"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer(alpha = 0.45f)
            ) {
                Box(
                    modifier = Modifier.size(width = 48.dp, height = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 16.sp)
                }
                Text(
                    text = "Ranking",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Tab 3: Locked "Estádio"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer(alpha = 0.45f)
            ) {
                Box(
                    modifier = Modifier.size(width = 48.dp, height = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏟️", fontSize = 16.sp)
                }
                Text(
                    text = "Estádio",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }

    // Confirmation reset dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Zerar Progresso?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text(text = "Você tem certeza que quer recomeçar sua carreira de gols do zero? Todas as melhorias serão perdidas!", color = SophisticatedTextPrimary) },
            containerColor = SophisticatedSurface,
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetGame()
                        showResetDialog = false
                    }
                ) {
                    Text(text = "SIM, ZERAR", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = "CANCELAR", color = Color.White)
                }
            }
        )
    }
}

// Float custom particle implementation
@Composable
fun FloatingParticle(particle: ClickParticle) {
    val animDuration = 800
    val alpha = remember { Animatable(1f) }
    val yOffset = remember { Animatable(0f) }
    val xOffset = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    val rotation = remember { Animatable(0f) }

    val randomDirection = remember { if (Math.random() > 0.5) 1 else -1 }
    val randomScatterX = remember { (Math.random() * 40f - 20f).toFloat() }

    LaunchedEffect(particle.id) {
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = animDuration, easing = LinearEasing)
            )
        }
        launch {
            yOffset.animateTo(
                targetValue = -180f,
                animationSpec = tween(durationMillis = animDuration, easing = FastOutLinearInEasing)
            )
        }
        launch {
            xOffset.animateTo(
                targetValue = randomScatterX + (randomDirection * 30f),
                animationSpec = tween(durationMillis = animDuration, easing = LinearEasing)
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = randomDirection * 90f,
                animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset(
                x = with(LocalDensity.current) { (particle.x).toDp() + xOffset.value.dp },
                y = with(LocalDensity.current) { (particle.y).toDp() + yOffset.value.dp }
            )
            .graphicsLayer(
                alpha = alpha.value,
                scaleX = scale.value,
                scaleY = scale.value
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                .background(SophisticatedSurface.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                .border(1.dp, SophisticatedOutline, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = particle.emoji,
                fontSize = 15.sp,
                modifier = Modifier
                    .graphicsLayer(rotationZ = rotation.value)
                    .padding(end = 4.dp)
            )
            Text(
                text = particle.text,
                color = SophisticatedPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }
    }
}

// Goals fully formatted with Brazilian thousand periods (e.g. 1.240.500)
fun formatGoalsFormatted(amount: Double): String {
    val rounded = amount.toLong()
    return String.format("%,d", rounded).replace(",", ".")
}

// Goals compact formatting for upgrade costs
fun formatGoalsCompact(amount: Double): String {
    return when {
        amount < 1000.0 -> "%.0f".format(amount)
        amount < 1_000_000.0 -> {
            val value = amount / 1000.0
            if (value % 1.0 == 0.0) "%.0fK".format(value) else "%.1fK".format(value).replace(".", ",")
        }
        amount < 1_000_000_000.0 -> {
            val value = amount / 1_000_000.0
            if (value % 1.0 == 0.0) "%.0fM".format(value) else "%.1fM".format(value).replace(".", ",")
        }
        else -> {
            val value = amount / 1_000_000_000.0
            if (value % 1.0 == 0.0) "%.0fB".format(value) else "%.1fB".format(value).replace(".", ",")
        }
    }
}

fun formatCompact(amount: Double): String {
    return formatGoalsCompact(amount)
}
