#!/bin/bash
min=$1
max=$2
dir=$3
find "$dir" -maxdepth 1 -type f -size +"$min"c -size -"$max"c 