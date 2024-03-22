package com.hm.hyeonminshinlottospring.global.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[MethodArgumentNotValidException], ex")
        val bindingResult = ex.bindingResult
        val stringBuilder = StringBuilder()
        for (fieldError in bindingResult.fieldErrors) {
            stringBuilder.append(fieldError.field).append(":")
            stringBuilder.append(fieldError.defaultMessage)
            stringBuilder.append(", ")
        }
        return getInvalidRequestResponse(stringBuilder.toString())
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[HttpMessageNotReadableException] ${ex.message}")
        val errorMessage =
            when (val cause = ex.cause) {
                is InvalidFormatException -> "${cause.path.joinToString(separator = ".") { it?.fieldName.orEmpty() }}: ${ex.message}"
                is MismatchedInputException -> {
                    "${cause.path.joinToString(separator = ".") { it?.fieldName.orEmpty() }}: ${ex.message}"
                }
                else -> "유효하지 않은 요청입니다"
            }
        return getInvalidRequestResponse(errorMessage)
    }

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[HttpRequestMethodNotSupportedException] ${ex.message}")
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[MissingServletRequestPart] ${ex.message}")
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[MissingServletRequestParameter] ${ex.message}")
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[MissingPathVariable] ${ex.message}")
        return getInvalidRequestResponse(ex.message)
    }

    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.error("[HttpMediaTypeNotSupported] ${ex.message}")
        return getInvalidRequestResponse(ex.message, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    @ExceptionHandler(
        IllegalArgumentException::class,
        IllegalStateException::class,
        ConstraintViolationException::class,
        DataIntegrityViolationException::class,
    )
    fun invalidRequestException(ex: RuntimeException): ResponseEntity<Any>? {
        logger.error("[InvalidRequestException]", ex)
        return getInvalidRequestResponse(ex.message)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun noSuchElementException(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        logger.error("[NoSuchElementException]", ex)
        val noSuchElementErrorCode = ErrorCode.NO_SUCH_ELEMENT
        return ResponseEntity.status(noSuchElementErrorCode.httpStatus)
            .body(ErrorResponse.of(noSuchElementErrorCode, ex.message))
    }

    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("[Exception]", ex)
        val internalServerErrorCode = ErrorCode.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(internalServerErrorCode.httpStatus)
            .body(ErrorResponse.of(internalServerErrorCode, ex.message))
    }

    private fun getInvalidRequestResponse(
        errorMessage: String?,
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    ): ResponseEntity<Any> {
        val invalidRequestErrorCode = ErrorCode.INVALID_REQUEST
        return ResponseEntity.status(httpStatus)
            .body(ErrorResponse.of(invalidRequestErrorCode, errorMessage))
    }
}
