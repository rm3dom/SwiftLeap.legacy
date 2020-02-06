module Main exposing (main)

import Model exposing (..)
import Msg exposing (..)
import Navigation
import Types.Flags exposing (Flags)
import Update exposing (..)
import View exposing (view)


main : Program Flags Model Msg
main =
    Navigation.programWithFlags UrlChange
        { view = View.view
        , init = Update.init
        , update = Update.update
        , subscriptions = Update.subscriptions
        }
