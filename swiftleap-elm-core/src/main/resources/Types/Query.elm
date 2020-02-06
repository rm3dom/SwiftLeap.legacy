module Types.Query exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.QuerySelect as QuerySelect
import Types.QueryDataSet as QueryDataSet


{- Generated from org.swiftleap.rules.web.api.model.query.QueryDto -}

type QueryFields 
    = Select (List QuerySelect.QuerySelect)
    | DataSets (List QueryDataSet.QueryDataSet)

type alias Query =
    { select : List QuerySelect.QuerySelect
    , dataSets : List QueryDataSet.QueryDataSet
    }

init: Query
init = 
    { select = []
    , dataSets = []
    }

decode: JD.Decoder Query
decode = 
    JDP.decode Query
        |> JDP.optional "select" (JD.list QuerySelect.decode) []
        |> JDP.optional "dataSets" (JD.list QueryDataSet.decode) []

encode: Query -> JE.Value
encode o = 
    JE.object 
        [ ( "select", o.select |> List.map QuerySelect.encode |> JE.list)
        , ( "dataSets", o.dataSets |> List.map QueryDataSet.encode |> JE.list)
        ]


