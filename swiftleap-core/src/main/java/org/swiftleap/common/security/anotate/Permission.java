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


package org.swiftleap.common.security.anotate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Permission for roles.
 * <p/>
 * Used with @Secured to specify a ACL list.
 * <p/>
 *
 * @see Secured
 * Created by ruan on 2015/08/08.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    /**
     * An array of roles which is allowed access.
     *
     * @return array of roles.
     */
    String[] value() default {"GUEST", "USER"};

    Access access() default Access.ALL;

    enum Access {
        READ(1),
        WRITE(2),
        DELETE(4),
        ALL(1 | 2 | 4);

        int level;

        Access(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public boolean hasRead() {
            return (level & READ.level) > 0;
        }

        public boolean hasWrite() {
            return (level & WRITE.level) > 0;
        }

        public boolean hasDelete() {
            return (level & DELETE.level) > 0;
        }

        public boolean hasAll() {
            return this == ALL;
        }
    }
}
