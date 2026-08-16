package com.fxn.mitension.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TensionCard(
    label: String,
    valor: String,
    colorAcento: Color,
    iconRes: Int? = null,
    unidad: String = "mmHg",
    paddingVertical: Dp = 20.dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // Box que ocupa todo el ancho para centrar el bloque de contenido fijo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingVertical, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Row con ancho fijo para garantizar que iconos y textos se alineen verticalmente entre cards
            Row(
                modifier = Modifier.width(220.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = colorAcento
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = if (valor.isEmpty()) "---" else valor,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = colorAcento
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = unidad,
                            modifier = Modifier.padding(bottom = 8.dp), // Ajuste para alinear con la base del número
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
