package com.example.semky.screens

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.SemPracaViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSemPracaScreen(
    viewModel: SemPracaViewModel,
    existingPraca: SemPraca? = null,
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf(existingPraca?.name ?: "") }
    var description by remember { mutableStateOf(existingPraca?.description ?: "") }
    var deadlines by remember { mutableStateOf(existingPraca?.deadlines ?: emptyList()) }
    var attachments by remember { mutableStateOf(existingPraca?.attachments ?: emptyList()) }
    var isEditing by remember { mutableStateOf(existingPraca == null || isEditMode) }

    var selectedAttachment by remember { mutableStateOf<Long?>(null) }
    var showAttachmentDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

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

            // Deadlines section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Termíny",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    deadlines.forEach { deadline ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatDate(deadline),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(
                                onClick = {
                                    deadlines = deadlines.filter { it != deadline }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove deadline"
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { showDatePicker(context) { timestamp -> 
                            deadlines = deadlines + timestamp
                        }},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add deadline"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pridať termín")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attachments section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Prílohy",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    attachments.forEach { attachmentId ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Príloha $attachmentId",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(
                                onClick = {
                                    attachments = attachments.filter { it != attachmentId }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove attachment"
                                )
                            }
                        }
                    }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let { selectedUri ->
                            val attachmentId = viewModel.addAttachment(selectedUri)
                            attachments = attachments + attachmentId
                        }
                    }
                    Button(
                        onClick = { launcher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add attachment"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pridať prílohu")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (existingPraca == null) {
                        val novaPraca = SemPraca(
                            name = name,
                            description = description,
                            isFinished = false,
                            deadlines = deadlines,
                            attachments = attachments
                        )
                        viewModel.addPraca(novaPraca)
                    } else {
                        val updatedPraca = existingPraca.copy(
                            name = name,
                            description = description,
                            deadlines = deadlines,
                            attachments = attachments
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

                    if (deadlines.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Termíny:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        deadlines.forEach { deadline ->
                            Text(
                                text = "• ${formatDate(deadline)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Prílohy:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        attachments.forEach { attachmentId ->
                            Button(
                                onClick = {
                                    selectedAttachment = attachmentId
                                    showAttachmentDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Príloha $attachmentId")
                            }
                        }
                    }

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

    if (showAttachmentDialog && selectedAttachment != null) {
        AttachmentGallery(
            attachmentId = selectedAttachment!!,
            viewModel = viewModel,
            onDismiss = {
                showAttachmentDialog = false
                selectedAttachment = null
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

private fun showDatePicker(
    context: Context,
    onDateSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(selectedCalendar.timeInMillis)
        },
        year,
        month,
        day
    ).show()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentGallery(
    attachmentId: Long,
    viewModel: SemPracaViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val file = viewModel.getAttachmentFile(attachmentId)

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Príloha",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (file != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(file)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "Príloha sa nenašla",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}