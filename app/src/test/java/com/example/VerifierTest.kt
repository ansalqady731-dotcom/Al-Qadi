package com.example

import com.example.verifier.VerificationStatus
import com.example.verifier.Verifier
import org.junit.Assert.assertEquals
import org.junit.Test

class VerifierTest {

    @Test
    fun testDerivativeAt() {
        // Derivative of (x^2 - x + 1)^5 at x=0 equals -5
        val result = Verifier.derivativeAt("(x^2 - x + 1)^5", "x", 0.0, -5.0)
        assertEquals(VerificationStatus.PASS, result.status)
    }

    @Test
    fun testDefiniteIntegral() {
        // Integral of 3 * x^2 from 0 to 3 equals 27
        val result = Verifier.definiteIntegral("3 * x^2", "x", 0.0, 3.0, 27.0)
        assertEquals(VerificationStatus.PASS, result.status)
    }

    @Test
    fun testLimitNumericSuccess() {
        // Limit of sin(3*x)/x at x=0 equals 3
        val result = Verifier.limitNumeric("sin(3*x)/x", "x", 0.0, 3.0)
        assertEquals(VerificationStatus.PASS, result.status)
    }

    @Test
    fun testLimitNumericDiverges() {
        // Limit of abs(x)/x at 0 diverges (+1 vs -1) => fail
        val result = Verifier.limitNumeric("abs(x)/x", "x", 0.0, 1.0)
        assertEquals(VerificationStatus.FAIL, result.status)
    }

    @Test
    fun testPermutations() {
        // permutations(6, 2) = 30
        val result = Verifier.evalCheck("permutations(6, 2)", 30.0)
        assertEquals(VerificationStatus.PASS, result.status)
    }

    @Test
    fun testCombinations() {
        // combinations(12, 5) = 792
        val result = Verifier.evalCheck("combinations(12, 5)", 792.0)
        assertEquals(VerificationStatus.PASS, result.status)
    }

    @Test
    fun testMaliciousExpressionReturnsSkip() {
        // Invalid/malicious expression returns skip rather than throwing or failing
        val result = Verifier.evalCheck("malicious_func(123)", 0.0)
        assertEquals(VerificationStatus.SKIP, result.status)
    }
}
