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
            // Colours per status
            var colours = {};
            colours['PASSED'] = '#11FF11';
            colours['INTERRUPTED'] = '#666666';
            colours['FAILED'] = '#DD0000';
            colours['INVESTIGATED'] = '#3290E3';
            colours['FIXED'] = '#CCED47';
            colours['DEFECTIVE'] = '#FA8219';
            colours['EXPLAINED'] = '#875624';
            // Indexation per status
            var stamps = [];
            var seriesPerStatus = {};
            var stampIndex = 0;
            for (var stamp in data.table) {
                stamps.push(stamp);
                for (var status in data.table[stamp]) {
                    var count = data.table[stamp][status];
                    if (!seriesPerStatus[status]) {
                        seriesPerStatus[status] = [];
                    }
                    seriesPerStatus[status].push([count, stampIndex]);
                }
                stampIndex++;
            }
            // Getting the series in flot format
            var series = [];
            for (var status in seriesPerStatus) {
                series.push({
                    label: status,
                    color: colours[status],
                    data: seriesPerStatus[status]
                })
            }
            // Ticks for the validation stamps
            var ticks = [];
            for (var i = 0 ; i < stamps.length ; i++) {
                ticks.push([i, stamps[i]])
            }
            // Plotting
            $(chart).plot(
                series,
                {
                    series: {
                        stack: true,
                        bars: {
                            show: true,
                            barWidth: 0.8,
                            lineWidth: 0,
                            horizontal: true,
                            align: 'center'
                        }
                    },
                    xaxis: {
                        tickSize: 1,
                        tickDecimals: 0
                    },
                    yaxis: {
                        ticks: ticks
                    }
                }
            );
        }
    }

})