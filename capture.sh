#!/bin/sh
# Copyright 2014 Intel Corporation. All rights reserved.
# License: BSD-3-clause-Intel, see LICENSE.txt

set -e

CURDIR="$(dirname "$0")"
[ -n "${CURDIR}" ] || CURDIR="."

usage () {
	echo "usage: ${CURDIR}/capture.sh [x|n] [s|c] [m7|n5|n7|z5] [test-case]" >&2
}

runtime=
app=
surface=SurfaceView

case "$2" in
	(s)
		app=social
		;;
	(c)
		app=contacts
		;;
	(*)
		usage
		exit 2
		;;
esac

case "$1" in
	(x)
		runtime="xwperf"
		;;
	(n)
		runtime="notxw"
		surface="com.collabora.xwperf.notxw_${app}/com.collabora.xwperf.notxw_${app}.MainActivity_"
		;;
	(*)
		usage
		exit 2
		;;
esac

device="$3"
test="$4"

echo "You should have opened ${runtime}_${app} already."
echo "Start doing whatever the test case is..."
sleep 1
beep || printf '\a'
echo "Starting to collect stats..."
"$CURDIR"/get_stats.py \
	--time=5 \
	--surface="${surface}" \
	--output="$CURDIR"/results/${runtime}_${app}_${device}_${test}.log \
	> "$CURDIR"/results/${runtime}_${app}_${device}_${test}.orig.txt
echo "OK, you can stop now"
beep || printf '\a'

"$CURDIR"/get_stats.py \
	--input="$CURDIR"/results/${runtime}_${app}_${device}_${test}.log \
	--graph="$CURDIR"/results/${runtime}_${app}_${device}_${test}.svg \
	> "$CURDIR"/results/${runtime}_${app}_${device}_${test}.txt
