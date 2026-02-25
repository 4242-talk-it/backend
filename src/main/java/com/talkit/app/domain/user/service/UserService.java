package com.talkit.app.domain.user.service;

import com.talkit.app.domain.user.dto.UserRequestDto;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.global.exception.BusinessLogicException;
import com.talkit.app.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(UserRequestDto.Login request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

        if (user.getDeleteAt() != null) {
            throw new BusinessLogicException(ExceptionType.ALREADY_WITHDRAWN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessLogicException(ExceptionType.NOT_FOUND_USER);
        }
        return user;
    }

    @Transactional
    public void signup(UserRequestDto.Signup request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
            .email(request.getEmail())
            .password(encodedPassword)
            .nickname(request.getNickname())
            .birthYear(request.getBirthYear())
            .gender(request.getGender())
            .temperature(36.5)
            .build();

        userRepository.save(user);
    }

    @Transactional
    public String updateNickname(Long userId, UserRequestDto.UpdateNickname request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

        user.changeNickname(request.getNickname());
        return user.getNickname();
    }

    @Transactional(readOnly = true)
    public void verifyPassword(Long userId, String password) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessLogicException(ExceptionType.INVALID_PASSWORD);
        }
    }

    @Transactional
    public void updatePassword(Long userId, UserRequestDto.UpdatePassword request) {
        verifyPassword(userId, request.getCurrentPassword());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedNewPassword);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        if (user.getDeleteAt() != null) {
            throw new IllegalStateException("이미 탈퇴 처리 중인 계정입니다.");
        }
        user.withdraw();
    }

    @Transactional
    public void restoreUser(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

        if(user.getDeleteAt() !=null) {
            user.restore();
        }
    }
}
