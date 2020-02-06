module Components.Updates exposing (Msg(..), Updates, init, update, view)

import AppJob exposing (AppJob)
import Components
import Components.Form as Form
import Html exposing (Html)
import Job exposing (CoreJob)
import Model.System exposing (System)


type Msg
    = NoOp


type alias Updates =
    {}


init : Updates
init =
    {}


update : System -> Msg -> Updates -> ( System, Updates, AppJob Msg )
update system msg model =
    case msg of
        NoOp ->
            ( system, model, Job.init )


view : System -> Updates -> Html Msg
view system model =
    let
        updateInfo =
            system.updateInfo

        ( btn, messages ) =
            case ( updateInfo.restartRequired, updateInfo.onLatest ) of
                ( False, True ) ->
                    ( Html.text "", Components.info [ "Your system is up to date" ] )

                ( True, _ ) ->
                    ( Form.primaryButtonLink "update.html" "Restart", Components.warn [ "Restart to apply the latest version. It is advisable to backup before upgrading." ] )

                ( _, False ) ->
                    ( Form.primaryButtonLink "update.html" "Update & Restart", Components.warn [ "There is a new version available. It is advisable to backup before upgrading." ] )
    in
    Html.div []
        [ Html.h3 [] [ Html.text "System Updates" ]
        , messages
        , Form.readOnlyInput "Current Version" updateInfo.currentVersion
        , Form.readOnlyInput "Latest Version" updateInfo.latestVersion
        , Form.buttonGroup [ btn ]
        ]
