package com.example.PeakPerform.services;

import java.util.List;

import com.example.PeakPerform.dto.AuthRequestDto;
import com.example.PeakPerform.dto.AuthResponseDto;
import com.example.PeakPerform.dto.RegisterDto;
import com.example.PeakPerform.dto.UserResponseDto;

public interface AuthService {

    UserResponseDto register(RegisterDto dto);

    AuthResponseDto login(AuthRequestDto dto);

    AuthResponseDto refreshToken(String refreshToken);

    List<UserResponseDto> getAllUsers();

    List<UserResponseDto> getUsersByRole(String role);

}