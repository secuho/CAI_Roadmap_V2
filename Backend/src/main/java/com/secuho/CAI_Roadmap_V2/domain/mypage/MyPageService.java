package com.secuho.CAI_Roadmap_V2.domain.mypage;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.Student;
import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.StudentRepository;
import com.secuho.CAI_Roadmap_V2.domain.auth.repository.UserRepository;
import com.secuho.CAI_Roadmap_V2.domain.mypage.dto.MyPageResponse;
import com.secuho.CAI_Roadmap_V2.global.exception.BusinessException;
import com.secuho.CAI_Roadmap_V2.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public MyPageResponse getMyPage(User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return new MyPageResponse(student.getEmail(), student.getPhone(), user.getTrack());
    }

    @Transactional
    public void updateTrack(User user, String track) {
        user.changeTrack("선택 안 함".equals(track) ? null : track);
        userRepository.save(user);
    }
}
