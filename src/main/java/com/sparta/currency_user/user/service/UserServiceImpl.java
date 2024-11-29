package com.sparta.currency_user.user.service;

import com.sparta.currency_user.exception.CustomException;
import com.sparta.currency_user.user.dto.UserRequestDto;
import com.sparta.currency_user.user.dto.UserResponseDto;
import com.sparta.currency_user.user.entity.User;
import com.sparta.currency_user.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.currency_user.exception.type.ErrorType.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        return new UserResponseDto(findUserById(id));
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::toDto)
                .toList();
    }

    @Transactional
    public UserResponseDto save(UserRequestDto userRequestDto) {

        User savedUser = userRepository.save(UserRequestDto.toEntity(userRequestDto));
        return new UserResponseDto(savedUser);
    }


    @Transactional
    public void deleteUserById(Long id) {
        this.findUserById(id);
        userRepository.deleteById(id);
    }

}
