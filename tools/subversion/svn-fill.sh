#!/bin/bash
# Fills a repository with commits

# Working copy
WC=./wc
echo Creating working copy at $WC
rm -rf $WC
svn checkout svn://localhost $WC --username admin --password test

# Folders
svn mkdir $WC/project/trunk --parents
svn mkdir $WC/project/tags --parents
svn mkdir $WC/project/branches --parents
svn commit $WC -m "Project structure" --username admin --password test

