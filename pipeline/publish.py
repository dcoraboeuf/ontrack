#!/usr/bin/python

import sys
import json
import urllib2

def publish(ontrackURL):
	# Gets the previous build from ontrack
	previousBuildURL = "%s/ui/manage/project/ontrack/branch/1.x/build/withPromotionLevel/CB.PROD" % (ontrackURL)
	sys.stdout.write("Getting previous build from %s\n" % previousBuildURL)
	previousBuildName = json.load(urllib2.urlopen(previousBuildURL))['name']
	sys.stdout.write("Previous build name is %s\n" % (previousBuildName))

if __name__ == '__main__':
	if len(sys.argv) < 2:
		sys.stderr.write("Usage: %s <ontrackURL> TXN\n" % (sys.argv[0]))
	else:
		publish(sys.argv[1])

