module HostApi exposing
    ( HostApiConfig
    , baseUrl
    , delete
    , deleteExpectNothing
    , get
    , getExpectNothing
    , init
    , post
    , postBody
    , postExpectNothing
    , postFile
    , put
    , putExpectNothing
    , toUrl
    )

import FileReader exposing (NativeFile)
import Http
import HttpBuilder
import Json.Decode as JD
import Json.Encode as JE
import Navigation
import Task exposing (Task)
import Time
import Types.Flags exposing (Flags)


type alias HostApiConfig =
    { baseUrl : String
    , apiToken : String
    , tenantId : String
    , role : String
    }


init : Flags -> Navigation.Location -> HostApiConfig
init flags nav =
    let
        proto =
            if String.contains "https" nav.protocol then
                "https"

            else
                "http"

        hostname =
            nav.hostname

        hostPort =
            if hostname == "localtest" then
                "8080"

            else
                nav.port_

        path = ""
            {-
            if nav.pathname == "/admin/index" || nav.pathname == "/admin/index.html" then
                "/admin"

            else if nav.pathname == "/" || nav.pathname == "/admin" || nav.pathname == "/admin.html" then
                ""

            else
                ""
            -}

        baseUrl =
            "api/v1/"
            --proto ++ "://" ++ hostname ++ ":" ++ hostPort ++ path ++ "/api/v1/"
    in
    { baseUrl = baseUrl
    , apiToken = flags.sessionId
    , tenantId = toString flags.tenantId
    , role = ""
    }


baseUrl : HostApiConfig -> String
baseUrl host =
    host.baseUrl


toUrl : HostApiConfig -> String -> String
toUrl host path =
    baseUrl host ++ path


post : String -> JE.Value -> JD.Decoder payload -> HostApiConfig -> Task Http.Error payload
post path body decoder host =
    postBody path (Http.jsonBody body) decoder host


postFile : String -> NativeFile -> JD.Decoder payload -> HostApiConfig -> Task Http.Error payload
postFile path file =
    let
        body =
            Http.multipartBody
                [ FileReader.filePart "file" file
                ]
    in
    postBody path body


postBody : String -> Http.Body -> JD.Decoder payload -> HostApiConfig -> Task Http.Error payload
postBody path body decoder host =
    HttpBuilder.post (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withBody body
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect (Http.expectJson decoder)
        |> HttpBuilder.toTask


postExpectNothing : String -> Http.Body -> HostApiConfig -> Task Http.Error String
postExpectNothing path body host =
    HttpBuilder.post (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withBody body
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect Http.expectString
        |> HttpBuilder.toTask


get : String -> JD.Decoder payload -> HostApiConfig -> Task Http.Error payload
get path decoder host =
    HttpBuilder.get (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect (Http.expectJson decoder)
        |> HttpBuilder.toTask


getExpectNothing : String -> HostApiConfig -> Task Http.Error String
getExpectNothing path host =
    HttpBuilder.get (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect Http.expectString
        |> HttpBuilder.toTask


put : String -> JD.Decoder payload -> HostApiConfig -> Task Http.Error payload
put path decoder host =
    HttpBuilder.put (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect (Http.expectJson decoder)
        |> HttpBuilder.toTask


putExpectNothing : String -> HostApiConfig -> Task Http.Error String
putExpectNothing path host =
    HttpBuilder.put (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect Http.expectString
        |> HttpBuilder.toTask


delete : String -> JD.Decoder payload -> HostApiConfig -> Task Http.Error payload
delete path decoder host =
    HttpBuilder.delete (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect (Http.expectJson decoder)
        |> HttpBuilder.toTask


deleteExpectNothing : String -> HostApiConfig -> Task Http.Error String
deleteExpectNothing path host =
    HttpBuilder.delete (toUrl host path)
        |> HttpBuilder.withHeader "X-SessionId" host.apiToken
        |> HttpBuilder.withHeader "X-TenantId" host.tenantId
        |> HttpBuilder.withTimeout (120 * Time.second)
        |> HttpBuilder.withExpect Http.expectString
        |> HttpBuilder.toTask
