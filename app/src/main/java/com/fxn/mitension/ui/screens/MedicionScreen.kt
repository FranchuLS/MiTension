package com.fxn.mitension.ui.screens

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fxn.mitension.ui.viewmodel.MedicionViewModel
import com.fxn.mitension.R
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.ui.viewmodel.MedicionViewModelFactory
import com.fxn.mitension.data.AppDatabase
import com.fxn.mitension.ui.components.TensionCard
import com.fxn.mitension.ui.components.TensionInputDialog
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
    val tituloPeriodo = stringResource(id = R.string.titulo_periodo, periodoNombre)
    val titulomedicion = if (uiState.numeroMedicion > 3) {
        stringResource(id = R.string.titulo_mediciones_completas, periodoNombre)
    } else {
        stringResource(id = R.string.titulo_medicion, uiState.numeroMedicion)
    }

    // Determinamos qué icono usar según el periodo
    val iconoPeriodo = when (uiState.periodo) {
        PeriodoDelDia.MAÑANA -> R.drawable.ic_sunrise
        PeriodoDelDia.TARDE -> R.drawable.ic_sun
        PeriodoDelDia.NOCHE -> R.drawable.ic_moon
        else -> R.drawable.ic_sun // Por si acaso
    }

    // Color del icono (opcional: puedes variarlo según el periodo)
    val colorIcono = when (uiState.periodo) {
        PeriodoDelDia.MAÑANA -> Color(0xFFFF7043) // Naranja atardecer/amanecer
        PeriodoDelDia.TARDE -> Color(0xFFFFD966)  // Amarillo sol
        PeriodoDelDia.NOCHE -> Color(0xFF8EACCD)  // Azul luna
        else -> Color(0xFFFFD966)
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
        containerColor = Color(0xFFFFFBF1), // Fondo Crema
        //Botones parte inferior.
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A71))
                    ) {
                        Text(stringResource(id = R.string.guardar), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToCalendario,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A71))
                    ) {
                        Text(
                            stringResource(id = R.string.ver_calendario), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFFBF1)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 6.dp,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp), // Padding inferior de la cabecera
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 16.dp,
                                bottom = 24.dp,
                                start = 20.dp,
                                end = 16.dp
                            ), // Espaciado elegante
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconoPeriodo),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = colorIcono
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = tituloPeriodo,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = Color(0xFF4A4A4A)
                            )

                            Text(
                                text = titulomedicion,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    letterSpacing = 0.6.sp
                                ),
                                color = Color(0xFFC2185B)
                            )
                        }
                    }
                    // Tarjeta de consejo
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDDE6ED).copy(
                                alpha = 0.4f
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF8EACCD)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.recuerdaesperar),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF505050)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tarjetas de tensión (Sistólica y Diastólica)
                TensionCard(
                    label = stringResource(id = R.string.tension_alta_label),
                    valor = uiState.sistolica,
                    colorAcento = Color(0xFFFF8A71),
                    iconRes = R.drawable.ic_blood_drop,
                    onClick = { mostrarPopupSistolica = true }
                )

                Spacer(modifier = Modifier.height(30.dp))

                TensionCard(
                    label = stringResource(id = R.string.tension_baja_label),
                    valor = uiState.diastolica,
                    colorAcento = Color(0xFF8EACCD),
                    iconRes = R.drawable.mi_tension_alerta_24,
                    onClick = { mostrarPopupDiastolica = true }
                )

                Spacer(modifier = Modifier.weight(1f))
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
