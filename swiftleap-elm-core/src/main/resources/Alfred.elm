module Alfred exposing (..)

import Helpers.String


toStrWithDefault : String -> Maybe a -> String
toStrWithDefault default =
    Maybe.map toStr >> Maybe.withDefault default


toStr : a -> String
toStr =
    Helpers.String.toStr


toIntWithDefault : Int -> String -> Int
toIntWithDefault default =
    String.toInt >> Result.withDefault default



----------------------------------------------------------------
-- Alfred does masks; because it formats stuff O^O --
----------------------------------------------------------------


maskLength : String -> Int
maskLength mask =
    String.length (String.filter ((==) '#') mask)


maskChars : String -> String
maskChars mask =
    String.filter ((/=) '#') mask


maskString : String -> String -> String
maskString mask value =
    let
        str =
            unmaskString mask value
    in
    maskString_ (String.toList str) (String.toList mask)
        |> String.fromList


maskString_ : List Char -> List Char -> List Char
maskString_ value mask =
    case ( value, mask ) of
        ( [], [] ) ->
            []

        ( rest, [] ) ->
            rest

        ( [], _ ) ->
            value

        ( headValue :: restValue, headMask :: restMask ) ->
            if headMask == '#' then
                headValue :: maskString_ restValue restMask
            else
                headMask :: maskString_ value restMask


unmaskString : String -> String -> String
unmaskString mask value =
    String.foldl (\maskChar str -> String.filter ((/=) maskChar) str) value (maskChars mask)


maskToPlaceholder : String -> String
maskToPlaceholder mask =
    String.map
        (\char ->
            if char == '#' then
                '_'
            else
                char
        )
        mask
