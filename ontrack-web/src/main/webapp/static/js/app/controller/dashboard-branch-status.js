define([], function () {

    return {
        url: function (config) {
            return 'ui/dashboard/project/{0}/branch/{1}/status'.format(config.project, config.branch)
        }
    }

})