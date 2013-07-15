define(['flot.stack'], function (flot) {

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
            // FIXME var height = Object.keys(data.table).length * 20;
            var height = 400;
            var chart = $(
                '<div id="{0}" style="width:{1}px;height:{2}px;"></div>'
                    .format(
                        chartId,
                        width,
                        height
                    )
            )
                .appendTo(container);
            // Indexation per status
            var seriesPerStatus = {};
            var stampIndex = 0;
            for (var stamp in data.table) {
                for (var status in data.table[stamp]) {
                    var count = data.table[stamp][status];
                    if (!seriesPerStatus[status]) {
                        seriesPerStatus[status] = [];
                    }
                    seriesPerStatus[status].push([stampIndex, count]);
                }
                stampIndex++;
            }
            // Getting the series in flot format
            var series = [];
            for (var status in seriesPerStatus) {
                series.push({
                    label: status,
                    data: seriesPerStatus[status]
                })
            }
            // Plotting
            $(chart).plot(
                series,
                {
                    series: {
                        stack: true,
                        bars: {
                            show: true,
                            barWidth: 1.0
                        }
                    }
                }
            );
        }
    }

})