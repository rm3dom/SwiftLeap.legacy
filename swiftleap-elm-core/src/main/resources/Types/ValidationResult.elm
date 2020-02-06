module Types.ValidationResult exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.ValidationResultDto -}

type ValidationResultFields 
    = Errors (List String)

type alias ValidationResult =
    { errors : List String
    }

init: ValidationResult
init = 
    { errors = []
    }

decode: JD.Decoder ValidationResult
decode = 
    JDP.decode ValidationResult
        |> JDP.optional "errors" (JD.list JD.string) []

encode: ValidationResult -> JE.Value
encode o = 
    JE.object 
        [ ( "errors", o.errors |> List.map JE.string |> JE.list)
        ]


