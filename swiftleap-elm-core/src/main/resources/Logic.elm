module Logic exposing (..)


orElse : Bool -> b -> b -> b
orElse cond l r =
    if cond then
        l
    else
        r


when : Bool -> (b -> b) -> b -> b
when cond f r =
    if cond then
        f r
    else
        r
