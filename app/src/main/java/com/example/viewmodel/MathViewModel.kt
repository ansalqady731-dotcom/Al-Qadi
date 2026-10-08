package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.MathSolution
import com.example.model.SolutionEntity
import com.example.repository.MathRepository
import com.example.repository.SolveProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

data class UiState(
    val rawQuestion: String = "",
    val questionType: String = "auto", // auto, mcq, true_false, open
    val isSolving: Boolean = false,
    val progressStage: String = "",
    val currentSolution: MathSolution? = null,
    val errorMessage: String? = null,
    val selectedImageBase64: String? = null,
    val dailyLimit: Int = 20,
    val remainingToday: Int = 20,
    val isDarkMode: Boolean = false,
    val fontSizeScale: Float = 1.0f
)

class MathViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MathRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val history = repository.solutionDao.getAllSolutions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites = repository.solutionDao.getFavoriteSolutions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuestionText(text: String) {
        _uiState.value = _uiState.value.copy(rawQuestion = text, errorMessage = null)
    }

    fun updateQuestionType(type: String) {
        _uiState.value = _uiState.value.copy(questionType = type)
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = _uiState.value.copy(selectedImageBase64 = null)
            return
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        _uiState.value = _uiState.value.copy(selectedImageBase64 = base64)
    }

    fun clearImage() {
        _uiState.value = _uiState.value.copy(selectedImageBase64 = null)
    }

    fun solveCurrentQuestion() {
        val state = _uiState.value
        val question = state.rawQuestion.trim()
        val image = state.selectedImageBase64

        if (question.isBlank() && image == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "يرجى كتابة نص السؤال أو إرفاق صورة أولاً")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSolving = true, errorMessage = null, currentSolution = null)
            try {
                repository.solveMath(
                    rawQuestion = question,
                    imageBase64 = image
                ) { progress ->
                    when (progress) {
                        is SolveProgress.ReadingQuestion -> {
                            _uiState.value = _uiState.value.copy(progressStage = "أقرأ السؤال...")
                        }
                        is SolveProgress.Solving -> {
                            _uiState.value = _uiState.value.copy(progressStage = "أحل المسألة بخطوات رياضية دقيقة...")
                        }
                        is SolveProgress.Verifying -> {
                            _uiState.value = _uiState.value.copy(progressStage = "أتحقق حسابياً من النتائج...")
                        }
                        is SolveProgress.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isSolving = false,
                                progressStage = "",
                                currentSolution = progress.solution,
                                remainingToday = maxOf(0, _uiState.value.remainingToday - 1)
                            )
                        }
                        is SolveProgress.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isSolving = false,
                                progressStage = "",
                                errorMessage = progress.message
                            )
                        }
                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSolving = false,
                    progressStage = "",
                    errorMessage = e.message ?: "حدث خطأ أثناء حل المسألة"
                )
            }
        }
    }

    fun toggleFavorite(solutionId: Long, currentFav: Boolean) {
        viewModelScope.launch {
            repository.solutionDao.setFavorite(solutionId, !currentFav)
        }
    }

    fun deleteHistoryItem(solutionId: Long) {
        viewModelScope.launch {
            repository.solutionDao.deleteById(solutionId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.solutionDao.deleteAll()
        }
    }

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(isDarkMode = !_uiState.value.isDarkMode)
    }

    fun setFontSizeScale(scale: Float) {
        _uiState.value = _uiState.value.copy(fontSizeScale = scale)
    }
}
