package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamQuestion
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockExamScreen(modifier: Modifier = Modifier) {
    var examStarted by remember { mutableStateOf(false) }
    var examSubmitted by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf("الكل") }
    var timeLeftSeconds by remember { mutableStateOf(90 * 60) } // 90 mins timer

    // Static sample questions modeled on the Egyptian Thanaweya Amma curriculum
    val examQuestions = remember {
        listOf(
            ExamQuestion(
                id = "1",
                topic = "الأعداد المركبة",
                questionText = "إذا كان ع = ت^14 ، فإن ع = -1",
                correctAnswer = "صح",
                explanation = "ت^14 = ت^12 * ت^2 = 1 * (-1) = -1 فالعبارة صحيحة.",
                points = 1,
                type = "true_false"
            ),
            ExamQuestion(
                id = "2",
                topic = "التباديل والتوافيق",
                questionText = "قيمة ن التي تحقق ⁿP₂ = 30 هي:",
                options = listOf("5", "6", "7", "8"),
                correctAnswer = "6",
                explanation = "6 × 5 = 30 إذن ن = 6.",
                points = 3,
                type = "mcq"
            ),
            ExamQuestion(
                id = "3",
                topic = "نظرية ذات الحدين",
                questionText = "عدد حدود مفكوك (س + ص)¹⁰ يساوي:",
                options = listOf("10", "11", "9", "12"),
                correctAnswer = "11",
                explanation = "عدد الحدود في مفكوك ذات الحدين يساوي ن + 1 = 10 + 1 = 11.",
                points = 3,
                type = "mcq"
            ),
            ExamQuestion(
                id = "4",
                topic = "التفاضل والتكامل",
                questionText = "مشتقة د(س) = جا(س) عند س = 0 تساوي 1",
                correctAnswer = "صح",
                explanation = "د'(س) = جتا(س) وعند س = 0 تكون جتا(0) = 1.",
                points = 1,
                type = "true_false"
            ),
            ExamQuestion(
                id = "5",
                topic = "القطوع المخروطية",
                questionText = "معادلة دليل القطع المكافئ ص² = 8س هي س = -2",
                correctAnswer = "صح",
                explanation = "4أ = 8 ومنها أ = 2، فمعادلة الدليل س = -أ أي س = -2.",
                points = 1,
                type = "true_false"
            ),
            ExamQuestion(
                id = "6",
                topic = "الاحتمالات",
                questionText = "إذا كان أ، ب حدثين متنافيين فإن حـا(أ ∩ ب) يساوي:",
                options = listOf("0", "1", "حـا(أ)", "حـا(ب)"),
                correctAnswer = "0",
                explanation = "الحدثان المتنافيان تقاطعهما فاي، وبالتالي احتماله صفر.",
                points = 3,
                type = "mcq"
            )
        )
    }

    val userAnswers = remember { mutableStateMapOf<String, String>() }

    // Timer effect
    LaunchedEffect(examStarted, examSubmitted) {
        if (examStarted && !examSubmitted) {
            while (timeLeftSeconds > 0) {
                delay(1000L)
                timeLeftSeconds--
            }
            if (timeLeftSeconds == 0) {
                examSubmitted = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("امتحان تجريبي (نظام البوكليت والوزارة)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                actions = {
                    if (examStarted && !examSubmitted) {
                        val mins = timeLeftSeconds / 60
                        val secs = timeLeftSeconds % 60
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    String.format("%02d:%02d", mins, secs),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (!examStarted) {
                // Exam Intro & Unit selection
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("تعليمات الامتحان التجريبي:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• يحاكي نمط اختبار الثانوية العامة النهائي في مادة الرياضيات التخصصية.", fontSize = 13.sp)
                        Text("• يتضمن أسئلة صح/خطأ (درجة واحدة) وأسئلة اختيار من متعدد (3 درجات).", fontSize = 13.sp)
                        Text("• مدة الاختبار القياسية 90 دقيقة مع إمكانية مراجعة إجاباتك فور الإرسال.", fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                examStarted = true
                                timeLeftSeconds = 90 * 60
                                examSubmitted = false
                                userAnswers.clear()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("start_exam_button")
                        ) {
                            Text("بدء الامتحان الآن", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else if (examSubmitted) {
                // Exam Score & Result Breakdown
                var totalScore = 0
                var maxScore = 0
                examQuestions.forEach { q ->
                    maxScore += q.points
                    if (userAnswers[q.id] == q.correctAnswer) {
                        totalScore += q.points
                    }
                }
                val percentage = if (maxScore > 0) (totalScore * 100) / maxScore else 0

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("نتيجة الامتحان التجريبي", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$totalScore / $maxScore درجة ($percentage%)", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                examStarted = false
                                examSubmitted = false
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إعادة الاختبار أو اختيار وحدة أخرى")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("مراجعة الأسئلة والإجابات النموذجية:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(examQuestions) { idx, q ->
                        val ans = userAnswers[q.id]
                        val isCorrect = ans == q.correctAnswer
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("السؤال ${idx + 1} (${q.topic}): ${q.questionText}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("إجابتك: ${ans ?: "لم تُجب"}", fontSize = 13.sp, color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                Text("الإجابة الصحيحة: ${q.correctAnswer}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("الشرح: ${q.explanation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                // Ongoing Exam Questions List
                LazyColumn(modifier = Modifier.weight(1f).testTag("ongoing_exam_list")) {
                    itemsIndexed(examQuestions) { idx, q ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("سؤال ${idx + 1} (${q.points} درجات)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(q.topic, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(q.questionText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(10.dp))

                                if (q.type == "true_false") {
                                    Row {
                                        listOf("صح", "خطأ").forEach { choice ->
                                            FilterChip(
                                                selected = userAnswers[q.id] == choice,
                                                onClick = { userAnswers[q.id] = choice },
                                                label = { Text(choice) },
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Column {
                                        q.options.forEach { opt ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                RadioButton(
                                                    selected = userAnswers[q.id] == opt,
                                                    onClick = { userAnswers[q.id] = opt }
                                                )
                                                Text(opt, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { examSubmitted = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_exam_button")
                ) {
                    Text("تسليم ورقة الإجابة وإنهاء الامتحان", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
