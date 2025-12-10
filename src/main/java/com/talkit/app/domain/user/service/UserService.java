package com.talkit.app.domain.user.service;

import com.talkit.app.domain.user.dto.UserSignupRequest;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupRequest request) {

        // 1️⃣ 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2️⃣ 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3️⃣ User 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .birthYear(request.getBirthYear())
                .gender(request.getGender())
                .build();

        // 4️⃣ 저장
        userRepository.save(user);
    }

    public boolean login(String email, String password) { //입력받은 email, password(평문)
        Optional<User> optionalUser = userRepository.findByEmail(email);


        if (optionalUser.isEmpty()) {
            //입력한 이메일 없을 시 -> 로그인실패
            return false;
        }

        User user=optionalUser.get();

        if(!passwordEncoder.matches(password, user.getPassword())){
            //비밀번호 불일치
            return false;
        }
        return true;
    }
}
