package com.fxn.mitension.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxn.mitension.data.FilaResumen
import com.fxn.mitension.data.Medicion
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.data.ResumenDiario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

data class CalendarioUiState(
    val fechaSeleccionada: LocalDate = LocalDate.now(),
    val resumenMensual: Map<Int, ResumenDiario> = emptyMap(),
    val resumenUltimosDias: List<FilaResumen> = emptyList()
) {
    val anioMes: YearMonth = YearMonth.from(fechaSeleccionada)
}

class CalendarioViewModel(private val repository: MedicionRepository) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _resumenMensual = _fechaSeleccionada.flatMapLatest { fecha ->
        val inicioDelMes = fecha.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val finDelMes = fecha.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        repository.obtenerResumenMensual(inicioDelMes, finDelMes).map { list ->
            list.associateBy { it.dia }
        }
    }

    private val _resumenUltimosDias = repository.obtenerMedicionesEnRango(
        inicio = LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        fin = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    ).map { mediciones ->
        procesarUltimosDias(mediciones)
    }

    val uiState: StateFlow<CalendarioUiState> = combine(
        _fechaSeleccionada,
        _resumenMensual,
        _resumenUltimosDias
    ) { fecha, mensual, ultimos ->
        CalendarioUiState(
            fechaSeleccionada = fecha,
            resumenMensual = mensual,
            resumenUltimosDias = ultimos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarioUiState()
    )

    private fun procesarUltimosDias(mediciones: List<Medicion>): List<FilaResumen> {
        val hoy = LocalDate.now()
        val ultimos8Dias = (0..7).map { hoy.minusDays(it.toLong()) }
        
        val medicionesPorDia = mediciones.groupBy { 
            Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        val todasLasFilas = mutableListOf<FilaResumen>()

        ultimos8Dias.forEach { fecha ->
            val medicionesDelDia = medicionesPorDia[fecha] ?: emptyList()
            
            val mañana = mutableListOf<Medicion>()
            val tarde = mutableListOf<Medicion>()
            val noche = mutableListOf<Medicion>()

            medicionesDelDia.forEach { medicion ->
                val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(medicion.timestamp), ZoneId.systemDefault())
                val mins = time.hour * 60 + time.minute
                when {
                    mins in 1..750 -> mañana.add(medicion)
                    mins in 751..1140 -> tarde.add(medicion)
                    else -> noche.add(medicion)
                }
            }

            // Seleccionamos la sesión de día: Mañana si existe, si no Tarde.
            val sesionDia = if (mañana.isNotEmpty()) mañana else tarde
            val sesionNoche = noche

            // Generamos hasta 2 filas por día
            for (i in 0..1) {
                val mDia = sesionDia.getOrNull(i)
                val mNoche = sesionNoche.getOrNull(i)
                
                // Solo añadimos la fila si hay algún dato o si es la primera fila del día
                if (i == 0 || mDia != null || mNoche != null) {
                    todasLasFilas.add(
                        FilaResumen(
                            fecha = fecha,
                            sistolicaManana = mDia?.sistolica,
                            diastolicaManana = mDia?.diastolica,
                            pulsoManana = mDia?.pulso,
                            sistolicaNoche = mNoche?.sistolica,
                            diastolicaNoche = mNoche?.diastolica,
                            pulsoNoche = mNoche?.pulso
                        )
                    )
                }
            }
        }
        return todasLasFilas
    }

    fun mesSiguiente() {
        _fechaSeleccionada.value = _fechaSeleccionada.value.plusMonths(1)
    }

    fun mesAnterior() {
        _fechaSeleccionada.value = _fechaSeleccionada.value.minusMonths(1)
    }
}
