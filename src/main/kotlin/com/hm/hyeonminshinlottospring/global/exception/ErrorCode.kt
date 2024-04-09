package com.hm.hyeonminshinlottospring.global.exception

import org.springframework.http.HttpStatus

/**
 * 1xxx : Common
 * 2xxx : Database
 * 3xxx : Client
 */

enum class ErrorCode(val httpStatus: HttpStatus, val code: Int, val errorMessage: String) {
    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 1000, "Invalid request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "Internal server error"),
    NO_SUCH_ELEMENT(HttpStatus.NOT_FOUND, 1002, "No such element"),
    // Database
    // Client
}
