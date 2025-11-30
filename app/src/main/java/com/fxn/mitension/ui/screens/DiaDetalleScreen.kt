package com.fxn.mitension.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fxn.mitension.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaDetalleScreen(
    diaDelMes: Int,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.detalle_dia_titulo, diaDelMes)) }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(id = R.string.info_dia_placeholder))
        }
    }
}
    