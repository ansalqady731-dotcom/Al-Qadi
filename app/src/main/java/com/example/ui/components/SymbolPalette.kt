package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SymbolPalette(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val categories = listOf(
        "الأكثر استخداماً",
        "أساسية",
        "جبر وأعداد مركبة",
        "مجموعات",
        "تفاضل وتكامل",
        "لوغاريتمات",
        "مثلثات",
        "احتمالات وتوافيق",
        "هندسة تحليلية",
        "مصفوفات"
    )

    val symbolsByCategory = remember {
        listOf(
            // Most used
            listOf("+", "−", "×", "÷", "=", "√()", "^", "π", "س", "ص", "ت", "ن!", "∫", "د'(س)", "جا()", "جتا()"),
            // Basic
            listOf("+", "−", "×", "÷", "=", "≠", "≤", "≥", "<", ">", "±", "(", ")", "[", "]", "{", "}", "|", "^", "/", "√()", "∛()", "²", "³", "⁻¹", "π", "∞", "°", "%", "،", "؛"),
            // Algebra & Complex
            listOf("ت", "|ع|", "ع̄", "Σ", "!", "∈", "∉", "∀", "∃", "⇒", "⇔", "∴", "∵", "س", "ص", "ع", "ن"),
            // Sets
            listOf("∪", "∩", "⊂", "⊃", "⊆", "∅", "ح", "ط", "ص", "ق", "أ′"),
            // Calculus
            listOf("∫()ءس", "∫[أ،ب]()ءس", "ءص/ءس", "د′(س)", "د″(س)", "د‴(س)", "د⁽ⁿ⁾(س)", "نها(س→ )()", "س→∞", "س→0⁺", "س→0⁻", "+ ث", "هـ^()"),
            // Logarithms
            listOf("لو()", "لو_2()", "لو_3()", "لو_10()", "لو_هـ()", "لن()"),
            // Trigonometry
            listOf("جا()", "جتا()", "ظا()", "قا()", "قتا()", "ظتا()", "جا²()", "جتا²()", "جا⁻¹()", "θ", "α", "β"),
            // Probabilities & Combinations
            listOf("ن ل ر", "ن ق ر", "حـا()", "حـا( | )", "أ′", "أ∪ب", "أ∩ب", "!"),
            // Analytic Geometry
            listOf("⊥", "∥", "∠", "△", "ص² = 4أس", "س²/أ² + ص²/ب² = 1", "س²/أ² − ص²/ب² = 1", "√((س₂−س₁)²+(ص₂−ص₁)²)"),
            // Matrices
            listOf("[ ]", "| |", "أ⁻¹", "أᵀ")
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Categories Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            edgePadding = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("palette_tabs")
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = { selectedCategoryIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Symbols List horizontally scrollable
        val currentSymbols = symbolsByCategory.getOrElse(selectedCategoryIndex) { emptyList() }
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            currentSymbols.forEach { sym ->
                FilledTonalButton(
                    onClick = {
                        val text = textFieldValue.text
                        val selection = textFieldValue.selection
                        val start = selection.min
                        val end = selection.max

                        val hasParen = sym.contains("()")
                        val insertText = if (hasParen) sym else sym

                        val newText = text.substring(0, start) + insertText + text.substring(end)
                        val newCursor = if (hasParen) {
                            val parenPos = sym.indexOf("()")
                            start + parenPos + 1
                        } else {
                            start + sym.length
                        }

                        onValueChange(
                            textFieldValue.copy(
                                text = newText,
                                selection = TextRange(newCursor)
                            )
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(38.dp)
                        .testTag("symbol_$sym")
                ) {
                    Text(
                        text = sym,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
