package com.example.semky.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.semky.R
import com.example.semky.viewmodel.SemPracaViewModel
import com.example.semky.viewmodel.SemPracaPointsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsScreen(
    viewModel: SemPracaViewModel,
    pointsViewModel: SemPracaPointsViewModel,
    modifier: Modifier = Modifier
) {
    val allPoints by pointsViewModel.allPoints.collectAsState()
    val semPraceList by viewModel.semPrace.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allPoints) { points ->
                val semPraca = semPraceList.find { it.id == points.semPracaId }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = semPraca?.name ?: stringResource(R.string.unknown_semPraca),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = points.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.nb_points, points.points),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}