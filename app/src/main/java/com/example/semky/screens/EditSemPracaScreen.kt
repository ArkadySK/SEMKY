 package com.example.semky.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.SemPracaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSemPracaScreen(
    viewModel: SemPracaViewModel,
    existingPraca: SemPraca? = null,
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(existingPraca?.name ?: "") }
    var description by remember { mutableStateOf(existingPraca?.description ?: "") }
    var isEditing by remember { mutableStateOf(existingPraca == null || isEditMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                existingPraca == null -> "Nová semestrálna práca"
                isEditing -> "Úprava semestrálnej práce"
                else -> existingPraca.name
            },
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Edit / Add mod 
        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Názov") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Informácie") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (existingPraca == null) {
                        val novaPraca = SemPraca(
                            name = name,
                            description = description,
                            isFinished = false,
                            deadlines = emptyList(), // TODO pridaj neskor
                            attachments = emptyList() // TODO implementovane neskôr
                        )
                        viewModel.addPraca(novaPraca)
                    } else {
                        val updatedPraca = existingPraca.copy(
                            name = name,
                            description = description
                        )
                        viewModel.updatePraca(updatedPraca)
                    }
                    onNavigateBack()
                },
                enabled = name.isNotEmpty() && description.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Uložiť")
            }
        } else {
            // View mod
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Informácie:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (existingPraca?.isFinished == true) "Stav: Dokončené" else "Stav: Nedokončené",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = {
                                existingPraca?.let {
                                    viewModel.updatePraca(it.copy(isFinished = !it.isFinished))
                                    // TODO: bodyViewmodel.calculatePoints(it.Id)
                                    onNavigateBack()
                                }
                            },
                            enabled = existingPraca?.isFinished == false
                        ) {
                            Text(
                                text = if (existingPraca?.isFinished == true)
                                    "Dokončené!"
                                else
                                    "Dokonči"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isEditing && existingPraca != null) {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Upraviť")
                }
            }
            
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isEditing) "Zrušiť" else "Zavrieť")
            }
        }
    }
}