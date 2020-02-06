module Components.DropDown
    exposing
        ( DropDown
        , add
        , addAll
        , addAllHtml
        , addHtml
        , addLabelSeparator
        , addSeparator
        , init
        , renderAnchor
        , renderButton
        , renderNav
        , withIcon
        , withTitle
        , addAllTitle
        )

import Components exposing (fontAwesome)
import Components.Event
import Html exposing (Attribute, Html, a, button, div, i, li, span, text, ul)
import Html.Attributes as Attr exposing (action, attribute, class, href, type_)
import Html.Events exposing (onSubmit, onClick)


type alias DropDown msg =
    { drops : List (Html msg)
    , title : String
    , icon : String
    }


init : DropDown msg
init =
    { drops = []
    , title = ""
    , icon = "fa-bars"
    }

withTitle : String -> DropDown msg -> DropDown msg
withTitle title drop =
    { drop | title = title }


withIcon : String -> DropDown msg -> DropDown msg
withIcon icon drop =
    { drop | icon = icon }


addHtml : Html msg -> DropDown msg -> DropDown msg
addHtml html drop =
    { drop | drops = drop.drops ++ [ html ] }


addLabelSeparator : String -> DropDown msg -> DropDown msg
addLabelSeparator string drop =
    let
        html =
            Html.b [ attribute "role" "separator", Attr.style [ ( "margin-left", "10px" ) ] ] [ Html.text string ]
    in
    { drop | drops = drop.drops ++ [ html ] }


addSeparator : DropDown msg -> DropDown msg
addSeparator drop =
    let
        html =
            li [ attribute "role" "separator", class "divider" ] []
    in
    { drop | drops = drop.drops ++ [ html ] }


add : String -> String -> msg -> DropDown msg -> DropDown msg
add dropText icon msg drop =
    let
        html =
            a [ href "javascript:;", class "dropdown-item", onClick msg ] [ fontAwesome icon, text " ", text dropText ]
    in
    { drop | drops = drop.drops ++ [ html ] }


addTitle : String -> String -> String -> msg -> DropDown msg -> DropDown msg
addTitle dropText title icon msg drop =
    let
        html =
            a [ Attr.title title , href "javascript:;", class "dropdown-item", onClick msg ] [ fontAwesome icon, text " ", text dropText ]
    in
    { drop | drops = drop.drops ++ [ html ] }


addAll : List ( String, msg ) -> DropDown msg -> DropDown msg
addAll list drop =
    List.foldl (\( s, m ) d -> add s "" m d) drop list


addAllTitle : List ( String, String, msg ) -> DropDown msg -> DropDown msg
addAllTitle list drop =
    List.foldl (\( s, t, m ) d -> addTitle s t "" m d) drop list


addAllHtml : List ( Html msg, msg ) -> DropDown msg -> DropDown msg
addAllHtml list drop =
    let
        withAnchor html msg =
            a [ href "javascript:;", class "dropdown-item", onClick msg ] [ html ]
    in
    List.foldl (\( s, m ) d -> addHtml (withAnchor s m) d) drop list


renderNav : DropDown msg -> Html msg
renderNav drop =
    li [ class "dropdown" ]
        [ anchorBtn drop
        , ul [ class "dropdown-menu" ]
            (List.map
                (\body -> li [] [ body ])
                drop.drops
            )
        ]


renderAnchor : DropDown msg -> Html msg
renderAnchor drop =
    let
        btn =
            anchorBtn drop
    in
    renderWithBtn btn drop


renderButton : DropDown msg -> Html msg
renderButton drop =
    let
        btn =
            Html.button
                [ class "btn btn-default btn-sm dropdown-toggle"
                , type_ "button"
                , attribute "data-toggle" "dropdown"
                , attribute "aria-haspopup" "true"
                , attribute "aria-expanded" "false"
                ]
                [ text drop.title
                , text " "
                , fontAwesome drop.icon
                ]
    in
    renderWithBtn btn drop


renderWithBtn : Html msg -> DropDown msg -> Html msg
renderWithBtn btn drop =
    span [ class "dropdown" ]
        [ btn
        , ul [ class "dropdown-menu", Attr.style [ ( "max-height", "40vh" ), ( "overflow", "auto" ) ] ]
            (List.map
                (\body -> li [] [ body ])
                drop.drops
            )
        ]


anchorBtn : DropDown msg -> Html msg
anchorBtn drop =
    a
        [ href "#"
        , class "dropdown-toggle"
        , attribute "data-toggle" "dropdown"
        , attribute "role" "button"
        , attribute "aria-haspopup" "true"
        , attribute "aria-expanded" "false"
        ]
        [ text drop.title
        , text " "
        , fontAwesome drop.icon
        ]
