#!/bin/bash

set -e

gomobile bind -v -androidapi=19

echo "Moving files to ../libs"
mv tts_server_lib.aar ../libs
mv tts_server_lib-sources.jar ../libs