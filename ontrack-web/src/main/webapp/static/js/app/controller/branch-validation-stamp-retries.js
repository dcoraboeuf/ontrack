define(['flot'], function (flot) {

    return {
        url: function (config) {
            return 'ui/chart/project/{0}/branch/{1}/chart/validation_stamp_retries'.format(
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
                var percentage = pair.value;
                ticks.push([i, stamp]);
                serie.push([percentage * 100.0, i]);
            });
            // Preparation of the chart area
            var width = $('#{0}-title'.format(id)).width();
            var height = serie.length * 24;
            // Not enough data
            if (height == 0) {
                $('<div></div>')
                    .addClass('alert')
                    .addClass('alert-warning')
                    .text('branch.charts.nodata'.loc())
                    .appendTo(container);
            } else {
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
                            bars: {
                                show: true,
                                barWidth: 0.8,
                                lineWidth: 0,
                                horizontal: true,
                                align: 'center'
                            }
                        },
                        xaxis: {
                            tickDecimals: 0
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