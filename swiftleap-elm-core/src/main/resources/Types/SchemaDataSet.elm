module Types.SchemaDataSet exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.SchemaColumnDef as SchemaColumnDef


{- Generated from org.swiftleap.rules.SchemaDataSet -}

type SchemaDataSetFields 
    = Columns (List SchemaColumnDef.SchemaColumnDef)
    | Name (String)

type alias SchemaDataSet =
    { columns : List SchemaColumnDef.SchemaColumnDef
    , name : String
    }

init: SchemaDataSet
init = 
    { columns = []
    , name = ""
    }

decode: JD.Decoder SchemaDataSet
decode = 
    JDP.decode SchemaDataSet
        |> JDP.optional "columns" (JD.list SchemaColumnDef.decode) []
        |> JDP.optional "name" JD.string ""

encode: SchemaDataSet -> JE.Value
encode o = 
    JE.object 
        [ ( "columns", o.columns |> List.map SchemaColumnDef.encode |> JE.list)
        , ( "name", o.name |> JE.string)
        ]


