package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class MathSolution(
    val topic: String = "عام",
    val difficulty: String = "متوسط",
    val confidence: Int = 90,
    val restatedQuestion: String = "",
    val questionType: String = "open",
    val finalAnswer: String = "",
    val finalAnswerText: String = "",
    val keyRule: String? = null,
    val keyFormula: String? = null,
    val commonMistake: String? = null,
    val steps: List<SolutionStep> = emptyList(),
    val machineCheck: MachineCheckDto? = null,
    val plotType: String? = null,
    val plotTitle: String? = null,
    val plotData: PlotDataDto? = null,
    val verificationBadge: String? = null,
    val isVerified: Boolean = false
)

@Serializable
data class SolutionStep(
    val stepNumber: Int = 1,
    val title: String = "",
    val explanation: String = "",
    val equation: String? = null
)

@Serializable
data class MachineCheckDto(
    val type: String = "none",
    val expression: String = "",
    val variable: String = "x",
    val at: Double = 0.0,
    val expected: Double = 0.0
)

@Serializable
data class PlotDataDto(
    val re: Double? = null,
    val im: Double? = null,
    val a: Double? = null
)

@Entity(tableName = "solution_history")
data class SolutionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionNormalized: String,
    val questionRaw: String,
    val solutionJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val topic: String = "",
    val confidence: Int = 90
)

@Entity(tableName = "exam_question")
data class ExamQuestion(
    val id: String,
    val topic: String,
    val questionText: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String,
    val points: Int = 1,
    val type: String = "true_false" // true_false or mcq
)
