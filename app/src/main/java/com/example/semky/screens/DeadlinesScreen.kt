package com.example.semky.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.semky.viewmodel.DeadlineViewModel
import com.example.semky.viewmodel.SemPracaViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun KalendarScreen(
    deadlineViewModel: DeadlineViewModel,
    semPracaViewModel: SemPracaViewModel,
    modifier: Modifier = Modifier
) {
    val deadlines by deadlineViewModel.deadlines.collectAsState()
    val semPraceList by semPracaViewModel.semPrace.collectAsState()
    val sortedDeadlines = deadlines.sortedBy { it.date }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        if (sortedDeadlines.isEmpty()) {
            Text(text = "Žiadne termíny", style = MaterialTheme.typography.bodyLarge)
        } else {
            sortedDeadlines.forEachIndexed { idx, deadline ->
                val semPraca = semPraceList.find { it.id == deadline.semPracaId }
                val pracaName = semPraca?.name ?: "Neznáma práca"
                val deadlineDate: LocalDate = deadline.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val isToday = deadlineDate.isEqual(LocalDate.now())
                Text(
                    text = "${idx + 1}. $pracaName: ${deadline.name} | ${dateFormat.format(deadline.date)}",
                    style = if(isToday) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
} 