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
package org.swiftleap.party;

import org.swiftleap.common.config.Config;
import org.swiftleap.common.party.Client;
import org.swiftleap.common.util.BarcodeType;
import org.swiftleap.common.util.BarcodeUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by ruans on 2017/06/04.
 */
public interface Party extends Client {
    Long getId();

    String getType();

    PartyEnums.PartyRole getRole();

    String getCountryCode();

    Config getConfig();

    @Override
    default String getClientId() {
        return getId().toString();
    }


    default String getCode() {
        if (getId() == null)
            return null;
        String code = "0" + (20000000000L + getId());
        code += BarcodeUtil.calcEan13CheckSum(code);
        return code;
    }

    default BufferedImage getBarCode(BarcodeType type) throws IOException {
        return BarcodeUtil.renderEan13(getCode(), 0);
    }
}
