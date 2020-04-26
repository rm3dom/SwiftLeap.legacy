#!/bin/bash

OUTPATH=./target/classes/static/lib/admin.js

if [ ! -d "node_modules" ]; then
    npm install
fi

#rename file and push the assets to the main dist
npx elm-make --yes  ./src/main/elm/Main.elm --output=$OUTPATH && npx gulp
RES=$?
if [ ! "$RES" -eq "0" ]; then
    exit $RES
fi

if [ -f "index.html" ]; then
    rm index.html
fi
