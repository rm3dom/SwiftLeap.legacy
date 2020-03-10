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

package org.swiftleap.common.comms.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.client.AsyncClientHttpRequest
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.swiftleap.common.comms.*
import org.swiftleap.common.service.ServiceException
import org.swiftleap.common.util.StringUtil
import java.io.IOException
import java.net.ConnectException
import java.net.URI
import java.util.*
import java.util.regex.Pattern
import kotlin.math.absoluteValue

private typealias BadHost = Triple<String, Long, Long>

class BasicHost(val originalPath: String = "",
                val method: BalancingMethod = BalancingMethod.Sticky,
                val hosts: List<String> = emptyList()) : HostSpec {
    var hostIndex = -1
    var badHosts = mutableMapOf<String, BadHost>()
    val random = Random(System.currentTimeMillis())
    override fun isValid() = hosts.isNotEmpty()

    operator fun component1() = method
    operator fun component2() = hosts
    operator fun component3() = hostIndex

    override fun toString() = originalPath

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasicHost

        if (originalPath != other.originalPath) return false

        return true
    }

    override fun hashCode() = originalPath.hashCode()
}

@Component
class BasicHostSolver : HostSolver {

    private val invalid: HostSpec = BasicHost()
    private val expMethod: Pattern = Pattern.compile("(@\\w+)?\\s*(.*)")
    private val expHosts: Pattern = Pattern.compile("([^\\s,;]+)[\\s,;]*")


    override fun solve(path: HostSpec) =
            synchronized(path) {
                if (!path.isValid())
                    throw ServiceException("Invalid path: $path")

                val host = path as BasicHost
                val (method, hosts) = host

                if (hosts.count() == 1)
                    return hosts[0]

                if (host.hostIndex < 0)
                    host.hostIndex = host.random.nextInt().absoluteValue % hosts.count()

                if (host.badHosts.count() > 0) {
                    val now = System.currentTimeMillis()
                    host.badHosts.values.toList().forEach {
                        val (key, time, span) = it
                        if (time + span < now)
                            host.badHosts.remove(key)
                    }
                }

                var goodHost = ""
                //Break for random numbers
                var count = 100
                while (count-- > 0) {
                    //RR
                    if (method == BalancingMethod.RoundRobbin) {
                        host.hostIndex++
                        if (host.hostIndex >= hosts.count())
                            host.hostIndex = 0
                    }

                    //Cases where the hosts changed
                    if (host.hostIndex >= hosts.count())
                        host.hostIndex = host.random.nextInt().absoluteValue % hosts.count();

                    goodHost = hosts[host.hostIndex];

                    if (host.badHosts.containsKey(goodHost)) {
                        if (method == BalancingMethod.Sticky)
                            host.hostIndex = host.random.nextInt().absoluteValue % hosts.count();
                        continue
                    }

                    break
                }
                goodHost
            }

    override fun badHost(path: HostSpec, hostPath: String, timeSpan: Long) =
            synchronized(path) {
                val host = path as BasicHost
                host.badHosts.put(hostPath, Triple(hostPath, System.currentTimeMillis(), timeSpan))
                Unit
            }

    override fun goodHost(path: HostSpec, hostPath: String) =
            synchronized(path) {
                val host = path as BasicHost
                host.badHosts.remove(hostPath)
                Unit
            }

    override fun hasGoodHost(path: HostSpec) =
            synchronized(path) {
                val host = path as BasicHost
                host.badHosts.count() < host.hosts.count()
            }

    override fun parse(path: String): HostSpec {
        if (path.isBlank())
            return invalid

        val hosts = mutableListOf<String>()

        val matchMethod = expMethod.matcher(path)
        if (!matchMethod.find())
            return invalid

        val methodName = matchMethod.group(1)?.toLowerCase()?.trim() ?: ""
        val hostsGroup = matchMethod.group(2)
        val matchHosts = expHosts.matcher(hostsGroup)
        while (matchHosts.find()) {
            hosts.add(matchHosts.group(1).trim())
        }
        val method =
                when (methodName) {
                    "@rr", "@round", "@roundrobbin" -> BalancingMethod.RoundRobbin
                    else -> BalancingMethod.Sticky
                }
        return BasicHost(path, method, hosts)
    }
}

