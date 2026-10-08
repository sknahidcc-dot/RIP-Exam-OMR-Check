package com.example.omr

object OmrCoordinates {
    const val PAGE_WIDTH = 1723f
    const val PAGE_HEIGHT = 2448f
    const val BUBBLE_DIAMETER = 38f
    const val BUBBLE_RADIUS = 19f

    // 4 Corner Alignment square markers in 1723x2448 coordinate space
    val CORNER_TOP_LEFT = Pair(80f, 290f)
    val CORNER_TOP_RIGHT = Pair(1643f, 290f)
    val CORNER_BOTTOM_LEFT = Pair(80f, 2320f)
    val CORNER_BOTTOM_RIGHT = Pair(1643f, 2320f)

    // Roll Number coordinate mapping matching the prompt JSON
    val ROLL_BUBBLES: List<RollBubbleCoord> = buildList {
        val yCoords = listOf(164.0f, 214.0f, 264.0f, 314.0f)
        val xCoords = listOf(519f, 581f, 643f, 705f, 767f, 829f, 891f, 953f, 1015f, 1077f)

        for (pos in 1..4) {
            val cy = yCoords[pos - 1]
            for (digit in 0..9) {
                val cx = xCoords[digit]
                add(RollBubbleCoord(digitPosition = pos, digit = digit, cx = cx, cy = cy))
            }
        }
    }

    // 100 Question Bubbles: 3 Columns (1..35, 36..70, 71..100)
    // Options: A="ক", B="খ", C="গ", D="ঘ"
    val QUESTION_BUBBLES: List<QuestionBubbleCoord> = buildList {
        val options = listOf("A", "B", "C", "D")

        // Column 1: Questions 1 to 35
        // Col 1 horizontal positions for options A, B, C, D
        val col1OptionsX = listOf(270f, 340f, 410f, 480f)
        val startY = 500f
        val rowStepY = 51f

        for (i in 0 until 35) {
            val qNum = i + 1
            val cy = startY + (i * rowStepY)
            options.forEachIndexed { optIndex, opt ->
                add(QuestionBubbleCoord(questionNumber = qNum, option = opt, cx = col1OptionsX[optIndex], cy = cy))
            }
        }

        // Column 2: Questions 36 to 70
        val col2OptionsX = listOf(770f, 840f, 910f, 980f)
        for (i in 0 until 35) {
            val qNum = 36 + i
            val cy = startY + (i * rowStepY)
            options.forEachIndexed { optIndex, opt ->
                add(QuestionBubbleCoord(questionNumber = qNum, option = opt, cx = col2OptionsX[optIndex], cy = cy))
            }
        }

        // Column 3: Questions 71 to 100 (30 questions)
        val col3OptionsX = listOf(1270f, 1340f, 1410f, 1480f)
        for (i in 0 until 30) {
            val qNum = 71 + i
            val cy = startY + (i * rowStepY)
            options.forEachIndexed { optIndex, opt ->
                add(QuestionBubbleCoord(questionNumber = qNum, option = opt, cx = col3OptionsX[optIndex], cy = cy))
            }
        }
    }

    fun getOptionBengaliLabel(option: String): String = when (option) {
        "A" -> "ক"
        "B" -> "খ"
        "C" -> "গ"
        "D" -> "ঘ"
        else -> option
    }

    fun getOptionFromBengali(label: String): String = when (label) {
        "ক" -> "A"
        "খ" -> "B"
        "গ" -> "C"
        "ঘ" -> "D"
        else -> label
    }
}
