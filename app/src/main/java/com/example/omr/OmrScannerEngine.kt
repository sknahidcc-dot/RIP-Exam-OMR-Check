package com.example.omr

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.math.sqrt

object OmrScannerEngine {

    suspend fun evaluateOmrBitmap(
        originalBitmap: Bitmap,
        totalQuestions: Int,
        passingPercentage: Float,
        negativeMarksPerWrong: Float,
        answerKeys: Map<Int, String> // Question number (1..N) -> "A"|"B"|"C"|"D"
    ): OmrScanResult = withContext(Dispatchers.Default) {
        val targetWidth = OmrCoordinates.PAGE_WIDTH.toInt()
        val targetHeight = OmrCoordinates.PAGE_HEIGHT.toInt()

        val alignedBitmap = if (originalBitmap.width != targetWidth || originalBitmap.height != targetHeight) {
            Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
        } else {
            originalBitmap
        }

        // Measure average background luminance
        val bgLuminance = samplePageBackground(alignedBitmap)
        // Adaptive dark threshold: a bubble filled with pen/pencil is darker than background
        val darkThreshold = (bgLuminance * 0.62f).coerceIn(80f, 155f)

        // 1. Roll Number Extraction
        val detectedRollDigits = StringBuilder()
        val rollConfidences = mutableListOf<Float>()

        val rollBubblesByPos = OmrCoordinates.ROLL_BUBBLES.groupBy { it.digitPosition }

        for (pos in 1..4) {
            val bubblesInPos = rollBubblesByPos[pos] ?: emptyList()
            var bestDigit = -1
            var bestFillRatio = 0f
            var secondBestFillRatio = 0f

            for (bubble in bubblesInPos) {
                val fillRatio = computeBubbleFillRatio(
                    alignedBitmap,
                    bubble.cx,
                    bubble.cy,
                    OmrCoordinates.BUBBLE_RADIUS,
                    darkThreshold
                )
                if (fillRatio > bestFillRatio) {
                    secondBestFillRatio = bestFillRatio
                    bestFillRatio = fillRatio
                    bestDigit = bubble.digit
                } else if (fillRatio > secondBestFillRatio) {
                    secondBestFillRatio = fillRatio
                }
            }

            // A bubble is considered marked if its fill ratio is at least 0.30 and distinct
            if (bestFillRatio >= 0.28f && (bestFillRatio - secondBestFillRatio) >= 0.08f) {
                detectedRollDigits.append(bestDigit)
                rollConfidences.add(bestFillRatio)
            } else if (bestFillRatio >= 0.35f) {
                detectedRollDigits.append(bestDigit)
                rollConfidences.add(bestFillRatio)
            } else {
                detectedRollDigits.append("?")
                rollConfidences.add(bestFillRatio)
            }
        }

        val detectedRoll = detectedRollDigits.toString()

        // 2. Question Answer Extraction
        val questionBubblesByQ = OmrCoordinates.QUESTION_BUBBLES.groupBy { it.questionNumber }
        val evaluations = mutableListOf<QuestionEvaluation>()

        var attemptedCount = 0
        var correctCount = 0
        var wrongCount = 0
        var unansweredCount = 0

        val maxQ = totalQuestions.coerceIn(1, 100)

        for (qNum in 1..maxQ) {
            val bubbles = questionBubblesByQ[qNum] ?: emptyList()
            val fillRatios = mutableMapOf<String, Float>()

            for (b in bubbles) {
                val fillRatio = computeBubbleFillRatio(
                    alignedBitmap,
                    b.cx,
                    b.cy,
                    OmrCoordinates.BUBBLE_RADIUS,
                    darkThreshold
                )
                fillRatios[b.option] = fillRatio
            }

            // Identify options marked
            val filledOptions = fillRatios.filter { it.value >= 0.30f }.keys.toList()
            val studentAnswer: String
            val isAttempted: Boolean

            when {
                filledOptions.size == 1 -> {
                    studentAnswer = filledOptions.first()
                    isAttempted = true
                }
                filledOptions.size > 1 -> {
                    studentAnswer = "MULTIPLE"
                    isAttempted = true
                }
                else -> {
                    // Check if highest option is reasonably confident
                    val sorted = fillRatios.entries.sortedByDescending { it.value }
                    if (sorted.isNotEmpty() && sorted.first().value >= 0.26f &&
                        (sorted.first().value - (sorted.getOrNull(1)?.value ?: 0f)) >= 0.09f
                    ) {
                        studentAnswer = sorted.first().key
                        isAttempted = true
                    } else {
                        studentAnswer = "NONE"
                        isAttempted = false
                    }
                }
            }

            val correctAnswer = answerKeys[qNum] ?: "A"
            val isCorrect = isAttempted && studentAnswer == correctAnswer

            if (isAttempted) {
                attemptedCount++
                if (isCorrect) {
                    correctCount++
                } else {
                    wrongCount++
                }
            } else {
                unansweredCount++
            }

            evaluations.add(
                QuestionEvaluation(
                    questionNumber = qNum,
                    studentAnswer = studentAnswer,
                    correctAnswer = correctAnswer,
                    isCorrect = isCorrect,
                    isAttempted = isAttempted,
                    fillRatios = fillRatios
                )
            )
        }

        val rawScore = (correctCount * 1.0f) - (wrongCount * negativeMarksPerWrong)
        val maxScore = maxQ * 1.0f
        val percentage = ((rawScore / maxScore) * 100f).coerceAtLeast(0f)
        val isPassed = percentage >= passingPercentage

        OmrScanResult(
            detectedRoll = detectedRoll,
            rollConfidences = rollConfidences,
            totalQuestions = maxQ,
            attemptedCount = attemptedCount,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unansweredCount = unansweredCount,
            rawScore = rawScore,
            maxScore = maxScore,
            percentage = percentage,
            isPassed = isPassed,
            passingPercentage = passingPercentage,
            negativeMarksPerWrong = negativeMarksPerWrong,
            questionEvaluations = evaluations
        )
    }

