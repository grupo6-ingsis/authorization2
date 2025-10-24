package org.gudelker.authorization

import org.gudelker.authorization.security.AuthenticationServiceApplication
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [AuthenticationServiceApplication::class])
class AuthorizationApplicationTests {
    @Test
    fun contextLoads() {
    }
}
