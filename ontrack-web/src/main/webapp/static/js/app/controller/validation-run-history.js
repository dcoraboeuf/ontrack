define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/validation_run/{0}/history?u=1'.format(config.validationrunid);
        },
        preProcessingFn: function (config, validationRunEvents, append) {
            var list = [];
            var thisRun = false;
            var thisBuild = false;
            var otherBuilds = false;
            $.each (validationRunEvents, function (index, validationRunEvent) {
                // This run?
                if (validationRunEvent.validationRun.id == config.validationrunid) {
                    if (!thisRun) {
                        list.push({
                            header: true,
                            title: 'validationRun.history.thisRun'.loc()
                        });
                        thisRun = true;
                    }
                    validationRunEvent.thisBuild = true;
                    validationRunEvent.thisRun = true;
                } else if (validationRunEvent.validationRun.build.id == config.buildid) {
                    if (!thisBuild) {
                        list.push({
                            header: true,
                            title: 'validationRun.history.thisBuild'.loc(),
                            link: 'gui/project/{0}/branch/{1}/build/{2}'.format(
                                validationRunEvent.validationRun.build.branch.project.name.html(),
                                validationRunEvent.validationRun.build.branch.name.html(),
                                validationRunEvent.validationRun.build.name.html()
                            )
                        });
                        thisBuild = true;
                    }
                    validationRunEvent.thisBuild = true;
                    validationRunEvent.thisRun = false;
                } else {
                    if (!otherBuilds) {
                        list.push({
                            header: true,
                            title: 'validationRun.history.allBuilds'.loc()
                        });
                        otherBuilds = true;
                    }
                    validationRunEvent.thisBuild = false;
                    validationRunEvent.thisRun = false;
                }
                list.push(validationRunEvent);
            });
            return list;
        },
        render: render.asTableTemplate('validation-run-history')
    }

});