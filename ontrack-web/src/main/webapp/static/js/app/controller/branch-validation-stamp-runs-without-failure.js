define(['flot'], function (flot) {

    return {
        url: function (config) {
            return 'ui/chart/project/{0}/branch/{1}/chart/validation_stamp_runs_without_failure'.format(
                config.project,
                config.branch
            )
        },
        render: function (container, append, config, data) {
            var id = config.id;
            var chartId = '{0}-chart'.format(id);
            // Getting the series in flot format
            var serie = [];
            var ticks = [];
            var index = 0;
            $.each(data, function (i, pair) {
                var stamp = pair.key;
                var count = pair.value;
                ticks.push([i, stamp]);
                serie.push([count, i]);
            });
            // Preparation of the chart area
            var width = $('#{0}-title'.format(id)).width();
            // Not enough data
            if (serie.length == 0) {
                $('<div></div>')
                    .addClass('alert')
                    .addClass('alert-warning')
                    .text('branch.charts.nodata'.loc())
                    .appendTo(container);
            } else {
                var height = Math.max(
                    serie.length * 24,
                    60);
                var chart = $(
                    '<div id="{0}" style="width:{1}px;height:{2}px;"></div>'
                        .format(
                            chartId,
                            width,
                            height
                        )
                )
                    .appendTo(container);
                // Plotting
                $(chart).plot(
                    [ serie ],
                    {
                        series: {
                            color: 'green',
                            bars: {
                                show: true,
                                barWidth: 0.8,
                                lineWidth: 0,
                                horizontal: true,
                                align: 'center'
                            }
                        },
                        xaxis: {
                            tickDecimals: 0,
                            max: 30
                        },
                        yaxis: {
                            ticks: ticks
                        }
                    }
                );
            }
        }
    }

})