package com.secuho.CAI_Roadmap_V2.domain.checklist;

import com.secuho.CAI_Roadmap_V2.domain.auth.entity.User;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.Enrollment;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.EnrollmentStatus;
import com.secuho.CAI_Roadmap_V2.domain.course.entity.TermSummary;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.EnrollmentRepository;
import com.secuho.CAI_Roadmap_V2.domain.course.repository.TermSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistService {

    private final EnrollmentRepository enrollmentRepository;
    private final TermSummaryRepository termSummaryRepository;

    public ChecklistResponse getChecklist(User user) {
        List<Enrollment> completed = enrollmentRepository.findByUserAndStatus(user, EnrollmentStatus.COMPLETED);
        List<TermSummary> termSummaries = termSummaryRepository.findByUserOrderByYearAscSemCdAsc(user);

        double gpa = round2(termSummaries.stream()
                .map(TermSummary::getGpa)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));

        List<Enrollment> passed = completed.stream()
                .filter(e -> !"F".equals(e.getGradeCd()))
                .toList();

        double major = sumCredit(passed, e -> "전공".equals(e.getCourse().getCpdvNm()));
        double common = sumCredit(passed, e -> "공교".equals(e.getCourse().getCpdvNm()));
        double totalCredit = sumCredit(passed, e -> true);
        double basic = sumCredit(passed, e -> CurriculumRequirements.BASIC_COURSES.contains(e.getCourse().getName()));
        double bsm = sumCredit(passed, e -> CurriculumRequirements.BSM_CREDIT_COURSES.contains(e.getCourse().getName()));

        int design1 = completed.stream().anyMatch(e -> CurriculumRequirements.DESIGN1_SBJ_NO.equals(e.getCourse().getSbjNo())) ? 1 : 0;
        int design2 = completed.stream().anyMatch(e -> CurriculumRequirements.DESIGN2_SBJ_NO.equals(e.getCourse().getSbjNo())) ? 1 : 0;

        int engCredit = (int) completed.stream().filter(e -> e.getCourse().isForeignLanguageCourse()).count();

        Set<String> completedNames = completed.stream()
                .map(e -> e.getCourse().getName())
                .collect(Collectors.toSet());

        List<String> missingBasic = missing(CurriculumRequirements.BASIC_COURSES, completedNames);
        boolean basicCompleted = takenCount(CurriculumRequirements.BASIC_COURSES, completedNames) >= 2;

        List<String> missingBsm = missing(CurriculumRequirements.BSM_REQUIRED_COURSES, completedNames);
        long experimentCount = completed.stream()
                .filter(e -> e.getCourse().getName() != null && e.getCourse().getName().contains(CurriculumRequirements.EXPERIMENT_NAME_PATTERN))
                .count();
        boolean bsmCompleted = takenCount(CurriculumRequirements.BSM_REQUIRED_COURSES, completedNames) == CurriculumRequirements.BSM_REQUIRED_COURSES.size()
                && experimentCount >= 1;
        String experimentStatus = experimentCount >= 1 ? "실험 과목 수강 완료" : "noexp";

        List<String> missingDongguk = missing(CurriculumRequirements.DONGGUK_ATTITUDE_COURSES, completedNames);
        boolean donggukAttitude = takenCount(CurriculumRequirements.DONGGUK_ATTITUDE_COURSES, completedNames) == CurriculumRequirements.DONGGUK_ATTITUDE_COURSES.size();

        List<String> missingSelfDev = missing(CurriculumRequirements.SELF_DEV_COURSES, completedNames);
        boolean selfDev = takenCount(CurriculumRequirements.SELF_DEV_COURSES, completedNames) == CurriculumRequirements.SELF_DEV_COURSES.size();

        List<String> missingThinkncom = missing(CurriculumRequirements.THINKNCOM_COURSES, completedNames);
        boolean englishTaken = completed.stream()
                .anyMatch(e -> e.getCourse().getName() != null && e.getCourse().getName().contains(CurriculumRequirements.ENGLISH_NAME_PATTERN));
        boolean thinkncom = takenCount(CurriculumRequirements.THINKNCOM_COURSES, completedNames) == CurriculumRequirements.THINKNCOM_COURSES.size()
                && englishTaken;

        boolean creative = completed.stream()
                .anyMatch(e -> e.getCourse().getName() != null && e.getCourse().getName().contains(CurriculumRequirements.CREATIVE_NAME_PATTERN));
        List<String> missingCreative = creative ? List.of() : List.of(CurriculumRequirements.CREATIVE_NAME_PATTERN);

        List<String> missingDigitaliter = missing(CurriculumRequirements.DIGITALITER_COURSES, completedNames);
        boolean digitaliter = takenCount(CurriculumRequirements.DIGITALITER_COURSES, completedNames) == CurriculumRequirements.DIGITALITER_COURSES.size();

        return new ChecklistResponse(
                major, common, gpa, totalCredit, engCredit, basic, bsm, design1, design2,
                basicCompleted, missingBasic, bsmCompleted, missingBsm, experimentStatus,
                donggukAttitude, selfDev, thinkncom, creative, digitaliter,
                missingDongguk, missingSelfDev, missingThinkncom, missingCreative, missingDigitaliter
        );
    }

    private double sumCredit(List<Enrollment> enrollments, Predicate<Enrollment> filter) {
        return enrollments.stream()
                .filter(filter)
                .mapToDouble(e -> e.getCourse().getCredit() != null ? e.getCourse().getCredit() : 0.0)
                .sum();
    }

    private long takenCount(List<String> required, Set<String> completedNames) {
        return required.stream().filter(completedNames::contains).count();
    }

    private List<String> missing(List<String> required, Set<String> completedNames) {
        return required.stream().filter(name -> !completedNames.contains(name)).toList();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
