package com.hm.hyeonminshinlottospring.global.exception

// import org.springframework.web.ErrorResponse 가 따로 있긴 하다.
open class ErrorResponse private constructor(val code: Int, val errorMessage: String) {
    protected constructor(errorCode: ErrorCode) : this(errorCode.code, errorCode.errorMessage)

    companion object {
        fun of(
            errorCode: ErrorCode,
            errorMessage: String?,
        ) = ErrorResponse(
            code = errorCode.code,
            errorMessage = errorMessage ?: errorCode.errorMessage,
        )
    }
}
