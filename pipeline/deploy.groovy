// Do not forget to set the Grape settings (http://groovy.codehaus.org/Grape#Grape-CustomizeIvysettings)
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )

import groovy.util.CliBuilder

println "Deploying on GitHub..."

// CLI arguments parsing

def cli = new CliBuilder(usage: 'deploy')
cli.u(longOpt: 'user', args: 1, argName: 'user', required: true, 'GitHub user')
cli.i(longOpt: 'token', args: 1, argName: 'token', required: true, 'GitHub API token')
cli.t(longOpt: 'tag', args: 1, argName: 'tag', required: true, 'GitHub tag name')
def options = cli.parse(args)
if (options == null) System.exit(-1)
def gitHubUser = options.user
def gitHubToken = options.token
def gitHubTag = options.tag

// Summary

println "* GitHub user:  ${gitHubUser}"
println "* GitHub token: ${gitHubToken}"
println "* GitHub tag:   ${gitHubTag}"

//
