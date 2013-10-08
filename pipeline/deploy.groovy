// Do not forget to set the Grape settings (http://groovy.codehaus.org/Grape#Grape-CustomizeIvysettings)
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.6')

import groovy.util.CliBuilder
import groovyx.net.http.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

println "Deploying on GitHub..."

// CLI arguments parsing

def cli = new CliBuilder(usage: 'deploy')
cli.u(longOpt: 'user', args: 1, argName: 'user', required: true, 'GitHub user')
cli.i(longOpt: 'token', args: 1, argName: 'token', required: true, 'GitHub API token')
cli.t(longOpt: 'tag', args: 1, argName: 'tag', required: true, 'GitHub tag name')
cli.d(longOpt: 'debug', 'Enables debugging')
cli.d(longOpt: 'verbose', 'Enables verbose')
def options = cli.parse(args)
if (options == null) System.exit(-1)
def gitHubUser = options.user
def gitHubToken = options.token
def gitHubTag = options.tag
def debug = options.debug
def verbose = options.verbose

// Summary

println "* GitHub user:  ${gitHubUser}"
println "* GitHub token: ${gitHubToken}"
println "* GitHub tag:   ${gitHubTag}"

// Logging
System.setProperty('org.apache.commons.logging.Log', 'org.apache.commons.logging.impl.SimpleLog')
if (verbose) System.setProperty('org.apache.commons.logging.simplelog.log.org.apache.http.wire', 'DEBUG')
else if (debug) System.setProperty('org.apache.commons.logging.simplelog.log.org.apache.http.headers', 'DEBUG')

// Creating the release on top of a tag

def http = new HTTPBuilder('https://api.github.com/repos/dcoraboeuf/ontrack/releases')
http.auth.basic gitHubUser, gitHubToken
http.request(POST, JSON) { req ->
    headers.Accept = 'application/vnd.github.manifold-preview'
    body = [
            tag_name: gitHubTag
    ]
    response.success = { resp, json ->
        println json
    }
}