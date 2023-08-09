package org.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EncryptUtilsTest {

    @Test
    void sh265_알고리즘으로_해시값을_구한다() {
        String value = "nonce";
        String expected = "78377B525757B494427F89014F97D79928F3938D14EB51E20FB5DEC9834EB304";

        String encrypt = EncryptUtils.encrypt(value);

        assertThat(encrypt.toLowerCase()).isEqualTo(expected.toLowerCase());
    }
}
