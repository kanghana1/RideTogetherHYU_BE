package com.ridetogether.server.global.apiPayload.exception.handler;

import com.ridetogether.server.global.apiPayload.code.BaseErrorCode;
import com.ridetogether.server.global.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
