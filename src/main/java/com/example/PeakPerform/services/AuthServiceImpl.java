package com.example.PeakPerform.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.PeakPerform.dto.AuthRequestDto;
import com.example.PeakPerform.dto.AuthResponseDto;
import com.example.PeakPerform.dto.RegisterDto;
import com.example.PeakPerform.dto.UserResponseDto;
import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.enums.Role;
import com.example.PeakPerform.exception.BusinessValidationException;
import com.example.PeakPerform.exception.ResourceNotFoundException;
import com.example.PeakPerform.repository.AppUserRepository;
import com.example.PeakPerform.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {

        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @Override
public UserResponseDto register(RegisterDto dto) {

    if (appUserRepository.existsByEmail(dto.getEmail())) {
        throw new BusinessValidationException(
                "Email already registered: " + dto.getEmail());
    }

    Role role;

    try {
        role = Role.valueOf(dto.getRole());
    } catch (IllegalArgumentException ex) {

        throw new BusinessValidationException(
                "Invalid role: " + dto.getRole()
                        + ". Valid values: ROLE_PERFORMANCE_ADMIN, ROLE_TEAM_LEAD, ROLE_GOAL_OWNER");
    }

    AppUserEntity user = new AppUserEntity();

    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRole(role);
    user.setDepartment(dto.getDepartment());

    AppUserEntity savedUser = appUserRepository.save(user);

    return mapToUserResponse(savedUser);
}


@Override
public AuthResponseDto login(AuthRequestDto dto) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()));

    AppUserEntity user = appUserRepository.findByEmail(dto.getEmail())
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    if (!user.getIsActive()) {
        throw new BusinessValidationException(
                "Your account has been deactivated. Contact administrator.");
    }

    String accessToken = jwtUtil.generateAccessToken(user);
    String refreshToken = jwtUtil.generateRefreshToken(user);

    AuthResponseDto response = new AuthResponseDto();

    response.setAccessToken(accessToken);
    response.setRefreshToken(refreshToken);
    response.setUserId(user.getId());
    response.setFullName(user.getFullName());
    response.setEmail(user.getEmail());
    response.setRole(user.getRole().name());

    return response;
}


@Override
public AuthResponseDto refreshToken(String refreshToken) {

    String email = jwtUtil.extractUsername(refreshToken);

    AppUserEntity user = appUserRepository.findByEmail(email)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    if (!jwtUtil.isTokenValid(refreshToken, user)) {
        throw new BusinessValidationException(
                "Refresh token is invalid or expired. Please log in again.");
    }

    AuthResponseDto response = new AuthResponseDto();

    response.setAccessToken(jwtUtil.generateAccessToken(user));
    response.setRefreshToken(refreshToken);
    response.setUserId(user.getId());
    response.setFullName(user.getFullName());
    response.setEmail(user.getEmail());
    response.setRole(user.getRole().name());
    user.setDepartment(user.getDepartment());

    return response;
}


@Override
public List<UserResponseDto> getAllUsers() {

    return appUserRepository.findAllActiveUsers()
            .stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
}

@Override
public List<UserResponseDto> getUsersByRole(String role) {

    Role userRole;

    try {
        userRole = Role.valueOf(role);
    } catch (IllegalArgumentException ex) {
        throw new BusinessValidationException(
                "Invalid role: " + role);
    }

    return appUserRepository.findByRole(userRole)
            .stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
}

private UserResponseDto mapToUserResponse(AppUserEntity user) {

    UserResponseDto dto = new UserResponseDto();

    dto.setId(user.getId());
    dto.setFullName(user.getFullName());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole().name());
    dto.setDepartment(user.getDepartment());
    dto.setIsActive(user.getIsActive());

    return dto;
}


}