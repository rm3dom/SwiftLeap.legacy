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

package org.swiftleap.common.web

import org.springframework.stereotype.Component
import org.swiftleap.common.security.SecurityContext
import org.swiftleap.common.security.UserPrincipal
import org.swiftleap.ui.BasicMenuEntry
import org.swiftleap.ui.MenuEntry
import org.swiftleap.ui.Module

@Component
open class CoreWebModule : Module {
    override fun name() = "Core Web"

    private fun userName(): String {
        val user = SecurityContext.getPrincipal() ?: UserPrincipal.Guest
        return user?.name ?: "Guest"
    }

    override fun menu() = listOf(
            BasicMenuEntry(
                    key = "settings", order = 1000, text = "Settings", roles = arrayOf("user"),
                    children = listOf(
                            BasicMenuEntry(key = "settings-me", order = 1, text = "Me: ${userName()}", link = "admin.html#me", roles = arrayOf("user")),
                            BasicMenuEntry(key = "settings-users", order = 2, text = "Users", link = "admin.html#users", roles = arrayOf("ruleadm")),
                            BasicMenuEntry(key = "settings-updates", order = 4, text = "Updates", link = "admin.html#updates", roles = arrayOf("ruleadm")),
                            BasicMenuEntry(key = "settings-logout", order = 1000, text = "Logout", icon = "fa-sign-out", link = "logout.html", roles = arrayOf("user"))
                    ) + when (SecurityContext.getTenantId()) {
                        SecurityContext.DEFAULT_TENANT_ID -> listOf(BasicMenuEntry(key = "settings-tenants", order = 3, text = "Tenants", link = "admin.html#tenants", roles = arrayOf("ruleadm")))
                        else -> emptyList()
                    }
            ),
            BasicMenuEntry(key = "login", order = 10001, text = "Login", link = "login.html", icon = "fa-sign-in", roles = arrayOf("guest"))
    )
}


open class HtmlUiRenderer {
    private fun isAllowed(user: UserPrincipal, entry: MenuEntry): Boolean {
        /*
        if (entry.roles.firstOrNull { e -> e.equals("guest", true) } != null) {
            if (user.isGuest)
                return true
            return false
        }
        if (user.isSuper)
            return true
         */
        return user.hasAnyActiveRole(entry.roles.asIterable())
    }

    fun render(user: UserPrincipal, siteName: String, indexUrl: String, iconUrl: String, entries: Iterable<MenuEntry>): String {
        val navStart = "<nav class=\"navbar navbar-inverse\"><div class=\"container-fluid\">" +
                "<div class=\"navbar-header\">" +
                "<button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"navbar-collapse-1\" aria-expanded=\"false\"></button>" +
                "<span class=\"sr-only\">Toggle navigation</span>" +
                "<a class=\"navbar-brand\" href=\"${indexUrl}\"><span><img src=\"$iconUrl\" height=\"26\"></span> $siteName</a>" +
                "</div>" +
                "<div class=\"collapse navbar-collapse\" id=\"navbar-collapse-1\">"


        val navEnd = "</div></div></nav>"

        var items = entries.map { e -> renderNavEntry(user, e) }.fold("", { r, o -> r + o })

        return "$navStart$items$navEnd"
    }

    protected fun renderIcon(entry: MenuEntry) =
            if (entry.icon.isEmpty())
                ""
            else
                "<i class=\"fa ${entry.icon}\"></i>"

    protected fun renderIcon(entry: String) =
            if (entry.isEmpty())
                ""
            else
                "<i class=\"fa ${entry}\"></i>"

    protected fun renderNavEntry(user: UserPrincipal, entry: MenuEntry): String {
        if (!isAllowed(user, entry))
            return ""

        val classes =
                if (entry.order >= 1000)
                    "navbar-right"
                else
                    ""
        return if (entry.children.count() < 1)
            "<a type=\"button\" role=\"button\" href=\"${entry.link}\" class=\"btn btn-primary navbar-btn btn-transparent $classes\">${renderIcon(entry)} ${entry.text}</a>"
        else
            "<ul class=\"nav navbar-nav $classes\">${renderMenuEntry(user, entry)}</ul>"
    }

    protected fun renderMenuEntry(user: UserPrincipal, entry: MenuEntry): String {
        if (!isAllowed(user, entry))
            return ""

        if (entry.children.count() < 1)
            return "<li class=\"\"><a href=\"${entry.link}\">${renderIcon(entry)} ${entry.text}</a></li>"

        val dropBtn =
                "<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">${entry.text} ${renderIcon("fa-chevron-down")}</a>"

        val spanStart = "<li class=\"dropdown\"> ${dropBtn} <ul class=\"dropdown-menu\" style=\"max-height: 40vh; overflow: auto;\">"
        val spanEnd = "</ul></li>"
        val items = entry.children
                .sortedBy { l -> l.order }
                .map { e -> renderMenuEntry(user, e) }
                .fold("", { r, o -> r + o })

        return "$spanStart$items$spanEnd"
    }
}