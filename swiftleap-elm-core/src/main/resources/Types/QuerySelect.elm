module Types.QuerySelect exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.query.QuerySelectDto -}

type QuerySelectFields 
    = DataSetName (String)
    | Alias (String)
    | ColumnName (String)

type alias QuerySelect =
    { dataSetName : String
    , alias : String
    , columnName : String
    }

init: QuerySelect
init = 
    { dataSetName = ""
    , alias = ""
    , columnName = ""
    }

decode: JD.Decoder QuerySelect
decode = 
    JDP.decode QuerySelect
        |> JDP.optional "dataSetName" JD.string ""
        |> JDP.optional "alias" JD.string ""
        |> JDP.optional "columnName" JD.string ""

encode: QuerySelect -> JE.Value
encode o = 
    JE.object 
        [ ( "dataSetName", o.dataSetName |> JE.string)
        , ( "alias", o.alias |> JE.string)
        , ( "columnName", o.columnName |> JE.string)
        ]


