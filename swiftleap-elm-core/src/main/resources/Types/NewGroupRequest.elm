module Types.NewGroupRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.NewGroupRequestDto -}

type NewGroupRequestFields 
    = Name (String)
    | ParentId (Int)

type alias NewGroupRequest =
    { name : String
    , parentId : Int
    }

init: NewGroupRequest
init = 
    { name = ""
    , parentId = 0
    }

decode: JD.Decoder NewGroupRequest
decode = 
    JDP.decode NewGroupRequest
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "parentId" JD.int 0

encode: NewGroupRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "name", o.name |> JE.string)
        , ( "parentId", o.parentId |> JE.int)
        ]


