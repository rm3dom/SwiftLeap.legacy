module Alfred.Validate
    exposing
        ( all
        , first
        , isBlank
        , isBlankOr
        , isDateTime
        , isEnumBlank
        , isInRange
        , isInvalidDateTime
        , isInvalidMaybeDateTime
        , isJustAnd
        , isNothingOr
        , isRegexMatch
        , notInRange
        )

{-| Alfred validates your sense of self worth... amongst other things.
Here we do client side form validation on specific pieces of data
and then accumulate them into a list of errors to be displayed.
-}

import Alfred.Dates
import Alfred.Logic
import Maybe.Extra
import Regex
import Time.DateTime exposing (DateTime)


all : List ( Bool, String ) -> List String
all list =
    List.foldl (\( pred, err ) errs -> Alfred.Logic.when pred ((::) err) errs) [] list
        |> List.reverse


first : List ( Bool, String ) -> List String
first validations =
    case validations of
        [] ->
            []

        ( True, err ) :: _ ->
            [ err ]

        ( False, _ ) :: rest ->
            first rest


isJustAnd : (a -> Bool) -> Maybe a -> Bool
isJustAnd f maybeX =
    Maybe.Extra.unwrap False f maybeX


isNothingOr : (a -> Bool) -> Maybe a -> Bool
isNothingOr f maybeX =
    Maybe.Extra.unwrap True f maybeX


isBlank : String -> Bool
isBlank =
    String.trim >> String.isEmpty


isEnumBlank : Int -> Bool
isEnumBlank =
    (==) 0


isBlankOr : (String -> Bool) -> String -> Bool
isBlankOr pred value =
    pred value || isBlank value


isRegexMatch : String -> String -> Bool
isRegexMatch pattern match =
    Regex.regex pattern |> flip Regex.contains match


isInRange : Int -> Int -> Int -> Bool
isInRange minVal maxVal val =
    val >= minVal && val <= maxVal


notInRange : Int -> Int -> Int -> Bool
notInRange min max =
    isInRange min max >> not


isDateTime : DateTime -> Bool
isDateTime dt =
    not (Alfred.Dates.isBeginningOfTime dt)


isInvalidDateTime : DateTime -> Bool
isInvalidDateTime datetime =
    Alfred.Dates.isBeginningOfTime datetime || Alfred.Dates.isEndOfTime datetime


isInvalidMaybeDateTime : Maybe DateTime -> Bool
isInvalidMaybeDateTime datetime =
    Maybe.Extra.unwrap True isInvalidDateTime datetime
