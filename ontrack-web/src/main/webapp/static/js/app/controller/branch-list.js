define(['render'], function(render){

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/status'.format(config.project);
        },
        render: render.asSimpleTemplate('branch-list')
    }

});