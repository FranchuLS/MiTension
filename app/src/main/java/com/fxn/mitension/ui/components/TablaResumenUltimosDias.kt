package com.fxn.mitension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fxn.mitension.R
import com.fxn.mitension.data.FilaResumen
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TablaResumenUltimosDias(
    filas: List<FilaResumen>,
    modifier: Modifier = Modifier
) {
    val scrollStateHorizontal = rememberScrollState()
    val scrollStateVertical = rememberScrollState()
    val formatter = DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(max = 280.dp), // Limitamos la altura para que no empuje todo, pero permitimos scroll
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // Contenedor con scroll horizontal
        Box(
            modifier = Modifier
                .padding(8.dp)
                .horizontalScroll(scrollStateHorizontal)
        ) {
            Column {
                // CABECERA FIJA (Solo horizontal scroll)
                Column(modifier = Modifier.background(Color.White)) {
                    // Grupos: Mañana/Tarde | Noche
                    Row(
                        modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CeldaHeader("", 60.dp) // Espacio para la fecha
                        CeldaHeaderGroup(
                            stringResource(id = R.string.periodo_manana) + "/" + stringResource(id = R.string.periodo_tarde),
                            150.dp,
                            Color(0xFFFF8A71).copy(alpha = 0.1f)
                        )
                        CeldaHeaderGroup(
                            stringResource(id = R.string.periodo_noche),
                            150.dp,
                            Color(0xFF8EACCD).copy(alpha = 0.1f)
                        )
                    }

                    // Columnas: Fecha | Sist | Diast | Pul | Sist | Diast | Pul
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CeldaHeader("Fecha", 60.dp)
                        CeldaHeader(stringResource(id = R.string.tension_alta_label_corta), 50.dp)
                        CeldaHeader(stringResource(id = R.string.tension_baja_label_corta), 50.dp)
                        CeldaHeader(stringResource(id = R.string.pulso_label_corta), 50.dp)
                        CeldaHeader(stringResource(id = R.string.tension_alta_label_corta), 50.dp)
                        CeldaHeader(stringResource(id = R.string.tension_baja_label_corta), 50.dp)
                        CeldaHeader(stringResource(id = R.string.pulso_label_corta), 50.dp)
                    }
                    Box(modifier = Modifier.width(360.dp).height(1.dp).background(Color.Gray.copy(alpha = 0.5f)))
                }

                // CUERPO SCROLLABLE (Vertical scroll)
                Column(
                    modifier = Modifier.verticalScroll(scrollStateVertical)
                ) {
                    filas.forEach { fila ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CeldaDato(fila.fecha.format(formatter), 60.dp, FontWeight.Bold)
                            
                            // Datos Mañana/Tarde
                            CeldaDato(fila.sistolicaManana?.toString() ?: "-", 50.dp, colorTexto = Color(0xFFFF8A71))
                            CeldaDato(fila.diastolicaManana?.toString() ?: "-", 50.dp, colorTexto = Color(0xFFFF8A71))
                            CeldaDato(fila.pulsoManana?.toString() ?: "-", 50.dp, colorTexto = Color(0xFF4CAF50))

                            // Datos Noche
                            CeldaDato(fila.sistolicaNoche?.toString() ?: "-", 50.dp, colorTexto = Color(0xFF8EACCD))
                            CeldaDato(fila.diastolicaNoche?.toString() ?: "-", 50.dp, colorTexto = Color(0xFF8EACCD))
                            CeldaDato(fila.pulsoNoche?.toString() ?: "-", 50.dp, colorTexto = Color(0xFF4CAF50))
                        }
                        Box(modifier = Modifier.width(360.dp).height(1.dp).background(Color.LightGray.copy(alpha = 0.3f)))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CeldaHeader(texto: String, ancho: androidx.compose.ui.unit.Dp) {
    Text(
        text = texto,
        modifier = Modifier.width(ancho).padding(vertical = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@Composable
fun CeldaHeaderGroup(texto: String, ancho: androidx.compose.ui.unit.Dp, colorFondo: Color) {
    Box(
        modifier = Modifier
            .width(ancho)
            .padding(2.dp)
            .background(colorFondo, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun CeldaDato(
    texto: String,
    ancho: androidx.compose.ui.unit.Dp,
    fontWeight: FontWeight = FontWeight.Normal,
    colorTexto: Color = Color.Black
) {
    Text(
        text = texto,
        modifier = Modifier.width(ancho).padding(vertical = 8.dp),
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        color = colorTexto
    )
}
