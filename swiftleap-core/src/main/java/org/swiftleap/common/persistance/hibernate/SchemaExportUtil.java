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
package org.swiftleap.common.persistance.hibernate;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.reflections.Reflections;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.util.EnumSet;

/**
 * Created by ruans on 2016/09/28.
 */
public class SchemaExportUtil {
    public static void export(String packageToScan) {
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect")
                        //.applySetting("javax.persistence.schema-generation-connection", connection)
                        .build());


        final Reflections reflections = new Reflections(packageToScan);
        for (Class<?> cl : reflections.getTypesAnnotatedWith(Embeddable.class)) {
            metadata.addAnnotatedClass(cl);
        }
        for (Class<?> cl : reflections.getTypesAnnotatedWith(MappedSuperclass.class)) {
            metadata.addAnnotatedClass(cl);
        }
        for (Class<?> cl : reflections.getTypesAnnotatedWith(Entity.class)) {
            metadata.addAnnotatedClass(cl);
        }

        SchemaExport export = new SchemaExport();
        export.setFormat(true);
        export.setDelimiter(";");
        export.create(EnumSet.of(TargetType.STDOUT), metadata.buildMetadata());
    }
}
