module Components.Form exposing (..)

import Components.Event
import Components.Input
import Components.Select
import Html exposing (Attribute, Html)
import Html.Attributes as HA
import Html.Events as HE exposing (onClick, onSubmit)


group : String -> Html msg -> Html msg
group label component =
    Html.div [ HA.class "form-group" ]
        [ Html.label [] [ Html.text label ]
        , component
        ]


check : String -> Html msg -> Html msg
check label component =
    Html.div [ HA.class "form-check" ]
        [ Html.label [ HA.class "form-check-label" ]
            [ component
            , Html.text label
            ]
        ]


input : String -> (String -> msg) -> String -> Html msg
input label msg value =
    Components.Input.input msg value Components.Input.init
        |> group label


passwordInput : String -> (String -> msg) -> String -> Html msg
passwordInput label msg value =
    Components.Input.init
        |> Components.Input.input_ msg value [ HA.type_ "password" ]
        |> group label


inputNoWhites : String -> (String -> msg) -> String -> Html msg
inputNoWhites label msg value =
    Components.Input.init
        |> Components.Input.withFilter (\c -> c /= ' ' && c /= '\t')
        |> Components.Input.input msg value
        |> group label


inputNoWhites_ : String -> (String -> msg) -> String -> List (Html.Attribute msg) -> Html msg
inputNoWhites_ label msg value attrs =
    Components.Input.init
        |> Components.Input.withFilter (\c -> c /= ' ' && c /= '\t')
        |> Components.Input.input_ msg value attrs
        |> group label

readOnlyInput : String -> String -> Html msg
readOnlyInput label value =
    Components.Input.init
        |> Components.Input.readonlyInput value
        |> group label


buttonGroup : List (Html msg) -> Html msg
buttonGroup =
    Html.div [ HA.class "btn-group" ]


textarea_ : String -> (String -> msg) -> String -> List (Attribute msg) -> Html msg
textarea_ label msg value attrs =
    Html.textarea (attrs ++ [ HA.class "form-control input-sm", HE.onInput msg ]) [ Html.text value ]
        |> group label


textarea : String -> (String -> msg) -> String -> Html msg
textarea label msg value =
    textarea_ label msg value []


intInput : String -> (Int -> msg) -> Int -> Html msg
intInput label msg value =
    Components.Input.intInput msg value Components.Input.init
        |> group label


toggleInput : String -> (Bool -> msg) -> Bool -> Html msg
toggleInput label msg value =
    Html.div []
        [ Html.label [ HA.class "toggle-switch toggle-switch-lg" ]
            [ Html.input
                [ HA.type_ "checkbox", HA.checked value, Components.Event.onClickNoBubble (msg (not value)) ]
                []
            , Html.span [ HA.class "slider round" ] []
            ]
        ]
        |> group label


multiIntSelect : String -> (Int -> msg) -> List Int -> List ( label, Int ) -> Html msg
multiIntSelect label msg values options =
    Components.Select.init
        |> Components.Select.multiIntSelect msg values options
        |> group label


intSelect : String -> (Int -> msg) -> Int -> List ( label, Int ) -> Html msg
intSelect label msg value options =
    Components.Select.init
        |> Components.Select.intSelect msg value options
        |> group label


select : String -> (String -> msg) -> String -> List ( label, String ) -> Html msg
select label msg value options =
    Components.Select.init
        |> Components.Select.select msg value options
        |> group label


form : msg -> List (Html msg) -> Html msg
form formMsg elems =
    form_ formMsg [] elems


form_ : msg -> List (Attribute msg) -> List (Html msg) -> Html msg
form_ formMsg attrs elems =
    let
        newAttrs =
            List.concat [ [ onSubmit formMsg, HA.action "javascript:void(0);" ], attrs ]
    in
    Html.form newAttrs elems


submitButton : String -> Html msg
submitButton labelText =
    Html.button [ HA.type_ "submit", HA.class "btn btn-primary" ] [ Html.text labelText ]


primaryButton : msg -> String -> Html msg
primaryButton msg labelText =
    button_ msg labelText [ HA.class "btn btn-primary" ]


primaryButtonLink : String -> String -> Html msg
primaryButtonLink link labelText =
    Html.a [ HA.href link, HA.type_ "submit", HA.class "btn btn-primary" ] [ Html.text labelText ]


button : msg -> String -> Html msg
button msg labelText =
    button_ msg labelText [ HA.class "btn btn-default" ]


button_ : msg -> String -> List (Attribute msg) -> Html msg
button_ msg label attrs =
    let
        inputAttrs =
            List.append [ HA.type_ "button", HE.onClick msg ] attrs
    in
    Html.button inputAttrs [ Html.text label ]
