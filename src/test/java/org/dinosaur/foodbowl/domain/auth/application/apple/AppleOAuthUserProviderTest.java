package org.dinosaur.foodbowl.domain.auth.application.apple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.PublicKey;
import org.dinosaur.foodbowl.domain.auth.application.dto.PlatformUser;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("NonAsciiCharacters")
class AppleOAuthUserProviderTest extends IntegrationTest {

    @Autowired
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @MockBean
    private AppleTokenParser appleTokenParser;

    @MockBean
    private AppleClient appleClient;

    @MockBean
    private ApplePublicKeyGenerator applePublicKeyGenerator;

    @MockBean
    private AppleClaimsValidator appleClaimsValidator;

    @Nested
    class 유저_정보_추출 {

        @Test
        void 토큰으로부터_추출한_애플_클레임이_유효하지_않으면_예외를_던진다() {
            given(appleClaimsValidator.isValid(any(Claims.class))).willReturn(false);

            assertThatThrownBy(() -> appleOAuthUserProvider.extractPlatformUser("token"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("페이로드가 유효하지 않은 토큰입니다.");
        }

        @Test
        void 토큰으로부터_추출한_애플_클레임이_유효하다면_유저_정보를_반환한다() {
            Claims claims = Jwts.claims().setSubject("1");
            claims.put("email", "foodbowl@foodbowl.com");
            given(applePublicKeyGenerator.generate(any(), any())).willReturn(mock(PublicKey.class));
            given(appleTokenParser.extractClaims(anyString(), any(PublicKey.class))).willReturn(claims);
            given(appleClaimsValidator.isValid(any(Claims.class))).willReturn(true);

            PlatformUser result = appleOAuthUserProvider.extractPlatformUser("token");

            assertAll(
                    () -> assertThat(result.socialType()).isEqualTo(SocialType.APPLE),
                    () -> assertThat(result.socialId()).isEqualTo("1"),
                    () -> assertThat(result.email()).isEqualTo("foodbowl@foodbowl.com")
            );
        }
    }
}
