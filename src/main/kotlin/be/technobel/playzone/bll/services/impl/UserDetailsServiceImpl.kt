package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.dal.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
	override fun loadUserByUsername(username: String): UserDetails =
		userRepository.findByEmail(username)
		?: throw UsernameNotFoundException("User not found")
}