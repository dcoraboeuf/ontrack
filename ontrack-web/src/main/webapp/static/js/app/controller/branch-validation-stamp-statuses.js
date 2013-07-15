define(['flot'], function (flot) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/chart/validation_stamp_statuses'.format(
                config.project,
                config.branch
            )
        },
        render: function (container, append, config, data) {
            var id = config.id;
            var chartId = '{0}-chart'.format(id);
            var width = $('#{0}-title'.format(id)).width();
            var height = Object.keys(data.table).length * 20;
            var chart = $(
                '<div id="{0}" style="width:{1}px;height:{2}px;background-color: yellow;"></div>'
                    .format(
                        chartId,
                        width,
                        height
                    )
            )
                .appendTo(container);
            // FIXME Series
            // FIXME Options
            // FIXME Plotting
        }
    }

})