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

import org.swiftleap.cms.*;
import org.swiftleap.common.types.ImageData;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/07/29.
 */
public class ImageTemplate {
    CmsService cmsService;
    String path;

    public ImageTemplate(CmsService cmsService, String path) {
        this.cmsService = cmsService;
        this.path = path;
    }

    public Stream<ImageData> getImages() {
        CmsNode item = cmsService.getItem(path);
        if (item == null || !item.isFolder())
            return Stream.empty();
        return ((CmsFolderNode) item).getChildren()
                .filter(CmsNode::isFile)
                .map(e -> new ImageDataImpl((CmsFileNode) e));
    }

    public ImageData addImage(String title, String mime, String fileName, byte[] data) throws IOException {
        return setImage(UUID.randomUUID().toString()
                , title
                , mime
                , fileName
                , data);
    }

    public void deleteImage(String id) throws IOException {
        CmsNode item = cmsService.getItem(path + "/" + id);
        if (item == null)
            return;
        item.delete();
    }

    public ImageData setImage(String id, String title, String mime, String fileName, byte[] data) throws IOException {
        CmsFolderNode folder = cmsService.getFolder(path);
        folder.create();
        CmsNode node = cmsService.getItem(path + "/" + id);
        CmsFileNode file = folder.writeFile(FileRequest.builder()
                .id(UUID.randomUUID().toString())
                .data(data)
                .name(fileName)
                .title(title)
                .mime(mime)
                .build());
        return new ImageDataImpl(file);
    }

    public ImageData getImage(String id) {
        CmsNode node = cmsService.getItem(path + "/" + id);
        if (node == null || !node.isFile())
            return null;
        return new ImageDataImpl((CmsFileNode) node);
    }
}
