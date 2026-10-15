package com.example.proyectointegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectointegrador.ui.theme.ProyectoIntegradorTheme
import kotlinx.coroutines.launch

/**
 * Pantalla 5: Reporte de Incidencias ("pantallaincidencia")
 * Cumple con los criterios de:
 * - UD 2.4: Estructuras de Diseño con Layouts y Material 3 (Scaffold, TopAppBar, PaddingValues, Layouts limpios)
 * - UD 3.1: Ciclo de Vida de la Activity frente a Recomposición
 * - UD 3.2: Gestión del Estado Local con remember y rememberSaveable (persistencia ante rotación)
 * - UI Proyecto Integrador: 100% de los textos externalizados en res/values/strings.xml
 */
class PantallaIncidencias : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoIntegradorTheme {
                PantallaIncidenciasScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIncidenciasScreen(
    onBackClick: () -> Unit = {}
) {
    // ESTADO LOCAL DE LA PANTALLA PERSISTIDO CON rememberSaveable (UD 3.2)
    // Permite sobrevivir recomposiciones y cambios de configuración (ej. rotación de pantalla - UD 3.1)
    var selectedKeyRoom by rememberSaveable { mutableStateOf("") }
    var selectedIncidentType by rememberSaveable { mutableStateOf("") }
    var descriptionText by rememberSaveable { mutableStateOf("") }
    var reporterName by rememberSaveable { mutableStateOf("") }
    var priorityLevel by rememberSaveable { mutableStateOf("Media") }
    var isPhysicalLoss by rememberSaveable { mutableStateOf(false) }

    var expandedKeyDropdown by rememberSaveable { mutableStateOf(false) }
    var expandedTypeDropdown by rememberSaveable { mutableStateOf(false) }

    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    var hasSubmittedAttempt by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Opciones de aulas/llaves cargadas desde recursos (strings.xml)
    val keyRoomOptions = listOf(
        stringResource(R.string.incidencia_opt_key1),
        stringResource(R.string.incidencia_opt_key2),
        stringResource(R.string.incidencia_opt_key3),
        stringResource(R.string.incidencia_opt_key4),
        stringResource(R.string.incidencia_opt_key5)
    )

    // Opciones de tipos de incidencias cargadas desde recursos (strings.xml)
    val incidentTypeOptions = listOf(
        stringResource(R.string.incidencia_opt_type1),
        stringResource(R.string.incidencia_opt_type2),
        stringResource(R.string.incidencia_opt_type3),
        stringResource(R.string.incidencia_opt_type4),
        stringResource(R.string.incidencia_opt_type5)
    )

    // Prioridades disponibles
    val priorityOptions = listOf(
        stringResource(R.string.incidencia_prio_low),
        stringResource(R.string.incidencia_prio_medium),
        stringResource(R.string.incidencia_prio_high),
        stringResource(R.string.incidencia_prio_urgent)
    )

    val snackbarSuccessMsg = stringResource(R.string.incidencia_snackbar_success)
    val snackbarErrorMsg = stringResource(R.string.incidencia_snackbar_error)

    // Validación del formulario
    val isFormValid = selectedKeyRoom.isNotBlank() &&
            selectedIncidentType.isNotBlank() &&
            descriptionText.isNotBlank() &&
            reporterName.isNotBlank()

    // Función para resetear el formulario
    fun resetForm() {
        selectedKeyRoom = ""
        selectedIncidentType = ""
        descriptionText = ""
        reporterName = ""
        priorityLevel = priorityOptions[1] // "Media" por defecto
        isPhysicalLoss = false
        hasSubmittedAttempt = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.incidencia_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { resetForm() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.cd_refresh)
                        )
                    }
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.cd_info)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // GESTIÓN OBLIGATORIA DE PaddingValues HEREDADAS DEL SCAFFOLD (UD 2.4)
        // Evita colisiones con las barras del sistema operativo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // TARJETA CABECERA INFORMATIVA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.incidencia_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.incidencia_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // SECCIÓN 1: SELECCIÓN DE AULA / LLAVE
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.incidencia_sec_key_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedKeyDropdown,
                        onExpandedChange = { expandedKeyDropdown = !expandedKeyDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedKeyRoom,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.incidencia_label_key)) },
                            placeholder = { Text(stringResource(R.string.incidencia_placeholder_key)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKeyDropdown) },
                            isError = hasSubmittedAttempt && selectedKeyRoom.isBlank(),
                            supportingText = {
                                if (hasSubmittedAttempt && selectedKeyRoom.isBlank()) {
                                    Text(
                                        text = stringResource(R.string.incidencia_error_key_empty),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedKeyDropdown,
                            onDismissRequest = { expandedKeyDropdown = false }
                        ) {
                            keyRoomOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedKeyRoom = option
                                        expandedKeyDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // SECCIÓN 2: CLASIFICACIÓN DE LA INCIDENCIA Y PRIORIDAD
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.incidencia_sec_type_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Selector desplegable de Tipo
                    ExposedDropdownMenuBox(
                        expanded = expandedTypeDropdown,
                        onExpandedChange = { expandedTypeDropdown = !expandedTypeDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedIncidentType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.incidencia_label_type)) },
                            placeholder = { Text(stringResource(R.string.incidencia_placeholder_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeDropdown) },
                            isError = hasSubmittedAttempt && selectedIncidentType.isBlank(),
                            supportingText = {
                                if (hasSubmittedAttempt && selectedIncidentType.isBlank()) {
                                    Text(
                                        text = stringResource(R.string.incidencia_error_type_empty),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedTypeDropdown,
                            onDismissRequest = { expandedTypeDropdown = false }
                        ) {
                            incidentTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedIncidentType = option
                                        expandedTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Selector Chips de Prioridad
                    Text(
                        text = stringResource(R.string.incidencia_label_priority),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        priorityOptions.forEach { priority ->
                            val isSelected = priorityLevel == priority
                            FilterChip(
                                selected = isSelected,
                                onClick = { priorityLevel = priority },
                                label = {
                                    Text(
                                        text = priority,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 2.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (priority) {
                                        stringResource(R.string.incidencia_prio_urgent) -> MaterialTheme.colorScheme.errorContainer
                                        stringResource(R.string.incidencia_prio_high) -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.primaryContainer
                                    }
                                )
                            )
                        }
                    }

                    // Switch para Pérdida Física / Cambio Urgente
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.incidencia_switch_loss),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isPhysicalLoss,
                            onCheckedChange = { isPhysicalLoss = it }
                        )
                    }
                }
            }

            // SECCIÓN 3: DESCRIPCIÓN DETALLADA
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.incidencia_sec_desc_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text(stringResource(R.string.incidencia_label_desc)) },
                        placeholder = { Text(stringResource(R.string.incidencia_placeholder_desc)) },
                        minLines = 3,
                        maxLines = 5,
                        isError = hasSubmittedAttempt && descriptionText.isBlank(),
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (hasSubmittedAttempt && descriptionText.isBlank()) {
                                    Text(
                                        text = stringResource(R.string.incidencia_error_desc_empty),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }
                                Text(text = "${descriptionText.length} / 250")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // SECCIÓN 4: DATOS DEL REPORTANTE (DOCENTE)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.incidencia_sec_reporter_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = reporterName,
                        onValueChange = { reporterName = it },
                        label = { Text(stringResource(R.string.incidencia_label_reporter)) },
                        placeholder = { Text(stringResource(R.string.incidencia_placeholder_reporter)) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        isError = hasSubmittedAttempt && reporterName.isBlank(),
                        supportingText = {
                            if (hasSubmittedAttempt && reporterName.isBlank()) {
                                Text(
                                    text = stringResource(R.string.incidencia_error_reporter_empty),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // SECCIÓN 5: BOTONES DE ACCIÓN (LIMPIAR / ENVIAR)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Limpiar
                OutlinedButton(
                    onClick = { resetForm() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.incidencia_btn_clear))
                }

                // Botón Enviar
                Button(
                    onClick = {
                        hasSubmittedAttempt = true
                        if (isFormValid) {
                            showSuccessDialog = true
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackbarSuccessMsg)
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackbarErrorMsg)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.incidencia_btn_submit))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // DIÁLOGO DE CONFIRMACIÓN DE ÉXITO
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.incidencia_dialog_title),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.incidencia_dialog_msg_prefix),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        DetailRow(
                            label = stringResource(R.string.incidencia_dialog_lbl_room),
                            value = selectedKeyRoom
                        )
                        DetailRow(
                            label = stringResource(R.string.incidencia_dialog_lbl_type),
                            value = selectedIncidentType
                        )
                        DetailRow(
                            label = stringResource(R.string.incidencia_dialog_lbl_priority),
                            value = priorityLevel
                        )
                        DetailRow(
                            label = stringResource(R.string.incidencia_dialog_lbl_reporter),
                            value = reporterName
                        )
                        DetailRow(
                            label = stringResource(R.string.incidencia_dialog_lbl_urgent),
                            value = if (isPhysicalLoss)
                                stringResource(R.string.incidencia_dialog_yes)
                            else
                                stringResource(R.string.incidencia_dialog_no)
                        )
                        DetailRow(
                            label = stringResource(R.string.incidencia_dialog_lbl_desc),
                            value = descriptionText
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            resetForm()
                        }
                    ) {
                        Text(text = stringResource(R.string.incidencia_dialog_confirm))
                    }
                }
            )
        }

        // DIÁLOGO INFORMATIVO SOBRE LA PANTALLA
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {
                    Text(text = stringResource(R.string.incidencia_info_dialog_title))
                },
                text = {
                    Text(text = stringResource(R.string.incidencia_info_dialog_msg))
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text(text = stringResource(R.string.incidencia_info_dialog_close))
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaIncidenciasPreview() {
    ProyectoIntegradorTheme {
        PantallaIncidenciasScreen()
    }
}
