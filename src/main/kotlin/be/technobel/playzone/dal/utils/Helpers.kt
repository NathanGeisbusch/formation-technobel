package be.technobel.playzone.dal.utils

private val escapeLikePatternRegex = Regex("[!%_\\[]")

fun escapeLikePattern(text: String): String {
	return text.replace(escapeLikePatternRegex) { "!${it.value}" }
}
