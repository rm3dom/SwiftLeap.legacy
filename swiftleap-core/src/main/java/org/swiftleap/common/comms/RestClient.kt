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

package org.swiftleap.common.comms

import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.ResponseEntity
import java.net.URI

typealias Query = Map<String, Any>

fun emptyQuery(): Query = emptyMap()

abstract class RestHandler {
    open fun prepare(request: HttpRequest) {}
    open fun prepare(uri: URI, httpMethod: HttpMethod, request: HttpRequest) {
        prepare(request)
    }

    @Throws
    open fun <T> handleError(response: ResponseEntity<T>) {
    }
}

interface RestProvider {
    fun <Request> doHttpPost(path: String, request: Request, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery())

    fun <Request, Response> doHttpPost(path: String, request: Request, responseClass: Class<Response>, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response?

    fun <Response> doHttpGet(path: String, responseClass: Class<Response>, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response?

    fun <Response> doHttpGetList(path: String, responseClass: Class<Response>, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): List<Response>

    fun doHttpDelete(path: String, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery())

    fun <Response> doHttpDelete(path: String, responseClass: Class<Response>, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response?

    fun doHttpPut(path: String, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery())

    fun <Response> doHttpPut(path: String, responseClass: Class<Response>, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response?
}

inline fun <Request, reified Response> RestProvider.doHttpPost(path: String, request: Request, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response? {
    return this.doHttpPost(path, request, Response::class.java, pathParams, queryParams)
}

inline fun <reified Response> RestProvider.doHttpGetList(path: String, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): List<Response> {
    return this.doHttpGetList(path, Response::class.java, pathParams, queryParams);
}

inline fun <reified Response> RestProvider.doHttpDelete(path: String, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response? {
    return this.doHttpDelete(path, Response::class.java, pathParams, queryParams);
}

inline fun <reified Response> RestProvider.doHttpPut(path: String, pathParams: Query = emptyQuery(), queryParams: Query = emptyQuery()): Response? {
    return this.doHttpPut(path, Response::class.java, pathParams, queryParams);
}

enum class BalancingMethod {
    RoundRobbin,
    Sticky
}

interface HostSpec {
    fun isValid(): Boolean
}

interface HostSolver {
    /**
     * Find a host.
     */
    fun solve(path: HostSpec): String

    /**
     * Mark a host bad for some time duration.
     * @param timeSpan time in milliseconds
     */
    fun badHost(path: HostSpec, hostPath: String, timeSpan: Long = 120000)

    fun goodHost(path: HostSpec, hostPath: String)
    fun parse(path: String): HostSpec
    fun hasGoodHost(path: HostSpec): Boolean
}

