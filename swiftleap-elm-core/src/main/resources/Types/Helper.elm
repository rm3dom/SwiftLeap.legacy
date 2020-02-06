module Types.Helper exposing (..)

import Json.Decode as JD exposing (fail, succeed)
import Json.Encode as JE
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


encodeMaybe : (a -> JE.Value) -> Maybe a -> JE.Value
encodeMaybe f =
    Maybe.map f
        >> Maybe.withDefault JE.null


initDateTime : DateTime
initDateTime =
    DateTime.fromTuple ( 1900, 1, 1, 0, 0, 0, 0 )


dateTimeDecoder : JD.Decoder DateTime
dateTimeDecoder =
    let
        convert raw =
            case ( DateTime.fromISO8601 raw, DateTime.fromISO8601 (String.slice 0 19 raw ++ "Z") ) of
                ( Ok date, _ ) ->
                    succeed date

                ( _, Ok date ) ->
                    succeed date

                ( Err e, _ ) ->
                    Debug.log "Helper.dateTimeDecoder: " (fail e)
    in
    JD.string |> JD.andThen convert


dateTimeEncoder : DateTime -> JE.Value
dateTimeEncoder val =
    case DateTime.compare val (DateTime.dateTime DateTime.zero) of
        EQ ->
            JE.string "0000-00-00"

        _ ->
            JE.string (DateTime.toISO8601 val)

dictEncoder: (Dict String String) -> JE.Value
dictEncoder dict =
    dict
        |> Dict.toList
        |> List.map (\( k, v ) -> ( k, JE.string v ))
        |> JE.object
