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
            for (var stamp in data) {
                var count = data[stamp];
                ticks.push([index, stamp]);
                serie.push([count, index++]);
            }
            // Preparation of the chart area
            var width = $('#{0}-title'.format(id)).width();
            var height = serie.length * 24;
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

})