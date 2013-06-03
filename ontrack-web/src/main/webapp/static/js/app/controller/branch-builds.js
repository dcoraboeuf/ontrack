define(['render', 'ajax', 'dynamic', 'common', 'dialog', 'jquery'], function (render, ajax, dynamic, common, dialog, $) {

    function getCurrentFilterFn(project, branch) {
        return function () {
            var filter = $('#branch-builds').data('filter');
            var hash = location.hash;
            var cookie = common.getCookie('{0}|{1}|filter'.format(project, branch));
            if (filter) {
                return filter;
            } else if (hash && hash != '' && hash != '#') {
                filter = common.deparam(hash.substring(1));
                $('#branch-builds').data('filter', filter);
                return filter;
            } else if (cookie) {
                var json = eval(cookie);
                filter = $.parseJSON(json);
                $('#branch-builds').data('filter', filter);
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

    function withFilter(buildFilter) {
        // Associates the filter with the build section
        $('#branch-builds').data('filter', buildFilter);
        // Fills the hash
        var params = $.param(buildFilter);
        // Sets as hash
        location.hash = params;
        // Reloads
        dynamic.reloadSection('branch-builds');
    }

    function filterWithForm(config, form) {
        // Conversion into a BuildFilter
        var filter = {
            sincePromotionLevel: form.sincePromotionLevel,
            withPromotionLevel: form.withPromotionLevel,
            limit: form.limit
        };
        // sinceValidationStamps
        if (form.sinceValidationStamp != '') {
            var statuses = [];
            if (form.sinceValidationStampStatus != '') {
                statuses.push(form.sinceValidationStampStatus);
            }
            filter.sinceValidationStamps = [
                {
                    validationStamp: form.sinceValidationStamp,
                    statuses: statuses
                }
            ];
        }
        // withValidationStamps
        if (form.withValidationStamp != '') {
            var statuses = [];
            if (form.withValidationStampStatus != '') {
                statuses.push(form.withValidationStampStatus);
            }
            filter.withValidationStamps = [
                {
                    validationStamp: form.withValidationStamp,
                    statuses: statuses
                }
            ];
        }
        // Filter
        withFilter(filter);
    }

    function isFilterActive(project, branch) {
        var filter = getCurrentFilterFn(project, branch)();
        return filter.limit != 10
            || filter.withPromotionLevel != ''
            || filter.sincePromotionLevel != ''
            || (filter.withValidationStamps && filter.withValidationStamps.length > 0)
            || (filter.sinceValidationStamps && filter.sinceValidationStamps.length > 0);
    }

    function buildRadioButtons() {
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

    function setupDiffActions() {
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

    function clearFilter(config) {
        // Clears the form
        config.form.find('#withPromotionLevel').val('');
        config.form.find('#sincePromotionLevel').val('');
        config.form.find('#withValidationStamp').val('');
        config.form.find('#withValidationStampStatus').val('');
        config.form.find('#sinceValidationStamp').val('');
        config.form.find('#sinceValidationStampStatus').val('');
        config.form.find('#limit').val('10');
    }

    function showFilter() {
        var config = dynamic.getSectionConfig('branch-builds');
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/filter'.format(config.project, config.branch),
            successFn: function (branchFilterData) {
                dialog.show({
                    templateId: 'branch-builds-filter',
                    title: 'query'.loc(),
                    width: 800,
                    buttons: [],
                    data: branchFilterData,
                    initFn: function (config) {
                        // Gets the current filter
                        var filter = getCurrentFilterFn(config.project, config.branch)();
                        // Converts into a form (best effort)
                        var form = {
                            limit: filter.limit,
                            withPromotionLevel: filter.withPromotionLevel,
                            sincePromotionLevel: filter.sincePromotionLevel
                        };
                        // Since validation stamp
                        if (filter.sinceValidationStamps && filter.sinceValidationStamps.length > 0) {
                            form.sinceValidationStamp = filter.sinceValidationStamps[0].validationStamp;
                            if (filter.sinceValidationStamps[0].statuses && filter.sinceValidationStamps[0].statuses.length > 0) {
                                form.sinceValidationStampStatus = filter.sinceValidationStamps[0].statuses[0];
                            }
                        }
                        // With validation stamp
                        if (filter.withValidationStamps && filter.withValidationStamps.length > 0) {
                            form.withValidationStamp = filter.withValidationStamps[0].validationStamp;
                            if (filter.withValidationStamps[0].statuses && filter.withValidationStamps[0].statuses.length > 0) {
                                form.withValidationStampStatus = filter.withValidationStamps[0].statuses[0];
                            }
                        }
                        // Initialization of fields
                        config.form.find('#withPromotionLevel').val(form.withPromotionLevel);
                        config.form.find('#sincePromotionLevel').val(form.sincePromotionLevel);
                        config.form.find('#withValidationStamp').val(form.withValidationStamp);
                        config.form.find('#withValidationStampStatus').val(form.withValidationStampStatus);
                        config.form.find('#sinceValidationStamp').val(form.sinceValidationStamp);
                        config.form.find('#sinceValidationStampStatus').val(form.sinceValidationStampStatus);
                        config.form.find('#limit').val(form.limit);
                        // Button: cancel
                        config.form.find('#filter-cancel').click(function () {
                            config.closeFn();
                        });
                        // Button: clear
                        config.form.find('#filter-clear').click(function () {
                            clearFilter(config);
                        });
                        // Form: submit
                        config.form.unbind('submit');
                        config.form.submit(function () {
                            // Gets the values
                            var form = common.values(config.form);
                            // Submitting the query
                            filterWithForm(config, form);
                            // OK
                            config.closeFn();
                            // Does not submit
                            return false;
                        });
                    }
                });
            }
        });
    }

    function setupFilterButton() {
        $('#filter-button').unbind('click');
        $('#filter-button').click(function () {
            showFilter();
        });
    }

    function generateTableBranchBuilds(target, config, branchBuilds) {

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
                    logged: (config.logged == 'true'),
                    branchBuilds: branchBuilds,
                    diffActions: diffActions,
                    totalColspan: branchBuilds.validationStamps.length + 4,
                    filterActive: isFilterActive(config.project, config.branch)
                }));

                // Activates the tooltips
                common.tooltips();
                // TODO gridHoverSetup();
                buildRadioButtons();
                setupDiffActions();
                setupFilterButton();
            });

        });
    }

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/build'.format(config.project, config.branch)
        },
        data: function (config) {
            return getCurrentFilterFn(config.project, config.branch)();
        },
        render: function (target, append, config, branchBuilds) {
            generateTableBranchBuilds(target, config, branchBuilds);
        }
    }

});