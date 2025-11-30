package com.fxn.mitension.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fxn.mitension.ui.viewmodel.MedicionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicionScreen(viewModel: MedicionViewModel = viewModel()) {
    val uiState by viewModel.uiState
    var mostrarPopupSistolica by remember { mutableStateOf(false) }
    var mostrarPopupDiastolica by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.getTitulo()) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Sistólica
            TensionDisplay(
                label = "La Alta (sistólica)",
                valor = viewModel.formatDisplayValue(uiState.sistolica),
                onClick = { mostrarPopupSistolica = true }
            )

            // Campo Diastólica
            TensionDisplay(
                label = "La Baja (diastólica)",
                valor = viewModel.formatDisplayValue(uiState.diastolica),
                onClick = { mostrarPopupDiastolica = true }
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja los botones hacia abajo

            // Botones inferiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { /* TODO: Lógica de guardar */ }) {
                    Text("Guardar")
                }
                Button(onClick = { /* TODO: Navegar a pantalla calendario */ }) {
                    Text("Ver Calendario")
                }
            }
        }
    }

    // Pop-ups
    if (mostrarPopupSistolica) {
        TensionInputDialog(
            titulo = "Introduce la Alta (sistólica)",
            valorInicial = uiState.sistolica.ifEmpty { "1" },
            onDismiss = { mostrarPopupSistolica = false },
            onConfirm = { valor ->
                viewModel.onSistolicaChanged(valor)
                mostrarPopupSistolica = false
            }
        )
    }

    if (mostrarPopupDiastolica) {
        TensionInputDialog(
            titulo = "Introduce la Baja (diastólica)",
            valorInicial = uiState.diastolica.ifEmpty { "0" },
            onDismiss = { mostrarPopupDiastolica = false },
            onConfirm = { valor ->
                viewModel.onDiastolicaChanged(valor)
                mostrarPopupDiastolica = false
            }
        )
    }
}


@Composable
fun TensionDisplay(label: String, valor: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valor.ifEmpty { "Pulsa para añadir" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (valor.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TensionInputDialog(
    titulo: String,
    valorInicial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember {
        mutableStateOf(
            TextFieldValue(
                text = valorInicial,
                selection = TextRange(valorInicial.length)
            )
        )
    }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(titulo, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { newValue ->
                        // Permitimos solo números y hasta 3 dígitos
                        if (newValue.text.length <= 3 && newValue.text.all { it.isDigit() }) {
                            text = newValue
                        }
                    },
                    modifier = Modifier.focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(text.text)
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onConfirm(text.text)
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}