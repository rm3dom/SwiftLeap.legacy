module Types.RuleAndTest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.Test as Test
import Types.Rule as Rule


{- Generated from org.swiftleap.rules.web.api.model.RuleAndTestDto -}

type RuleAndTestFields 
    = Test (Maybe Test.Test)
    | Rule (Maybe Rule.Rule)

type alias RuleAndTest =
    { test : Maybe Test.Test
    , rule : Maybe Rule.Rule
    }

init: RuleAndTest
init = 
    { test = Nothing
    , rule = Nothing
    }

decode: JD.Decoder RuleAndTest
decode = 
    JDP.decode RuleAndTest
        |> JDP.required "test" (JD.nullable Test.decode)
        |> JDP.required "rule" (JD.nullable Rule.decode)

encode: RuleAndTest -> JE.Value
encode o = 
    JE.object 
        [ ( "test", o.test |> encodeMaybe Test.encode)
        , ( "rule", o.rule |> encodeMaybe Rule.encode)
        ]