    private fun computeBubbleFillRatio(
        bitmap: Bitmap,
        cx: Float,
        cy: Float,
        radius: Float,
        darkThreshold: Float
    ): Float {
        val centerX = cx.roundToInt()
        val centerY = cy.roundToInt()
        val r = (radius * 0.85f).roundToInt() // slightly inner radius to avoid bubble border lines

        var totalPixels = 0
        var darkPixels = 0

        val minX = (centerX - r).coerceAtLeast(0)
        val maxX = (centerX + r).coerceAtMost(bitmap.width - 1)
        val minY = (centerY - r).coerceAtLeast(0)
        val maxY = (centerY + r).coerceAtMost(bitmap.height - 1)

        val rSquared = r * r

        for (y in minY..maxY) {
            val dy = y - centerY
            for (x in minX..maxX) {
                val dx = x - centerX
                if (dx * dx + dy * dy <= rSquared) {
                    totalPixels++
                    val pixel = bitmap.getPixel(x, y)
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)
                    val luminance = 0.299f * red + 0.587f * green + 0.114f * blue
                    if (luminance < darkThreshold) {
                        darkPixels++
                    }
                }
            }
        }

        return if (totalPixels > 0) darkPixels.toFloat() / totalPixels.toFloat() else 0f
    }

    private fun samplePageBackground(bitmap: Bitmap): Float {
        // Sample several white areas on the page to determine illumination
        val samplePoints = listOf(
            Pair(100, 100),
            Pair(1600, 100),
            Pair(100, 2400),
            Pair(1600, 2400),
            Pair(860, 400)
        )
        var totalLuminance = 0f
        var count = 0
        for ((x, y) in samplePoints) {
            if (x < bitmap.width && y < bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val lum = 0.299f * Color.red(pixel) + 0.587f * Color.green(pixel) + 0.114f * Color.blue(pixel)
                totalLuminance += lum
                count++
            }
        }
        return if (count > 0) totalLuminance / count else 220f
    }
}
