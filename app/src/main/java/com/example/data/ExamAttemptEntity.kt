package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentRoll: String,
    val studentName: String,
    val examTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val negativeMarksPerWrong: Float,
    val score: Float,
    val maxScore: Float,
    val percentage: Float,
    val isPassed: Boolean,
    val passingPercentage: Float,
    val detectedRoll: String,
    val answersJson: String = "" // Question number -> chosen option string
)
