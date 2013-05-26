define(['render'], function(render){

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch'.format(config.project);
        },
        render: render.asTableTemplate('branch-row')
    }

});