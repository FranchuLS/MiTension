package com.fxn.mitension.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.fxn.mitension.util.clasificarTension
import com.fxn.mitension.util.obtenerColorPorEstado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaDetalleScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { MedicionRepository(AppDatabase.getDatabase(context).medicionDao()) }
    val factory = remember { DiaDetalleViewModelFactory(repository) }
    val viewModel: DiaDetalleViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.detalle_dia_titulo, uiState.dia)) }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.navigationBarsPadding().height(64.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A71))
                    ) {
                        Text(
                            stringResource(id = R.string.volver_al_calendario),
                            fontWeight = FontWeight.Bold
                        )
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
            PeriodoDelDia.entries.forEach { periodo ->
                val medicionesDelPeriodo = uiState.medicionesAgrupadas[periodo]
                if (!medicionesDelPeriodo.isNullOrEmpty()) {
                    item {
                        PeriodoHeader(periodo)
                    }
                    items(medicionesDelPeriodo) { medicion ->
                        MedicionItem(medicion)
                    }
                    val mediaSistolica = medicionesDelPeriodo.map { it.sistolica }.average().toInt()
                    val mediaDiastolica =
                        medicionesDelPeriodo.map { it.diastolica }.average().toInt()
                    val estadoDeLaTension = clasificarTension(mediaSistolica, mediaDiastolica)

                    item {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.tension_alta_label_corta) + ": ${medicion.sistolica}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.tension_baja_label_corta) + ": ${medicion.diastolica}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (medicion.pulso != null) {
                Text(
                    text = stringResource(id = R.string.pulso_label_corta) + ": ${medicion.pulso}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = hora,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PeriodoMediaItem(mediaSistolica: Int, mediaDiastolica: Int, colorFondo: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = stringResource(id = R.string.media_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "$mediaSistolica / $mediaDiastolica",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
