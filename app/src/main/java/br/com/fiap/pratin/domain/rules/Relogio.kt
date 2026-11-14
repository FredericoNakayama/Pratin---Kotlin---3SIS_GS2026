package br.com.fiap.pratin.domain.rules

import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/** "Agora" a partir do relógio injetado (ADR-08), sem frações de segundo. */
fun Clock.agora(): LocalDateTime = LocalDateTime.now(this).truncatedTo(ChronoUnit.SECONDS)

/** Data de hoje a partir do relógio injetado (ADR-08). */
fun Clock.hoje(): LocalDate = LocalDate.now(this)
