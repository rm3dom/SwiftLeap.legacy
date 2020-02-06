module Alfred.Logic exposing (..)

{-| Alfred does logic. Specifically, these should not be dependant on any types.
-}


{-| when the predicate is true, transform the data
-}
when : Bool -> (a -> a) -> a -> a
when predicate f x =
    if predicate then
        f x
    else
        x

orElse : Bool -> a -> a -> a
orElse predicate a b =
    if predicate then
        a
    else
        b

unless : Bool -> (a -> a) -> a -> a
unless predicate =
    when (not predicate)


{-| Adds element to list if it's not nothing
-}
maybeAddToList : Maybe a -> List a -> List a
maybeAddToList couldBe list =
    case couldBe of
        Just is ->
            is :: list

        _ ->
            list


appendWhen : Bool -> a -> List a -> List a
appendWhen pred newItem list =
    if pred then
        list ++ [ newItem ]
    else
        list


appendMap : (a -> b) -> Maybe a -> List b -> List b
appendMap f maybeA list =
    case maybeA of
        Just value ->
            list ++ [ f value ]

        Nothing ->
            list


prependWhen : Bool -> a -> List a -> List a
prependWhen pred newItem list =
    if pred then
        newItem :: list
    else
        list


combinePredicates : List (a -> Bool) -> (a -> Bool)
combinePredicates allPreds =
    \a -> List.all (\pred -> pred a) allPreds


whenMap : (a -> b -> b) -> Maybe a -> b -> b
whenMap apply maybeA acc =
    case maybeA of
        Just a ->
            apply a acc

        Nothing ->
            acc
