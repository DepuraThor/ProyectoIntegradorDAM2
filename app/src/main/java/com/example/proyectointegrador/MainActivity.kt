
package com.example.proyectointegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val azul = Color(0xFF2563EB)
val fondo = Color(0xFFFAFBFC)
val gris = Color(0xFF64748B)
val texto = Color(0xFF1F2937)
val campo = Color(0xFFEDF1F5)
val borde = Color(0xFFD8E1EB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaIncidencias()
        }
    }
}

@Composable
fun PantallaIncidencias() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "U-tad / Llaves",
                    color = azul,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFE8EFFD),
                            RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RR",
                        color = azul,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reportar incidencia",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = texto
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuéntanos qué ha ocurrido.",
                fontSize = 16.sp,
                color = gris
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Llave o aula
            Text(
                text = "Llave o aula",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = texto
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(campo, RoundedCornerShape(12.dp))
                    .border(1.dp, borde, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Aula 102 · L-002",
                    color = gris,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tipo de incidencia
            Text(
                text = "Tipo de incidencia",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = texto
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(
                            Color(0xFFE8EFFD),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            azul,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "● Desperfecto",
                        color = azul,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(campo, RoundedCornerShape(12.dp))
                        .border(1.dp, borde, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "○ Pérdida",
                        color = gris,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Descripción
            Text(
                text = "Descripción",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = texto
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .background(campo, RoundedCornerShape(12.dp))
                    .border(1.dp, borde, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {

                Text(
                    text = "Describe el problema de la llave...",
                    color = gris,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "0 / 500 caracteres",
                    color = gris,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Revisa los datos antes de enviar.",
                color = gris,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón visual, sin funcionalidad
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(azul, RoundedCornerShape(12.dp))
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Enviar incidencia",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Barra inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(borde)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconoMenu("⚿", "Llaves", false)
            IconoMenu("♧", "NFC", false)
            IconoMenu("↺", "Préstamos", false)
            IconoMenu("!", "Incidencias", true)
        }
    }
}

@Composable
fun RowScope.IconoMenu(
    icono: String,
    nombre: String,
    activo: Boolean
) {

    val color = if (activo) azul else gris

    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .width(78.dp)
                .height(32.dp)
                .background(
                    if (activo) Color(0xFFE8EFFD) else fondo,
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icono,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = nombre,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (activo) FontWeight.Bold
            else FontWeight.Normal
        )
    }
}
