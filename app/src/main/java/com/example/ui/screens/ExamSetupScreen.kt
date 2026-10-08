package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omr.OmrCoordinates
import com.example.ui.MainViewModel
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ExamSetupScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val examConfig by viewModel.examConfig.collectAsState()

    var examTitle by remember(examConfig.examTitle) { mutableStateOf(examConfig.examTitle) }
    var totalQuestions by remember(examConfig.totalQuestions) { mutableIntStateOf(examConfig.totalQuestions) }
    var passingPercentage by remember(examConfig.passingPercentage) { mutableFloatStateOf(examConfig.passingPercentage) }
    var negativeMarks by remember(examConfig.negativeMarksPerWrong) { mutableFloatStateOf(examConfig.negativeMarksPerWrong) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 16.dp)
            .testTag("exam_setup_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Parameters Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exam Scoring Parameters",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = examTitle,
                        onValueChange = { examTitle = it },
                        label = { Text("Exam Name / Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("exam_title_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = CyanNeon,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Total Questions Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Questions", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = "$totalQuestions Questions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    Slider(
                        value = totalQuestions.toFloat(),
                        onValueChange = { totalQuestions = it.toInt() },
                        valueRange = 10f..100f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanNeon,
                            activeTrackColor = CyanNeon,
                            inactiveTrackColor = DarkBorder
                        ),
                        modifier = Modifier.testTag("total_questions_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Passing Percentage Slider (Default 80%)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Passing Percentage", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = "${passingPercentage.toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                    Slider(
                        value = passingPercentage,
                        onValueChange = { passingPercentage = it },
                        valueRange = 40f..100f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldGreen,
                            activeTrackColor = EmeraldGreen,
                            inactiveTrackColor = DarkBorder
                        ),
                        modifier = Modifier.testTag("passing_percentage_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Negative Marking Slider (Default 0.25)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Negative Mark per Wrong", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = "-${String.format("%.2f", negativeMarks)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleNeon
                        )
                    }
                    Slider(
                        value = negativeMarks,
                        onValueChange = { negativeMarks = (Math.round(it * 20.0f) / 20.0f) },
                        valueRange = 0.0f..1.0f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleNeon,
                            activeTrackColor = PurpleNeon,
                            inactiveTrackColor = DarkBorder
                        ),
                        modifier = Modifier.testTag("negative_marking_slider")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.updateExamParameters(
                                totalQuestions = totalQuestions,
                                passingPercentage = passingPercentage,
                                negativeMarksPerWrong = negativeMarks,
                                title = examTitle
                            )
                            Toast.makeText(context, "Parameters saved successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_parameters_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanNeon,
                            contentColor = AmoledBlack
                        )
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply Parameters", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Answer Key Module
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Answer Key Module (1 to $totalQuestions)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Set correct options: ক (A), খ (B), গ (C), ঘ (D)",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick fill helper buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setAllAnswerKeys("A") },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                        ) {
                            Text("All ক", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.setAllAnswerKeys("B") },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                        ) {
                            Text("All খ", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val opts = listOf("A", "B", "C", "D")
                                (1..totalQuestions).forEach { q ->
                                    viewModel.setQuestionAnswerKey(q, opts[(q - 1) % 4])
                                }
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                        ) {
                            Text("ABCD", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.randomizeAnswerKeys() },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Mix", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // List of questions to set answer key
        val questionList = (1..totalQuestions).toList()
        items(questionList) { qNum ->
            val selectedOption = examConfig.answerKeys[qNum] ?: "A"
            AnswerKeyRow(
                questionNumber = qNum,
                selectedOption = selectedOption,
                onOptionSelected = { opt ->
                    viewModel.setQuestionAnswerKey(qNum, opt)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun AnswerKeyRow(
    questionNumber: Int,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("A", "B", "C", "D")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${String.format("%02d", questionNumber)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { opt ->
                    val isSelected = opt.equals(selectedOption, ignoreCase = true)
                    val label = OmrCoordinates.getOptionBengaliLabel(opt)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) CyanNeon else DarkSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) CyanNeon else DarkBorder,
                                CircleShape
                            )
                            .clickable { onOptionSelected(opt) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$label",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) AmoledBlack else TextWhite
                        )
                    }
                }
            }
        }
    }
}
