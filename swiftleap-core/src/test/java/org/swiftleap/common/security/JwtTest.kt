package org.swiftleap.common.security

import org.junit.Test

import org.junit.Assert.*

class JwtTest {

    @Test
    fun decode() {
        val signingKey = "phdjwtsigningkey"
        val principal =
                Jwt.decode("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI4ZWIxY2I3MmEzMzI0ZjkwOTVmMTg5Y2EyYWUzMmYwOSIsInVuaXF1ZV9uYW1lIjpbInBoZHN5cyIsInBoZHN5cyJdLCJ0ZW5hbnRJZCI6IjAiLCJyb2xlIjpbImFkbWluIiwiU2VuaW9yTWVtYmVyQ2FyZSIsIk1lbWJlckNhcmVNYWluIl0sIlNlY3VyaXR5TGV2ZWwiOiIxMCIsIm5iZiI6MTU4NzgwMjc4MiwiZXhwIjoxNTg3ODM4NzgyLCJpYXQiOjE1ODc4MDI3ODIsImlzcyI6Imh0dHBzOi8vcGFjaWZpY2hlYWx0aGR5bmFtaWNzLmNvbS5hdSIsImF1ZCI6Imh0dHBzOi8vcGFjaWZpY2hlYWx0aGR5bmFtaWNzLmNvbS5hdSJ9.8GwRxgpMAxEEJe_Xz-AtopnLN_s6wg1NKMF5ljKJSc0"
                        , signingKey.toByteArray())

        val roles = principal.claims["role"]
        val x = roles
    }
}