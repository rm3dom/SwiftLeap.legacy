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
  
package org.swiftleap.ui

import org.junit.Test

class UiKtTest {

    @Test
    fun merge() {
        val a = listOf(
                BasicMenuEntry(
                        key="menu-1",
                        text="1",
                        link= "/1",
                        children = listOf(
                                BasicMenuEntry(
                                        key="menu-1.1",
                                        text = "1.1",
                                        link = "/1/1"
                                ),
                                BasicMenuEntry(
                                        "menu-1.2",
                                        "1.2",
                                        "/1/2"
                                )
                        )
                ),
                BasicMenuEntry(
                        key = "menu-2",
                        text = "2",
                        link = "/2",
                        children = listOf(
                                BasicMenuEntry(
                                        key="menu-2.1",
                                        text = "2.1",
                                        link = "/2/1",
                                        children = listOf(

                                        )
                                ),
                                BasicMenuEntry(
                                        key ="menu-2.2",
                                        text = "2.2",
                                        link = "/2/2",
                                        children = listOf(

                                        )
                                )
                        )
                )
        )


        val c = listOf(
                BasicMenuEntry(
                        key = "menu-1",
                        children = listOf(
                                BasicMenuEntry(
                                        "menu-1.1",
                                        "1.1",
                                        "/1/1"
                                ),
                                BasicMenuEntry(
                                        "menu-1.3",
                                        "1.3",
                                        "/1/3"
                                )
                        )
                ),
                BasicMenuEntry(
                        key = "menu-2",
                        children = listOf(
                                BasicMenuEntry(
                                        "menu-2.1",
                                        "2.1",
                                        "/2/1"
                                ),
                                BasicMenuEntry(
                                        "menu-2.3",
                                        "2.3",
                                        "/2/3"
                                )
                        )
                )
        )

        println(a.merge(c))
    }
}