package com.fxn.mitension.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fxn.mitension.R
import com.fxn.mitension.data.AppDatabase
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.data.ResumenDiario
import com.fxn.mitension.ui.components.TablaResumenUltimosDias
import com.fxn.mitension.ui.viewmodel.CalendarioViewModel
import com.fxn.mitension.ui.viewmodel.CalendarioViewModelFactory
import com.fxn.mitension.util.EstadoTension
import com.fxn.mitension.util.clasificarTension
import com.fxn.mitension.util.obtenerColorPorEstado
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    onNavigateToMedicion: () -> Unit,
    onNavigateToDiaDetalle: (Int, Int, Int) -> Unit,
) {
    val context = LocalContext.current
    val repository = remember {
        MedicionRepository(AppDatabase.getDatabase(context).medicionDao())
    }
    val factory = remember {
        CalendarioViewModelFactory(repository)
    }
    val viewModel: CalendarioViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var menuVisible by remember { mutableStateOf(false) }
    var dialogoLeyendaVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFFFBF1),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.titulo_calendario)) },
                actions = {
                    IconButton(onClick = { menuVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.menu_descripcion)
                        )
                    }
                    DropdownMenu(
                        expanded = menuVisible,
                        onDismissRequest = { menuVisible = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menu_leyenda_colores)) },
                            onClick = {
                                menuVisible = false
                                dialogoLeyendaVisible = true
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.navigationBarsPadding().height(64.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onNavigateToMedicion,
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A71))
                    ) {
                        Text(
                            stringResource(id = R.string.anadir_nuevo_registro),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            
            Spacer(modifier = Modifier.height(16.dp))

            // Nueva Tabla Resumen
            TablaResumenUltimosDias(
                filas = uiState.resumenUltimosDias,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
    
    if (dialogoLeyendaVisible) {
        DialogoLeyenda(onDismiss = { dialogoLeyendaVisible = false })
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

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row {
            val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        var diaActual = 1
        for (semana in 0..5) {
            Row {
                for (diaSemana in 0..6) {
                    val indice = semana * 7 + diaSemana
                    if (indice >= offset && diaActual <= diasEnMes) {
                        val dia = diaActual
                        CeldaDiaCalendario(
                            dia = dia,
                            resumen = resumenMensual[dia],
                            onClick = { onDiaClick(dia) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.2f)
                        )
                        diaActual++
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (diaActual > diasEnMes) break
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
        modifier = modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dia.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                IndicadorColor(resumen?.mediaSistolicaManana, resumen?.mediaDiastolicaManana)
                IndicadorColor(resumen?.mediaSistolicaTarde, resumen?.mediaDiastolicaTarde)
                IndicadorColor(resumen?.mediaSistolicaNoche, resumen?.mediaDiastolicaNoche)
            }
        }
    }
}

@Composable
fun RowScope.IndicadorColor(sis: Double?, diast: Double?) {
    val color = if (sis != null && diast != null) {
        obtenerColorPorEstado(clasificarTension(sis.roundToInt(), diast.roundToInt()))
    } else {
        Color.Transparent
    }
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(color)
    )
}

@Composable
fun DialogoLeyenda(onDismiss: () -> Unit) {
    val colorBaja = obtenerColorPorEstado(EstadoTension.BAJA)
    val colorNormal = obtenerColorPorEstado(EstadoTension.NORMAL)
    val colorElevada = obtenerColorPorEstado(EstadoTension.ELEVADA)
    val colorAlta1 = obtenerColorPorEstado(EstadoTension.ALTA_1)
    val colorAlta2 = obtenerColorPorEstado(EstadoTension.ALTA_2)
    val colorCrisis = obtenerColorPorEstado(EstadoTension.CRISIS_HIPERTENSIVA)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(id = R.string.leyenda_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))
                LeyendaItem(colorBaja, stringResource(id = R.string.leyenda_baja))
                LeyendaItem(colorNormal, stringResource(id = R.string.leyenda_normal))
                LeyendaItem(colorElevada, stringResource(id = R.string.leyenda_elevada))
                LeyendaItem(colorAlta1, stringResource(id = R.string.leyenda_alta_1))
                LeyendaItem(colorAlta2, stringResource(id = R.string.leyenda_alta_2))
                LeyendaItem(colorCrisis, stringResource(id = R.string.leyenda_crisis))
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.5f).height(40.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(id = R.string.cerrar))
                }
            }
        }
    }
}

@Composable
fun LeyendaItem(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(20.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = texto, style = MaterialTheme.typography.bodyLarge)
    }
}
