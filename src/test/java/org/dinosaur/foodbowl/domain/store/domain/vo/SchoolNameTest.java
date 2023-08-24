package org.dinosaur.foodbowl.domain.store.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SchoolNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"부산대학교", "서울대학교", "연세대학교 송도캠퍼스", "고려대학교 조치원 캠퍼스", "서울사이버 대학교"})
    void 학교_이름_객체를_생성한다(String name) {
        SchoolName schoolName = new SchoolName(name);

        assertThat(schoolName.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!부산대학교", "@서울대학교@", "+연세대학교-", "!@#!$"})
    void 학교_이름에_한글_숫자_영문_이외의_문자가_있으면_예외가_발생한다(String schoolName) {
        assertThatThrownBy(() -> new SchoolName(schoolName))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("학교 이름 형식이 잘못되었습니다.");
    }
}
