module Components.Input
    exposing
        ( Input
        , init
        , input
        , input_
        , intInput
        , onChangeInput
        , onChangeInput_
        , readonlyInput
        , withFilter
        , withLeft
        , withMask
        , withPlaceHolder
        , withRegexFilter
        , withRequired
        , withRight
        , withStringFilter
        , withValidator
        )

import Alfred
import Components.Event
import Html exposing (Html)
import Html.Attributes as HA
import Html.Events as HE
import Regex exposing (Regex)


type alias Input msg =
    { validator : String -> Bool
    , allow : Maybe (Char -> Bool)
    , required : Bool
    , mask : String
    , unmask : Bool
    , placeHolder : String
    , left : Maybe (Html msg)
    , right : Maybe (Html msg)
    }


init : Input msg
init =
    { validator = \_ -> True
    , allow = Nothing
    , required = False
    , mask = ""
    , unmask = True
    , placeHolder = ""
    , left = Nothing
    , right = Nothing
    }


withValidator : (String -> Bool) -> Input msg -> Input msg
withValidator validator options =
    { options | validator = validator }


withLeft : Html msg -> Input msg -> Input msg
withLeft html options =
    { options | left = Just html }


withRight : Html msg -> Input msg -> Input msg
withRight html options =
    { options | right = Just html }


withRequired : Bool -> Input msg -> Input msg
withRequired required options =
    { options | required = required }


withPlaceHolder : String -> Input msg -> Input msg
withPlaceHolder placeHolder options =
    { options | placeHolder = placeHolder }


withStringFilter : String -> Input msg -> Input msg
withStringFilter string options =
    { options | allow = Just (\c1 -> String.any ((==) c1) string) }


withFilter : (Char -> Bool) -> Input msg -> Input msg
withFilter filter options =
    { options | allow = Just filter }


{-| Allowed characters, not the whole string.
-}
withRegexFilter : String -> Input msg -> Input msg
withRegexFilter regex options =
    let
        exp =
            Regex.regex regex
    in
    { options | allow = Just (\c -> Regex.contains exp (Alfred.toStr c)) }


{-| The mask string in form (#_[^#]_)*.

If unmask is true then the mask chars will be removed on msg.

-}
withMask : String -> Bool -> Input msg -> Input msg
withMask mask unmask options =
    let
        length =
            Alfred.maskLength mask

        validator value =
            if String.length value > 0 && String.length value < length then
                False
            else
                True
    in
    { options | mask = mask, unmask = unmask, validator = validator }


readonlyInput : String -> Input msg -> Html msg
readonlyInput value options =
    let
        maskedValue =
            Alfred.maskString options.mask value

        attribs =
            [ HA.value maskedValue, HA.readonly True, HA.disabled True ]
    in
    render "is-readonly" attribs options


onChangeInput : (String -> msg) -> String -> Input msg -> Html msg
onChangeInput msg value options =
    base_ Nothing (Just msg) value [] options


onChangeInput_ : (String -> msg) -> String -> List (Html.Attribute msg) -> Input msg -> Html msg
onChangeInput_ msg value attributes options =
    base_ Nothing (Just msg) value attributes options


intInput : (Int -> msg) -> any -> Input msg -> Html msg
intInput msg value options =
    let
        msgToInt string =
            msg (Alfred.toIntWithDefault 0 string)
    in
    options
        |> input_ msgToInt (Alfred.toStr value) [ HA.type_ "number" ]


input : (String -> msg) -> String -> Input msg -> Html msg
input msg value options =
    base_ (Just msg) Nothing value [] options


input_ : (String -> msg) -> String -> List (Html.Attribute msg) -> Input msg -> Html msg
input_ msg value attributes options =
    base_ (Just msg) Nothing value attributes options


base_ : Maybe (String -> msg) -> Maybe (String -> msg) -> String -> List (Html.Attribute msg) -> Input msg -> Html msg
base_ onInput onChange value attributes options =
    let
        masked =
            not (String.isEmpty options.mask)

        maskedValue =
            Alfred.maskString options.mask value

        onInputFilter msg str =
            let
                newValue =
                    options.allow
                        |> Maybe.map (\allow -> String.filter allow str)
                        |> Maybe.withDefault str
            in
            if options.unmask then
                msg (Alfred.unmaskString options.mask newValue)
            else
                msg (Alfred.maskString options.mask newValue)

        inputAttribute =
            case ( onInput, onChange ) of
                ( Just msg, Nothing ) ->
                    HE.onInput (onInputFilter msg)

                ( Nothing, Just msg ) ->
                    Components.Event.onChangeNoBubble (onInputFilter msg)

                ( _, _ ) ->
                    HA.name "error"

        class =
            --bypass the validator when empty
            if not options.required && String.isEmpty value then
                "is-valid"
            else if options.required && String.isEmpty value then
                "is-invalid"
            else if options.validator value then
                "is-valid"
            else
                "is-invalid"

        placeHolder =
            if String.length options.placeHolder > 0 then
                options.placeHolder
            else if masked then
                Alfred.maskToPlaceholder options.mask
            else
                ""

        attribs =
            if masked then
                [ HA.value maskedValue
                , inputAttribute
                , HA.placeholder placeHolder
                , HA.maxlength (String.length options.mask)
                ]
            else
                [ HA.value value, inputAttribute, HA.placeholder placeHolder ]
    in
    render class (attribs ++ attributes) options


render : String -> List (Html.Attribute msg) -> Input msg -> Html msg
render class attributes options =
    let
        left =
            options.left
                |> Maybe.map (\content -> Html.span [ HA.class "input-group-addon" ] [ content ])
                |> Maybe.withDefault (Html.text "")

        right =
            options.right
                |> Maybe.map (\content -> Html.span [ HA.class "input-group-addon" ] [ content ])
                |> Maybe.withDefault (Html.text "")
    in
    Html.div
        [ HA.class ("input-group " ++ class) ]
        [ left
        , Html.input (HA.class "form-control input-sm" :: attributes) []
        , right
        ]
