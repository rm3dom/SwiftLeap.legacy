module Types.SchemaColumnDef exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.SchemaColumnDef -}

type SchemaColumnDefFields 
    = Name (String)
    | Description (String)
    | Type (String)

type alias SchemaColumnDef =
    { name : String
    , description : String
    , type_ : String
    }

init: SchemaColumnDef
init = 
    { name = ""
    , description = ""
    , type_ = "NULL"
    }

decode: JD.Decoder SchemaColumnDef
decode = 
    JDP.decode SchemaColumnDef
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "type" JD.string "NULL"

encode: SchemaColumnDef -> JE.Value
encode o = 
    JE.object 
        [ ( "name", o.name |> JE.string)
        , ( "description", o.description |> JE.string)
        , ( "type", o.type_ |> JE.string)
        ]


