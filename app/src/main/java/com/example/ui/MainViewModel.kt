package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExamAttemptEntity
import com.example.data.StudentProfileEntity
import com.example.omr.OmrCoordinates
import com.example.omr.OmrScanResult
import com.example.omr.OmrScannerEngine
import com.example.omr.OmrSheetRenderer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class AppTab(val title: String, val icon: String) {
    SCANNER("Scan OMR", "qr_code_scanner"),
    EXAM_SETUP("Exam Setup", "tune"),
    STUDENTS("Students", "people"),
    ANALYTICS("Analytics", "insights"),
    FLUTTER_EXPORT("Flutter & CI", "code")
}

data class ExamConfigState(
    val examTitle: String = "RIP Exam 2026",
    val totalQuestions: Int = 100,
    val passingPercentage: Float = 80.0f,
    val negativeMarksPerWrong: Float = 0.25f,
    val answerKeys: Map<Int, String> = defaultAnswerKeys()
) {
    companion object {
        fun defaultAnswerKeys(): Map<Int, String> {
            val options = listOf("A", "B", "C", "D")
            return (1..100).associateWith { q ->
                options[(q - 1) % 4]
            }
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()

    val studentProfiles: StateFlow<List<StudentProfileEntity>> = dao.getAllStudentProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examAttempts: StateFlow<List<ExamAttemptEntity>> = dao.getAllExamAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(AppTab.SCANNER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    private val _showDeveloperDialog = MutableStateFlow(false)
    val showDeveloperDialog: StateFlow<Boolean> = _showDeveloperDialog.asStateFlow()

    private val _examConfig = MutableStateFlow(ExamConfigState())
    val examConfig: StateFlow<ExamConfigState> = _examConfig.asStateFlow()

    // OMR Scanning & Preview State
    private val _activeBitmap = MutableStateFlow<Bitmap?>(null)
    val activeBitmap: StateFlow<Bitmap?> = _activeBitmap.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _scanResult = MutableStateFlow<OmrScanResult?>(null)
    val scanResult: StateFlow<OmrScanResult?> = _scanResult.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Interactive Demo / Simulator values
    val demoRollNumber = MutableStateFlow("2026")
    val demoStudentName = MutableStateFlow("Nahid Hasan")

    init {
        // Seed default student if empty
        viewModelScope.launch {
            if (dao.getStudentProfile("2026") == null) {
                dao.insertStudentProfile(
                    StudentProfileEntity(
                        rollNumber = "2026",
                        name = "Nahid Hasan",
                        batch = "Barishal Uni 2026"
                    )
                )
                dao.insertStudentProfile(
                    StudentProfileEntity(
                        rollNumber = "1001",
                        name = "Rafiq Islam",
                        batch = "Batch RIP-A"
                    )
                )
                dao.insertStudentProfile(
                    StudentProfileEntity(
                        rollNumber = "1002",
                        name = "Sadia Ahmed",
                        batch = "Batch RIP-A"
                    )
                )
            }
        }
    }

    fun dismissSplash() {
        _isSplashVisible.value = false
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setDeveloperDialog(show: Boolean) {
        _showDeveloperDialog.value = show
    }

    fun updateExamParameters(
        totalQuestions: Int,
        passingPercentage: Float,
        negativeMarksPerWrong: Float,
        title: String
    ) {
        _examConfig.value = _examConfig.value.copy(
            totalQuestions = totalQuestions.coerceIn(1, 100),
            passingPercentage = passingPercentage.coerceIn(0f, 100f),
            negativeMarksPerWrong = negativeMarksPerWrong.coerceAtLeast(0f),
            examTitle = title
        )
    }

    fun setQuestionAnswerKey(questionNumber: Int, option: String) {
        val currentKeys = _examConfig.value.answerKeys.toMutableMap()
        currentKeys[questionNumber] = option
        _examConfig.value = _examConfig.value.copy(answerKeys = currentKeys)
    }

    fun setAllAnswerKeys(option: String) {
        val total = _examConfig.value.totalQuestions
        val updated = (1..total).associateWith { option }
        _examConfig.value = _examConfig.value.copy(answerKeys = updated)
    }

    fun randomizeAnswerKeys() {
        val options = listOf("A", "B", "C", "D")
        val total = _examConfig.value.totalQuestions
        val updated = (1..total).associateWith { options.random() }
        _examConfig.value = _examConfig.value.copy(answerKeys = updated)
    }

    fun addStudent(rollNumber: String, name: String, batch: String) {
        viewModelScope.launch {
            if (rollNumber.isNotBlank() && name.isNotBlank()) {
                dao.insertStudentProfile(
                    StudentProfileEntity(
                        rollNumber = rollNumber.trim(),
                        name = name.trim(),
                        batch = batch.trim().ifBlank { "RIP Batch" }
                    )
                )
                _statusMessage.value = "Student $name (Roll: $rollNumber) saved!"
            }
        }
    }

    fun deleteStudent(student: StudentProfileEntity) {
        viewModelScope.launch {
            dao.deleteStudentProfile(student)
        }
    }

    fun deleteAttempt(attempt: ExamAttemptEntity) {
        viewModelScope.launch {
            dao.deleteExamAttempt(attempt)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.clearAllAttempts()
            _statusMessage.value = "All exam attempt records cleared."
        }
    }

    // Generate synthetic OMR sheet for instant testing in emulator or without physical paper
    fun generateTestSheetAndScan(
        roll: String = demoRollNumber.value,
        name: String = demoStudentName.value,
        simulatedAccuracy: Float = 0.85f // ~85% correct answers filled
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            _statusMessage.value = "Generating test OMR sheet for Roll $roll..."

            val config = _examConfig.value
            val simulatedStudentAnswers = mutableMapOf<Int, String>()
            val options = listOf("A", "B", "C", "D")

            for (q in 1..config.totalQuestions) {
                val correct = config.answerKeys[q] ?: "A"
                if (Math.random() < 0.95) { // 95% attempted
                    if (Math.random() < simulatedAccuracy) {
                        simulatedStudentAnswers[q] = correct
                    } else {
                        // pick a wrong option
                        val wrongOpts = options.filter { it != correct }
                        simulatedStudentAnswers[q] = wrongOpts.random()
                    }
                }
            }

            val renderedBitmap = OmrSheetRenderer.generateTemplateOmrBitmap(
                rollNumber = roll,
                answers = simulatedStudentAnswers,
                studentName = name,
                batchName = "Barishal Uni",
                examDate = "2026-10-08"
            )

            _activeBitmap.value = renderedBitmap
            _statusMessage.value = "Analyzing bubbles with Computer Vision..."

            val result = OmrScannerEngine.evaluateOmrBitmap(
                originalBitmap = renderedBitmap,
                totalQuestions = config.totalQuestions,
                passingPercentage = config.passingPercentage,
                negativeMarksPerWrong = config.negativeMarksPerWrong,
                answerKeys = config.answerKeys
            )

            _scanResult.value = result
            _isProcessing.value = false

            // Auto-save attempt
            saveEvaluationToDb(result, roll, name)
        }
    }

    fun evaluateCustomBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _isProcessing.value = true
            _activeBitmap.value = bitmap
            _statusMessage.value = "Processing OMR alignment & bubbles..."

            val config = _examConfig.value
            val result = OmrScannerEngine.evaluateOmrBitmap(
                originalBitmap = bitmap,
                totalQuestions = config.totalQuestions,
                passingPercentage = config.passingPercentage,
                negativeMarksPerWrong = config.negativeMarksPerWrong,
                answerKeys = config.answerKeys
            )

            _scanResult.value = result
            _isProcessing.value = false

            // Match student name if roll exists
            val matchedStudent = dao.getStudentProfile(result.detectedRoll)
            val studentName = matchedStudent?.name ?: "Student #${result.detectedRoll}"

            saveEvaluationToDb(result, result.detectedRoll, studentName)
        }
    }

    private suspend fun saveEvaluationToDb(result: OmrScanResult, roll: String, name: String) {
        val config = _examConfig.value
        val answersJsonObj = JSONObject()
        result.questionEvaluations.forEach {
            answersJsonObj.put(it.questionNumber.toString(), it.studentAnswer)
        }

        val attempt = ExamAttemptEntity(
            studentRoll = roll,
            studentName = name,
            examTitle = config.examTitle,
            timestamp = System.currentTimeMillis(),
            totalQuestions = result.totalQuestions,
            attemptedCount = result.attemptedCount,
            correctCount = result.correctCount,
            wrongCount = result.wrongCount,
            unansweredCount = result.unansweredCount,
            negativeMarksPerWrong = result.negativeMarksPerWrong,
            score = result.rawScore,
            maxScore = result.maxScore,
            percentage = result.percentage,
            isPassed = result.isPassed,
            passingPercentage = result.passingPercentage,
            detectedRoll = result.detectedRoll,
            answersJson = answersJsonObj.toString()
        )
        dao.insertExamAttempt(attempt)
        _statusMessage.value = "Evaluated: ${result.detectedRoll} - Score: ${result.rawScore}/${result.maxScore} (${if (result.isPassed) "PASSED" else "FAILED"})"
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
