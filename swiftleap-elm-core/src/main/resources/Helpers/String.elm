module Helpers.String exposing (..)

import Alfred.List


toStr : a -> String
toStr value =
    let
        dropStartingQuote str =
            if String.left 1 str == "\"" || String.left 1 str == "'" then
                String.dropLeft 1 str
            else
                str
    in
    value
        |> toString
        |> dropStartingQuote
        |> String.reverse
        |> dropStartingQuote
        |> String.reverse
        |> String.split "\\\""
        |> String.join "\""


maybeToString : Maybe a -> String
maybeToString m =
    m |> Maybe.map toString |> Maybe.withDefault ""


toDefInt : String -> Int -> Int
toDefInt str def =
    case String.toInt str of
        Ok i ->
            i

        Err _ ->
            def


toMaybeInt : String -> Maybe Int
toMaybeInt str =
    case String.toInt str of
        Ok i ->
            Just i

        Err _ ->
            Nothing



{- Avoid long lines of text and trim them short with a "..." at the end. -}


trimLineWithBreaks : Int -> String -> String
trimLineWithBreaks maxLen val =
    let
        str =
            if String.length val > maxLen then
                String.slice 0 maxLen val ++ " ..."
            else
                val
    in
    forceWordBreaks str



{-
   Break into words with a maximum word size of 50.
   Longest word in english dict is pneumonoultramicroscopicsilicovolcanoconiosis (45).
-}


forceWordBreaks : String -> String
forceWordBreaks =
    forceWordBreaksSize 50



{-
   Break into words with a maximum word size of wordSize.
   Longest word in english dict is pneumonoultramicroscopicsilicovolcanoconiosis (45).
-}


forceWordBreaksSize : Int -> String -> String
forceWordBreaksSize wordSize val =
    let
        len =
            String.length val

        found =
            val |> String.indexes " " |> Alfred.List.first |> Maybe.withDefault wordSize

        endPos =
            min wordSize (found + 1)
    in
    if len <= 0 then
        val
    else
        String.slice 0 endPos val ++ " " ++ forceWordBreaksSize wordSize (String.slice endPos len val)
