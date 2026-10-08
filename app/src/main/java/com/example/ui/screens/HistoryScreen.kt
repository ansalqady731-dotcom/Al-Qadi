package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MathSolution
import com.example.model.SolutionEntity
import com.example.viewmodel.MathViewModel
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.history.collectAsState()
    var selectedEntity by remember { mutableStateOf<SolutionEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredList = remember(historyList, searchQuery) {
        if (searchQuery.isBlank()) historyList
        else historyList.filter {
            it.questionRaw.contains(searchQuery, ignoreCase = true) ||
                    it.topic.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سجل المسائل المحلولة", fontWeight = FontWeight.Bold) },
                actions = {
                    if (historyList.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearHistory() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "مسح الكل")
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث في المسائل أو المحاور...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد مسائل محفوظة حتى الآن", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().testTag("history_list")) {
                    items(filteredList, key = { it.id }) { item ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedEntity = item }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            item.topic.ifBlank { "رياضيات عامة" },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Row {
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(item.id, item.isFavorite) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "مفضلة",
                                                tint = if (item.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteHistoryItem(item.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "حذف", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    item.questionRaw,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedEntity?.let { entity ->
        val solution = remember(entity) {
            try {
                Json { ignoreUnknownKeys = true }.decodeFromString<MathSolution>(entity.solutionJson)
            } catch (e: Exception) {
                null
            }
        }
        AlertDialog(
            onDismissRequest = { selectedEntity = null },
            confirmButton = {
                TextButton(onClick = { selectedEntity = null }) {
                    Text("إغلاق")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "سؤال: ${entity.questionRaw}\n\nالإجابة: ${solution?.finalAnswerText ?: ""}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة المسألة"))
                    }
                ) {
                    Text("مشاركة")
                }
            },
            title = { Text(solution?.topic ?: "تفاصيل الحل") },
            text = {
                Column {
                    Text("السؤال: ${entity.questionRaw}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الإجابة النهائية: ${solution?.finalAnswerText ?: ""}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("القاعدة: ${solution?.keyRule ?: "قواعد المنهج المصري"}", fontSize = 13.sp)
                }
            }
        )
    }
}
