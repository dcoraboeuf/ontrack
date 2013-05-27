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

    function buildRadioButtons () {
        // Last of the froms
        var froms = $('input[name="buildFrom"]');
        if (froms.length > 0) {
            froms[froms.length - 1].setAttribute('checked', 'checked');
        }
        // First of the tos
        var tos = $('input[name="buildTo"]');
        if (tos.length > 0) {
            tos[0].setAttribute('checked', 'checked');
        }
    }

    function setupDiffActions () {
        $('.diff-action').each(function (index, action) {
            // Action attributes
            var path = $(action).attr('path');
            var project = $(action).attr('project');
            var branch = $(action).attr('branch');
            // From & to
            var from = $('input[name="buildFrom"]:checked').val();
            var to = $('input[name="buildTo"]:checked').val();
            // URL
            var url = '{0}?project={1}&branch={2}&from={3}&to={4}'.format(path, project, branch, from, to);
            // Go
            $(action).unbind('click');
            $(action).click(function () {
                location.href = url;
            });
        });
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

            // Diff actions
            var diffActions = [];
            $('.extension-diff-action').each(function (index, def) {
                diffActions.push({
                    extension: $(def).attr('extension'),
                    name: $(def).attr('name'),
                    path: $(def).attr('path'),
                    project: $(def).attr('project'),
                    branch: $(def).attr('branch'),
                    title: $(def).attr('title')
                });
            });

            render.withTemplate('branch-builds', function (branchBuildTemplate) {
                $(target).empty();
                $(target).html(branchBuildTemplate({
                    project: config.project,
                    branch: config.branch,
                    logger: (config.logged == 'true'),
                    branchBuilds: branchBuilds,
                    diffActions: diffActions,
                    totalColspan: branchBuilds.validationStamps.length + 4,
                    filterActive: isFilterActive(project, branch)
                }));

                // Activates the tooltips
                common.tooltips();
                // TODO gridHoverSetup();
                buildRadioButtons();
                setupDiffActions();
            });

        });
    }

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/build'.format(config.project, config.branch)
        },
        data: getCurrentFilterFn(project, branch),
        render: function (target, append, config, branchBuilds) {
            generateTableBranchBuilds(target, config, branchBuilds);
        }
    }

});