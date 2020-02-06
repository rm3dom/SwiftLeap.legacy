module Components.Grid
    exposing
        ( Alignment(..)
        , Grid
        , Msg
        , StatefulGrid
        , addCol
        , addColExt
        , addColMore
        , addCols
        , addColsMore
        , clearSelection
        , empty
        , init
        , initMultiSelectable
        , initNonPagerSelectable
        , initSelectable
        , initStatefulMorePager
        , map
        , render
        , renderRaw
        , renderStateful
        , selection
        , setSelection
        , update
        , withMore
        , withPageSize
        , withPager
        , withRowClicked
        , withRowFormat
        , withSelected
        , withSelection
        , withShowMore
        , withSort
        )

{-| A simple grid which can perform the following:

  - Row selection
  - Row clicks
  - Row formatting by adding classes
  - Render any Html in the columns
  - Toggle show more less columns
  - Align text in columns

More complex grid components can be built by using this module, this just provided
the basics.

TODO Columns sorting

-}

import Alfred
import Components as Components
import Html as Html exposing (Html)
import Html.Attributes as Attr
import Html.Events exposing (onClick)


type Msg subject
    = RowSelected subject
    | GotoPage Int
    | PageSizeChanged Int
    | ToggleShowMore


type Alignment
    = Left
    | Right
    | Middle


type alias StatefulGrid subject =
    Grid subject (Msg subject)


type alias Grid subject msg =
    { columns : List (Column subject msg)
    , showMore : Bool
    , selected : subject -> Bool
    , rowClicked : Maybe (subject -> msg)
    , rowFormatter : subject -> String
    , pageSizeChanged : Maybe (Int -> msg)
    , pageClicked : Maybe (Int -> msg)
    , pageSize : Int
    , pagePosition : Int
    , hasShowMore : Bool
    , toggleMore : Maybe msg
    , maxHeight : Int
    , comparator : subject -> subject -> Bool
    , selection : List subject
    , multiSelect : Bool
    , sorter : subject -> subject -> Order
    , sortGrid : Bool
    }


type alias Column subject msg =
    { title : Html msg
    , alignment : Alignment
    , func : subject -> Html msg
    , more : Bool
    }


empty : Grid subject msg
empty =
    { columns = []
    , showMore = False
    , selected = \_ -> False
    , rowClicked = Nothing
    , rowFormatter = \_ -> ""
    , pageClicked = Nothing
    , pageSize = 10
    , pagePosition = 0
    , pageSizeChanged = Nothing
    , hasShowMore = False
    , toggleMore = Nothing
    , maxHeight = 600
    , comparator = \_ _ -> False
    , selection = []
    , multiSelect = False
    , sorter = \_ _ -> EQ
    , sortGrid = False
    }


init : StatefulGrid subject
init =
    empty
        |> withPager 10 0 GotoPage PageSizeChanged


initStatefulMorePager : StatefulGrid subject
initStatefulMorePager =
    init
        |> withMore 600 ToggleShowMore


initSelectable : (subject -> subject -> Bool) -> StatefulGrid subject
initSelectable comparator =
    init
        |> withMore 600 ToggleShowMore
        |> withRowClicked RowSelected
        |> withSelection comparator False


initMultiSelectable : (subject -> subject -> Bool) -> StatefulGrid subject
initMultiSelectable comparator =
    init
        |> withRowClicked RowSelected
        |> withSelection comparator True


initNonPagerSelectable : (subject -> subject -> Bool) -> StatefulGrid subject
initNonPagerSelectable comparator =
    empty
        |> withRowClicked RowSelected
        |> withSelection comparator False


initColl : Column subject msg
initColl =
    { title = Html.text ""
    , alignment = Left
    , func = \_ -> Html.text ""
    , more = False
    }


memberSelection : subject -> StatefulGrid subject -> Bool
memberSelection newSelection grid =
    List.any (grid.comparator newSelection) grid.selection


removeSelection : subject -> StatefulGrid subject -> StatefulGrid subject
removeSelection newSelection grid =
    let
        selection_ =
            List.filter (grid.comparator newSelection >> not) grid.selection
    in
    { grid | selection = selection_ }


toggleSelection : subject -> StatefulGrid subject -> StatefulGrid subject
toggleSelection newSelection grid =
    if memberSelection newSelection grid then
        removeSelection newSelection grid
    else
        { grid | selection = newSelection :: grid.selection }


update : Msg subject -> StatefulGrid subject -> StatefulGrid subject
update msg grid =
    case msg of
        RowSelected subject ->
            case grid.multiSelect of
                False ->
                    { grid | selection = [ subject ] }

                True ->
                    toggleSelection subject grid

        GotoPage page ->
            { grid | pagePosition = page }

        PageSizeChanged pageSize ->
            { grid | pageSize = pageSize }

        ToggleShowMore ->
            { grid | showMore = not grid.showMore }


