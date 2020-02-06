module Types.QueryResults exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.QueryResult as QueryResult


{- Generated from org.swiftleap.rules.web.api.model.query.QueryResultsDto -}

type QueryResultsFields 
    = Results (List QueryResult.QueryResult)

type alias QueryResults =
    { results : List QueryResult.QueryResult
    }

init: QueryResults
init = 
    { results = []
    }

decode: JD.Decoder QueryResults
decode = 
    JDP.decode QueryResults
        |> JDP.optional "results" (JD.list QueryResult.decode) []

encode: QueryResults -> JE.Value
encode o = 
    JE.object 
        [ ( "results", o.results |> List.map QueryResult.encode |> JE.list)
        ]


