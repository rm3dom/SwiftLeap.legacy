module Toast
    exposing
        ( Toast
        , ToastMsg(..)
        , init
        , update
        , view
        )

import Components exposing (fontAwesome)
import Html exposing (Html, div, text)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)
import Http
import Json.Decode as JD
import Time exposing (Time)
import Types.Error


type ToastMsg
    = HideToast
    | ShowHttpError Http.Error
    | ShowInfo String
    | ShowWarning String
    | ShowError String
    | ShowLoader String
    | HideLoader


type ToastLevel
    = NoMessage
    | Info
    | Warning
    | Error


type alias ToastMessage =
    { message : String
    , level : ToastLevel
    , createdTime : Time
    }


type alias Toast =
    { message : ToastMessage
    , loader : Maybe String
    }


levelToStr : ToastLevel -> String
levelToStr level =
    case level of
        NoMessage ->
            ""

        Info ->
            "info"

        Warning ->
            "warn"

        Error ->
            "error"


levelToInt : ToastLevel -> Int
levelToInt level =
    case level of
        NoMessage ->
            0

        Info ->
            1

        Warning ->
            2

        Error ->
            3


intToLevel : Int -> ToastLevel
intToLevel val =
    case val of
        1 ->
            Info

        2 ->
            Warning

        3 ->
            Error

        _ ->
            NoMessage


initNoMessage : ToastMessage
initNoMessage =
    { message = ""
    , level = NoMessage
    , createdTime = 0
    }


initMessageNoTime : ToastLevel -> String -> ToastMessage
initMessageNoTime level message =
    { message = message
    , level = level
    , createdTime = 0
    }


initMessage : Time -> ToastLevel -> String -> ToastMessage
initMessage time level message =
    { message = message
    , level = level
    , createdTime = time
    }


fromHttpError : Time -> Http.Error -> ToastMessage
fromHttpError time err =
    let
        ( level, message, apiErr ) =
            case err of
                Http.BadUrl _ ->
                    ( 500, "System error, bad url", Nothing )

                Http.Timeout ->
                    ( 500, "System error, timeout connecting to host", Nothing )

                Http.NetworkError ->
                    ( 500, "System error, error connecting to host", Nothing )

                Http.BadStatus resp ->
                    let
                        apiErr =
                            case JD.decodeString Types.Error.decode resp.body of
                                Ok res ->
                                    Just res

                                Err _ ->
                                    Nothing
                    in
                    ( resp.status.code, "System error: " ++ resp.status.message, apiErr )

                Http.BadPayload msg resp ->
                    let
                        _ =
                            Debug.log ("Bad payload in: " ++ msg) resp
                    in
                    ( 500, "System error, bad payload format", Nothing )

        mapMessage a =
            if String.length a.reference > 0 then
                a.message ++ ". Your support ref: " ++ a.reference
            else
                a.message

        actualMessage =
            apiErr
                |> Maybe.map mapMessage
                |> Maybe.withDefault message
    in
    if level > -1 && level < 400 then
        { message = actualMessage, level = Info, createdTime = time }
    else if level > 399 && level < 500 then
        { message = actualMessage, level = Warning, createdTime = time }
    else
        { message = actualMessage, level = Error, createdTime = time }


init : Toast
init =
    { message = initNoMessage
    , loader = Nothing
    }


update : Time -> ToastMsg -> Toast -> Toast
update time msg toast =
    case msg of
        HideToast ->
            { toast | message = initNoMessage, loader = Nothing }

        ShowHttpError err ->
            { toast | message = fromHttpError time err }

        ShowInfo msg ->
            --Hack to hide the loader on error, need to have an error callback on api
            { toast | message = initMessage time Info msg, loader = Nothing  }

        ShowLoader msg ->
            { toast | loader = Just msg }

        HideLoader ->
            { toast | loader = Nothing }

        ShowWarning msg ->
            --Hack to hide the loader on error, need to have an error callback on api
            { toast | message = initMessage time Warning msg, loader = Nothing   }

        ShowError msg ->
            --Hack to hide the loader on error, need to have an error callback on api
            { toast | message = initMessage time Error msg, loader = Nothing   }


view : Time -> Toast -> Html ToastMsg
view time toast =
    let
        message =
            toast.message

        duration =
            case message.level of
                Error ->
                    20000

                Warning ->
                    5000

                _ ->
                    3000

        classes =
            "toast " ++ levelToStr message.level


        loaderElem =
            case toast.loader of
                Nothing ->
                    text ""

                Just msg ->
                    Html.div [class "loader"] [text msg]

        toastView =
            case message.level of
                NoMessage ->
                    loaderElem

                _ ->
                    if time >= (message.createdTime + duration) then
                        text ""
                    else
                        div [ class classes ]
                            [ Html.a [ class "toast-close", onClick HideToast ] [ fontAwesome "fa-window-close" ]
                            , div [ class "toast-message" ] [ text message.message ]
                            ]

    in
    toastView