{-| Map the message types of grid
-}
map : (a -> b) -> Grid subject a -> Grid subject b
map mapper grid =
    let
        rowClicked =
            Maybe.map (\msg -> msg >> mapper) grid.rowClicked

        pageClicked =
            Maybe.map (\msg -> msg >> mapper) grid.pageClicked

        pageSizeChanged =
            Maybe.map (\msg -> msg >> mapper) grid.pageSizeChanged

        toggleMore =
            Maybe.map (\msg -> mapper msg) grid.toggleMore

        cols =
            List.map (\c -> { c | func = c.func >> Html.map mapper, title = Html.map mapper c.title }) grid.columns
    in
    { grid
        | columns = cols
        , rowClicked = rowClicked
        , pageClicked = pageClicked
        , pageSizeChanged = pageSizeChanged
        , toggleMore = toggleMore
    }


{-| Get the selected items
-}
selection : Grid subject msg -> List subject
selection =
    .selection


clearSelection : Grid subject msg -> Grid subject msg
clearSelection grid =
    { grid | selection = [] }


setSelection : List subject -> Grid subject msg -> Grid subject msg
setSelection selection grid =
    { grid | selection = selection }


{-| Take in a subject and return true or false when selected.
Used when building a custom grid, see withSelection for pager.
-}
withSelected : (subject -> Bool) -> Grid subject msg -> Grid subject msg
withSelected func grid =
    { grid | selected = func }


withSelection : (subject -> subject -> Bool) -> Bool -> StatefulGrid subject -> StatefulGrid subject
withSelection comparator multiSelect grid =
    { grid | comparator = comparator, multiSelect = multiSelect }


{-| Used with paging grid or custom grid.
-}
withRowClicked : (subject -> msg) -> Grid subject msg -> Grid subject msg
withRowClicked clicker grid =
    { grid | rowClicked = Just clicker }


{-| Take in a subject and produce a html class
-}
withRowFormat : (subject -> String) -> Grid subject msg -> Grid subject msg
withRowFormat func grid =
    { grid | rowFormatter = func }


{-| DEPRECIATED: Please use PagingGrid instead where this is handled internally
Show all columns when true.
-}
withShowMore : Bool -> Grid subject msg -> Grid subject msg
withShowMore showMore grid =
    { grid | showMore = showMore }


withMore : Int -> msg -> Grid subject msg -> Grid subject msg
withMore maxHeight toggleMore grid =
    { grid | toggleMore = Just toggleMore, maxHeight = maxHeight }


{-| Render a pager with the grid.
-}
withPager : Int -> Int -> (Int -> msg) -> (Int -> msg) -> Grid subject msg -> Grid subject msg
withPager pageSize pagePosition pageClicked pageSizeChanged grid =
    { grid
        | pageSize = pageSize
        , pagePosition = pagePosition
        , pageClicked = Just pageClicked
        , pageSizeChanged = Just pageSizeChanged
    }


withPageSize : Int -> Grid subject msg -> Grid subject msg
withPageSize pageSize grid =
    { grid | pageSize = pageSize }


withSort : (subject -> subject -> Order) -> Grid subject msg -> Grid subject msg
withSort sorter grid =
    { grid | sorter = sorter, sortGrid = True }


toHtmlText : (subject -> comparable) -> (subject -> Html msg)
toHtmlText func =
    func >> Alfred.toStr >> Html.text


{-| Add a simple column.
-}
addCol : String -> (subject -> comparable) -> Grid subject msg -> Grid subject msg
addCol title func grid =
    let
        column =
            { initColl | title = Html.text title, func = toHtmlText func }
    in
    { grid | columns = column :: grid.columns }


{-| Add a list of simple column.
-}
addCols : List ( String, subject -> comparable ) -> Grid subject msg -> Grid subject msg
addCols cols grid =
    let
        reducer ( title, func ) =
            addCol title func
    in
    List.foldl reducer grid cols


{-| Add a column that should only be displayed when showing more.
-}
addColMore : String -> (subject -> comparable) -> Grid subject msg -> Grid subject msg
addColMore title func grid =
    let
        column =
            { initColl | title = Html.text title, func = toHtmlText func, more = True }
    in
    { grid | columns = column :: grid.columns, hasShowMore = True }


{-| Add a column that should only be displayed when showing more.
-}
addColsMore : List ( String, subject -> comparable ) -> Grid subject msg -> Grid subject msg
addColsMore cols grid =
    let
        reducer ( title, func ) =
            addColMore title func
    in
    List.foldl reducer grid cols


{-| Add a column with custom Html.
-}
addColExt : Html msg -> Alignment -> Bool -> (subject -> Html msg) -> Grid subject msg -> Grid subject msg
addColExt title alignment more func grid =
    let
        column =
            { initColl
                | title = title
                , func = func
                , alignment = alignment
                , more = more
            }
    in
    { grid | columns = column :: grid.columns, hasShowMore = grid.hasShowMore || more }


{-| Render the grid, and optional pager.
-}
render : List subject -> Grid subject msg -> Html msg
render subjects =
    renderRaw [ Attr.class "grid" ] subjects


