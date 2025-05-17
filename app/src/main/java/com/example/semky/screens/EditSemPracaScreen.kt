package com.example.semky.screens

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.semky.data.model.Deadline
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.DeadlineViewModel
import com.example.semky.viewmodel.SemPracaPointsViewModel
import com.example.semky.viewmodel.SemPracaViewModel
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun EditSemPracaScreen(
    viewModel: SemPracaViewModel,
    pointsViewModel: SemPracaPointsViewModel,
    deadlineViewModel: DeadlineViewModel,
    existingPraca: SemPraca? = null,
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf(existingPraca?.name ?: "") }
    var description by rememberSaveable { mutableStateOf(existingPraca?.description ?: "") }
    val deadlinesDb by deadlineViewModel.getAllByPracaId(existingPraca?.id).collectAsState() // originalne data z db, menia sa po ulozeni :)
    var attachments by rememberSaveable {mutableStateOf(existingPraca?.attachments ?: emptyList()) }
    var isEditing by rememberSaveable { mutableStateOf(existingPraca == null || isEditMode) }
    var newDeadlineName by rememberSaveable { mutableStateOf("") }
    var newDeadlineDate by rememberSaveable { mutableStateOf<Date?>(null) }

    var deadlines by rememberSaveable { mutableStateOf(deadlinesDb?: emptyList<Deadline>()) }

    var selectedAttachment by rememberSaveable { mutableStateOf<Long?>(null) }
    var showAttachmentDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = when {
                    existingPraca == null -> "Nová semestrálna práca"
                    isEditing -> "Úprava semestrálnej práce"
                    else -> existingPraca.name
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Edit / Add mod 
        if (isEditing) {
            Card(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Názov") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Informácie") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    minLines = 3
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                            Column {
                                Text(
                                    text = deadline.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = formatDate(deadline.date),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
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
                    OutlinedTextField(
                        value = newDeadlineName,
                        onValueChange = { newDeadlineName = it },
                        label = { Text("Názov termínu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showDatePicker(context) { timestamp ->
                                newDeadlineDate = timestamp
                                if (newDeadlineName.isNotEmpty() && newDeadlineDate != null) {
                                    val newDeadline = Deadline(
                                        id = 0,
                                        semPracaId = existingPraca?.id ?: -1L, // temp id, will be replaced on save
                                        date = newDeadlineDate!!,
                                        name = newDeadlineName
                                    )
                                    deadlines = deadlines + newDeadline
                                    newDeadlineName = ""
                                    newDeadlineDate = null
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = newDeadlineName.isNotEmpty()
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
                            attachments = attachments
                        )
                        // uloz nove terminy s novym id
                        scope.launch {
                            val newId = viewModel.addPraca(novaPraca)
                            deadlines.forEach { deadline ->
                                val toSave = deadline.copy(semPracaId = newId)
                                deadlineViewModel.addDeadline(toSave)
                            }
                            onNavigateBack()
                        }
                    } else {
                        val updatedPraca = existingPraca.copy(
                            name = name,
                            description = description,
                            attachments = attachments
                        )
                        viewModel.updatePraca(updatedPraca)
                        // Odstran a uloz nove terminy
                        deadlinesDb.forEach { deadlineViewModel.deleteDeadline(it) }
                        deadlines.forEach { deadline ->
                            val toSave = deadline.copy(semPracaId = existingPraca.id)
                            deadlineViewModel.addDeadline(toSave)
                        }
                        onNavigateBack()
                    }
                },
                enabled = name.isNotEmpty() && description.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Uložiť")
            }
        } else {
            // View mod
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
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

                    if (deadlinesDb.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Termíny:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        deadlinesDb.forEach { deadline ->
                            Text(
                                text = "• ${deadline.name}: ${formatDate(deadline.date)}",
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
                                    val updatedPraca = it.copy(isFinished = !it.isFinished)
                                    viewModel.updatePraca(updatedPraca)
                                    if (!it.isFinished) {
                                        pointsViewModel.calculateSubmissionPoints(updatedPraca)
                                    }
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
            if (!isEditing && existingPraca != null && !existingPraca.isFinished) {
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

private fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(date)
}

private fun showDatePicker(
    context: Context,
    onDateSelected: (Date) -> Unit
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
            onDateSelected(selectedCalendar.time)
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
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val context = LocalContext.current
    val file = viewModel.getAttachmentFile(attachmentId)

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Späť")
            }

            if (file != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(file)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
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