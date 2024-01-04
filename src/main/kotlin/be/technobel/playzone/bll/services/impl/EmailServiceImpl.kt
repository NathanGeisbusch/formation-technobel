package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.services.EmailService
import be.technobel.playzone.dal.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.MailPreparationException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
data class MailProperties(
	@Value("\${spring.mail.username}")
	val username: String
)

@Service
class EmailServiceImpl(
	private val emailSender: JavaMailSender,
	private val userRepository: UserRepository,
	private val mailProperties: MailProperties,
) : EmailService {
	@Throws(MailException::class)
	override fun sendPasswordModificationEmail(toEmail: String, url: String) {
		val user = userRepository.findByEmail(toEmail) ?: throw MailPreparationException("")
		val msg = emailSender.createMimeMessage()
		MimeMessageHelper(msg, "utf-8").apply {
			setTo(toEmail)
			setFrom(mailProperties.username)
			setSubject("PlayZone - Changement de mot de passe")
			setText("""
				<p>${user.fullName()},</p>
	
				<p>Nous vous informons que votre demande de modification de mot de passe a bien été prise en compte.</p>
	
				<p>Pour modifier votre mot de passe, veuillez cliquer sur le lien ci-dessous :</p>
	
				<a href="$url">$url</a>
	
				<p>Si vous n'avez pas effectué cette demande de modification ou si vous avez des questions,
				veuillez nous contacter à l'adresse ${mailProperties.username} pour obtenir de l'aide.</p>
	
				<p>Cordialement,</p>
	
				<p>L'équipe support</p>
			""".trimIndent(), true)
		}
		emailSender.send(msg)
	}

	@Throws(MailException::class)
	override fun sendPasswordModificationConfirmationEmail(toEmail: String) {
		val user = userRepository.findByEmail(toEmail) ?: throw MailPreparationException("")
		val msg = emailSender.createMimeMessage()
		MimeMessageHelper(msg, "utf-8").apply {
			setTo(toEmail)
			setFrom(mailProperties.username)
			setSubject("PlayZone - Confirmation de changement de mot de passe")
			setText("""
				<p>${user.fullName()},</p>

				<p>Nous vous confirmons que votre mot de passe a été modifié avec succès.</p>
	
				<p>Si vous n'avez pas effectué cette modification ou si vous avez des questions,
				veuillez nous contacter à l'adresse ${mailProperties.username} pour obtenir de l'aide.</p>
	
				<p>Cordialement,</p>
	
				<p>L'équipe support</p>
			""".trimIndent(), true)
		}
		emailSender.send(msg)
	}
}