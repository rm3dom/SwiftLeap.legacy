module Components.Help exposing (..)

import Components
import Html exposing (Html)
import Html.Attributes as HA
import Model.System exposing (System)


helpUrl : String -> String -> Html msg
helpUrl =
    helpUrlStyle []


helpUrlStyle : List ( String, String ) -> String -> String -> Html msg
helpUrlStyle style title url =
    Html.a
        [ HA.href url
        , HA.target "help"
        , HA.class "help-link"
        , HA.style style
        ]
        [ Html.text title
        ]


helpLink : String -> System -> Html msg
helpLink =
    helpLinkStyle []


helpLinkStyle : List ( String, String ) -> String -> System -> Html msg
helpLinkStyle style context system =
    let
        link =
            system.helpUrl ++ context ++ system.helpSuffix

        text =
            "Help " ++ context
    in
    Html.a
        [ HA.href link
        , HA.target "help"
        , HA.class "help-link"
        , HA.style style
        ]
        [ Components.fontAwesome "fa-question-circle"
        , Html.text text
        ]
