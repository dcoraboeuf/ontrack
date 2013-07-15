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
            // Series
            var countsPerStatus = {};
            for (var stamp in data.table) {
                for (var status in data.table[stamp]) {
                    var count = data.table[stamp][status];
                    if (!countsPerStatus[status]) {
                        countsPerStatus[status] = [];
                    }
                    countsPerStatus[status].push(count);
                }
            }
            // FIXME Options
            // FIXME Plotting
            $(chart).plot(
                [{
                    label: 'PASSED',
                    data: [[
                        0, 2
                    ], [
                        1, 4
                    ], [
                        2, 0
                    ]]
                }, {
                    label: 'FAILED',
                    data: [[
                        0, 8
                    ], [
                        1, 0
                    ], [
                        2, 4
                    ]]
                }],
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