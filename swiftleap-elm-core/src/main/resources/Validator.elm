module Validator exposing (..)

import Alfred


all : List ( Bool, String ) -> List String
all list =
    let
        errors =
            List.foldl
                (\( b, s ) l ->
                    if b then
                        s :: l
                    else
                        l
                )
                []
                list
    in
    List.reverse errors


empty : any -> String -> ( Bool, String )
empty a s =
    ( String.length (Alfred.toStr a) == 0, s )


listEmpty : List any -> String -> ( Bool, String )
listEmpty a s =
    ( List.isEmpty a, s )
