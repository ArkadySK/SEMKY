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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.SemPracaViewModel

@Composable
fun SemPraceScreen(
    viewModel: SemPracaViewModel,
    modifier: Modifier = Modifier
) {
    val semPraceList by viewModel.semPrace.collectAsState()
    var selectedPraca by remember { mutableStateOf<SemPraca?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

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
                    onDelete = { viewModel.deletePraca(praca) },
                    onClick = { 
                        selectedPraca = praca
                        isEditMode = false
                        showDialog = true
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        Button(
            onClick = { 
                selectedPraca = null
                isEditMode = true
                showDialog = true
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Pridať novú prácu")
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            EditSemPracaScreen(
                viewModel = viewModel,
                existingPraca = selectedPraca,
                isEditMode = isEditMode,
                onNavigateBack = { showDialog = false }
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
    viewModel: SemPracaViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
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
                            color = MaterialTheme.colorScheme.primary
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
            
            if (praca.deadlines.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Termíny:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                praca.deadlines.forEach { deadline ->
                    Text(
                        text = "• ${formatDate(deadline)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

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

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
} 