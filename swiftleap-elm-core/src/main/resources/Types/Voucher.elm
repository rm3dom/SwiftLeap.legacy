module Types.Voucher exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.seed.web.api.mobile.model.VoucherDto -}

type VoucherFields 
    = Expires (String)
    | Code (String)
    | Color (String)
    | ValueType (String)
    | ShortDesc (String)
    | Value (Int)
    | LongDesc (String)

type alias Voucher =
    { expires : String
    , code : String
    , color : String
    , valueType : String
    , shortDesc : String
    , value : Int
    , longDesc : String
    }

init: Voucher
init = 
    { expires = ""
    , code = ""
    , color = ""
    , valueType = ""
    , shortDesc = ""
    , value = 0
    , longDesc = ""
    }

decode: JD.Decoder Voucher
decode = 
    JDP.decode Voucher
        |> JDP.optional "expires" JD.string ""
        |> JDP.optional "code" JD.string ""
        |> JDP.optional "color" JD.string ""
        |> JDP.optional "valueType" JD.string ""
        |> JDP.optional "shortDesc" JD.string ""
        |> JDP.optional "value" JD.int 0
        |> JDP.optional "longDesc" JD.string ""

encode: Voucher -> JE.Value
encode o = 
    JE.object 
        [ ( "expires", o.expires |> JE.string)
        , ( "code", o.code |> JE.string)
        , ( "color", o.color |> JE.string)
        , ( "valueType", o.valueType |> JE.string)
        , ( "shortDesc", o.shortDesc |> JE.string)
        , ( "value", o.value |> JE.int)
        , ( "longDesc", o.longDesc |> JE.string)
        ]


