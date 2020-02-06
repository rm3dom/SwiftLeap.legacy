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

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.swiftleap.common.config.PropKeys
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

@Component
class SimpleCORSFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Methods", "*, POST, PUT, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "*, X-TenantId, Content-Type, Accept, Authorization, X-Requested-With, remember-me")
        chain.doFilter(req, res)
    }

    override fun init(filterConfig: FilterConfig) {}
    override fun destroy() {}
}

@Order(0)
@Component
class RedirectFilter : Filter {


    @Value(value = PropKeys._WEB_BASE_URL)
    var webBaseUrl = ""
    val absPattern = Regex("\\w+[:]//.*")

    private fun toAbsoluteUrl(base: String, url: String?): String? {
        var sanUrl = url
        if (sanUrl == "//" || sanUrl == "//#")
            sanUrl = sanUrl.replace("//", "/")
        return when {
            base.isBlank() -> sanUrl
            sanUrl.isNullOrEmpty() -> sanUrl
            sanUrl.matches(absPattern) -> sanUrl
            else -> base.trimEnd('/') + "/" + sanUrl.trimStart('/')
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val httpReq = req as HttpServletRequest
        val response = object : HttpServletResponseWrapper(res as HttpServletResponse) {

            override fun sendRedirect(location: String?) {

                var base = webBaseUrl

                if (base.isBlank()) {
                    val forwardedHost = httpReq.getHeader("x-forwarded-host")
                    val forwardedPrefix = httpReq.getHeader("x-forwarded-prefix")
                    val forwardedProto = httpReq.getHeader("x-forwarded-proto")

                    if (!forwardedHost.isNullOrBlank() && !forwardedProto.isNullOrBlank())
                        base = "${forwardedProto}://${forwardedHost}/${forwardedPrefix?.trimStart('/') ?: ""}"
                }

                super.sendRedirect(toAbsoluteUrl(base, location))
            }
        }

        chain.doFilter(req, response)
    }

    override fun init(filterConfig: FilterConfig) {}
    override fun destroy() {}
}
