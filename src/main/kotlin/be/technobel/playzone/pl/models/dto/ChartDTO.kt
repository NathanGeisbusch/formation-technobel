package be.technobel.playzone.pl.models.dto

import java.math.BigDecimal

data class ChartDTO(
	val labels: List<String>,
	val datasets: List<ChartDataset>,
)

data class ChartDataset(
	val label: String?,
	val data: List<BigDecimal>,
)
