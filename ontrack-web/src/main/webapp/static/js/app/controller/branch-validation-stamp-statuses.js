define(function () {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/chart/validation_stamp_statuses'.format(
                config.project,
                config.branch
            )
        }
    }

})