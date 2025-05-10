package com.example.semky.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.semky.data.model.SemPraca
import com.example.semky.viewmodel.SemPracaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSemPracaScreen(
    viewModel: SemPracaViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nazov by remember { mutableStateOf("") }
    var informacie by remember { mutableStateOf("") }
    var terminyText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nová semestrálna práca",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nazov,
            onValueChange = { nazov = it },
            label = { Text("Názov") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = informacie,
            onValueChange = { informacie = it },
            label = { Text("Informácie") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = terminyText,
            onValueChange = { terminyText = it },
            label = { Text("Termíny (oddelené čiarkou)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val novaPraca = SemPraca(
                    nazov = nazov,
                    informacie = informacie,
                    terminy = emptyList(), // TODO pridaj neskor
                    prilohy = emptyList() // TODO implementovane neskôr
                )
                viewModel.addPraca(novaPraca)
                onNavigateBack()
            },
            enabled = nazov.isNotEmpty() && informacie.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Uložiť")
        }
    }
} 