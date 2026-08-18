package com.dreamapps.AppList.exception
/*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 1. Atrapa los IllegalArgumentException (Ej. "La lista con ID ... no existe")
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf(
            "error" to "Not Found / Bad Request",
            "message" to (ex.message ?: "El recurso solicitado no es válido o no existe")
        )
        // Devolvemos 404 Not Found para que el celular sepa que el dato ya no está
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    // 2. Atrapa las excepciones genéricas (Ej. throw Exception("Lista no encontrada con el id..."))
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {

        // Si el mensaje contiene "no encontrada", devolvemos 404
        if (ex.message?.contains("no encontrada", ignoreCase = true) == true) {
            val errorResponse = mapOf(
                "error" to "Not Found",
                "message" to (ex.message ?: "El recurso no fue encontrado")
            )
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        }

        // Para cualquier otro error crítico, mantenemos el 500 pero con un JSON limpio
        val errorResponse = mapOf(
            "error" to "Internal Server Error",
            "message" to "Ocurrió un error inesperado en el servidor"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}*/