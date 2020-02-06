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
package org.swiftleap.update.impl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.swiftleap.update.NodeInfo;

import java.util.Date;

@Getter
@Setter
@ToString
public class NodeInfoDbo implements NodeInfo {
    String os;
    String mac;
    String hostName;
    String localIp;
    String remoteIp;
    String appName;
    String version;
    String instanceName;
    Date dateTime;

    public NodeInfoDbo() {
    }

    public NodeInfoDbo(NodeInfo info) {
        this.os = info.getOs();
        this.mac = info.getMac();
        this.hostName = info.getHostName();
        this.localIp = info.getLocalIp();
        this.remoteIp = info.getRemoteIp();
        this.appName = info.getAppName();
        this.version = info.getVersion();
        this.instanceName = info.getInstanceName();
        this.dateTime = info.getDateTime();
    }
}
