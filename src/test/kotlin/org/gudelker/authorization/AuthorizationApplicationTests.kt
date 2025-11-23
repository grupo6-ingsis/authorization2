package org.gudelker.authorization

import org.gudelker.authorization.security.AuthenticationServiceApplication
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [AuthenticationServiceApplication::class])
@ActiveProfiles("test")
class AuthorizationApplicationTests {
    @Test
    fun contextLoads() {
    }
}
