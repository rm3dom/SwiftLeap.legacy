module Types.TestData exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.TestData -}

type TestDataFields 
    = DataSetName (String)
    | Values ((Dict String String))

type alias TestData =
    { dataSetName : String
    , values : (Dict String String)
    }

init: TestData
init = 
    { dataSetName = ""
    , values = Dict.empty
    }

decode: JD.Decoder TestData
decode = 
    JDP.decode TestData
        |> JDP.optional "dataSetName" JD.string ""
        |> JDP.optional "values" (JD.dict JD.string) Dict.empty

encode: TestData -> JE.Value
encode o = 
    JE.object 
        [ ( "dataSetName", o.dataSetName |> JE.string)
        , ( "values", o.values |> dictEncoder)
        ]


