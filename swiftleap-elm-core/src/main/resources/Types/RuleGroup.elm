module Types.RuleGroup exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.GroupDto -}

type RuleGroupFields 
    = GroupType (Int)
    | Name (String)
    | Id (Int)
    | ParentId (Int)

type alias RuleGroup =
    { groupType : Int
    , name : String
    , id : Int
    , parentId : Int
    }

init: RuleGroup
init = 
    { groupType = 0
    , name = ""
    , id = 0
    , parentId = 0
    }

decode: JD.Decoder RuleGroup
decode = 
    JDP.decode RuleGroup
        |> JDP.optional "groupType" JD.int 0
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "id" JD.int 0
        |> JDP.optional "parentId" JD.int 0

encode: RuleGroup -> JE.Value
encode o = 
    JE.object 
        [ ( "groupType", o.groupType |> JE.int)
        , ( "name", o.name |> JE.string)
        , ( "id", o.id |> JE.int)
        , ( "parentId", o.parentId |> JE.int)
        ]


