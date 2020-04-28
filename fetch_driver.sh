#!/bin/sh

set -e

if [ ! -d lib ]; then
    mkdir -p lib
fi

if [ ! -f lib/simba.zip ]; then
    echo "Downloading Simba driver from Google..."
    curl -s --output lib/simba.zip https://storage.googleapis.com/simba-bq-release/jdbc/SimbaJDBCDriverforGoogleBigQuery42_1.2.2.1004.zip

    echo "Unpacking Simba driver..."
    unzip -q lib/simba.zip -d lib
fi
