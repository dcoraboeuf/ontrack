#!/usr/bin/python

import sys
import json
import urllib2
import base64
import argparse

# Gets the previous build from ontrack
def getLastBuild(options):
    print "Getting previous build from ontrack..."
    url = "%s/ui/manage/project/ontrack/branch/%s/build/withPromotionLevel/CB.PROD" % (
        options.ontrack_url,
        options.ontrack_branch
    )
    previousBuildName = json.load(urllib2.urlopen(url))['name']
    print "Previous build is %s" % previousBuildName
    return previousBuildName


def createBuild(options):
    buildName = "ontrack-%s" % options.version
    print "Creating build %s on ontrack..." % buildName
    url = "%s/ui/control/project/ontrack/branch/%s/build" % (
        options.ontrack_url,
        options.ontrack_branch
    )
    form = {'name': buildName, 'description': 'Created by build'}
    req = urllib2.Request(url)
    req.add_header('Content-Type', 'application/json')
    base64string = base64.encodestring("%s:%s" % (options.ontrack_user, options.ontrack_password)).replace('\n', '')
    req.add_header("Authorization", "Basic %s" % base64string)
    urllib2.urlopen(req, json.dumps(form))


def getChangeLog(options, previousVersion):
    buildName = "ontrack-%s" % options.version
    print "Getting change log since %s" % previousVersion
    url = "%s/ui/extension/github/issues/text" % (
        options.ontrack_url
    )
    # FIXME Reference tag
    form = {'project': 'ontrack', 'branch': options.ontrack_branch, 'from': previousVersion, 'to': 'ontrack-1.42'}
    req = urllib2.Request(url)
    req.add_header('Content-Type', 'application/json')
    changelog = urllib2.urlopen(req, json.dumps(form)).read()
    print "Change log is:\n%s\n" % changelog
    return changelog


def callGithub(options, url, form, type='application/json'):
    req = urllib2.Request(url)
    req.add_header('Content-Type', type)
    req.add_header('Accept', 'application/vnd.github.manifold-preview')
    base64string = base64.encodestring("%s:%s" % (options.github_user, options.github_token)).replace('\n', '')
    req.add_header("Authorization", "Basic %s" % base64string)
    try:
        if type == 'application/json':
            data = json.dumps(form)
        else:
            data = form
        return urllib2.urlopen(req, data)
    except urllib2.HTTPError as e:
        sys.stderr.write("GitHub error:\n%s\n" % e)


def updateGithubRelease(githubUser, githubPassword, releaseId, changeLog):
    githubURL = "https://api.github.com/repos/%s/ontrack/releases/%s" % (githubUser, releaseId)
    sys.stdout.write("Editing release at %s\n" % githubURL)
    form = {'body': changeLog}
    req = urllib2.Request(githubURL)
    req.add_header('Content-Type', 'application/json')
    req.add_header('Accept', 'application/vnd.github.manifold-preview')
    base64string = base64.encodestring("%s:%s" % (githubUser, githubPassword)).replace('\n', '')
    req.add_header("Authorization", "Basic %s" % base64string)
    urllib2.urlopen(req, json.dumps(form))


def createGithubRelease(options):
    print "Creating release %s on GitHub..." % (options.version)
    response = callGithub(
        options,
        "https://api.github.com/repos/%s/ontrack/releases" % options.github_user,
        {'tag_name': ("ontrack-%s" % options.version), 'name': ("v%s" % options.version)})
    releaseId = json.load(response)['id']
    print "Release ID is %d" % releaseId
    return releaseId


def uploadGithubArtifact(options, releaseId, name, type, path):
    print "Uploading artifact %s to release %s on GitHub from %s..." % (name, options.version, path)
    # Opens the artifact
    data = open(path, 'rb').read()
    response = callGithub(
        options,
        "https://uploads.github.com/repos/%s/ontrack/releases/%s/assets?name=%s" % (
            options.github_user, releaseId, name),
        data,
        type
    )


def uploadGithubRelease(options, releaseId):
    # HPI
    uploadGithubArtifact(options, releaseId, 'ontrack.hpi', 'application/zip', 'ontrack-jenkins/target/ontrack.hpi')
    # WAR
    uploadGithubArtifact(options, releaseId, 'ontrack.war', 'application/zip', 'ontrack-war/target/ontrack.war')


def publish(options):
    # Creating the release on GitHub
    releaseId = createGithubRelease(options)
    # Upload of artifacts
    if (options.github_upload):
        uploadGithubRelease(options, releaseId)
    # Gets the previous build from ontrack
    previousVersion = getLastBuild(options)
    #  Notifying the build creation for ontrack
    createBuild(options)
    # Getting the list of issues as text
    changelog = getChangeLog(options, previousVersion)
    # # Updating the release body on GitHub
    # updateGithubRelease(githubUser, githubPassword, releaseId, changelog)


if __name__ == '__main__':
    # Argument definitions
    parser = argparse.ArgumentParser(description='GitHub/ontrack publication')
    parser.add_argument('--ontrack-url', required=True, help='ontrack URL')
    parser.add_argument('--ontrack-branch', required=False, help='ontrack Branch (optional, defaults to "1.x")',
                        default='1.x')
    parser.add_argument('--version', required=True, help='Version to publish')
    parser.add_argument('--ontrack-user', required=True, help='ontrack user used to create the build')
    parser.add_argument('--ontrack-password', required=True, help='ontrack password used to create the build')
    parser.add_argument('--github-upload', action='store_true', required=False,
                        help='Set to upload artifacts on GitHub')
    parser.add_argument('--github-user', required=True, help='GitHub user used to publish the release')
    parser.add_argument('--github-token', required=True,
                        help='GitHub password or API token used to publish the release')
    # Parsing of arguments
    options = parser.parse_args()
    # Calling the publication
    publish(options)

