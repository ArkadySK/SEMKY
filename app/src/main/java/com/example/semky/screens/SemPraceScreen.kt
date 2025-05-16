package com.example.semky.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.semky.data.model.Deadline
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.DeadlineViewModel
import com.example.semky.viewmodel.SemPracaViewModel
import com.example.semky.viewmodel.SemPracaPointsViewModel
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SemPraceScreen(
    viewModel: SemPracaViewModel,
    pointsViewModel: SemPracaPointsViewModel,
    deadlineViewModel: DeadlineViewModel,
    modifier: Modifier = Modifier
) {
    val semPraceList by viewModel.semPrace.collectAsState()
    var selectedPracaId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val selectedPraca = selectedPracaId?.let { id ->
        semPraceList.find { it.id == id }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(semPraceList) { praca ->
                PracaCard(
                    praca = praca,
                    onDelete = {
                        pointsViewModel.deletePoints(praca)
                        viewModel.deletePraca(praca)
                    },
                    onClick = {
                        selectedPracaId = praca.id
                        showDialog = true
                    },
                    modifier = Modifier
                )
            }
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { 
                showDialog = false
                selectedPracaId = null
            }
        ) {
            EditSemPracaScreen(
                viewModel = viewModel,
                existingPraca = selectedPraca,
                pointsViewModel = pointsViewModel,
                deadlineViewModel = deadlineViewModel,
                onNavigateBack = { 
                    showDialog = false
                    selectedPracaId = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracaCard(
    praca: SemPraca,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = praca.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (praca.isFinished) {
                        Text(
                            text = "Dokončené",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }

            // TODO: toto sa bude zobrazovat iba vnutri dialogu
//            if (deadlines.count() > 0) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Termíny:",
//                    style = MaterialTheme.typography.titleSmall,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//                deadlines.forEach { deadline ->
//                    Text(
//                        text = "• ${deadline.name}: ${formatDate(deadline.date)}",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }

            if (praca.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Prílohy: " + praca.attachments.count(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(date)
} 