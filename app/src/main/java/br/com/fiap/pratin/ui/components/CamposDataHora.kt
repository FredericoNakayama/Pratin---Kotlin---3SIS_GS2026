package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.format.Formatadores
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

/** Campo de data só leitura que abre o DatePicker, limitado a [primeiroDia]..[ultimoDia]. */
@Composable
fun DateField(
    valor: LocalDate?,
    onValorChange: (LocalDate) -> Unit,
    rotulo: String,
    primeiroDia: LocalDate,
    ultimoDia: LocalDate,
    modifier: Modifier = Modifier,
    erro: String? = null,
) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    CampoClicavel(
        texto = valor?.let(Formatadores::data).orEmpty(),
        rotulo = rotulo,
        erro = erro,
        icone = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
        onClick = { aberto = true },
        modifier = modifier,
    )
    if (aberto) {
        SeletorData(
            inicial = valor ?: primeiroDia,
            primeiroDia = primeiroDia,
            ultimoDia = ultimoDia,
            onConfirmar = {
                aberto = false
                onValorChange(it)
            },
            onDispensar = { aberto = false },
        )
    }
}

/** Campo de data e hora: abre o DatePicker e, em seguida, o TimePicker (24 h). */
@Composable
fun DateTimeField(
    valor: LocalDateTime?,
    onValorChange: (LocalDateTime) -> Unit,
    rotulo: String,
    primeiroDia: LocalDate,
    ultimoDia: LocalDate,
    horaSugerida: LocalTime,
    modifier: Modifier = Modifier,
    erro: String? = null,
) {
    var etapa by rememberSaveable { mutableIntStateOf(0) } // 0 = fechado, 1 = data, 2 = hora
    var dataEscolhida by rememberSaveable { mutableStateOf<String?>(null) }

    CampoClicavel(
        texto = valor?.let { "${Formatadores.data(it.toLocalDate())} ${Formatadores.hora(it)}" }.orEmpty(),
        rotulo = rotulo,
        erro = erro,
        icone = { Icon(Icons.Filled.Schedule, contentDescription = null) },
        onClick = { etapa = 1 },
        modifier = modifier,
    )
    when (etapa) {
        1 -> SeletorData(
            inicial = valor?.toLocalDate()?.coerceIn(primeiroDia, ultimoDia) ?: primeiroDia,
            primeiroDia = primeiroDia,
            ultimoDia = ultimoDia,
            onConfirmar = {
                dataEscolhida = it.toString()
                etapa = 2
            },
            onDispensar = { etapa = 0 },
        )
        2 -> SeletorHora(
            inicial = valor?.toLocalTime() ?: horaSugerida,
            onConfirmar = { hora ->
                etapa = 0
                dataEscolhida?.let { onValorChange(LocalDate.parse(it).atTime(hora)) }
            },
            onDispensar = { etapa = 0 },
        )
    }
}

/** TextField só leitura; o toque em qualquer ponto abre o seletor. */
@Composable
private fun CampoClicavel(
    texto: String,
    rotulo: String,
    erro: String?,
    icone: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        PratinTextField(
            valor = texto,
            onValorChange = {},
            rotulo = rotulo,
            erro = erro,
            trailingIcon = icone,
        )
        // A camada clicável cobre o campo; por isso ela mesma descreve rótulo, valor e erro
        // para leitores de tela.
        val descricao = listOfNotNull(rotulo, texto.ifEmpty { null }, erro?.ifEmpty { null }).joinToString(": ")
        Box(
            Modifier
                .matchParentSize()
                .clickable(onClickLabel = rotulo, role = Role.Button, onClick = onClick)
                .semantics { contentDescription = descricao },
        )
    }
}

/**
 * O DatePicker trabalha em milissegundos UTC: as conversões usam [ZoneOffset.UTC] para não
 * deslocar o dia em fusos negativos como o de Brasília.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorData(
    inicial: LocalDate,
    primeiroDia: LocalDate,
    ultimoDia: LocalDate,
    onConfirmar: (LocalDate) -> Unit,
    onDispensar: () -> Unit,
) {
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = inicial.paraMillisUtc(),
        yearRange = primeiroDia.year..ultimoDia.year,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis.paraDataUtc() in primeiroDia..ultimoDia

            override fun isSelectableYear(year: Int): Boolean = year in primeiroDia.year..ultimoDia.year
        },
    )
    DatePickerDialog(
        onDismissRequest = onDispensar,
        confirmButton = {
            TextButton(
                onClick = { estado.selectedDateMillis?.let { onConfirmar(it.paraDataUtc()) } },
                enabled = estado.selectedDateMillis != null,
            ) { Text(stringResource(R.string.acao_ok)) }
        },
        dismissButton = { TextButton(onClick = onDispensar) { Text(stringResource(R.string.acao_cancelar)) } },
    ) {
        // Data selecionada em formato curto (dd/MM/aaaa): o padrão "14 de nov. de 2026" não cabe no cabeçalho.
        val formatador = remember { DatePickerDefaults.dateFormatter(selectedDateSkeleton = "yMd") }
        DatePicker(state = estado, dateFormatter = formatador)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorHora(inicial: LocalTime, onConfirmar: (LocalTime) -> Unit, onDispensar: () -> Unit) {
    val estado = rememberTimePickerState(initialHour = inicial.hour, initialMinute = inicial.minute, is24Hour = true)
    AlertDialog(
        onDismissRequest = onDispensar,
        title = { Text(stringResource(R.string.selecione_horario)) },
        text = { TimePicker(state = estado) },
        confirmButton = {
            TextButton(onClick = { onConfirmar(LocalTime.of(estado.hour, estado.minute)) }) {
                Text(stringResource(R.string.acao_ok))
            }
        },
        dismissButton = { TextButton(onClick = onDispensar) { Text(stringResource(R.string.acao_cancelar)) } },
    )
}

private fun LocalDate.paraMillisUtc(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.paraDataUtc(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
