package com.fxn.mitension.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fxn.mitension.ui.viewmodel.MedicionViewModel
import com.fxn.mitension.R
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.ui.viewmodel.MedicionViewModelFactory
import com.fxn.mitension.data.AppDatabase
import com.fxn.mitension.util.PeriodoDelDia
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicionScreen(onNavigateToCalendario: () -> Unit) {
    // Obtener el nuevo string de error
    val errorViewModel = stringResource(id = R.string.error_clase_view_model_desconocida)

    val context = LocalContext.current
    // Creamos instancias de la DB, DAO, Repo y la Factoría.
    // Usamos 'remember' para que no se creen en cada recomposición.
    val medicionDao = remember { AppDatabase.getDatabase(context).medicionDao() }
    val repository = remember { MedicionRepository(medicionDao) }
    val factory = remember { MedicionViewModelFactory(repository, errorViewModel) }

    // Pasamos la factoría al composable 'viewModel'
    val viewModel: MedicionViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState
    var mostrarPopupSistolica by remember { mutableStateOf(false) }
    var mostrarPopupDiastolica by remember { mutableStateOf(false) }

    // Añadimos el nuevo mensaje de éxito
    val mensajeErrorCampos = stringResource(id = R.string.error_campos_obligatorios)
    val mensajeErrorPeriodoLleno = stringResource(id = R.string.error_periodo_lleno)
    val mensajeExito = stringResource(id = R.string.guardado_con_exito)

    val periodoNombre = when (uiState.periodo) {
        PeriodoDelDia.MAÑANA -> stringResource(id = R.string.periodo_manana)
        PeriodoDelDia.TARDE -> stringResource(id = R.string.periodo_tarde)
        PeriodoDelDia.NOCHE -> stringResource(id = R.string.periodo_noche)
    }

    val tituloCompleto = if (uiState.numeroMedicion > 3) {
        stringResource(id = R.string.titulo_mediciones_completas, periodoNombre)
    } else {
        stringResource(
            id = R.string.titulo_medicion,
            periodoNombre,
            uiState.numeroMedicion
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.evento.collectLatest { evento ->
            when (evento) {
                is MedicionViewModel.UiEvento.MostrarMensaje -> {
                    Toast.makeText(context, evento.mensaje, Toast.LENGTH_LONG).show()
                }

                is MedicionViewModel.UiEvento.GuardadoConExito -> {
                    Toast.makeText(context, evento.mensaje, Toast.LENGTH_SHORT).show()
                    viewModel.onGuardadoExitoso()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloCompleto) },
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
                label = stringResource(id = R.string.tension_alta_label),
                valor = uiState.sistolica,
                onClick = { mostrarPopupSistolica = true }
            )

            // Campo Diastólica
            TensionDisplay(
                label = stringResource(id = R.string.tension_baja_label),
                valor = uiState.diastolica,
                onClick = { mostrarPopupDiastolica = true }
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja los botones hacia abajo

            // Botones inferiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.guardarMedicion(
                            mensajeErrorCampos,
                            mensajeErrorPeriodoLleno,
                            mensajeExito
                        )
                    },
                    modifier = Modifier
                        .weight(3f)
                        .height(48.dp)
                ) {
                    Text(
                        stringResource(id = R.string.guardar),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Button(
                    onClick = { onNavigateToCalendario() },
                    modifier = Modifier
                        .weight(3f)
                        .height(48.dp)
                ) {
                    Text(
                        stringResource(id = R.string.ver_calendario),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Pop-ups
    if (mostrarPopupSistolica) {
        TensionInputDialog(
            titulo = stringResource(id = R.string.dialog_alta_titulo),
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
            titulo = stringResource(id = R.string.dialog_baja_titulo),
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
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.large)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (valor.isEmpty()) stringResource(id = R.string.pulsa_para_anadir) else valor,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (valor.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        stringResource(id = R.string.confirmar),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}