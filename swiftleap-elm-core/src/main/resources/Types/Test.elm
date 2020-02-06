module Types.Test exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.TestSchema as TestSchema


{- Generated from org.swiftleap.rules.web.api.model.TestDto -}

type TestFields 
    = Schema (List TestSchema.TestSchema)
    | Inverse (Bool)
    | LastUpdatedBy (String)
    | CreationTime (DateTime)
    | CreatedBy (String)
    | Name (String)
    | Id (Int)
    | RuleId (String)
    | LastUpdateTime (DateTime)

type alias Test =
    { schema : List TestSchema.TestSchema
    , inverse : Bool
    , lastUpdatedBy : String
    , creationTime : DateTime
    , createdBy : String
    , name : String
    , id : Int
    , ruleId : String
    , lastUpdateTime : DateTime
    }

init: Test
init = 
    { schema = []
    , inverse = False
    , lastUpdatedBy = ""
    , creationTime = initDateTime
    , createdBy = ""
    , name = ""
    , id = -1
    , ruleId = ""
    , lastUpdateTime = initDateTime
    }

decode: JD.Decoder Test
decode = 
    JDP.decode Test
        |> JDP.optional "schema" (JD.list TestSchema.decode) []
        |> JDP.optional "inverse" JD.bool False
        |> JDP.optional "lastUpdatedBy" JD.string ""
        |> JDP.optional "creationTime" dateTimeDecoder initDateTime
        |> JDP.optional "createdBy" JD.string ""
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "id" JD.int -1
        |> JDP.optional "ruleId" JD.string ""
        |> JDP.optional "lastUpdateTime" dateTimeDecoder initDateTime

encode: Test -> JE.Value
encode o = 
    JE.object 
        [ ( "schema", o.schema |> List.map TestSchema.encode |> JE.list)
        , ( "inverse", o.inverse |> JE.bool)
        , ( "lastUpdatedBy", o.lastUpdatedBy |> JE.string)
        , ( "creationTime", o.creationTime |> dateTimeEncoder)
        , ( "createdBy", o.createdBy |> JE.string)
        , ( "name", o.name |> JE.string)
        , ( "id", o.id |> JE.int)
        , ( "ruleId", o.ruleId |> JE.string)
        , ( "lastUpdateTime", o.lastUpdateTime |> dateTimeEncoder)
        ]


