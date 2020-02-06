module Types.Config exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.config.Config -}

type ConfigFields 
    = 

type alias Config =
    { 
    }

init: Config
init = 
    { 
    }

decode: JD.Decoder Config
decode = 
    JDP.decode Config

encode: Config -> JE.Value
encode o = 
    JE.object 
        [
        ]


