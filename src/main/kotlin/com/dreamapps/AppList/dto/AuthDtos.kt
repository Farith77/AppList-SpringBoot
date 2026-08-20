package com.dreamapps.AppList.dto

import com.dreamapps.AppList.entity.AuthProvider
import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "El email no puede estar vacío")
    @field:Email(message = "El formato de email no es válido")
    val email: String,

    @field:NotBlank(message = "El nombre de usuario no puede estar vacío")
    @field:Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    val username: String,

    @field:NotBlank(message = "La contraseña no puede estar vacía")
    @field:Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    val password: String
)

data class LoginRequest(
    @JsonAlias("username", "email", "usuario", "correo")
    @field:NotBlank(message = "El usuario o email es requerido")
    val identifier: String = "",

    @field:NotBlank(message = "La contraseña es requerida")
    val password: String = ""
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val username: String,
    val email: String,
    val authProvider: AuthProvider,
    val type: String = "Bearer"
)
