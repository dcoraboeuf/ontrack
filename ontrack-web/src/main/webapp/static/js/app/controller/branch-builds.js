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

    function saveFilterFn (project, branch, filterName) {
        return function (filter) {
            ajax.put({
                url: 'ui/admin/project/{0}/branch/{1}/filter'.format(project, branch),
                data: {
                    filterName: filterName,
                    filter: filter
                }
            })
        }
    }

    function withFilter(buildFilter, postFilterFn) {
        // Associates the filter with the build section
        $('#branch-builds').data('filter', buildFilter);
        // Fills the hash
        var params = $.param(buildFilter);
        // Sets as hash
        location.hash = params;
        // Reloads
        dynamic.reloadSection('branch-builds');
        // Post filter
        if (postFilterFn) {
            postFilterFn(buildFilter);
        }
    }

    function filterWithForm(config, form, postFilterFn) {
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
        // withProperty
        if (form.withProperty != '') {
            filter.withProperty = {};
            var index = form.withProperty.indexOf('-');
            filter.withProperty.extension = form.withProperty.substring(0, index);
            filter.withProperty.name = form.withProperty.substring(index + 1);
            filter.withProperty.value = form.withPropertyValue;
        }
        // Filter
        withFilter(filter, postFilterFn);
    }

    function isFilterActive(project, branch) {
        var filter = getCurrentFilterFn(project, branch)();
        return filter.limit != 10
            || filter.withPromotionLevel != ''
            || filter.sincePromotionLevel != ''
            || (filter.withValidationStamps && filter.withValidationStamps.length > 0)
            || (filter.sinceValidationStamps && filter.sinceValidationStamps.length > 0)
            || (filter.withProperty)
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
            // Go
            $(action).unbind('click');
            $(action).click(function () {
                // From & to
                var from = $('input[name="buildFrom"]:checked').val();
                var to = $('input[name="buildTo"]:checked').val();
                // URL
                '{0}?project={1}&branch={2}&from={3}&to={4}'.format(path, project, branch, from, to).goto();
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
        config.form.find('#withProperty').val('');
        config.form.find('#withPropertyValue').val('');
        config.form.find('#limit').val('10');
    }

    function showFilter(logged) {
        var section = dynamic.getSectionConfig('branch-builds');
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/filter'.format(section.project, section.branch),
            successFn: function (branchFilterData) {
                branchFilterData.logged = logged;
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
                            sincePromotionLevel: filter.sincePromotionLevel,
                            withProperty: '',
                            withPropertyValue: ''
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
                        // With property
                        if (filter.withProperty) {
                            form.withProperty = '{0}-{1}'.format(filter.withProperty.extension, filter.withProperty.name);
                            form.withPropertyValue = filter.withProperty.value;
                        }
                        // Initialization of fields
                        config.form.find('#withPromotionLevel').val(form.withPromotionLevel);
                        config.form.find('#sincePromotionLevel').val(form.sincePromotionLevel);
                        config.form.find('#withValidationStamp').val(form.withValidationStamp);
                        config.form.find('#withValidationStampStatus').val(form.withValidationStampStatus);
                        config.form.find('#sinceValidationStamp').val(form.sinceValidationStamp);
                        config.form.find('#sinceValidationStampStatus').val(form.sinceValidationStampStatus);
                        config.form.find('#withProperty').val(form.withProperty);
                        config.form.find('#withPropertyValue').val(form.withPropertyValue);
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
                            $('#branch-builds').data('filterName', '');
                            filterWithForm(config, form);
                            // OK
                            config.closeFn();
                            // Does not submit
                            return false;
                        });
                        // Form: save
                        config.form.find('#filter-save').unbind('click');
                        config.form.find('#filter-save').click(function () {
                            // Gets the filter name
                            var filterName = config.form.find('#filter-name').val();
                            if (filterName.trim() != '') {
                                // Gets the values
                                var form = common.values(config.form);
                                // Submitting the query
                                filterWithForm(config, form, saveFilterFn(section.project, section.branch, filterName));
                                // Closed the dialog
                                config.closeFn();
                            }
                        });
                    }
                });
            }
        });
    }

    function setupFilterButton(logged) {
        $('#filter-button').unbind('click');
        $('#filter-button').click(function () {
            showFilter(logged);
        });
        // Filter name
        var filterName = $('#branch-builds').data('filterName');
        if (filterName && filterName != '') {
            $('#filter-button').text(
                '{0} - {1}'.format(
                    'query'.loc(),
                    filterName
                )
            );
        }
    }

    function setupSavedFilters(config, branchBuilds) {
        $('.saved-filter').each(function (index, a) {
            var filterName = $(a).attr('data-filter-name');
            // Looking for the corresponding filter
            var filter = null;
            $.each(branchBuilds.savedBuildFilters, function (i, savedBuildFilter) {
                if (savedBuildFilter.filterName == filterName) {
                    filter = savedBuildFilter.filter;
                }
            });
            // In case of filter found, makes a link
            if (filter != null) {
                $(a).click(function () {
                    $('#branch-builds').data('filterName', filterName);
                    withFilter(filter);
                });
            // Filter not found, do not display it
            } else {
                $(a).remove();
            }
        })
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
                setupFilterButton(config.logged == 'true');
                setupSavedFilters(config, branchBuilds);
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