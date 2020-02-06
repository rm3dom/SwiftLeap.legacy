module Types.MailFilterStats exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.zabbix.mailfilter.MailFilterStats -}

type MailFilterStatsFields 
    = Forwarded (Int)
    | NoActionCount (Int)
    | Resolved (Int)

type alias MailFilterStats =
    { forwarded : Int
    , noActionCount : Int
    , resolved : Int
    }

init: MailFilterStats
init = 
    { forwarded = 0
    , noActionCount = 0
    , resolved = 0
    }

decode: JD.Decoder MailFilterStats
decode = 
    JDP.decode MailFilterStats
        |> JDP.optional "forwarded" JD.int 0
        |> JDP.optional "noActionCount" JD.int 0
        |> JDP.optional "resolved" JD.int 0

encode: MailFilterStats -> JE.Value
encode o = 
    JE.object 
        [ ( "forwarded", o.forwarded |> JE.int)
        , ( "noActionCount", o.noActionCount |> JE.int)
        , ( "resolved", o.resolved |> JE.int)
        ]


