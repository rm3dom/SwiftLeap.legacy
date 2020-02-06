module AppJob exposing (AppJob, init)

import Job exposing (..)
import Routing exposing (..)


type alias AppJob msg =
    Job msg Route Action


init : AppJob msg
init =
    Job.init
