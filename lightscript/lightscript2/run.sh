#!/bin/sh
js parser.js < parser.js > t && diff parser.js t
