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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.swiftleap.common.util.ReflectionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ruans on 2017/06/13.
 */
@ControllerAdvice
public class DefaultControllerAdvice {
    static final Logger log = LoggerFactory.getLogger(DefaultControllerAdvice.class);

    /**
     * Return errors in a standard form.
     * <p/>
     * <p>
     * A common error class allows the javascript libraries to handle errors uniformly.
     * </p>
     *
     * @param res http response.
     * @param e   the exception thrown
     * @return ErrorMessage
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorDto defaultErrorHandler(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Exception e) {
        int code = res.getStatus();
        //This will be null if the classloader erased the annotation.
        ResponseStatus rs = ReflectionUtil.getAnnotation(ResponseStatus.class, e.getClass(), true);
        if (rs != null) {
            code = rs.value().value();
        }
        ErrorDto error = new ErrorDto(code, e);
        res.setStatus(error.getCode());

        log.error("#Ref: " + error.getReference() + " " + e.getMessage(), e);
        return error;
    }
}