renderRaw : List (Html.Attribute msg) -> List subject -> Grid subject msg -> Html msg
renderRaw attributes subjectList grid =
    let
        subjects =
            getSubjects subjectList grid
                |> sortSubjects

        sortSubjects subjects =
            if grid.sortGrid then
                List.sortWith grid.sorter subjects
            else
                subjects

        columns =
            grid.columns
                |> List.filter (\c -> not c.more || c.more == grid.showMore)
                |> List.reverse

        -- Function to map over each subject
        rowMapper subject =
            let
                trClass =
                    grid.rowFormatter subject

                -- Function to map over each field
                colMapper col =
                    let
                        collClass =
                            alignmentToStr col.alignment
                    in
                    Html.td [ Attr.class collClass ] [ col.func subject ]
            in
            case grid.rowClicked of
                Nothing ->
                    Html.tr [ Attr.class trClass ] (List.map colMapper columns)

                Just clicker ->
                    Html.tr
                        [ Attr.style [ ( "cursor", "pointer" ) ]
                        , Attr.class
                            (if isSelected grid subject then
                                "selected " ++ trClass
                             else
                                trClass
                            )
                        , Components.onClickNoBubble (clicker subject)
                        ]
                        (List.map colMapper columns)

        headMapper col =
            Html.th [ Attr.class (alignmentToStr col.alignment) ] [ col.title ]

        tableBody =
            Html.div [ Attr.class "scrollbar overflow-width" ]
                [ Html.table [ Attr.attribute "width" "100%" ]
                    [ Html.thead []
                        [ Html.tr [] (List.map headMapper columns)
                        ]
                    , Html.tbody [] (List.map rowMapper subjects)
                    ]
                ]

        pager =
            renderStateful subjectList grid

        tableMore =
            if grid.hasShowMore then
                grid.toggleMore
                    |> Maybe.map
                        (\clicker ->
                            Html.div []
                                [ Html.span
                                    [ Attr.class "show-more" ]
                                    [ Components.toggleCheck clicker grid.showMore
                                    , Html.text "Show More"
                                    ]
                                , tableBody
                                ]
                        )
                    |> Maybe.withDefault tableBody
            else
                tableBody
    in
    Html.div attributes [ tableMore, pager ]


{-| Render the pager, footer of the grid.
-}
renderStateful : List subject -> Grid subject msg -> Html msg
renderStateful subjects grid =
    let
        length =
            List.length subjects
    in
    case grid.pageClicked of
        Nothing ->
            Html.span
                [ Attr.class "no-results" ]
                [ Html.text (toString length ++ " Results") ]

        Just clicker ->
            let
                activeClass index =
                    if index == grid.pagePosition then
                        Attr.class "active"
                    else
                        Attr.class ""



                maxPages =
                    if length % grid.pageSize == 0 then
                        (length // grid.pageSize) - 1
                    else
                        length // grid.pageSize

                hi =
                    -- next 8 pages or second last
                    min (maxPages - 1) (max 9 (grid.pagePosition + 4))

                lo =
                    -- the next page or the previous 8 after last
                    max 1 (hi - 8)

                firstPage =
                    Html.a
                        [ activeClass 0, onClick (clicker 0) ]
                        [ Html.text "1" ]

                pages =
                    List.range lo hi
                        |> List.map (\i -> Html.a [ activeClass i, onClick (clicker i) ] [ Html.text (toString (i + 1)) ])

                lastPage =
                    if maxPages > 0 then
                        Html.a
                            [ activeClass maxPages, onClick (clicker maxPages) ]
                            [ Html.text (toString (maxPages + 1)) ]
                    else
                        Html.text ""

                pageSizes =
                    [ 10, 20, 50, 200 ]
                        |> List.map toString
                        |> List.map (\i -> ( i, i ))

                pageSizeChanged clicker str =
                    clicker (Alfred.toIntWithDefault 0 str)

                pageDrop =
                    case grid.pageSizeChanged of
                        Nothing ->
                            Html.text ""

                        Just clicker ->
                            Components.select (pageSizeChanged clicker) pageSizes (toString grid.pageSize)

                count =
                    Html.span
                        [ Attr.class "no-results" ]
                        [ Html.text (toString length ++ " Results") ]
            in
            Html.div [ Attr.class "grid-pager" ] (List.concat [ [ firstPage ], pages, [ lastPage, pageDrop, count ] ])


{-| Determine if a row is selected
-}
isSelected : Grid subject msg -> subject -> Bool
isSelected grid subject =
    List.foldl (\s b -> b || grid.comparator s subject) False grid.selection || grid.selected subject


{-| Get a list of subjects to display.

If we have a pager we only return the current page.

-}
getSubjects : List subject -> Grid subject msg -> List subject
getSubjects subjects grid =
    case grid.pageClicked of
        Nothing ->
            subjects

        Just _ ->
            let
                pos =
                    min grid.pagePosition (List.length subjects // grid.pageSize)
            in
            subjects
                |> List.drop (pos * grid.pageSize)
                |> List.take grid.pageSize


{-| Return the string representation used as a class for the alignment
-}
alignmentToStr : Alignment -> String
alignmentToStr alignment =
    case alignment of
        Left ->
            "left"

        Right ->
            "right"

        Middle ->
            "middle"
