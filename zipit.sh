#!/bin/bash
echo == ZIPIT ==

TAR=Project4CourseRecommender.zip.gz

rm -f ../$TAR

tar cvzf ../$TAR \
    --exclude '.idea/*' \
    --exclude '.settings/*' \
    --exclude 'logs/*' \
    --exclude 'target/*' \
    --exclude '.gitignore' \
    --exclude '.git/*' \
    --exclude '.DS_Store' \
    -C .. \
    Project4CourseRecommender/

echo == ZIPPED-UP ==
ls -l ../$TAR

