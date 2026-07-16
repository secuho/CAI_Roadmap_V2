package com.secuho.CAI_Roadmap_V2.domain.recommend;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.CourseRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.EnrollmentRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.TermSummaryRepository;
import com.secuho.CAI_Roadmap_V2.domain.recommend.dto.LiberalArtsRecommendation;
import com.secuho.CAI_Roadmap_V2.domain.recommend.dto.MajorRecommendation;
import com.secuho.CAI_Roadmap_V2.domain.recommend.dto.RecommendResponse;
import com.secuho.CAI_Roadmap_V2.domain.recommend.entity.CourseCandidate;
import com.secuho.CAI_Roadmap_V2.domain.recommend.entity.LiberalArtsCourse;
import com.secuho.CAI_Roadmap_V2.domain.recommend.repository.CourseCandidateRepository;
import com.secuho.CAI_Roadmap_V2.domain.recommend.repository.LiberalArtsCourseRepository;
import com.secuho.CAI_Roadmap_V2.global.exception.BusinessException;
import com.secuho.CAI_Roadmap_V2.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * V1 recommend_utils.py의 get_recommendations(전공) + get_cat(교양)을 포팅.
 * 전공/교양 후보 마스터(CourseCandidate/LiberalArtsCourse)는 아직 빈 스키마라 실 데이터가 채워지기 전까지는
 * 빈 목록을 반환하는 게 정상 동작이다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {

    private static final double WEIGHT_TO = 0.5;
    private static final double WEIGHT_URGENCY = 5.0;
    private static final double WEIGHT_UNLOCK = 10.0;
    private static final double WEIGHT_TYPE = 1.0;
    private static final double WEIGHT_TRACK = 3.0;
    private static final double SCORE_TYPE_CONSTANT = 100.0;

    private static final List<String> LIBERAL_ARTS_MUTUALLY_EXCLUSIVE_CODES = List.of("PRI4029", "PRI4030", "PRI4028");

    private final CourseCandidateRepository courseCandidateRepository;
    private final LiberalArtsCourseRepository liberalArtsCourseRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TermSummaryRepository termSummaryRepository;

    public RecommendResponse getRecommendations(User user, String nextTerm) {
        NextTerm target = parseNextTerm(nextTerm);

        Set<String> excludedCodes = enrollmentRepository.findByUser(user).stream()
                .map(e -> e.getCourse().getSbjNo())
                .collect(java.util.stream.Collectors.toSet());

        int term = termSummaryRepository.findByUserOrderByYearAscSemCdAsc(user).size();
        String userTrack = user.getTrack();

        List<MajorRecommendation> majors = recommendMajors(target, excludedCodes, term, userTrack);
        List<LiberalArtsRecommendation> liberalArts = recommendLiberalArts(target, excludedCodes);

        return new RecommendResponse(majors, liberalArts);
    }

    private List<MajorRecommendation> recommendMajors(NextTerm target, Set<String> excludedCodes, int term, String userTrack) {
        List<CourseCandidate> candidates = courseCandidateRepository.findAll().stream()
                .filter(c -> c.getCapacity() == null || c.getCapacity() > -1)
                .filter(c -> !excludedCodes.contains(c.getCode()))
                .filter(c -> isPrereqMet(c.getPrereq(), excludedCodes))
                .toList();

        List<MajorRecommendation> scored = new ArrayList<>();
        for (CourseCandidate candidate : candidates) {
            int toCount = openSeatCount(candidate.getCode(), target);
            boolean isOpened = courseRepository.existsBySbjNoAndOpenYearAndSemCd(candidate.getCode(), target.year(), target.semCd());
            if (!isOpened || toCount <= 0) {
                continue;
            }

            boolean hasTrack = !candidate.getTrackNames().isEmpty();
            scored.add(score(candidate, term, userTrack, toCount, hasTrack));
        }

        return scored.stream()
                .sorted(Comparator.comparingDouble(MajorRecommendation::score).reversed())
                .toList();
    }

    private MajorRecommendation score(CourseCandidate candidate, int term, String userTrack, int toCount, boolean applyTrackWeight) {
        double sTo = toCount;
        double sUnl = candidate.getEnrollmentScore() != null ? candidate.getEnrollmentScore() : 0.0;
        double sTyp = SCORE_TYPE_CONSTANT;

        int recommendGrade = recommendedGrade(candidate.getCode());
        int userNextGrade = 1 + term / 2;
        double sUrg = (userNextGrade - recommendGrade) * 50.0;

        double sTrk;
        boolean hasUserTrack = userTrack != null && !userTrack.isBlank();
        if (applyTrackWeight) {
            sTrk = hasUserTrack && candidate.getTrackNames().contains(userTrack) ? 100.0 : 0.0;
        } else {
            sTrk = hasUserTrack ? 100.0 : 0.0;
        }

        double total = sTo * WEIGHT_TO + sUrg * WEIGHT_URGENCY + sUnl * WEIGHT_UNLOCK
                + sTyp * WEIGHT_TYPE + sTrk * WEIGHT_TRACK;

        return new MajorRecommendation(
                candidate.getTitle(), candidate.getCode(), recommendGrade,
                candidate.getTrackNames().stream().sorted().toList(),
                candidate.getCredit(), candidate.getDescription(), total
        );
    }

    private int recommendedGrade(String code) {
        if (code == null) {
            return 2;
        }
        if (code.startsWith("PRI")) {
            return 2;
        }
        if (code.startsWith("CSC") && code.length() > 3 && Character.isDigit(code.charAt(3))) {
            return Character.getNumericValue(code.charAt(3));
        }
        return 2;
    }

    private boolean isPrereqMet(String prereq, Set<String> excludedCodes) {
        if (prereq == null || prereq.isBlank()) {
            return true;
        }
        return java.util.Arrays.stream(prereq.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .allMatch(excludedCodes::contains);
    }

    /** 대상 학기에 개설된 모든 분반의 정원 합. V1의 하드코딩 50 대신 실제 정원(Course.capacity) 사용. */
    private int openSeatCount(String code, NextTerm target) {
        return courseRepository.findBySbjNoAndOpenYearAndSemCd(code, target.year(), target.semCd()).stream()
                .mapToInt(c -> c.getCapacity() != null ? c.getCapacity() : 0)
                .sum();
    }

    private List<LiberalArtsRecommendation> recommendLiberalArts(NextTerm target, Set<String> excludedCodes) {
        List<LiberalArtsCourse> all = liberalArtsCourseRepository.findAll();

        Set<String> excludedSameGroups = all.stream()
                .filter(c -> excludedCodes.contains(c.getCode()) && c.getSameGroup() != null)
                .map(LiberalArtsCourse::getSameGroup)
                .collect(java.util.stream.Collectors.toSet());

        Map<String, Double> accumulatedCreditByMidArea = new HashMap<>();
        for (LiberalArtsCourse course : all) {
            if (excludedCodes.contains(course.getCode()) && course.getMidArea() != null) {
                accumulatedCreditByMidArea.merge(course.getMidArea(), course.getCredit() != null ? course.getCredit() : 0.0, Double::sum);
            }
        }

        List<LiberalArtsRecommendation> result = new ArrayList<>();
        for (LiberalArtsCourse course : all) {
            if (course.getMidArea() != null) {
                Double accumulated = accumulatedCreditByMidArea.get(course.getMidArea());
                if (accumulated != null && course.getTotalCredit() != null && accumulated >= course.getTotalCredit()) {
                    continue;
                }
            }
            if (excludedCodes.contains(course.getCode())) {
                continue;
            }
            if (course.getSameGroup() != null && excludedSameGroups.contains(course.getSameGroup())) {
                continue;
            }
            if (isMutuallyExcluded(course.getCode(), excludedCodes)) {
                continue;
            }
            if (!courseRepository.existsBySbjNoAndOpenYearAndSemCd(course.getCode(), target.year(), target.semCd())) {
                continue;
            }

            result.add(new LiberalArtsRecommendation(
                    course.getCode(), course.getName(), course.getCredit(), course.getTotalCredit(),
                    course.getMidArea(), course.getCategory(), course.getDescription()
            ));
        }

        return result;
    }

    /** V1에 하드코딩된 상호배타 과목 쌍(신구 교육과정 중복 과목). */
    private boolean isMutuallyExcluded(String code, Set<String> excludedCodes) {
        return switch (code) {
            case "PRI4029" -> excludedCodes.contains("PRI4002") || excludedCodes.contains("PRI4013");
            case "PRI4030" -> excludedCodes.contains("PRI4003") || excludedCodes.contains("PRI4014");
            case "PRI4028" -> excludedCodes.contains("PRI4004") || excludedCodes.contains("PRI4015");
            default -> false;
        };
    }

    private NextTerm parseNextTerm(String nextTerm) {
        String[] parts = nextTerm.split("-");
        if (parts.length != 2) {
            throw new BusinessException(ErrorCode.INVALID_NEXT_TERM);
        }
        try {
            return new NextTerm(Integer.parseInt(parts[0]), parts[1]);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_NEXT_TERM);
        }
    }

    private record NextTerm(int year, String semCd) {
    }
}
