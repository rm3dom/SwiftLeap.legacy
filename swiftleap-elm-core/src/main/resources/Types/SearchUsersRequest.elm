module Types.SearchUsersRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.web.api.system.model.SearchUsersRequestDto -}

type SearchUsersRequestFields 
    = Filter (String)
    | MaxResults (Int)
    | Start (Int)
    | Status (String)

type alias SearchUsersRequest =
    { filter : String
    , maxResults : Int
    , start : Int
    , status : String
    }

init: SearchUsersRequest
init = 
    { filter = ""
    , maxResults = 0
    , start = 0
    , status = "UNKNOWN"
    }

decode: JD.Decoder SearchUsersRequest
decode = 
    JDP.decode SearchUsersRequest
        |> JDP.optional "filter" JD.string ""
        |> JDP.optional "maxResults" JD.int 0
        |> JDP.optional "start" JD.int 0
        |> JDP.optional "status" JD.string "UNKNOWN"

encode: SearchUsersRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "filter", o.filter |> JE.string)
        , ( "maxResults", o.maxResults |> JE.int)
        , ( "start", o.start |> JE.int)
        , ( "status", o.status |> JE.string)
        ]


