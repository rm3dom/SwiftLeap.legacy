module View exposing (view)

import Bootstrap.CDN
import Components
import Components.DropDown as DropDown
import Components.Me as Me
import Components.Tenants as Tenants
import Components.Updates as Updates
import Components.Users as Users
import Core as Core
import Html exposing (Html, a, button, div, input, li, nav, option, select, span, text, ul)
import Html.Attributes as HA exposing (attribute, class, href, id, type_, value)
import Html.Events as HE
import Model as Model exposing (Model)
import Model.System as System exposing (System)
import Msg exposing (..)
import Routing as Routing
import Security as Security exposing (Security)
import Toast as Toast


view : Model -> Html Msg
view model =
    let
        route =
            Routing.currentRoute model.routing

        system =
            Core.getSystem model.core

        body =
            if System.isLoggedIn system then
                viewMain model

            else
                viewLogin system model
    in
    div [ class "main" ]
        [ Toast.view system.time model.core.toast
            |> Html.map (CoreMsg << Core.ToastMsg)
        , body
        ]


viewLogin : System -> Model -> Html Msg
viewLogin system model =
    let
        updateText =
            if system.updateInfo.onLatest then
                Html.text ""

            else
                Components.warn [ "New version available: " ++ system.updateInfo.latestVersion ]
    in
    Html.div [ HA.class "login-group" ]
        [ Html.div [ HA.class "login-logo-top" ] []
        , Html.div
            [ HA.class "login-panel" ]
            [ Html.div
                [ HA.class "login-head" ]
                [ Html.span [] [ Html.img [ HA.src (system.flags.resourceUrl ++ "img/ruleslogo.png"), HA.height 26 ] [] ]
                , Html.text (" " ++ system.flags.siteName)
                ]
            , Html.div
                [ HA.class "login-content" ]
                [ Security.view system model.core.security |> Html.map (CoreMsg << Core.SecurityMsg)
                ]
            ]
        , Html.div [ HA.class "login-logo-bottom" ] []
        , Html.div [ HA.class "login-footer" ]
            [ Html.a [ HA.target "company", HA.href system.flags.companyWebSiteUrl ] [ Html.text (system.flags.companyName ++ " - " ++ system.flags.companyWebSiteUrl) ]
            , Html.br [] []
            , Html.text system.updateInfo.currentVersion
            , updateText
            ]
        ]


viewMain : Model -> Html Msg
viewMain model =
    let
        route =
            Routing.currentRoute model.routing

        system =
            Core.getSystem model.core

        body =
            case route of
                Routing.RouteUsers ->
                    Users.view system model.users |> Html.map Msg.UsersMsg

                Routing.RouteTenants ->
                    Tenants.view system model.tenants |> Html.map Msg.TenantsMsg

                Routing.RouteUpdates ->
                    Updates.view system model.updates |> Html.map Msg.UpdatesMsg

                _ ->
                    Me.view system model.me |> Html.map Msg.MeMsg
    in
    div [] [ body ]
