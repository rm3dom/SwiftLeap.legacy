module Types.QueryResult exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.query.QueryResultDto -}

type QueryResultFields 
    = RuleCode (String)
    | Severity (Int)
    | Select ((Dict String String))
    | Name (String)
    | MappedCode (String)
    | Message (String)
    | RuleId (String)
    | Version (Int)

type alias QueryResult =
    { ruleCode : String
    , severity : Int
    , select : (Dict String String)
    , name : String
    , mappedCode : String
    , message : String
    , ruleId : String
    , version : Int
    }

init: QueryResult
init = 
    { ruleCode = ""
    , severity = 0
    , select = Dict.empty
    , name = ""
    , mappedCode = ""
    , message = ""
    , ruleId = ""
    , version = 0
    }

decode: JD.Decoder QueryResult
decode = 
    JDP.decode QueryResult
        |> JDP.optional "ruleCode" JD.string ""
        |> JDP.optional "severity" JD.int 0
        |> JDP.optional "select" (JD.dict JD.string) Dict.empty
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "mappedCode" JD.string ""
        |> JDP.optional "message" JD.string ""
        |> JDP.optional "ruleId" JD.string ""
        |> JDP.optional "version" JD.int 0

encode: QueryResult -> JE.Value
encode o = 
    JE.object 
        [ ( "ruleCode", o.ruleCode |> JE.string)
        , ( "severity", o.severity |> JE.int)
        , ( "select", o.select |> dictEncoder)
        , ( "name", o.name |> JE.string)
        , ( "mappedCode", o.mappedCode |> JE.string)
        , ( "message", o.message |> JE.string)
        , ( "ruleId", o.ruleId |> JE.string)
        , ( "version", o.version |> JE.int)
        ]


