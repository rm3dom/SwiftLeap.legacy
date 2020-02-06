module Routing exposing (Route(..), Routing, currentRoute, gotoRoute, init, newUrl, parseRoute, routeToString, toUrl, updateLocation)

import Job
import Navigation
import UrlParser as Url exposing ((</>), (<?>), int, s, string, stringParam, top)


type Route
    = RouteHome
    | RouteLogin
    | RouteLogout
    | RouteUsers
    | RouteTenants
    | RoutePortal
    | RouteUpdates
    | RouteMe
    | NoRoute Job.NoRoute


type alias Routing =
    { current : Route
    }


init : Navigation.Location -> Routing
init location =
    updateLocation
        location
        { current = RouteHome
        }


updateLocation : Navigation.Location -> Routing -> Routing
updateLocation location model =
    let
        maybeRoute =
            Url.parseHash parseRoute location
    in
    case maybeRoute of
        Just route ->
            case route of
                NoRoute _ ->
                    model

                _ ->
                    { model | current = route }

        Nothing ->
            model


gotoRoute : Route -> Routing -> ( Routing, Cmd msg )
gotoRoute route model =
    case route of
        NoRoute _ ->
            ( model, Cmd.none )

        _ ->
            ( { model | current = route }, newUrl route )


currentRoute : Routing -> Route
currentRoute model =
    model.current


parseRoute : Url.Parser (Route -> a) a
parseRoute =
    Url.oneOf
        [ Url.map RouteHome top
        , Url.map RouteLogin (s "login")
        , Url.map RouteLogout (s "logout")
        , Url.map RoutePortal (s "portal")
        , Url.map RouteUsers (s "users")
        , Url.map RouteTenants (s "tenants")
        , Url.map RouteUpdates (s "updates")
        , Url.map RouteMe (s "me")
        ]


routeToString : Route -> String
routeToString route =
    case route of
        RouteHome ->
            ""

        RouteLogin ->
            "login"

        RouteLogout ->
            "logout"

        RoutePortal ->
            "portal"

        RouteUsers ->
            "users"

        RouteMe ->
            "me"


        RouteTenants ->
            "tenants"

        RouteUpdates ->
            "updates"

        NoRoute _ ->
            ""


toUrl : Route -> String
toUrl route =
    case route of
        RouteLogout ->
            "logout.html"

        RoutePortal ->
            "portal.html"

        _ ->
            "#" ++ routeToString route


newUrl : Route -> Cmd msg
newUrl route =
    Navigation.newUrl <| "#" ++ routeToString route
