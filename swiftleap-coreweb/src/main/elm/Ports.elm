port module Ports exposing (showTerm)


port showTerm : (String -> msg) -> Sub msg
