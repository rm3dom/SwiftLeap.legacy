module Model.System exposing (LoginState(..), System, addTenant, addUser, getTenantId, getUser, getUserName, hasAnyRoles, init, isLoggedIn, isNotViewer, parseParams, setFlags, setLocation, setRoles, setTenant, setTenantId, setTenants, setTime, setUpdateInfo, setUser, setUsers, toKeyValuePair)

import Dict exposing (Dict)
import HostApi exposing (HostApiConfig)
import Http
import Navigation
import Time exposing (Time)
import Types.AuthRequest as AuthRequest exposing (AuthRequest)
import Types.Flags as Flags exposing (Flags)
import Types.Pair exposing (Pair)
import Types.Tenant exposing (Tenant)
import Types.UpdateInfo exposing (UpdateInfo)
import Types.User exposing (User)


type LoginState
    = LoggedIn User
    | NotLoggedIn AuthRequest


type alias System =
    { time : Time
    , host : HostApiConfig
    , users : List User
    , tenants : List Tenant
    , updateInfo : UpdateInfo
    , user : LoginState
    , roles : List Pair
    , helpUrl : String
    , helpSuffix : String
    , flags : Flags
    }


init : Flags -> Navigation.Location -> System
init flags location =
    { time = 0
    , host = HostApi.init flags location
    , users = []
    , tenants = []
    , updateInfo = Types.UpdateInfo.init
    , user = NotLoggedIn AuthRequest.init
    , roles = []
    , helpUrl = "https://www.pacificdynamics.com.au/cat/doc/index.html#!"
    , helpSuffix = ".md"
    , flags = flags
    }


isLoggedIn : System -> Bool
isLoggedIn system =
    not (String.isEmpty system.host.apiToken)


hasAnyRoles : List String -> System -> Bool
hasAnyRoles roles system =
    let
        lroles =
            List.map String.toLower roles
    in
    case system.user of
        NotLoggedIn _ ->
            List.any ((==) "guest") lroles

        LoggedIn auser ->
            List.foldl (\r v -> v || List.any ((==) r) lroles) False (List.map String.toLower auser.roles)


isNotViewer : System -> Bool
isNotViewer system =
    hasAnyRoles [ "ruleauth", "ruleadm", "sysadm" ] system


setFlags : Flags -> System -> System
setFlags flags system =
    { system | flags = flags }
        |> setTenantId flags.tenantId
        |> setSessionId flags.sessionId


setTime : Time -> System -> System
setTime time system =
    { system | time = time }


addTenant : Tenant -> System -> System
addTenant tenant system =
    let
        newTenants =
            system.tenants
                |> List.filter (\t -> t.tenantId /= tenant.tenantId)
                |> List.append [ tenant ]
    in
    { system | tenants = newTenants }


setTenants : List Tenant -> System -> System
setTenants tenants system =
    { system | tenants = tenants }


setUsers : List User -> System -> System
setUsers users system =
    { system | users = users }


setRoles : List Pair -> System -> System
setRoles roles system =
    { system | roles = roles }


addUser : User -> System -> System
addUser user system =
    let
        newUsers =
            system.users
                |> List.filter (\u -> u.userName /= user.userName && u.id /= user.id)
                |> List.append [ user ]
    in
    { system | users = newUsers }


setUser : Maybe User -> System -> System
setUser user system =
    let
        host =
            system.host

        ( apiToken, tenantId ) =
            case user of
                Nothing ->
                    ( "", 0 )

                Just u ->
                    ( u.sessionId, Maybe.withDefault 0 u.tenantId )

        userState =
            case user of
                Nothing ->
                    NotLoggedIn AuthRequest.init

                Just u ->
                    LoggedIn u
    in
    { system | user = userState, host = { host | apiToken = apiToken, tenantId = toString tenantId } }


getUserName : System -> String
getUserName system =
    case system.user of
        NotLoggedIn _ ->
            ""

        LoggedIn user ->
            user.name


getUser : System -> Maybe User
getUser system =
    case system.user of
        NotLoggedIn _ ->
            Nothing

        LoggedIn u ->
            Just u


setTenant : String -> System -> System
setTenant tenant system =
    let
        host =
            system.host
    in
    { system | host = { host | tenantId = tenant } }


setTenantId : Int -> System -> System
setTenantId tenantId system =
    let
        host =
            system.host
    in
    { system | host = { host | tenantId = toString tenantId } }


setSessionId : String -> System -> System
setSessionId sessionId system =
    let
        host =
            system.host
    in
    { system | host = { host | apiToken = sessionId } }


getTenantId : System -> Int
getTenantId system =
    case String.toInt system.host.tenantId of
        Err msg ->
            0

        Ok val ->
            val


setUpdateInfo : UpdateInfo -> System -> System
setUpdateInfo updateInfo system =
    { system | updateInfo = updateInfo }


setLocation : Navigation.Location -> System -> System
setLocation nav system =
    let
        host =
            system.host

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

        path =
            if nav.pathname == "/rules/index" || nav.pathname == "/rules/index.html" then
                "/rules"

            else if nav.pathname == "/" || nav.pathname == "/index" || nav.pathname == "/index.html" then
                ""

            else
                nav.pathname

        baseUrl =
            proto ++ "://" ++ hostname ++ ":" ++ hostPort ++ path ++ "/api/v1/"
    in
    { system | host = { host | baseUrl = baseUrl } }


parseParams : String -> Dict String String
parseParams queryString =
    queryString
        |> String.dropLeft 1
        |> String.split "&"
        |> List.filterMap toKeyValuePair
        |> Dict.fromList


toKeyValuePair : String -> Maybe ( String, String )
toKeyValuePair segment =
    case String.split "=" segment of
        [ key, value ] ->
            Maybe.map2 (,) (Http.decodeUri key) (Http.decodeUri value)

        _ ->
            Nothing
