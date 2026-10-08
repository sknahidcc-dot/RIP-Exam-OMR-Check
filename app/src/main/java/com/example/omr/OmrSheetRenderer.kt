package com.example.omr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object OmrSheetRenderer {

    suspend fun generateTemplateOmrBitmap(
        rollNumber: String = "2026",
        answers: Map<Int, String> = emptyMap(),
        studentName: String = "Nahid Hasan",
        batchName: String = "RIP Batch A",
        examDate: String = "2026-10-08"
    ): Bitmap = withContext(Dispatchers.Default) {
        val width = OmrCoordinates.PAGE_WIDTH.toInt()
        val height = OmrCoordinates.PAGE_HEIGHT.toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background: clean crisp white
        canvas.drawColor(Color.WHITE)

        val bgPaint = Paint().apply {
            color = Color.parseColor("#E91E63") // Magenta header matching PDF
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 58f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val blackPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.parseColor("#333333")
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val bubbleOutlinePaint = Paint().apply {
            color = Color.parseColor("#E91E63")
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val filledBubblePaint = Paint().apply {
            color = Color.parseColor("#1A1A1A") // Filled pen/pencil ink
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val labelTextPaint = Paint().apply {
            color = Color.parseColor("#E91E63")
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val headerTextPaint = Paint().apply {
            color = Color.parseColor("#111111")
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }

        // 1. Draw 4 Corner Square Black Alignment Markers
        val markerSize = 65f
        canvas.drawRect(
            OmrCoordinates.CORNER_TOP_LEFT.first - markerSize / 2,
            OmrCoordinates.CORNER_TOP_LEFT.second - markerSize / 2,
            OmrCoordinates.CORNER_TOP_LEFT.first + markerSize / 2,
            OmrCoordinates.CORNER_TOP_LEFT.second + markerSize / 2,
            blackPaint
        )
        canvas.drawRect(
            OmrCoordinates.CORNER_TOP_RIGHT.first - markerSize / 2,
            OmrCoordinates.CORNER_TOP_RIGHT.second - markerSize / 2,
            OmrCoordinates.CORNER_TOP_RIGHT.first + markerSize / 2,
            OmrCoordinates.CORNER_TOP_RIGHT.second + markerSize / 2,
            blackPaint
        )
        canvas.drawRect(
            OmrCoordinates.CORNER_BOTTOM_LEFT.first - markerSize / 2,
            OmrCoordinates.CORNER_BOTTOM_LEFT.second - markerSize / 2,
            OmrCoordinates.CORNER_BOTTOM_LEFT.first + markerSize / 2,
            OmrCoordinates.CORNER_BOTTOM_LEFT.second + markerSize / 2,
            blackPaint
        )
        canvas.drawRect(
            OmrCoordinates.CORNER_BOTTOM_RIGHT.first - markerSize / 2,
            OmrCoordinates.CORNER_BOTTOM_RIGHT.second - markerSize / 2,
            OmrCoordinates.CORNER_BOTTOM_RIGHT.first + markerSize / 2,
            OmrCoordinates.CORNER_BOTTOM_RIGHT.second + markerSize / 2,
            blackPaint
        )

        // 2. Top Header Banner ("RIP Exam", "Questions 1 - 100", "OMR Answer Sheet - Developed by Nahid Hasan")
        val headerRect = RectF(140f, 40f, 1583f, 115f)
        canvas.drawRoundRect(headerRect, 12f, 12f, bgPaint)
        canvas.drawText("RIP Exam", 170f, 96f, textPaint)

        val subHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            isAntiAlias = true
        }
        canvas.drawText("Questions 1 – 100", 580f, 88f, subHeaderPaint)

        val rightHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("OMR Answer Sheet", 1550f, 75f, rightHeaderPaint)
        val devSubPaint = Paint().apply {
            color = Color.parseColor("#FFF3B0")
            textSize = 21f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("Developed by Nahid Hasan", 1550f, 102f, devSubPaint)

        // 3. Roll Number Info Section
        canvas.drawText("রোল নম্বর / ROLL NO", 160f, 160f, headerTextPaint)
        // Draw 4 boxes for Roll digits
        for (i in 0 until 4) {
            val boxLeft = 380f
            val boxTop = 135f + (i * 50f)
            canvas.drawRect(boxLeft, boxTop, boxLeft + 65f, boxTop + 42f, linePaint)
            val digitChar = rollNumber.getOrNull(i)?.toString() ?: ""
            if (digitChar.isNotEmpty()) {
                val boxNumPaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 28f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText(digitChar, boxLeft + 32.5f, boxTop + 32f, boxNumPaint)
            }
        }

        // Student Info Box on Right (Name, Batch, Date)
        val infoBox = RectF(1160f, 130f, 1580f, 320f)
        canvas.drawRect(infoBox, linePaint)
        val infoPaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isAntiAlias = true
        }
        canvas.drawText("নাম / Name: $studentName", 1175f, 170f, infoPaint)
        canvas.drawText("ব্যাচ / Batch: $batchName", 1175f, 220f, infoPaint)
        canvas.drawText("তারিখ / Date: $examDate", 1175f, 270f, infoPaint)

        // 4. Draw Roll Number Bubbles Matrix (from OmrCoordinates)
        // Digit positions 1..4, digits 0..9
        for (bubble in OmrCoordinates.ROLL_BUBBLES) {
            val isTargetDigit = rollNumber.getOrNull(bubble.digitPosition - 1)?.digitToIntOrNull() == bubble.digit

            if (isTargetDigit) {
                canvas.drawCircle(bubble.cx, bubble.cy, OmrCoordinates.BUBBLE_RADIUS, filledBubblePaint)
            } else {
                canvas.drawCircle(bubble.cx, bubble.cy, OmrCoordinates.BUBBLE_RADIUS, bubbleOutlinePaint)
                canvas.drawText(
                    bubble.digit.toString(),
                    bubble.cx,
                    bubble.cy + 7f,
                    labelTextPaint
                )
            }
        }

        // 5. Column Headers for Questions (3 Columns)
        val colHeadersX = listOf(140f, 640f, 1140f)
        colHeadersX.forEach { leftX ->
            val colHeaderRect = RectF(leftX, 420f, leftX + 440f, 470f)
            canvas.drawRect(colHeaderRect, linePaint)
            canvas.drawText("ক্র.নং", leftX + 25f, 455f, headerTextPaint)
            canvas.drawText("উত্তর (ক খ গ ঘ)", leftX + 160f, 455f, headerTextPaint)
        }

        // 6. Draw 100 Question Rows and Bubbles
        val qNumPaint = Paint().apply {
            color = Color.BLACK
            textSize = 21f
            isFakeBoldText = true
            isAntiAlias = true
        }

        for (q in 1..100) {
            val qStr = String.format("%02d", q)
            // Draw Q number near its bubbles
            val bubbles = OmrCoordinates.QUESTION_BUBBLES.filter { it.questionNumber == q }
            val firstBubble = bubbles.firstOrNull()
            if (firstBubble != null) {
                canvas.drawText(qStr, firstBubble.cx - 95f, firstBubble.cy + 7f, qNumPaint)
            }

            val chosenOption = answers[q] // e.g. "A", "B", "C", "D"

            bubbles.forEach { b ->
                val isFilled = chosenOption != null && chosenOption.equals(b.option, ignoreCase = true)
                if (isFilled) {
                    canvas.drawCircle(b.cx, b.cy, OmrCoordinates.BUBBLE_RADIUS, filledBubblePaint)
                } else {
                    canvas.drawCircle(b.cx, b.cy, OmrCoordinates.BUBBLE_RADIUS, bubbleOutlinePaint)
                    val label = OmrCoordinates.getOptionBengaliLabel(b.option)
                    canvas.drawText(label, b.cx, b.cy + 7f, labelTextPaint)
                }
            }
        }

        bitmap
    }
}
