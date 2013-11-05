#!/usr/bin/python

import sys
import json
import urllib2
import base64

# Gets the previous build from ontrack
def getLastBuild(ontrackURL, ontrackBranch):
    previousBuildURL = "%s/ui/manage/project/ontrack/branch/%s/build/withPromotionLevel/CB.PROD" % (ontrackURL, ontrackBranch)
    sys.stdout.write("Getting previous build from %s\n" % previousBuildURL)
    previousBuildName = json.load(urllib2.urlopen(previousBuildURL))['name']
    return previousBuildName
	
def createBuild(ontrackURL, ontrackBranch, version, username, password):
    buildCreationURL = "%s/ui/control/project/ontrack/branch/%s/build" % (ontrackURL, ontrackBranch)
    sys.stdout.write("Creating build %s at %s\n" % (version, buildCreationURL))
    form = { 'name': version, 'description': 'Created by build' }
    req = urllib2.Request(buildCreationURL)
    req.add_header('Content-Type', 'application/json')
    base64string = base64.encodestring("%s:%s" % (username, password)).replace('\n', '')
    req.add_header("Authorization", "Basic %s" % base64string)
    urllib2.urlopen(req, json.dumps(form))

def getChangeLog(ontrackURL, ontrackBranch, version, previousVersion):
    changeLogURL = "%s/ui/extension/github/issues/text" % (ontrackURL)
    sys.stdout.write("Getting change log from %s" % changeLogURL)
    form = { 'project': 'ontrack', 'branch' : ontrackBranch, 'from': previousVersion, 'to': version}
    req = urllib2.Request(changeLogURL)
    req.add_header('Content-Type', 'application/json')
    return urllib2.urlopen(req, json.dumps(form)).read()

def updateGithubRelease(githubUser, githubPassword, releaseId, changeLog):
    githubURL = "https://api.github.com/repos/%s/ontrack/releases/%d" % (githubUser, releaseId)
    sys.stdout.write("Editing release at %s\n" % githubURL)
    form = { 'body': changeLog }
    req = urllib2.Request(githubURL)
    req.add_header('Content-Type', 'application/json')
    req.add_header('Accept', 'application/vnd.github.manifold-preview')
    base64string = base64.encodestring("%s:%s" % (githubUser, githubPassword)).replace('\n', '')
    req.add_header("Authorization", "Basic %s" % base64string)
    urllib2.urlopen(req, json.dumps(form))

def publish(ontrackURL, ontrackBranch, version, ontrackUser, ontrackPassword, githubUser, githubPassword, releaseId):
    # Gets the previous build from ontrack
    previousVersion = getLastBuild(ontrackURL, ontrackBranch)
    sys.stdout.write("Previous build name is %s\n" % previousVersion)
    #  Notifying the build creation for ontrack
    createBuild(ontrackURL, ontrackBranch, version, ontrackUser, ontrackPassword)
    # Getting the list of issues as text
    changelog = getChangeLog(ontrackURL, ontrackBranch, version, previousVersion)
    sys.stdout.write("Change log is \n%s\n" % changelog)
    # Updating the release body on GitHub
    updateGithubRelease(githubUser, githubPassword, releaseId, changelog)

if __name__ == '__main__':
    if len(sys.argv) < 9:
        sys.stderr.write("Usage: %s <ontrackURL> <ontrackBranch> <version> <ontrackUsername> <ontrackPassword> <githubUser> <githubToken> <githubReleaseId>\n" % (sys.argv[0]))
    else:
        publish(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6], sys.argv[7], sys.argv[8])

