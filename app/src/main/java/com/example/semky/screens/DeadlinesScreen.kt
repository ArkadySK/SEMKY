package com.example.semky.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.semky.R
import com.example.semky.viewmodel.DeadlineViewModel
import com.example.semky.viewmodel.SemPracaViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun DeadlinesScreen(
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
            Text(
                text = stringResource(R.string.no_deadlines),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = stringResource(R.string.todays_deadlines),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                sortedDeadlines.forEach { deadline ->
                    val deadlineDate: LocalDate = deadline.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val isToday = deadlineDate.isEqual(LocalDate.now())
                    if (isToday) {
                        val semPraca = semPraceList.find { it.id == deadline.semPracaId }
                        val pracaName = if (semPraca?.isFinished == true) {
                            "${semPraca.name} (${stringResource(R.string.finished)})"
                        } else {
                            semPraca?.name ?: stringResource(R.string.unknown_semPraca)
                        }
                        Text(
                            text = "$pracaName - ${deadline.name}: ${dateFormat.format(deadline.date)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if(semPraca?.isFinished == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.typography.bodyLarge.color,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.other_deadlines),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.typography.titleLarge.color,
                modifier = Modifier.padding(8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                sortedDeadlines.forEach { deadline ->
                    val deadlineDate: LocalDate = deadline.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val isToday = deadlineDate.isEqual(LocalDate.now())
                    if (!isToday) {
                        val semPraca = semPraceList.find { it.id == deadline.semPracaId }
                        val pracaName = if (semPraca?.isFinished == true) {
                            "${semPraca.name} (${stringResource(R.string.finished)})"
                        } else {
                            semPraca?.name ?: stringResource(R.string.unknown_semPraca)
                        }
                        Text(
                            text = "$pracaName - ${deadline.name}: ${dateFormat.format(deadline.date)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if(semPraca?.isFinished == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.typography.bodyLarge.color,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}