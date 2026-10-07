#!/bin/sh
set -eu
base_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
version=3.9.12
archive="$base_dir/.mvn/wrapper/apache-maven-$version-bin.zip"
home="$base_dir/.mvn/wrapper/apache-maven-$version"
if [ ! -x "$home/bin/mvn" ]; then
  mkdir -p "$base_dir/.mvn/wrapper"
  if [ ! -f "$archive" ]; then
    curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$version/apache-maven-$version-bin.zip" -o "$archive"
  fi
  unzip -q "$archive" -d "$base_dir/.mvn/wrapper"
fi
exec "$home/bin/mvn" "$@"
