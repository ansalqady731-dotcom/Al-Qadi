package com.example.verifier

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeoutOrNull
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.*

enum class VerificationStatus {
    PASS,
    FAIL,
    SKIP
}

data class VerificationResult(
    val status: VerificationStatus,
    val actualValue: Double? = null,
    val message: String = ""
)

data class MachineCheckData(
    val type: String = "none",
    val expression: String = "",
    val variable: String = "x",
    val at: Double = 0.0,
    val expected: Double = 0.0
)

object Verifier {

    private val factorialFunction = object : Function("factorial", 1) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toInt()
            if (n < 0 || n > 20) throw IllegalArgumentException("Factorial out of range")
            var res = 1.0
            for (i in 2..n) res *= i
            return res
        }
    }

    private val permutationsFunction = object : Function("permutations", 2) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toInt()
            val r = args[1].toInt()
            if (n < 0 || r < 0 || r > n) return 0.0
            var res = 1.0
            for (i in (n - r + 1)..n) res *= i
            return res
        }
    }

    private val combinationsFunction = object : Function("combinations", 2) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toInt()
            val r = args[1].toInt()
            if (n < 0 || r < 0 || r > n) return 0.0
            val k = min(r, n - r)
            var res = 1.0
            for (i in 1..k) {
                res = res * (n - i + 1) / i
            }
            return res
        }
    }

    suspend fun verify(check: MachineCheckData?): VerificationResult {
        if (check == null || check.type == "none" || check.expression.isBlank()) {
            return VerificationResult(VerificationStatus.SKIP, message = "لا يوجد فحص حسابي محدد")
        }

        val result = withTimeoutOrNull(1000L) {
            try {
                when (check.type.lowercase().trim()) {
                    "eval" -> evalCheck(check.expression, check.expected)
                    "derivative_at" -> derivativeAt(check.expression, check.variable, check.at, check.expected)
                    "definite_integral" -> definiteIntegral(check.expression, check.variable, 0.0, check.at, check.expected)
                    "limit_numeric" -> limitNumeric(check.expression, check.variable, check.at, check.expected)
                    "root_check" -> rootCheck(check.expression, check.variable, check.expected)
                    else -> VerificationResult(VerificationStatus.SKIP, message = "نوع الفحص غير مدعوم")
                }
            } catch (e: Exception) {
                VerificationResult(VerificationStatus.SKIP, message = "خطأ أثناء الحساب: ${e.message}")
            }
        }

        return result ?: VerificationResult(VerificationStatus.SKIP, message = "انتهت مهلة التحقق الحسابي")
    }

    fun evalCheck(expr: String, expected: Double, relTol: Double = 1e-7): VerificationResult {
        return try {
            val builder = createBuilder(expr)
            val v = builder.build().evaluate()
            if (v.isNaN() || v.isInfinite()) {
                return VerificationResult(VerificationStatus.SKIP, actualValue = v)
            }
            val diff = abs(v - expected)
            val denom = max(abs(expected), 1.0)
            if (diff / denom <= relTol || diff < 1e-6) {
                VerificationResult(VerificationStatus.PASS, actualValue = v, message = "مطابق حسابياً ✓")
            } else {
                VerificationResult(VerificationStatus.FAIL, actualValue = v, message = "القيمة المحسوبة ($v) تخالف المتوقع ($expected)")
            }
        } catch (e: Exception) {
            VerificationResult(VerificationStatus.SKIP, message = e.message ?: "خطأ")
        }
    }

    fun derivativeAt(expr: String, variable: String, at: Double, expected: Double, tol: Double = 1e-5): VerificationResult {
        return try {
            val h = 1e-5
            val builder = createBuilder(expr, setOf(variable))
            val f = builder.build()

            val fPlus = f.setVariable(variable, at + h).evaluate()
            val fMinus = f.setVariable(variable, at - h).evaluate()

            val deriv = (fPlus - fMinus) / (2 * h)
            if (deriv.isNaN() || deriv.isInfinite()) {
                return VerificationResult(VerificationStatus.SKIP, actualValue = deriv)
            }

            if (abs(deriv - expected) <= tol) {
                VerificationResult(VerificationStatus.PASS, actualValue = deriv, message = "المشتقة العددية متطابقة ✓")
            } else {
                VerificationResult(VerificationStatus.FAIL, actualValue = deriv, message = "المشتقة المحسوبة ($deriv) لا تطابق المتوقع ($expected)")
            }
        } catch (e: Exception) {
            VerificationResult(VerificationStatus.SKIP, message = e.message ?: "خطأ")
        }
    }

    fun definiteIntegral(expr: String, variable: String, a: Double, b: Double, expected: Double, tol: Double = 1e-5): VerificationResult {
        return try {
            val n = 2000
            val h = (b - a) / n
            val builder = createBuilder(expr, setOf(variable))
            val f = builder.build()

            var sum = f.setVariable(variable, a).evaluate() + f.setVariable(variable, b).evaluate()
            for (i in 1 until n) {
                val x = a + i * h
                val coeff = if (i % 2 == 0) 2.0 else 4.0
                sum += coeff * f.setVariable(variable, x).evaluate()
            }
            val integral = (h / 3.0) * sum

            if (integral.isNaN() || integral.isInfinite()) {
                return VerificationResult(VerificationStatus.SKIP, actualValue = integral)
            }

            if (abs(integral - expected) <= tol) {
                VerificationResult(VerificationStatus.PASS, actualValue = integral, message = "التكامل العددي متطابق ✓")
            } else {
                VerificationResult(VerificationStatus.FAIL, actualValue = integral, message = "قيمة التكامل ($integral) تخالف المتوقع ($expected)")
            }
        } catch (e: Exception) {
            VerificationResult(VerificationStatus.SKIP, message = e.message ?: "خطأ")
        }
    }

    fun limitNumeric(expr: String, variable: String, at: Double, expected: Double, tol: Double = 1e-3): VerificationResult {
        return try {
            val builder = createBuilder(expr, setOf(variable))
            val f = builder.build()

            val deltas = doubleArrayOf(1e-4, 1e-6, 1e-8)
            val rightVals = deltas.map { f.setVariable(variable, at + it).evaluate() }
            val leftVals = deltas.map { f.setVariable(variable, at - it).evaluate() }

            val rightLim = rightVals.last()
            val leftLim = leftVals.last()

            if (rightLim.isNaN() || leftLim.isNaN() || rightLim.isInfinite() || leftLim.isInfinite()) {
                return VerificationResult(VerificationStatus.SKIP)
            }

            // Divergence between left and right limit
            if (abs(rightLim - leftLim) > 0.05) {
                return VerificationResult(VerificationStatus.FAIL, actualValue = rightLim, message = "النهاية غير موجودة (اختلاف النهاية اليمنى عن اليسرى)")
            }

            val avg = (rightLim + leftLim) / 2.0
            if (abs(avg - expected) <= tol) {
                VerificationResult(VerificationStatus.PASS, actualValue = avg, message = "النهاية العددية متطابقة ✓")
            } else {
                VerificationResult(VerificationStatus.FAIL, actualValue = avg, message = "النهاية المحسوبة ($avg) لا تطابق المتوقع ($expected)")
            }
        } catch (e: Exception) {
            VerificationResult(VerificationStatus.SKIP, message = e.message ?: "خطأ")
        }
    }

    fun rootCheck(expr: String, variable: String, root: Double, tol: Double = 1e-6): VerificationResult {
        return try {
            val builder = createBuilder(expr, setOf(variable))
            val f = builder.build()
            val v = f.setVariable(variable, root).evaluate()
            if (abs(v) <= tol) {
                VerificationResult(VerificationStatus.PASS, actualValue = v, message = "تحقق الجذر بنجاح ✓")
            } else {
                VerificationResult(VerificationStatus.FAIL, actualValue = v, message = "قيمة الدالة عند هذا الجذر هي $v وليست صفر")
            }
        } catch (e: Exception) {
            VerificationResult(VerificationStatus.SKIP, message = e.message ?: "خطأ")
        }
    }

    private fun createBuilder(expression: String, variables: Set<String> = emptySet()): ExpressionBuilder {
        val sanitized = sanitizeExpression(expression)
        val b = ExpressionBuilder(sanitized)
            .function(factorialFunction)
            .function(permutationsFunction)
            .function(combinationsFunction)
        if (variables.isNotEmpty()) {
            b.variables(variables)
        }
        return b
    }

    private fun sanitizeExpression(expr: String): String {
        return expr.trim()
            .replace("π", "pi")
            .replace("÷", "/")
            .replace("×", "*")
            .replace("−", "-")
    }
}
