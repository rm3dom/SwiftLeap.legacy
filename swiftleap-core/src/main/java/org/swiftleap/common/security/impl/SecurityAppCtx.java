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
package org.swiftleap.common.security.impl;

import org.swiftleap.cms.CmsService;
import org.swiftleap.common.security.EncryptionController;
import org.swiftleap.common.security.SecurityService;

/**
 * Created by ruans on 2017/07/30.
 */
public class SecurityAppCtx {
    static SecurityService securityService;
    static CmsService cmsService;
    static EncryptionController encryptionController;

    public static SecurityService getSecurityService() {
        return securityService;
    }

    public static void setSecurityService(SecurityService securityService) {
        SecurityAppCtx.securityService = securityService;
    }

    public static EncryptionController getEncryptionController() {
        return encryptionController;
    }

    public static void setEncryptionController(EncryptionController encryptionController) {
        SecurityAppCtx.encryptionController = encryptionController;
    }

    public static CmsService getCmsService() {
        return cmsService;
    }

    public static void setCmsService(CmsService cmsService) {
        SecurityAppCtx.cmsService = cmsService;
    }
}
