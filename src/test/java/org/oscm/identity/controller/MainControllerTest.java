/**
 * *****************************************************************************
 *
 * <p>Copyright FUJITSU LIMITED 2019
 *
 * <p>Creation Date: July 22, 2019
 *
 * <p>*****************************************************************************
 */
package org.oscm.identity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Java6Assertions.fail;

@SpringBootTest
public class MainControllerTest {

        @Test
        public void shouldSucceed_whenGetToDefault() {
                fail("Not implemented");
        }

        @Test
        public void shouldRedirect_whenGetToLogin() {
                fail("Not implemented");
        }

        @Test
        public void shouldRedirectToHomeWithToken_whenPostToIdToken_givenNoErrors() {
                fail("Not implemented");
        }

        @Test
        public void shouldReturnError_whenPostToIdToken_givenAnError() {
                fail("Not implemented");
        }

        @Test
        public void shouldSucceed_whenPostToVerifyToken_givenValidToken() {
                fail("Not implemented");
        }

        @Test
        public void shouldReturnError_whenPostToVerifyToken_givenInvalidToken() {
                fail("Not implemented");
        }
}