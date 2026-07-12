package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.repository.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl loadUserByUsername called for login {}", login);
        return adminRepository.findByLogin(login).orElseThrow(() -> {
            log.info("UserDetailsServiceImpl loadUserByUsername failed for login {}", login);
            return new UsernameNotFoundException(login);
        });
    }
}
