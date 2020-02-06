module Components.NavBar exposing (..)

import Html exposing (Html)
import Html.Attributes as HA
import Html.Events as HE


button : String -> msg -> Html msg
button label msg =
    Html.button
        [ HE.onClick msg, HA.type_ "button", HA.class "margin-l10 btn navbar-left btn-default navbar-btn" ]
        [ Html.text label ]


primaryButton : String -> msg -> Html msg
primaryButton label msg =
    Html.button
        [ HE.onClick msg, HA.type_ "button", HA.class "margin-l10 btn navbar-left btn-primary navbar-btn" ]
        [ Html.text label ]


defaultNavbar : List (Html msg) -> Html msg
defaultNavbar =
    Html.nav
        [ HA.class "navbar navbar-default" ]


form : List (Html msg) -> Html msg
form =
    Html.div [ HA.class "navbar-form navbar-left margin-l10", HA.attribute "role" "form" ]


input : String -> (String -> msg) -> String -> Html msg
input label msg value =
    Html.div
        [ HA.class "form-group margin-r5" ]
        [ Html.input [ HE.onInput msg, HA.value value, HA.placeholder label, HA.class "form-control" ] []
        ]
