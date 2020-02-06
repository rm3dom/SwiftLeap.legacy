module Lookup exposing (..)

import Job exposing (Job)

type Msg =
    NoOp

type alias Lookup a =
    {}

init: (Lookup a, Job Msg)
init =
    ({}, Job.init)


update: Msg -> Lookup a -> (Lookup a, Job Msg)
update msg model =
    case msg of
        NoOp ->
            (model, Job.init)

