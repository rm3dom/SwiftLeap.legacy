module Types.Schema exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.SchemaDataSet as SchemaDataSet


{- Generated from org.swiftleap.rules.Schema -}

type SchemaFields 
    = Name (String)
    | DataSets (List SchemaDataSet.SchemaDataSet)
    | Validate (Bool)

type alias Schema =
    { name : String
    , dataSets : List SchemaDataSet.SchemaDataSet
    , validate : Bool
    }

init: Schema
init = 
    { name = ""
    , dataSets = []
    , validate = False
    }

decode: JD.Decoder Schema
decode = 
    JDP.decode Schema
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "dataSets" (JD.list SchemaDataSet.decode) []
        |> JDP.optional "validate" JD.bool False

encode: Schema -> JE.Value
encode o = 
    JE.object 
        [ ( "name", o.name |> JE.string)
        , ( "dataSets", o.dataSets |> List.map SchemaDataSet.encode |> JE.list)
        , ( "validate", o.validate |> JE.bool)
        ]


