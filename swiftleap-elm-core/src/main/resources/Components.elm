module Components exposing (..)

{-| NOTE: This file is deprecated, use Components.* instead
-}

import Alfred
import Components.Event as Event
import Components.Form as Form
import Components.Input as Input exposing (Input)
import Components.Layout as Layout
import Components.Select as Select
import Html exposing (Attribute, Html)
import Html.Attributes as HA
import Html.Events as HE exposing (onClick, onSubmit)


onChange : (String -> msg) -> Html.Attribute msg
onChange =
    Event.onChange


noBubble : HE.Options
noBubble =
    Event.noBubble


onClickNoBubble : msg -> Html.Attribute msg
onClickNoBubble =
    Event.onClickNoBubble


mainPanel : List (Attribute msg) -> List (Html msg) -> Html msg
mainPanel attrs =
    Html.div (List.append [ HA.class "main-panel" ] attrs)


mainPanelMenu : List (Attribute msg) -> List (Html msg) -> Html msg
mainPanelMenu attrs htmlBody =
    let
        favs =
            []
    in
    Html.div (List.append [ HA.class "main-panel-menu panel panel-default" ] attrs)
        [ Html.div [ HA.class "panel-body" ]
            (List.append htmlBody favs)
        ]


mainPanelContent : List (Attribute msg) -> List (Html msg) -> Html msg
mainPanelContent attrs =
    Html.div (List.append [ HA.class "main-panel-content container" ] attrs)


navButton : List (Attribute msg) -> List (Html msg) -> Html msg
navButton attrs body =
    let
        inputAttrs =
            List.append [ HA.class "btn btn-default" ] attrs
    in
    Html.button inputAttrs body


row : List (Html msg) -> Html msg
row =
    Layout.row


col : String -> List (Html msg) -> Html msg
col =
    Layout.col


h2 : String -> Html msg
h2 label =
    Html.h2 [] [ Html.text label ]


hblock : List (Html msg) -> Html msg
hblock =
    Layout.divPadH15


block : List (Html msg) -> Html msg
block =
    Layout.divPad15


check : (Bool -> msg) -> Bool -> Html msg
check msg value =
    Html.input [ HA.type_ "checkbox", HA.class "form-check-input", HA.checked value, HE.onCheck msg ] []


toggleCheck : msg -> Bool -> Html msg
toggleCheck msg value =
    Html.label [ HA.class "toggle-switch" ]
        [ Html.input
            [ HA.type_ "checkbox", HA.checked value, onClickNoBubble msg ]
            []
        , Html.span [ HA.class "slider round" ] []
        ]


toggleLarge : (Bool -> msg) -> Bool -> Html msg
toggleLarge msg value =
    Html.span []
        [ Html.label [ HA.class "toggle-switch toggle-switch-lg" ]
            [ Html.input
                [ HA.type_ "checkbox", HA.checked value, HE.onClick (msg (not value)) ]
                []
            , Html.span [ HA.class "slider round" ] []
            ]
        ]


input : (String -> msg) -> any -> Html msg
input msg value =
    Input.init
        |> Input.input msg (Alfred.toStr value)


intInput : (Int -> msg) -> any -> Html msg
intInput msg value =
    Input.intInput msg value Input.init


readonlyInput : any -> Html msg
readonlyInput value =
    Input.init
        |> Input.readonlyInput (Alfred.toStr value)


select : (String -> msg) -> List ( String, String ) -> String -> Html msg
select msg options rawSelectedValue =
    Select.init
        |> Select.select msg rawSelectedValue options


intSelect : (Int -> msg) -> List ( String, Int ) -> Int -> Html msg
intSelect msg options rawSelectedValue =
    Select.init
        |> Select.intSelect msg rawSelectedValue options


passwordInput : (String -> msg) -> any -> Html msg
passwordInput msg value =
    Input.init
        |> Input.input_ msg (Alfred.toStr value) [ HA.type_ "password" ]


formInput : (String -> msg) -> String -> any -> Html msg
formInput msg labelText value =
    input msg value
        |> Form.group labelText


formPasswordInput : (String -> msg) -> String -> any -> Html msg
formPasswordInput msg labelText value =
    passwordInput msg value
        |> Form.group labelText


formReadonlyInput : String -> String -> Html msg
formReadonlyInput labelText value =
    readonlyInput value
        |> Form.group labelText


formCheck : (Bool -> msg) -> String -> Bool -> Html msg
formCheck msg labelText value =
    check msg value
        |> Form.check labelText


iconButton : msg -> String -> String -> Html msg
iconButton msg icon title =
    iconButton_ msg icon title []


iconButton_ : msg -> String -> String -> List (Attribute msg) -> Html msg
iconButton_ msg icon title attrs =
    Html.a (attrs ++ [ HA.title title, HA.class "link-button", onClickNoBubble msg ]) [ fontAwesome icon ]


linkButton : msg -> String -> String -> Html msg
linkButton msg icon title =
    Html.a [ HA.class "link-button", onClickNoBubble msg ] [ fontAwesome icon, Html.text (" " ++ title) ]


linkRButton : msg -> String -> String -> Html msg
linkRButton msg icon title =
    Html.a [ HA.class "link-button", onClickNoBubble msg ] [ Html.text (" " ++ title), fontAwesome icon ]


button : msg -> String -> Html msg
button =
    Form.button


submitButton : String -> Html msg
submitButton =
    Form.submitButton


button_ : msg -> String -> List (Attribute msg) -> Html msg
button_ =
    Form.button_


userImage : String -> String -> Html msg
userImage mime data =
    if String.length data < 1 then
        Html.img [ HA.class "user-image-sm", HA.attribute "width" "183px" ] []
    else
        Html.img [ HA.class "user-image-sm", HA.attribute "width" "183px", HA.src ("data:" ++ mime ++ ";base64," ++ data) ] []


form : msg -> List (Html msg) -> Html msg
form =
    Form.form


form_ : msg -> List (Attribute msg) -> List (Html msg) -> Html msg
form_ =
    Form.form_


fontAwesome : String -> Html msg
fontAwesome font =
    let
        cls =
            "fa " ++ font

        newAttrs =
            [ HA.class cls, HA.attribute "aria-hidden" "true" ]
    in
    Html.i newAttrs []


fontAwesomeAttr : String -> List (Attribute msg) -> Html msg
fontAwesomeAttr font attrs =
    let
        cls =
            "fa " ++ font

        newAttrs =
            List.concat [ [ HA.class cls, HA.attribute "aria-hidden" "true" ], attrs ]
    in
    Html.i newAttrs []


script : String -> Html msg
script code =
    Html.node "script" [ HA.type_ "text/javascript" ] [ Html.text code ]


alert : String -> List String -> Html msg
alert class messages =
    if List.length messages < 1 then
        Html.text ""
    else if List.length messages == 1 then
        Html.div [ HA.class ("alert " ++ class) ] (List.map (\message -> Html.text message) messages)
    else
        Html.div [ HA.class ("alert " ++ class) ]
            [ Html.ul [] (List.map (\message -> Html.li [] [ Html.text message ]) messages)
            ]


success : List String -> Html msg
success messages =
    alert "alert-success" messages


info : List String -> Html msg
info messages =
    alert "alert-info" messages


warn : List String -> Html msg
warn messages =
    alert "alert-warning" messages


error : List String -> Html msg
error messages =
    alert "alert-danger" messages
