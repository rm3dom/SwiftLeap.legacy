module Types.QueryRow exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.query.QueryRowDto -}

type QueryRowFields 
    = Values ((Dict String String))

type alias QueryRow =
    { values : (Dict String String)
    }

init: QueryRow
init = 
    { values = Dict.empty
    }

decode: JD.Decoder QueryRow
decode = 
    JDP.decode QueryRow
        |> JDP.optional "values" (JD.dict JD.string) Dict.empty

encode: QueryRow -> JE.Value
encode o = 
    JE.object 
        [ ( "values", o.values |> dictEncoder)
        ]


