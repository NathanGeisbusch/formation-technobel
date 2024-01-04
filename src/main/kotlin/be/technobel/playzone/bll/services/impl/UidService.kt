package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.pl.rest.FormError
import be.technobel.playzone.pl.rest.InvalidParamException
import be.technobel.playzone.pl.validation.utils.REGEX_EMAIL
import io.azam.ulidj.MonotonicULID
import io.azam.ulidj.ULID
import org.springframework.stereotype.Service
import java.util.*


@Service
class UidService {
	private val uid = MonotonicULID()
	private val b64Enc = Base64.getUrlEncoder().withoutPadding()
	private val b64Dec = Base64.getUrlDecoder()

	fun generate(): ByteArray = uid.generateBinary()

	fun generateBase64(): String = b64Enc.encodeToString(uid.generateBinary())

	fun isValid(uid: ByteArray): Boolean = ULID.isValidBinary(uid)

	fun toBase64(uid: ByteArray): String = b64Enc.encodeToString(uid)

	@Throws(IllegalArgumentException::class)
	fun fromBase64(uid: String): ByteArray = b64Dec.decode(uid)

	@Throws(InvalidParamException::class)
	fun fromBase64Validated(uid: String, errFieldName: String = "id"): ByteArray {
		try {
			val uidBin = fromBase64(uid)
			if(!isValid(uidBin)) throw InvalidParamException(
				FormError(errFieldName, "$errFieldName is invalid")
			)
			return uidBin
		}
		catch(ex: IllegalArgumentException) {
			throw InvalidParamException(errFieldName, "$errFieldName is invalid")
		}
	}

	@Throws(InvalidParamException::class)
	fun decodeEmailBase64Validated(emailBase64: String, errFieldName: String = "emailBase64"): String {
		try {
			val email = String(fromBase64(emailBase64))
			return if(REGEX_EMAIL.matches(email)) email
			else throw InvalidParamException("emailBase64", "email")
		}
		catch(ex: IllegalArgumentException) {
			throw InvalidParamException(errFieldName, "$errFieldName is invalid")
		}
	}
}