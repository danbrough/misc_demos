#!/bin/bash

cd $(dirname $0) && cd ..

LIBDIR=build/lib
[ ! -d $LIBDIR ] && mkdir -p $LIBDIR
LIBDIR=$(realpath $LIBDIR)
LIBFILE=$LIBDIR/libgo.a

cd golib/src/go || exit

CGO_ENABLED=1 go build  -tags=shell,node \
  -ldflags '-linkmode external -extldflags "-static"'  -buildmode=c-archive -o $LIBFILE  .

