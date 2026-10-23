package com.example.proyectointegrador

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScreen() {

    // Paleta de colores necesaria.
    val primaryBlue = Color(0xFF2B5BB9)
    val backgroundLight = Color(0xFFF8FAFC)
    val cardBackground = Color(0xFFEBF2FF)
    val textGray = Color(0xFF64748B)

    // Usamos Scaffold para definir el andamiaje principal de la pantalla.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundLight,
        bottomBar = {
            NfcBottomNavigationBar(activeColor = primaryBlue, inactiveColor = textGray)
        }
    ) { innerPadding ->

        // Estructuramos todos los elementos verticalmente.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Fila superior para poner el titulo de la ruta y el avatar.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "U-tad / Llaves",
                    color = primaryBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(cardBackground, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RR",
                        color = primaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Inicializamos los encabezados principales de la pantalla.
            Text(
                text = "Lectura NFC",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Identifica la llave con un toque.",
                color = textGray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Diseñamos la tarjeta contenedora central de aproximacion.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Contenedor para dibujar el anillo de lectura central.
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        // Usamos canvas para simular las lineas discontinuas de forma nativa.
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = primaryBlue.copy(alpha = 0.3f),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Llave",
                            modifier = Modifier.size(44.dp),
                            tint = primaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Texto informativo inferiores de la tarjeta.
                    Text(
                        text = "Acerca el móvil al tag del aula",
                        color = Color.Black,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun NfcBottomNavigationBar(activeColor: Color, inactiveColor: Color) {

    // Estructura la barra inferior reservando sus respectivos slots.
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {

        // Menu con los iconos basicos del core por defecto para evitar fallos.
        val menuItems = listOf(
            MenuData("Llaves", Icons.Default.Home, false),
            MenuData("NFC", Icons.Default.Star, true),
            MenuData("Préstamos", Icons.Default.Edit, false),
            MenuData("Incidencias", Icons.Default.Warning, false)
        )

        // Iteramos de forma eficiente sobre cada elemento para pintar los botones.
        menuItems.forEach { item ->
            NavigationBarItem(
                selected = item.isSelected,
                onClick = { },
                label = { Text(text = item.title) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = activeColor,
                    selectedTextColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    unselectedTextColor = inactiveColor,
                    indicatorColor = activeColor.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// Atributos necesarios para modelar las opciones de la barra inferior.
data class MenuData(val title: String, val icon: ImageVector, val isSelected: Boolean)

// Permite habilitar la vista previa interactiva en el panel lateral del IDE.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NfcScreenPreview() {
    NfcScreen()
}


