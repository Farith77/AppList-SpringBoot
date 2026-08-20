package com.dreamapps.AppList.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 1. Error de credenciales incorrectas (Login)
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf(
            "error" to "Unauthorized",
            "message" to "Usuario, correo o contraseña incorrectos"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    // 2. Error de lectura de JSON (campos faltantes o tipos incompatibles)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf(
            "error" to "Bad Request",
            "message" to "El formato de la solicitud JSON es inválido o faltan campos requeridos"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // 3. Error de validación de campos (@NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Campo inválido") }
        val errorResponse = mapOf(
            "error" to "Validation Error",
            "message" to "Error de validación en los campos enviados",
            "details" to errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // 4. Argumentos ilegales (recursos no encontrados o duplicados)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val message = ex.message ?: "Solicitud inválida"
        val status = if (message.contains("no existe", ignoreCase = true) || message.contains("no encontrada", ignoreCase = true)) {
            HttpStatus.NOT_FOUND
        } else {
            HttpStatus.BAD_REQUEST
        }

        val errorResponse = mapOf(
            "error" to if (status == HttpStatus.NOT_FOUND) "Not Found" else "Bad Request",
            "message" to message
        )
        return ResponseEntity.status(status).body(errorResponse)
    }

    // 5. Excepciones genéricas
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        if (ex.message?.contains("no encontrada", ignoreCase = true) == true) {
            val errorResponse = mapOf(
                "error" to "Not Found",
                "message" to (ex.message ?: "El recurso no fue encontrado")
            )
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        }

        val errorResponse = mapOf(
            "error" to "Internal Server Error",
            "message" to (ex.message ?: "Ocurrió un error inesperado en el servidor")
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}