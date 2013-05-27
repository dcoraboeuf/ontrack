define(['render','ajax','dynamic','common'], function (render, ajax, dynamic, common) {

    function getCurrentFilterFn (project, branch) {
        return function () {
            var filter = $('#branch-builds').data('filter');
            var hash = location.hash;
            var cookie = common.getCookie('{0}|{1}|filter'.format(project, branch));
            if (filter) {
                return filter;
            } else if (hash && hash != '' && hash != '#') {
                filter = $.deparam(hash.substring(1));
                $('#builds').data('filter', filter);
                return filter;
            } else if (cookie) {
                var json = eval(cookie);
                filter = $.parseJSON(json);
                $('#builds').data('filter', filter);
                return filter;
            } else {
                return {
                    limit: 10,
                    sincePromotionLevel: '',
                    withPromotionLevel: '',
                    sinceValidationStamps: [],
                    withValidationStamps: []
                };
            }
        }
    }

    function isFilterActive (project, branch) {
        var filter = getCurrentFilterFn(project, branch)();
        return filter.limit != 10
            || filter.withPromotionLevel != ''
            || filter.sincePromotionLevel != ''
            || (filter.withValidationStamps && filter.withValidationStamps.length > 0)
            || (filter.sinceValidationStamps && filter.sinceValidationStamps.length > 0);
    }

    function generateTableBranchBuilds (target, config, branchBuilds) {

        render.withTemplate('branch-build-stamp', function (branchBuildStamp) {

            Handlebars.registerHelper('branchBuildValidationStampFn', function (buildName, context, options) {
                // Looks for the build
                var oBuild = $.grep(branchBuilds.builds, function (aBuild) {
                    return aBuild.name == buildName;
                })[0];
                // Looks for the validation stamp build
                var oBuildValidationStamp = oBuild.validationStamps[context.summary.name];
                // Last run
                var lastRun;
                if (oBuildValidationStamp.run) {
                    lastRun = oBuildValidationStamp.runs[oBuildValidationStamp.runs.length - 1];
                }
                // Rendering
                return branchBuildStamp({
                    validationStamp: this,
                    project: config.project,
                    branch: config.branch,
                    build: buildName,
                    buildValidationStamp: oBuildValidationStamp,
                    lastRun: lastRun
                });
            });

            render.withTemplate('branch-builds', function (branchBuildTemplate) {
                $(target).empty();
                $(target).html(branchBuildTemplate({
                    project: config.project,
                    branch: config.branch,
                    logger: (config.logged == 'true'),
                    branchBuilds: branchBuilds,
                    totalColspan: branchBuilds.validationStamps.length + 4,
                    filterActive: isFilterActive(project, branch)
                }));

                // Activates the tooltips
                common.tooltips();
            });

        });
    }

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/build'.format(config.project, config.branch)
        },
        data: getCurrentFilterFn(project, branch),
        render: function (target, append, config, branchBuilds) {
            if (append === true && $(target).has('tbody').length) {
                $(target).find('tbody').append(generateTableBuildRows(project, branch, branchBuilds));
            } else {
                // No table defined, or no need to append
                generateTableBranchBuilds(target, config, branchBuilds);
            }
        }
    }

});