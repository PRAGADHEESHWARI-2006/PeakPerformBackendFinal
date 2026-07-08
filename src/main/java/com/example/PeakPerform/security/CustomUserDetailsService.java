package com.example.PeakPerform.security;

import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.repository.AppUserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



import org.springframework.context.annotation.Primary;


@Primary
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }



@Override
public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {
            

    AppUserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException("No user found with email: " + email));

    return user;
}
}

