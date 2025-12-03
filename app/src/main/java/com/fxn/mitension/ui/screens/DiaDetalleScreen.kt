package com.fxn.mitension.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fxn.mitension.R
import com.fxn.mitension.data.AppDatabase
import com.fxn.mitension.data.Medicion
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.ui.viewmodel.DiaDetalleViewModel
import com.fxn.mitension.ui.viewmodel.DiaDetalleViewModelFactory
import com.fxn.mitension.util.PeriodoDelDia
import java.time.Instant
import java.time.ZoneId
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.fxn.mitension.util.clasificarTension
import com.fxn.mitension.util.obtenerColorPorEstado
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaDetalleScreen(onNavigateBack: () -> Unit) {
    // Creamos el ViewModel usando su factoría
    val context = LocalContext.current
    val repository = remember { MedicionRepository(AppDatabase.getDatabase(context).medicionDao()) }
    val factory = remember { DiaDetalleViewModelFactory(repository) }
    val viewModel: DiaDetalleViewModel = viewModel(factory = factory)

    // Recogemos el estado de la UI del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.detalle_dia_titulo, uiState.dia)) }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onNavigateBack) {
                        Text(stringResource(id = R.string.volver_al_calendario))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Iteramos sobre los períodos que tienen mediciones
            PeriodoDelDia.entries.forEach { periodo ->
                val medicionesDelPeriodo = uiState.medicionesAgrupadas[periodo]
                if (!medicionesDelPeriodo.isNullOrEmpty()) {
                    // Mostramos un encabezado para el período
                    item {
                        PeriodoHeader(periodo)
                    }
                    // Mostramos cada medición del período
                    items(medicionesDelPeriodo) { medicion ->
                        MedicionItem(medicion)
                    }
                    // Calculamos las medias a partir de la lista.
                    // Usamos average() y lo redondeamos a Int.
                    val mediaSistolica = medicionesDelPeriodo.map { it.sistolica }.average().toInt()
                    val mediaDiastolica = medicionesDelPeriodo.map { it.diastolica }.average().toInt()
                    // Clasificamos la media para obtener su estado (Normal, Alta, etc.)
                    val estadoDeLaTension = clasificarTension(mediaSistolica, mediaDiastolica)

                    //Mostramos la media del período
                    item {
                        // Obtenemos el color correspondiente a ese estado.
                        val colorDeEstado = obtenerColorPorEstado(estado = estadoDeLaTension)

                        PeriodoMediaItem(
                            mediaSistolica = mediaSistolica,
                            mediaDiastolica = mediaDiastolica,
                            colorFondo = colorDeEstado
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodoHeader(periodo: PeriodoDelDia) {
    val nombrePeriodo = when (periodo) {
        PeriodoDelDia.MAÑANA -> stringResource(id = R.string.periodo_manana)
        PeriodoDelDia.TARDE -> stringResource(id = R.string.periodo_tarde)
        PeriodoDelDia.NOCHE -> stringResource(id = R.string.periodo_noche)
    }
    Text(
        text = nombrePeriodo,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun MedicionItem(medicion: Medicion) {
    val timeFormatter = remember { java.time.format.DateTimeFormatter.ofPattern("HH:mm") }
    val hora = Instant.ofEpochMilli(medicion.timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(timeFormatter)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.tension_alta_label) + ": ${medicion.sistolica}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(id = R.string.tension_baja_label) + ": ${medicion.diastolica}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = hora,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun PeriodoMediaItem(mediaSistolica: Int, mediaDiastolica: Int, colorFondo: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // Usamos el color que nos pasan como parámetro
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Título para la media
            Text(
                text = stringResource(id = R.string.media_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                // Hacemos el texto blanco para que contraste con los fondos oscuros
                color = Color.White
            )
            // Valor de la media
            Text(
                text = "$mediaSistolica / $mediaDiastolica",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                // Texto blanco también aquí
                color = Color.White
            )
        }
    }
}