package com.dreamapps.AppList.security

import com.dreamapps.AppList.entity.AuthProvider
import com.dreamapps.AppList.entity.Usuario
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private lateinit var jwtService: JwtService
    private val secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    private val expirationMs: Long = 3600000 // 1 hora

    @BeforeEach
    fun setUp() {
        jwtService = JwtService(secretKey, expirationMs)
    }

    @Test
    fun `generateToken and extractUsername should return expected username`() {
        val usuario = Usuario(
            email = "test@example.com",
            authProvider = AuthProvider.LOCAL
        ).apply {
            setUsername("testuser")
        }

        val token = jwtService.generateToken(usuario)
        assertNotNull(token)
        assertTrue(token.isNotBlank())

        val extractedUsername = jwtService.extractUsername(token)
        assertEquals("testuser", extractedUsername)

        val isValid = jwtService.isTokenValid(token, usuario)
        assertTrue(isValid)
    }
}
