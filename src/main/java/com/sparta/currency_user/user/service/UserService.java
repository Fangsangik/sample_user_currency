package com.sparta.currency_user.user.service;

import com.sparta.currency_user.user.dto.UserRequestDto;
import com.sparta.currency_user.user.dto.UserResponseDto;
import com.sparta.currency_user.user.entity.User;

import java.util.List;

public interface UserService {
    User findUserById(Long id);
    List<UserResponseDto> findAll();
    void deleteUserById(Long id);
    UserResponseDto save(UserRequestDto userRequestDto);
    UserResponseDto findById(Long id);
}
