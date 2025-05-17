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
import com.example.semky.data.model.Deadline
import com.example.semky.data.model.SemPraca
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
    showFinished: Boolean = false,
    modifier: Modifier = Modifier
) {
    val deadlines by deadlineViewModel.deadlines.collectAsState()
    val semPraceList by semPracaViewModel.semPrace.collectAsState()
    var sortedDeadlines = deadlines.sortedBy { it.date }
    val filteredSemPraceList = if (showFinished) semPraceList else semPraceList.filter { !it.isFinished }
    val filteredDeadlines = if (!showFinished) {
        sortedDeadlines.filter { deadline ->
            filteredSemPraceList.any { it.id == deadline.semPracaId }
        }
    } else {
        sortedDeadlines
    }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val today = LocalDate.now()

    val (todayDeadlines, pastDeadlines, futureDeadlines) = remember(filteredDeadlines) {
        val todayList = mutableListOf<Deadline>()
        val pastList = mutableListOf<Deadline>()
        val futureList = mutableListOf<Deadline>()
        for (deadline in filteredDeadlines) {
            val deadlineDate =
                deadline.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            when {
                deadlineDate.isEqual(today) -> todayList.add(deadline)
                deadlineDate.isBefore(today) -> pastList.add(deadline)
                deadlineDate.isAfter(today) -> futureList.add(deadline)
            }
        }
        Triple(todayList, pastList, futureList)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        if (filteredDeadlines.isEmpty()) {
            Text(
                text = stringResource(R.string.no_deadlines),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            DeadlineListCard(
                title = stringResource(R.string.late_deadlines),
                deadlines = pastDeadlines,
                semPraceList = filteredSemPraceList,
                dateFormat = dateFormat,
                titleColor = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            DeadlineListCard(
                title = stringResource(R.string.todays_deadlines),
                deadlines = todayDeadlines,
                semPraceList = filteredSemPraceList,
                dateFormat = dateFormat,
                titleColor = MaterialTheme.typography.titleLarge.color
            )
            Spacer(modifier = Modifier.height(8.dp))
            DeadlineListCard(
                title = stringResource(R.string.future_deadlines),
                deadlines = futureDeadlines,
                semPraceList = filteredSemPraceList,
                dateFormat = dateFormat,
                titleColor = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DeadlineListCard(
    title: String,
    deadlines: List<Deadline>,
    semPraceList: List<SemPraca>,
    dateFormat: SimpleDateFormat,
    titleColor: androidx.compose.ui.graphics.Color
) {
    if (deadlines.isNotEmpty()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = titleColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column {
                deadlines.forEach { deadline ->
                    val semPraca = semPraceList.find { it.id == deadline.semPracaId }
                    val pracaName = if (semPraca?.isFinished == true) {
                        "${semPraca.name} (${stringResource(R.string.finished)})"
                    } else {
                        semPraca?.name ?: stringResource(R.string.unknown_semPraca)
                    }
                    Text(
                        text = "$pracaName - ${deadline.name}: ${dateFormat.format(deadline.date)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (semPraca?.isFinished == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.typography.bodyLarge.color,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}