/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.common.service;

/**
 * Created by ruans on 2017/06/09.
 */
public class ServiceException extends RuntimeException implements ServiceError {
    String reference;

    public ServiceException() {
        reference = ServiceError.getReferenceNo();
    }

    public ServiceException(String message) {
        super(message);
        reference = ServiceError.getReferenceNo();
    }

    public ServiceException(String message, String reference, Throwable cause) {
        super(message, cause);
        this.reference = reference;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        from(cause);
    }

    public ServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
        from(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        from(cause);
    }

    private void from(Throwable cause) {
        if (cause instanceof ServiceError)
            reference = ((ServiceError) cause).getReference();
        else
            reference = ServiceError.getReferenceNo();
    }

    @Override
    public String getReference() {
        return reference;
    }
}


