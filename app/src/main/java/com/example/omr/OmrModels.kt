package com.example.omr

data class RollBubbleCoord(
    val digitPosition: Int, // 1, 2, 3, 4
    val digit: Int, // 0..9
    val cx: Float,
    val cy: Float
)

data class QuestionBubbleCoord(
    val questionNumber: Int, // 1..100
    val option: String, // "A", "B", "C", "D" or "ক", "খ", "গ", "ঘ"
    val cx: Float,
    val cy: Float
)

data class QuestionEvaluation(
    val questionNumber: Int,
    val studentAnswer: String, // "A", "B", "C", "D", "MULTIPLE", or "NONE"
    val correctAnswer: String,
    val isCorrect: Boolean,
    val isAttempted: Boolean,
    val fillRatios: Map<String, Float>
)

data class OmrScanResult(
    val detectedRoll: String,
    val rollConfidences: List<Float>,
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val rawScore: Float,
    val maxScore: Float,
    val percentage: Float,
    val isPassed: Boolean,
    val passingPercentage: Float,
    val negativeMarksPerWrong: Float,
    val questionEvaluations: List<QuestionEvaluation>,
    val processedTimestamp: Long = System.currentTimeMillis()
)
