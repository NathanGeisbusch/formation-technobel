package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmailOrPseudonym(username)
      .orElseThrow(Exceptions.USERNAME_NOT_FOUND::create);
  }
}
