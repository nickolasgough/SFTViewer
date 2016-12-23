#!/bin/bash

current_directory=`dirname $0`
cd $current_directory
java -jar -Xmn1280m pdfviewer_V7.jar
exit 0
