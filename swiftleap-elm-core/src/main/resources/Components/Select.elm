module Components.Select
    exposing
        ( init
        , intSelect
        , multiIntSelect
        , multiSelect
        , select
        , select_
        , withLeft
        , withMulti
        , withRequired
        , withRight
        )

import Alfred
import Html exposing (Html)
import Html.Attributes as HA
import Html.Events as HE
import Logic


type alias Select msg =
    { left : Maybe (Html msg)
    , right : Maybe (Html msg)
    , required : Bool
    , multi : Bool
    }


init : Select msg
init =
    { left = Nothing
    , right = Nothing
    , required = False
    , multi = False
    }


withLeft : Html msg -> Select msg -> Select msg
withLeft html options =
    { options | left = Just html }


withRight : Html msg -> Select msg -> Select msg
withRight html options =
    { options | right = Just html }


withRequired : Bool -> Select msg -> Select msg
withRequired required options =
    { options | required = required }


withMulti : Bool -> Select msg -> Select msg
withMulti multi options =
    { options | multi = multi }


select_ : (String -> msg) -> List value -> List ( label, value ) -> List (Html.Attribute msg) -> Select msg -> Html msg
select_ msg selection list attributes model =
    let
        strSelection =
            List.map Alfred.toStr selection

        strList =
            List.map (\( k, v ) -> ( Alfred.toStr k, Alfred.toStr v )) list

        selected v =
            List.any (\value -> value == v) strSelection

        option ( k, v ) =
            let
                isSelected =
                    selected v

                classes =
                    if isSelected then
                        "selected-input"
                    else
                        ""
            in
            Html.option [ HA.class classes, HA.selected isSelected, HA.value v ] [ Html.text k ]

        ( options, class ) =
            if model.multi || List.any (\( k, v ) -> selected v) strList then
                ( List.map option strList, "is-valid" )
            else
                ( option ( "Please select", "" ) :: List.map option strList
                , Logic.orElse model.required "is-invalid" "is-valid"
                )

        left =
            model.left
                |> Maybe.map (\content -> Html.span [ HA.class "input-group-addon" ] [ content ])
                |> Maybe.withDefault (Html.text "")

        right =
            model.right
                |> Maybe.map (\content -> Html.span [ HA.class "input-group-addon" ] [ content ])
                |> Maybe.withDefault (Html.text "")

        attribs =
            [ HA.class "form-control", HE.onInput msg ]
                |> Logic.when model.multi ((++) [ HA.attribute "multiple" "multiple" ])
                |> (++) attributes
    in
    Html.div
        [ HA.class ("input-group " ++ class) ]
        [ left
        , Html.select attribs options
        , right
        ]


multiIntSelect : (Int -> msg) -> List Int -> List ( label, Int ) -> Select msg -> Html msg
multiIntSelect msg values options model =
    model
        |> withMulti True
        |> select_ (String.toInt >> Result.withDefault 0 >> msg) values options []


intSelect : (Int -> msg) -> Int -> List ( label, Int ) -> Select msg -> Html msg
intSelect msg value options model =
    select_ (String.toInt >> Result.withDefault 0 >> msg) [ value ] options [] model


multiSelect : (String -> msg) -> List String -> List ( label, String ) -> Select msg -> Html msg
multiSelect msg values options model =
    model
        |> withMulti True
        |> select_ msg values options []


select : (String -> msg) -> String -> List ( label, String ) -> Select msg -> Html msg
select msg value options model =
    select_ msg [ value ] options [] model
