define(['flot'], function (flot) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/chart/validation_stamp_statuses'.format(
                config.project,
                config.branch
            )
        },
        render: function (container, append, config, data) {
            var chartId = '{0}-chart'.format($(container).attr('id'));
            var chart = $('<div id="{0}" style="width:800px;height:400px;background-color: yellow;"></div>'.format(chartId))
                .appendTo(container);
            // FIXME Series
            // FIXME Options
            // FIXME Plotting
        }
    }

})