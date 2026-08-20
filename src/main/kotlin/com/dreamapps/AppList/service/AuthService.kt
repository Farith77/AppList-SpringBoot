package com.dreamapps.AppList.service

import com.dreamapps.AppList.dto.AuthResponse
import com.dreamapps.AppList.dto.LoginRequest
import com.dreamapps.AppList.dto.RegisterRequest
import com.dreamapps.AppList.entity.AuthProvider
import com.dreamapps.AppList.entity.Usuario
import com.dreamapps.AppList.repository.UsuarioRepository
import com.dreamapps.AppList.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val normalizedEmail = request.email.trim().lowercase()
        val normalizedUsername = request.username.trim()

        if (usuarioRepository.existsByEmail(normalizedEmail)) {
            throw IllegalArgumentException("El correo electrónico '$normalizedEmail' ya está registrado")
        }

        if (usuarioRepository.existsByUsername(normalizedUsername)) {
            throw IllegalArgumentException("El nombre de usuario '$normalizedUsername' ya está en uso")
        }

        val nuevoUsuario = Usuario(
            email = normalizedEmail,
            authProvider = AuthProvider.LOCAL,
            enabled = true
        )
        nuevoUsuario.setUsername(normalizedUsername)
        nuevoUsuario.setPassword(passwordEncoder.encode(request.password))

        val usuarioGuardado = usuarioRepository.save(nuevoUsuario)
        val token = jwtService.generateToken(usuarioGuardado)

        return AuthResponse(
            token = token,
            userId = usuarioGuardado.userId,
            username = usuarioGuardado.username,
            email = usuarioGuardado.email,
            authProvider = usuarioGuardado.authProvider
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val identifier = request.identifier.trim()

        // Autenticar credenciales con Spring Security
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(identifier, request.password)
        )

        // Recuperar datos completos del usuario
        val usuario = usuarioRepository.findByUsernameOrEmail(identifier, identifier)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con identificador: $identifier") }

        val token = jwtService.generateToken(usuario)

        return AuthResponse(
            token = token,
            userId = usuario.userId,
            username = usuario.username,
            email = usuario.email,
            authProvider = usuario.authProvider
        )
    }
}
