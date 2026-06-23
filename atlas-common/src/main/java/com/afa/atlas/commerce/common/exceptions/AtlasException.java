package com.afa.atlas.commerce.common.exceptions;

import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import lombok.Getter;

@Getter
public class AtlasException extends RuntimeException {

    private final AtlasErrorCode errorCode;
    private final Exception exception;

    public AtlasException(
            final AtlasErrorCode errorCode,
            final String message
    ) {
        super(message);
        this.errorCode = errorCode;
        this.exception = null;
    }

    public AtlasException(
            final AtlasErrorCode errorCode,
            final String message,
            final Exception exception
    ) {
        super(message);
        this.errorCode = errorCode;
        this.exception = exception;
    }
}
