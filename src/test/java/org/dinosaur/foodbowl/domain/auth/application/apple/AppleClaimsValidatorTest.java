package org.dinosaur.foodbowl.domain.auth.application.apple;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppleClaimsValidatorTest {

    private static final AppleClaimsValidator appleClaimsValidator =
            new AppleClaimsValidator("iss", "clientId", "nonce");

    @Nested
    class 클레임_검증 {

        @Test
        void 이슈가_존재하지_않으면_유효하지_않은_클레임이다() {
            Claims claims = Jwts.claims()
                    .setAudience("clientId");
            claims.put("nonce", "78377B525757B494427F89014F97D79928F3938D14EB51E20FB5DEC9834EB304");

            boolean result = appleClaimsValidator.isValid(claims);

            assertThat(result).isFalse();
        }

        @Test
        void 이슈가_일치하지_않으면_유효하지_않은_클레임이다() {
            Claims claims = Jwts.claims()
                    .setIssuer("ssi")
                    .setAudience("clientId");
            claims.put("nonce", "78377B525757B494427F89014F97D79928F3938D14EB51E20FB5DEC9834EB304");

            boolean result = appleClaimsValidator.isValid(claims);

            assertThat(result).isFalse();
        }

        @Test
        void 클라이언트_아이디가_일치하지_않으면_유효하지_않은_클레임이다() {
            Claims claims = Jwts.claims()
                    .setIssuer("iss")
                    .setAudience("clientIdA");
            claims.put("nonce", "78377B525757B494427F89014F97D79928F3938D14EB51E20FB5DEC9834EB304");

            boolean result = appleClaimsValidator.isValid(claims);

            assertThat(result).isFalse();
        }

        @Test
        void 암호화값이_일치하지_않으면_유효하지_않은_클레임이다() {
            Claims claims = Jwts.claims()
                    .setIssuer("iss")
                    .setAudience("clientId");
            claims.put("nonce", "78377B525757B494427F89014F97D79928F");

            boolean result = appleClaimsValidator.isValid(claims);

            assertThat(result).isFalse();
        }

        @Test
        void 이슈_클라이언트_아이디_암호화값이_일치하면_유효한_클레임이다() {
            Claims claims = Jwts.claims()
                    .setIssuer("iss")
                    .setAudience("clientId");
            claims.put("nonce", "78377B525757B494427F89014F97D79928F3938D14EB51E20FB5DEC9834EB304");

            boolean result = appleClaimsValidator.isValid(claims);

            assertThat(result).isTrue();
        }
    }
}
