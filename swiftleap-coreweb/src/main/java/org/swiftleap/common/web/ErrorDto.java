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


package org.swiftleap.common.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.swiftleap.common.codegen.anotate.CGAlias;
import org.swiftleap.common.codegen.anotate.CGInclude;
import org.swiftleap.common.service.BadRequestException;
import org.swiftleap.common.service.ErrorMessage;
import org.swiftleap.common.service.SecurityError;
import org.swiftleap.common.service.ServiceError;

import javax.servlet.http.HttpServletResponse;

/**
 * Standard error returned to clients by the API services.
 *
 * @see ServiceError
 * Created by ruans on 2015/08/18.
 */
@CGAlias("Error")
@CGInclude
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDto implements ErrorMessage {
    private static final long serialVersionUID = -8036895860774172839L;
    protected String message;
    protected int code;
    protected String reference;

    public ErrorDto() {

    }

    public ErrorDto(Throwable e) {
        this(0, e);
    }

    public ErrorDto(int code, Throwable e) {
        this.code = code;

        e = unwrap(e);

        if (e instanceof ServiceError) {
            reference = ((ServiceError) e).getReference();
        }
        if (reference == null)
            reference = ServiceError.getReferenceNo();

        //The code is success, we cant allow it
        if (this.code == 0 || (this.code >= 200 && this.code < 400)) {
            if (SecurityError.class.isAssignableFrom(e.getClass())) {
                this.code = HttpServletResponse.SC_FORBIDDEN;
            } else if (SecurityException.class.isAssignableFrom(e.getClass())) {
                this.code = HttpServletResponse.SC_FORBIDDEN;
            } else if (BadRequestException.class.isAssignableFrom(e.getClass())) {
                this.code = HttpServletResponse.SC_BAD_REQUEST;
                this.reference = "";
            } else if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                this.code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            } else {
                this.code = HttpServletResponse.SC_BAD_REQUEST;
            }
        }

        message = e.getLocalizedMessage();
    }

    public static Throwable unwrap(Throwable e) {
        Throwable t = e;
        if (t instanceof ServiceError)
            return t;
        while (t.getCause() != null) {
            t = t.getCause();
            if (t instanceof ServiceError)
                return t;
        }
        return e;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