class BasicRestProvider(
        val pathSpec: String,
        val handlers: Iterable<RestHandler> = emptyList(),
        val hostSolver: HostSolver = BasicHostSolver()
) : RestProvider {
    private val restTemplate: RestTemplate
    private val hostSpec: HostSpec = hostSolver.parse(pathSpec)

    init {

        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val converter = MappingJackson2HttpMessageConverter(mapper);

        restTemplate = RestTemplate(object : SimpleClientHttpRequestFactory() {
            @Throws(IOException::class)
            override fun createRequest(uri: URI, httpMethod: HttpMethod): ClientHttpRequest {
                val req = super.createRequest(uri, httpMethod)
                prepare(uri, httpMethod, req)
                return req
            }

            @Throws(IOException::class)
            override fun createAsyncRequest(uri: URI, httpMethod: HttpMethod): AsyncClientHttpRequest {
                val req = super.createAsyncRequest(uri, httpMethod)
                prepare(uri, httpMethod, req)
                return req
            }
        })

        restTemplate.messageConverters.add(0, converter)
    }

    private fun prepare(uri: URI, httpMethod: HttpMethod, request: HttpRequest) {
        request.headers.accept = listOf(MediaType.APPLICATION_JSON)
        if (httpMethod == HttpMethod.POST)
            request.headers.contentType = MediaType.APPLICATION_JSON

        handlers.forEach {
            it.prepare(uri, httpMethod, request)
        }
    }

    private fun <T> ResponseEntity<T>.toBody(): T? {
        if (this.statusCode == HttpStatus.NO_CONTENT)
            return null
        if (this.statusCode.value() >= 200 || this.statusCode.value() < 300)
            return this.body
        handlers.forEach {
            it.handleError(this)
        }
        throw ServiceException("${this.statusCodeValue}")
    }

    private fun toUrl(path: String, queryParams: Query) = "${StringUtil.trimr(hostSolver.solve(hostSpec), '/')}/${StringUtil.triml(path, '/')}"

    override fun <Request> doHttpPost(path: String, request: Request, pathParams: Query, queryParams: Query) {
        doHttpRequest(path, request, pathParams, queryParams)
    }

    override fun <Request, Response> doHttpPost(path: String, request: Request, responseClass: Class<Response>, pathParams: Query, queryParams: Query): Response? =
            doHttpRequestResponse(path, request, responseClass, pathParams, queryParams)

    override fun <Response> doHttpGet(path: String, responseClass: Class<Response>, pathParams: Query, queryParams: Query): Response? =
            doHttpResponse(HttpMethod.GET, path, responseClass, pathParams, queryParams)

    override fun <Response> doHttpGet(path: String, responseType: ParameterizedTypeReference<Response>, pathParams: Query, queryParams: Query): Response? =
            doHttpResponse(HttpMethod.GET, path, responseType, pathParams, queryParams)

    override fun doHttpDelete(path: String, pathParams: Query, queryParams: Query) =
            doHttpEmpty(HttpMethod.DELETE, path, pathParams, queryParams)

    override fun <Response> doHttpDelete(path: String, responseClass: Class<Response>, pathParams: Query, queryParams: Query): Response? =
            doHttpResponse(HttpMethod.DELETE, path, responseClass, pathParams, queryParams)

    override fun doHttpPut(path: String, pathParams: Query, queryParams: Query) =
            doHttpEmpty(HttpMethod.PUT, path, pathParams, queryParams)

    override fun <Response> doHttpPut(path: String, responseClass: Class<Response>, pathParams: Query, queryParams: Query): Response? =
            doHttpResponse(HttpMethod.PUT, path, responseClass, pathParams, queryParams)

    private fun doHttpEmpty(
            method: HttpMethod,
            path: String,
            pathParams: Query,
            queryParams: Query) {
        //org.springframework.web.client.ResourceAccessException, java.net.ConnectException
        restTemplate.exchange(
                toUrl(path, queryParams),
                method,
                HttpEntity.EMPTY,
                object : ParameterizedTypeReference<String>() {},
                pathParams)
                .toBody()
    }


    private fun <Request> doHttpRequest(
            path: String,
            request: Request,
            pathParams: Query,
            queryParams: Query) {
        //org.springframework.web.client.ResourceAccessException, java.net.ConnectException
        restTemplate.exchange(
                toUrl(path, queryParams),
                HttpMethod.POST,
                HttpEntity(request),
                object : ParameterizedTypeReference<String>() {},
                pathParams)
                .toBody()
    }

    private fun toPrettyException(ex: Exception): Throwable =
        when( ex) {
            is ResourceAccessException -> ServiceException("Unable to connect to host. Please try again later.")
            is ConnectException -> ServiceException("Unable to connect to host. Please try again later.")
            else -> ServiceException(ex)
        }

    private fun <Request, Response> doHttpRequestResponse(
            path: String,
            request: Request,
            responseClass: Class<Response>,
            pathParams: Query,
            queryParams: Query): Response? {
        try {
            return restTemplate.exchange(
                            toUrl(path, queryParams),
                            HttpMethod.POST,
                            HttpEntity(request),
                            responseClass,
                            pathParams)
                    .toBody()
        } catch (ex : Exception) {
            throw toPrettyException(ex)
        }
    }

    private fun <Response> doHttpResponse(
            method: HttpMethod,
            path: String,
            responseClass: Class<Response>,
            pathParams: Query,
            queryParams: Query): Response? {
        try {
            return restTemplate.exchange(
                    toUrl(path, queryParams),
                    method,
                    HttpEntity.EMPTY,
                    responseClass,
                    pathParams)
                    .toBody()
        } catch (ex : Exception) {
            throw toPrettyException(ex)
        }
    }


    private fun <Response> doHttpResponse(
            method: HttpMethod,
            path: String,
            responseType: ParameterizedTypeReference<Response>,
            pathParams: Query,
            queryParams: Query): Response? {
        try {
            return restTemplate.exchange(
                    toUrl(path, queryParams),
                    method,
                    HttpEntity.EMPTY,
                    responseType,
                    pathParams)
                    .toBody()
        } catch (ex : Exception) {
            throw toPrettyException(ex)
        }
    }
}

