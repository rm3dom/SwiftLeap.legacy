module Types.QueryDataSet exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.QueryRow as QueryRow


{- Generated from org.swiftleap.rules.web.api.model.query.QueryDataSetDto -}

type QueryDataSetFields 
    = Name (String)
    | Rows (List QueryRow.QueryRow)

type alias QueryDataSet =
    { name : String
    , rows : List QueryRow.QueryRow
    }

init: QueryDataSet
init = 
    { name = ""
    , rows = []
    }

decode: JD.Decoder QueryDataSet
decode = 
    JDP.decode QueryDataSet
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "rows" (JD.list QueryRow.decode) []

encode: QueryDataSet -> JE.Value
encode o = 
    JE.object 
        [ ( "name", o.name |> JE.string)
        , ( "rows", o.rows |> List.map QueryRow.encode |> JE.list)
        ]


