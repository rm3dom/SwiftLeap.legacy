module Types.BasicItem exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.zabbix.web.api.model.BasicItemDto -}

type BasicItemFields 
    = ItemId (Int)
    | Name (String)
    | Description (String)
    | HostId (Int)

type alias BasicItem =
    { itemId : Int
    , name : String
    , description : String
    , hostId : Int
    }

init: BasicItem
init = 
    { itemId = 0
    , name = ""
    , description = ""
    , hostId = 0
    }

decode: JD.Decoder BasicItem
decode = 
    JDP.decode BasicItem
        |> JDP.optional "itemId" JD.int 0
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "hostId" JD.int 0

encode: BasicItem -> JE.Value
encode o = 
    JE.object 
        [ ( "itemId", o.itemId |> JE.int)
        , ( "name", o.name |> JE.string)
        , ( "description", o.description |> JE.string)
        , ( "hostId", o.hostId |> JE.int)
        ]


