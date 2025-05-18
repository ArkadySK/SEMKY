package com.example.semky.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.semky.R
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.DeadlineViewModel
import com.example.semky.viewmodel.SemPracaViewModel
import com.example.semky.viewmodel.SemPracaPointsViewModel

@Composable
fun SemPraceScreen(
    viewModel: SemPracaViewModel,
    pointsViewModel: SemPracaPointsViewModel,
    deadlineViewModel: DeadlineViewModel,
    modifier: Modifier = Modifier
) {
    val semPraceList by viewModel.semPrace.collectAsState()
    val semPraceFiltered = semPraceList.filter { x -> !x.isFinished }
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(semPraceFiltered) { praca ->
                PracaCard(
                    praca = praca,
                    onDelete = {
                        pointsViewModel.deletePoints(praca)
                        deadlineViewModel.deleteByPracaId(praca.id)
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

@Composable
fun PracaCard(
    praca: SemPraca,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        )
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
                            text = stringResource(R.string.finished),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.accessibility_delete)
                    )
                }
            }

            if (praca.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${stringResource(R.string.attachments)}: " + praca.attachments.count(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}