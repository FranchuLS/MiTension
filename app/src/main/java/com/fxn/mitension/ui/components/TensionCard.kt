package com.fxn.mitension.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TensionCard(
    label: String,
    valor: String,
    colorAcento: Color,
    iconRes: Int? = null,
    unidad: String = "mmHg",
    paddingVertical: Dp = 24.dp,
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
        Column(
            modifier = Modifier.padding(vertical = paddingVertical, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(if (paddingVertical < 18.dp) 32.dp else 48.dp),
                        tint = colorAcento
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = if (iconRes != null) 40.dp else 0.dp)
            ) {
                Text(
                    text = if (valor.isEmpty()) "---" else valor,
                    style = if (paddingVertical < 18.dp) 
                        MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)
                    else 
                        MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = colorAcento
                )

                Text(
                    "  $unidad",
                    modifier = Modifier.padding(top = if (paddingVertical < 18.dp) 10.dp else 20.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.LightGray
                )
            }
        }
    }
}
