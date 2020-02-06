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
package org.swiftleap.cms.image;

import lombok.Getter;
import lombok.Setter;
import org.swiftleap.cms.CmsFileNode;
import org.swiftleap.common.types.ImageData;

import java.io.IOException;

/**
 * Created by ruans on 2017/06/03.
 */
@Getter
@Setter
public class ImageDataImpl implements ImageData {
    CmsFileNode item;

    public ImageDataImpl() {
    }

    public ImageDataImpl(CmsFileNode item) {
        this.item = item;
    }

    @Override
    public String getId() {
        return item.getPath();
    }

    @Override
    public String getMime() {
        return item.getMime();
    }

    @Override
    public byte[] getData(ImageSize size) {
        try {
            return item.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
