package be.technobel.playzone.pl.models.dto

data class PagesDTO<T> (
	val pages: Int,
	val page: Int,
	val size: Int,
	val data: List<T>,
)