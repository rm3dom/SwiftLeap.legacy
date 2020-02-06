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
package org.swiftleap.common.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class RecaptchaUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    public static boolean validate(String remoteip, String response, String secret) {
        ResponseEntity<RecaptchaResponse> reResponse =
                restTemplate.getForEntity("https://www.google.com/recaptcha/api/siteverify?remoteip={remoteip}&response={response}&secret={secret}",
                        RecaptchaResponse.class,
                        ArrayUtil.asMap(new String[][]{
                                {"remoteip", remoteip},
                                {"response", response},
                                {"secret", secret}
                        }));

        if (!reResponse.getStatusCode().is2xxSuccessful() || !reResponse.getBody().isSuccess())
            return false;
        return true;
    }

    @Getter
    @Setter
    public static class RecaptchaResponse {
        boolean success;
        Date challenge_ts;
        String hostname;
    }

}
