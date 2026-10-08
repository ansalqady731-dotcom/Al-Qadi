package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MathViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MathViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    var showTermsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات وحول التطبيق", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Appearance card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("المظهر والواجهة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("الوضع الداكن (Dark Mode)", fontSize = 14.sp)
                        }
                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("حجم الخط الرياضي: ${String.format("%.1f", uiState.fontSizeScale)}x", fontSize = 13.sp)
                    Slider(
                        value = uiState.fontSizeScale,
                        onValueChange = { viewModel.setFontSizeScale(it) },
                        valueRange = 0.8f..1.4f,
                        steps = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Curriculum & Curriculum Info
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("المنهج والمحاور المدعومة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• الصف الثالث الثانوي (علمي رياضيات) - جمهورية مصر العربية.", fontSize = 13.sp)
                    Text("• الجبر والهندسة الفراغية (الأعداد المركبة، التباديل، التوافيق، ذات الحدين، المصفوفات، الفراغية).", fontSize = 13.sp)
                    Text("• التفاضل والتكامل (المشتقات العليا، نهايات الدوال المرتبطة بالعدد هـ، المعدلات الزمنية، رسم المنحنيات، التكامل المحدد).", fontSize = 13.sp)
                    Text("• الإحصاء والاحتمالات (الارتباط والانحدار، الاحتمال الشرطي، المتغيرات العشوائية والتوزيع الطبيعي).", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legal & Terms
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("الشروط والخصوصية", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showTermsDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إخلاء المسؤولية وسياسة الخصوصية")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Version
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("المساعد الرياضي الشامل v1.0", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("محرك الحلول والتحقق الرياضي الذاتي للثانوية العامة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("أوافق وأفهم")
                }
            },
            title = { Text("إخلاء المسؤولية والشروط") },
            text = {
                Text(
                    "هذا التطبيق أداة تعليمية مساعدة مخصصة لطلاب الثانوية العامة لفهم خطوات حل المسائل الرياضية وطرق التحقق منها.\n\n" +
                            "التطبيق غير تابع رسمياً لوزارة التربية والتعليم، وحلول الذكاء الاصطناعي مدعمة بفحص حسابي وتحقّق ولكن يوصى دائماً بمراجعة المعلم والنماذج الوزارية المعتمدة.",
                    fontSize = 13.5.sp,
                    lineHeight = 22.sp
                )
            }
        )
    }
}
