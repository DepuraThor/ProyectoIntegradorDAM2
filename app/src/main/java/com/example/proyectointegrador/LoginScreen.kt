package com.example.proyectointegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectointegrador.ui.theme.ProyectoIntegradorTheme

val FondoPantalla = Color(0xFFF4F6F8)
val AzulSuave = Color(0xFFE8EFFD)
val CampoTextoFondo = Color(0xFFEDF1F5)
val AzulPrincipal = Color(0xFF2563EB)
val TextoPrincipal = Color(0xFF1F2937)
val TextoSecundario = Color(0xFF64748B)
val BordeCampo = Color(0xFFDCE2E9)
val TextoBoton = Color(0xFFFAFBFC)

@Composable
fun LoginHeaderScreen(modifier: Modifier = Modifier) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoPantalla)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.login_header_tag),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextoSecundario
            )

            Text(
                text = "•••",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AzulSuave)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_utad),
                    contentDescription = stringResource(R.string.logo_content_description),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(95.dp),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = null,
                        tint = AzulPrincipal,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.module_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Accede con tu cuenta del centro.",
                fontSize = 14.sp,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Correo institucional",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("nombre@u-tad.com", color = TextoSecundario) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CampoTextoFondo,
                    focusedContainerColor = CampoTextoFondo,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AzulPrincipal
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Contraseña",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••", color = TextoSecundario) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CampoTextoFondo,
                    focusedContainerColor = CampoTextoFondo,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AzulPrincipal
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Acceso de demostración",
                fontSize = 13.sp,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Acción de Acceso */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPrincipal
                )
            ) {
                Text(
                    text = "Acceder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoBoton
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tus aulas, siempre a mano.",
                fontSize = 13.sp,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "U-tad · Portal para docentes",
                fontSize = 12.sp,
                color = TextoSecundario
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginHeaderScreenPreview() {
    ProyectoIntegradorTheme {
        LoginHeaderScreen()
    }
}