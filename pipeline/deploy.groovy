import groovy.util.CliBuilder

println "Deploying on GitHub..."

def cli = new CliBuilder(usage: 'deploy')
cli.u(longOpt: 'user', args: 1, argName: 'user', required: true, 'GitHub user')
def options = cli.parse(args)
if (options == null) System.exit(-1)
def gitHubUser = options.user

println "* GitHub user: ${gitHubUser}"
