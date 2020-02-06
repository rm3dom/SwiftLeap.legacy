module Alfred.List exposing (..)

import Array


get : Int -> List a -> Maybe a
get index list =
    Array.fromList list |> Array.get index


first : List a -> Maybe a
first list =
    case list of
        [] ->
            Nothing

        [ x ] ->
            Just x

        x :: xs ->
            Just x



{- replace a matching element, if nothing matches to nothing -}


replace : (a -> Bool) -> a -> List a -> List a
replace predicate with list =
    let
        repFunc i =
            if predicate i then
                with
            else
                i
    in
    List.map repFunc list


slice : Int -> Int -> List a -> List a
slice b e l =
    Array.fromList l |> Array.slice b e |> Array.toList



{- Merge an element into a list using a comparator, if nothing matches append it -}


mergeOne : (a -> a -> Bool) -> a -> List a -> List a
mergeOne comparator one list =
    case list of
        [] ->
            [ one ]

        [ x ] ->
            if comparator one x then
                [ one ]
            else
                [ x, one ]

        x :: xs ->
            if comparator one x then
                List.append [ one ] xs
            else
                List.append [ x ] (mergeOne comparator one xs)



{- Merge to lists together using a comparator -}


merge : (a -> a -> Bool) -> List a -> List a -> List a
merge comparator left right =
    case left of
        [] ->
            right

        [ x ] ->
            mergeOne comparator x right

        x :: xs ->
            let
                res =
                    mergeOne comparator x right
            in
            merge comparator xs res
