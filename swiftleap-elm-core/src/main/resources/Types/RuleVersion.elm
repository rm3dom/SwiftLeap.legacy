module Types.RuleVersion exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.VersionDto -}

type RuleVersionFields 
    = LastUpdatedBy (String)
    | Current (Bool)
    | CreationTime (DateTime)
    | CreatedBy (String)
    | Description (String)
    | Published (Bool)
    | Version (Int)
    | LastUpdateTime (DateTime)

type alias RuleVersion =
    { lastUpdatedBy : String
    , current : Bool
    , creationTime : DateTime
    , createdBy : String
    , description : String
    , published : Bool
    , version : Int
    , lastUpdateTime : DateTime
    }

init: RuleVersion
init = 
    { lastUpdatedBy = ""
    , current = False
    , creationTime = initDateTime
    , createdBy = ""
    , description = ""
    , published = False
    , version = 0
    , lastUpdateTime = initDateTime
    }

decode: JD.Decoder RuleVersion
decode = 
    JDP.decode RuleVersion
        |> JDP.optional "lastUpdatedBy" JD.string ""
        |> JDP.optional "current" JD.bool False
        |> JDP.optional "creationTime" dateTimeDecoder initDateTime
        |> JDP.optional "createdBy" JD.string ""
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "published" JD.bool False
        |> JDP.optional "version" JD.int 0
        |> JDP.optional "lastUpdateTime" dateTimeDecoder initDateTime

encode: RuleVersion -> JE.Value
encode o = 
    JE.object 
        [ ( "lastUpdatedBy", o.lastUpdatedBy |> JE.string)
        , ( "current", o.current |> JE.bool)
        , ( "creationTime", o.creationTime |> dateTimeEncoder)
        , ( "createdBy", o.createdBy |> JE.string)
        , ( "description", o.description |> JE.string)
        , ( "published", o.published |> JE.bool)
        , ( "version", o.version |> JE.int)
        , ( "lastUpdateTime", o.lastUpdateTime |> dateTimeEncoder)
        ]


