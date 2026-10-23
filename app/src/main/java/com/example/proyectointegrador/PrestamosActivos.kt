package com.example.proyectointegrador



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



@Composable
fun PrestamosActivosScreen() {

    Scaffold(
        containerColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.Superficie,
        bottomBar = {
            NavigationBar(containerColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.Superficie) {
                MenuPrestamos(Icons.Default.VpnKey, "Llaves", false)
                MenuPrestamos(Icons.Default.Nfc, "NFC", false)
                MenuPrestamos(Icons.Default.Replay, "Préstamos", true)
                MenuPrestamos(Icons.Default.PriorityHigh, "Incidencias", false)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {

            Spacer(Modifier.height(12.dp))

            // Cabecera
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "U-tad / Llaves",
                    style = MaterialTheme.typography.labelLarge,
                    color = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal
                )

                Box(
                    Modifier.size(40.dp)
                        .background(_root_ide_package_.com.example.proyectointegrador.ui.theme.AzulSuave, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "RR",
                        color = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Título
            Text(
                "Préstamos activos",
                style = MaterialTheme.typography.headlineMedium,
                color = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoPrincipal
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Consulta y devuelve las llaves.",
                style = MaterialTheme.typography.bodyLarge,
                color = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoSecundario
            )

            Spacer(Modifier.height(26.dp))

            // Aviso
            Box(
                Modifier.fillMaxWidth()
                    .height(52.dp)
                    .background(
                        _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulSuave,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "2 llaves pendientes de devolución",
                    color = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Primera tarjeta
            TarjetaPrestamo(
                "Aula 102",
                "Ana García",
                "09:15"
            )

            Spacer(Modifier.height(24.dp))

            // Segunda tarjeta
            TarjetaPrestamo(
                "Laboratorio 2",
                "Rubén Ruiz",
                "10:30"
            )
        }
    }
}

@Composable
fun TarjetaPrestamo(
    aula: String,
    docente: String,
    hora: String
) {

    Column(
        Modifier.fillMaxWidth()
            .background(
                _root_ide_package_.com.example.proyectointegrador.ui.theme.CamposYTarjetas,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                Color(0xFFD8E1EB),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.VpnKey,
                contentDescription = null,
                tint = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal,
                modifier = Modifier.size(44.dp)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                aula,
                style = MaterialTheme.typography.titleMedium,
                color = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoPrincipal
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Docente: $docente",
            style = MaterialTheme.typography.bodyLarge,
            color = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoPrincipal
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Préstamo: Hoy · $hora",
            style = MaterialTheme.typography.bodyMedium,
            color = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoSecundario
        )

        Spacer(Modifier.height(16.dp))

        // Botón decorativo
        Box(
            Modifier.fillMaxWidth()
                .height(44.dp)
                .background(
                    _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "Devolver llave",
                color = _root_ide_package_.com.example.proyectointegrador.ui.theme.Blanco,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun RowScope.MenuPrestamos(
    icono: ImageVector,
    nombre: String,
    seleccionado: Boolean
) {

    NavigationBarItem(
        selected = seleccionado,
        onClick = {},
        icon = {
            Icon(icono, contentDescription = null)
        },
        label = {
            Text(
                nombre,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (seleccionado)
                    FontWeight.Bold
                else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal,
            selectedTextColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulPrincipal,
            indicatorColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.AzulSuave,
            unselectedIconColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoSecundario,
            unselectedTextColor = _root_ide_package_.com.example.proyectointegrador.ui.theme.TextoSecundario
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PrestamosActivosPreview() {
    _root_ide_package_.com.example.proyectointegrador.ui.theme.ProyectoIntegradorTheme {
        PrestamosActivosScreen()
    }
}
