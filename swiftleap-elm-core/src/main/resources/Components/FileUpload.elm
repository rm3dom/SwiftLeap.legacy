module Components.FileUpload exposing (..)

import Components
import FileReader exposing (NativeFile)
import FileReader.FileDrop as DZ
import Html exposing (Html)
import Html.Attributes as HA
import Html.Events as HE
import Json.Encode
import Task


type alias FileUpload =
    { file : Maybe NativeFile
    , dragHovering : Int
    }


init : FileUpload
init =
    { file = Nothing
    , dragHovering = 0
    }


type Msg
    = OnDragEnter Int
    | OnDrop (List NativeFile)
    | NoOp


hasFile : FileUpload -> Bool
hasFile model =
    model.file /= Nothing


getFile : FileUpload -> Maybe NativeFile
getFile model =
    model.file


update : Msg -> FileUpload -> FileUpload
update message model =
    case message of
        OnDragEnter inc ->
            { model | dragHovering = model.dragHovering + inc }

        OnDrop file ->
            case file of
                -- Only handling case of a single file
                f :: _ ->
                    { model | file = Just f, dragHovering = 0 }

                _ ->
                    { model | dragHovering = 0 }

        NoOp ->
            model


view : FileUpload -> Html Msg
view model =
    let
        dzAttrs_ =
            DZ.dzAttrs (OnDragEnter 1) (OnDragEnter -1) NoOp OnDrop

        dzClass =
            if model.dragHovering > 0 then
                HA.class "drop-zone active" :: dzAttrs_
            else
                HA.class "drop-zone" :: dzAttrs_

        dropContent =
            case model.file of
                Just nf ->
                    Html.div [ HA.class "drop-zone-info" ] [ Html.text nf.name ]

                Nothing ->
                    Html.p
                        []
                        [ Components.fontAwesome "fa-upload"
                        , Html.br [] []
                        , Html.text "Drag & Drop"
                        ]
    in
    Html.div dzClass
        [ Html.input
            [ HA.type_ "file"
            , FileReader.onFileChange OnDrop
            , HA.multiple False
            ]
            []
        , dropContent
        ]
