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
package org.swiftleap.cms;

import org.swiftleap.AbstractCoreTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by ruans on 2017/06/03.
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
public class CmsServiceTest extends AbstractCoreTest {
    @Autowired
    CmsService cmsService;

    @Before
    public void init() {
        initSecurityContext();
    }

    @Test
    public void testCreateStuff() throws IOException {
        CmsNode item = cmsService.getRoot()
                .createFolder("f1")
                .createFolder("f2")
                .writeFile(FileRequest.builder()
                    .data("dsadas".getBytes())
                    .name("text.txt")
                    .title("title")
                    .build());

        item = cmsService.getItem("/f1/f2/" + item.getPath());
        assertEquals("title", ((CmsFileNode)item).getTitle());

        CmsFolderNode folder = cmsService.getFolder("blah");
        assertFalse(folder.exists());
        folder.create();
        assertTrue(folder.exists());

        folder.delete();
        assertFalse(folder.exists());

        folder = cmsService.getFolder("f1");
        folder.delete();
        assertFalse(folder.exists());
    }
}