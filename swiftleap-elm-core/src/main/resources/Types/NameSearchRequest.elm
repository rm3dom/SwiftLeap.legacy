module Types.NameSearchRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.types.NameSearchRequest -}

type NameSearchRequestFields 
    = MaxResults (Int)
    | Name (String)
    | Start (Int)

type alias NameSearchRequest =
    { maxResults : Int
    , name : String
    , start : Int
    }

init: NameSearchRequest
init = 
    { maxResults = 0
    , name = ""
    , start = 0
    }

decode: JD.Decoder NameSearchRequest
decode = 
    JDP.decode NameSearchRequest
        |> JDP.optional "maxResults" JD.int 0
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "start" JD.int 0

encode: NameSearchRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "maxResults", o.maxResults |> JE.int)
        , ( "name", o.name |> JE.string)
        , ( "start", o.start |> JE.int)
        ]


