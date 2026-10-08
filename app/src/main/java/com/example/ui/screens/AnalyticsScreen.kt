package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamAttemptEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val attempts by viewModel.examAttempts.collectAsState()
    val students by viewModel.studentProfiles.collectAsState()

    var selectedRollFilter by remember { mutableStateOf<String?>(null) }

    val filteredAttempts = if (selectedRollFilter != null) {
        attempts.filter { it.studentRoll == selectedRollFilter }.sortedBy { it.timestamp }
    } else {
        attempts.sortedBy { it.timestamp }
    }

    val totalCount = filteredAttempts.size
    val passCount = filteredAttempts.count { it.isPassed }
    val failCount = totalCount - passCount
    val passRate = if (totalCount > 0) (passCount.toFloat() / totalCount.toFloat()) * 100f else 0f
    val avgScore = if (totalCount > 0) filteredAttempts.map { it.score }.average().toFloat() else 0f
    val highestScore = if (totalCount > 0) filteredAttempts.maxOf { it.score } else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Interactive Analytics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Pass/Fail metrics & Student Progress Over Time",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(CyanNeon.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Insights, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Student Roll Filter Pills
        item {
            Column {
                Text(
                    text = "Filter by Student Roll No:",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterPill(
                            label = "All Students (${attempts.size})",
                            isSelected = selectedRollFilter == null,
                            onClick = { selectedRollFilter = null }
                        )
                    }

                    val distinctRolls = attempts.map { it.studentRoll }.distinct()
                    items(distinctRolls) { roll ->
                        val studentName = students.find { it.rollNumber == roll }?.name ?: "Roll $roll"
                        FilterPill(
                            label = "Roll $roll ($studentName)",
                            isSelected = selectedRollFilter == roll,
                            onClick = { selectedRollFilter = roll }
                        )
                    }
                }
            }
        }

        // Top KPI Statistics Grid (4 Cards)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiStatCard(
                        title = "Pass Rate",
                        value = "${String.format("%.1f", passRate)}%",
                        subtitle = "$passCount Passed / $totalCount Total",
                        color = if (passRate >= 70f) EmeraldGreen else PinkNeon,
                        modifier = Modifier.weight(1f)
                    )

                    KpiStatCard(
                        title = "Average Score",
                        value = String.format("%.2f", avgScore),
                        subtitle = "Overall attempts",
                        color = CyanNeon,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiStatCard(
                        title = "Highest Score",
                        value = String.format("%.2f", highestScore),
                        subtitle = "Peak achievement",
                        color = PurpleNeon,
                        modifier = Modifier.weight(1f)
                    )

                    KpiStatCard(
                        title = "Failed Attempts",
                        value = failCount.toString(),
                        subtitle = "Below passing criteria",
                        color = PinkNeon,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Student Improvement Trend Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Score Progression Trend",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = if (selectedRollFilter != null) "Student Roll $selectedRollFilter trajectory" else "Sequential exam timeline",
                                fontSize = 11.sp,
                                color = CyanNeon
                            )
                        }

                        Icon(Icons.Default.BarChart, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (filteredAttempts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No exam data to plot yet. Scan or simulate OMR tests to view live charts.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        // Canvas Line Chart
                        ProgressLineChart(attempts = filteredAttempts)
                    }
                }
            }
        }

        // Recent performance items list
        item {
            Text(
                text = "Chronological Performance Log",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(filteredAttempts.takeLast(10).reversed()) { attempt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${attempt.studentName} (Roll: ${attempt.studentRoll})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Correct: ${attempt.correctCount} • Wrong: ${attempt.wrongCount} • Blank: ${attempt.unansweredCount}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format("%.1f", attempt.percentage)}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (attempt.isPassed) EmeraldGreen else PinkNeon
                        )
                        Text(
                            text = if (attempt.isPassed) "PASSED" else "FAILED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (attempt.isPassed) EmeraldGreen else PinkNeon
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) CyanNeon else DarkSurface)
            .border(1.dp, if (isSelected) CyanNeon else DarkBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) AmoledBlack else TextWhite
        )
    }
}

@Composable
private fun KpiStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
private fun ProgressLineChart(
    attempts: List<ExamAttemptEntity>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(DarkCardBg, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            if (attempts.isEmpty()) return@Canvas

            // Horizontal grid lines
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0xFF22222E),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Map attempt percentages to coordinates
            val maxPercent = 100f
            val minPercent = 0f

            val points = attempts.mapIndexed { index, attempt ->
                val x = if (attempts.size == 1) {
                    width / 2f
                } else {
                    (index.toFloat() / (attempts.size - 1).toFloat()) * width
                }
                val normalizedY = (attempt.percentage - minPercent) / (maxPercent - minPercent)
                val y = height - (normalizedY * height).coerceIn(0f, height)
                Offset(x, y)
            }

            if (points.size >= 2) {
                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }

                // Gradient stroke
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(listOf(CyanNeon, PurpleNeon)),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Fill under curve
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(points.last().x, height)
                    lineTo(points.first().x, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(CyanNeon.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
            }

            // Draw circular data nodes
            points.forEachIndexed { idx, pt ->
                val isPass = attempts[idx].isPassed
                val nodeColor = if (isPass) EmeraldGreen else PinkNeon

                drawCircle(
                    color = nodeColor,
                    radius = 4.5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = AmoledBlack,
                    radius = 2.dp.toPx(),
                    center = pt
                )
            }
        }
    }
}
