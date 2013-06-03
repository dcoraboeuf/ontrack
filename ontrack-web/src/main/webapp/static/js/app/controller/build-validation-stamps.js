define(['jquery','render'], function($, render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/build/{2}/validationStamps'.format(
                config.project,
                config.branch,
                config.build
            )
        },
        preProcessingFn: function (config, stamps) {
            $.each(stamps, function (index, stamp) {
                if (stamp.run) {
                    var lastRun = stamp.runs[stamp.runs.length - 1];
                    if (lastRun.status == 'PASSED') {
                        stamp.passed = true;
                    }
                }
            });
            return stamps;
        },
        render: render.asSimpleTemplate(
            'build-validation-stamps',
            function (stamps, config) {
                return {
                    project: config.project,
                    branch: config.branch,
                    build: config.build,
                    stamps: stamps
                }
            })
    }

});