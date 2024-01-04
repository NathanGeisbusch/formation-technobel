package be.technobel.playzone.bll.services

import org.springframework.mail.MailException

interface EmailService {
	@Throws(MailException::class)
	fun sendPasswordModificationConfirmationEmail(toEmail: String)

	@Throws(MailException::class)
	fun sendPasswordModificationEmail(toEmail: String, url: String)
}