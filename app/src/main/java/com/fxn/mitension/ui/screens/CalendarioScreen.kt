package com.fxn.mitension.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fxn.mitension.ui.viewmodel.CalendarioViewModel
import java.time.DayOfWeek
import androidx.compose.ui.res.stringResource
import com.fxn.mitension.R
import com.fxn.mitension.data.AppDatabase
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.data.ResumenDiario
import com.fxn.mitension.ui.viewmodel.CalendarioViewModelFactory
import com.fxn.mitension.util.clasificarTension
import com.fxn.mitension.util.obtenerColorPorEstado
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun CalendarioScreen(
    onNavigateToMedicion: () -> Unit,
    onNavigateToDiaDetalle: (Int, Int, Int) -> Unit,
) {
    val context = LocalContext.current
    // Usamos 'remember' para que no se cree en cada recomposición.
    val repository = remember {
        MedicionRepository(AppDatabase.getDatabase(context).medicionDao())
    }

    // Creamos la nueva factoría, pasándole el repositorio.
    val factory = remember {
        CalendarioViewModelFactory(repository)
    }

    // Pasamos la factoría al composable 'viewModel' para que cree la instancia.
    val viewModel: CalendarioViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onNavigateToMedicion) {
                        Text(stringResource(id = R.string.anadir_nuevo_registro))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CalendarioHeader(
                anioMes = uiState.anioMes,
                onMesAnterior = { viewModel.mesAnterior() },
                onMesSiguiente = { viewModel.mesSiguiente() }
            )
            CalendarioGrid(
                anioMes = uiState.anioMes,
                resumenMensual = uiState.resumenMensual,
                onDiaClick = { dia ->
                    onNavigateToDiaDetalle(uiState.anioMes.year, uiState.anioMes.monthValue, dia)
                }
            )
        }
    }
}

@Composable
fun CalendarioHeader(
    anioMes: YearMonth,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onMesAnterior) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.mes_anterior)
            )
        }
        Text(
            text = anioMes.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineMedium
        )
        IconButton(onClick = onMesSiguiente) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = stringResource(id = R.string.mes_siguiente)
            )
        }
    }
}

@Composable
fun CalendarioGrid(
    anioMes: YearMonth,
    resumenMensual: Map<Int, ResumenDiario>,
    onDiaClick: (Int) -> Unit
) {
    val diasEnMes = anioMes.lengthOfMonth()
    val primerDiaDelMes = anioMes.atDay(1).dayOfWeek
    val offset = primerDiaDelMes.value - 1

    Column {
        // Cabecera con los días de la semana
        Row {
            for (i in 0..6) {
                val dia = DayOfWeek.values()[(i + 6) % 7] // L M X J V S D
                Text(
                    text = dia.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val totalCeldas = (diasEnMes + offset)
        val numSemanas = if (totalCeldas % 7 == 0) totalCeldas / 7 else (totalCeldas / 7) + 1

        // Días del calendario
        for (semana in 0..5) {
            Row {
                for (diaSemana in 1..7) {
                    val diaMes = (semana * 7 + diaSemana) - offset
                    if (diaMes in 1..diasEnMes) {
                        CeldaDiaCalendario(
                            dia = diaMes,
                            resumen = resumenMensual[diaMes],
                            onClick = { onDiaClick(diaMes) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        // Celda vacía para los días fuera del mes
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CeldaDiaCalendario(
    dia: Int,
    resumen: ResumenDiario?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Número del día
            Text(
                text = dia.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Fila para los indicadores de color
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Mañana
                val colorManana = resumen?.mediaSistolicaManana?.let { sist ->
                    resumen.mediaDiastolicaManana?.let { diast ->
                        obtenerColorPorEstado(
                            clasificarTension(
                                sist.roundToInt(),
                                diast.roundToInt()
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorManana ?: Color.Transparent)
                )

                // Tarde
                val colorTarde = resumen?.mediaSistolicaTarde?.let { sist ->
                    resumen.mediaDiastolicaTarde?.let { diast ->
                        obtenerColorPorEstado(
                            clasificarTension(
                                sist.roundToInt(),
                                diast.roundToInt()
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorTarde ?: Color.Transparent)
                )

                // Noche
                val colorNoche = resumen?.mediaSistolicaNoche?.let { sist ->
                    resumen.mediaDiastolicaNoche?.let { diast ->
                        obtenerColorPorEstado(
                            clasificarTension(
                                sist.roundToInt(),
                                diast.roundToInt()
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colorNoche ?: Color.Transparent)
                )
            }
        }
    }
}