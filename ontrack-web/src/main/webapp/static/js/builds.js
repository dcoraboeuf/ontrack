var Builds = function () {

    function generateTableBuildRows (project, branch, branchBuilds) {
        Handlebars.registerHelper('promotionLevelsFn', function(options) {
            return generateBuildPromotionLevels(project,branch)(this.promotionLevels);
        });
        Handlebars.registerHelper('compactValidationStampFn', function(build, context) {
            // Looks for the build
            var oBuild = $.grep(branchBuilds.builds, function (aBuild) {
                return aBuild.name == build;
            })[0];
            // Looks for the validation stamp build
            var oBuildValidationStamp = oBuild.validationStamps[this.summary.name];
            // Last run
            var lastRun;
            if (oBuildValidationStamp.run) {
                lastRun = oBuildValidationStamp.runs[oBuildValidationStamp.runs.length - 1];
            }
            // Rendering
            return Template.render('compactValidationStampTemplate', {
                validationStamp: this,
                project: project,
                branch: branch,
                build: build,
                buildValidationStamp: oBuildValidationStamp,
                lastRun: lastRun
            });
        });
        return Template.render('branchBuildsRowTemplate', {
                project: project,
                branch: branch,
                branchBuilds: branchBuilds
            }
        );
    }

    function generateTableBranchBuilds (project, branch, branchBuilds) {
        return Template.render('branchBuildsTemplate', {
                project: project,
                branch: branch,
                branchBuilds: branchBuilds,
                totalColspan: branchBuilds.validationStamps.length + 4,
                rowFn: function (text, renderFn) {
                    return generateTableBuildRows (project, branch, branchBuilds);
                },
                filterActive: isFilterActive(project, branch)
            }
        );
    }

    function isFilterActive (project, branch) {
        var filter = getCurrentFilter(project, branch)();
        return filter.limit != 10
            || filter.withPromotionLevel != ''
            || filter.sincePromotionLevel != ''
            || (filter.withValidationStamps && filter.withValidationStamps.length > 0)
            || (filter.sinceValidationStamps && filter.sinceValidationStamps.length > 0);
    }

    function gridHoverSetup () {
        // Clean-up
        $('td[build]').off('mouseenter mouseleave');
        $('td[validation_stamp]').off('mouseenter mouseleave');
        // Build hovering
        $('td[build]').hover(
            function (e) {
                var build = $(e.currentTarget).attr('build');
                $("td[build='{0}']".format(build)).addClass('build-hover');
            },
            function (e) {
                var build = $(e.currentTarget).attr('build');
                $("td[build='{0}']".format(build)).removeClass('build-hover');
            }
        );
        // Build hovering
        $('td[validation_stamp]').hover(
            function (e) {
                var validation_stamp = $(e.currentTarget).attr('validation_stamp');
                $("td[validation_stamp='{0}']".format(validation_stamp)).addClass('validation-stamp-hover');
            },
            function (e) {
                var validation_stamp = $(e.currentTarget).attr('validation_stamp');
                $("td[validation_stamp='{0}']".format(validation_stamp)).removeClass('validation-stamp-hover');
            }
        );
    }

    function getCurrentFilter (project, branch) {
        return function () {
            var filter = $('#builds').data('filter');
            var hash = location.hash;
            var cookie = getCookie('{0}|{1}|filter'.format(project, branch));
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
	
	function buildTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/build'.format(project, branch),
	        more: false, // Managed by the filter
	        refresh: true,
	        data: getCurrentFilter(project, branch),
	        render: function (containerId, append, config, branchBuilds) {
                var containerSelector = '#' + containerId;
                if (append === true && $(containerSelector).has("tbody").length) {
                    $(containerSelector + " tbody").append(generateTableBuildRows(project, branch, branchBuilds));
                } else {
                    // No table defined, or no need to append
                    $(containerSelector).empty();
                    $(containerSelector).append(generateTableBranchBuilds(project, branch, branchBuilds));
                }
	        },
	        postRenderFn: function () {
	            Application.tooltips();
	            gridHoverSetup();
	            buildRadioButtons();
	        }
         });
	}

	function buildValidationStampTemplate (project, branch, build) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/build/{2}/validationStamps'.format(project, branch, build),
	        refresh: true,
            preProcessingFn: function (stamps) {
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
	        render: Template.asSimpleTemplate('buildValidationStampsTemplate', function (stamps) {
                return {
                    project: project,
                    branch: branch,
                    build: build,
                    stamps: stamps
	            };
            }),
	        postRenderFn: Application.tooltips
	    });
	}

	function generateBuildPromotionLevels (project, branch) {
	    return function (promotionLevels) {
	        return Template.render('promotionLevelsTemplate', {
	            project: project,
	            branch: branch,
	            promotionLevels: promotionLevels
	        });
        };
	}

	function buildPromotionLevelsTemplate (project, branch, build) {
        return Template.config({
            url: 'ui/manage/project/{0}/branch/{1}/build/{2}/promotionLevels'.format(project, branch, build),
            refresh: true,
            render: Template.fill(generateBuildPromotionLevels(project,branch))
        });
	}

	function filterFormTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/filter'.format(project, branch),
	        render: Template.asSimpleTemplate('filterFormTemplate')
	    });
	}

	function withFilter (buildFilter) {
        // Associates the filter with the build section
        $('#builds').data('filter', buildFilter);
        // Fills the hash
        var params = $.param(buildFilter);
        // Sets as hash
        location.hash = params;
        // Reloads
        Template.reload('builds');
	}

	function filterWithForm (form) {
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
            filter.sinceValidationStamps = [{
                validationStamp: form.sinceValidationStamp,
                statuses: statuses
            }];
        }
        // withValidationStamps
        if (form.withValidationStamp != '') {
            var statuses = [];
            if (form.withValidationStampStatus != '') {
                statuses.push(form.withValidationStampStatus);
            }
            filter.withValidationStamps = [{
                validationStamp: form.withValidationStamp,
                statuses: statuses
            }];
        }
        // Filter
        withFilter(filter);
	}

	function showFilter (project, branch) {
        Application.dialog({
            id: 'filter-form',
            title: loc('query'),
            width: 800,
            openFn: function () {
                // Gets the current filter
                var filter = getCurrentFilter(project, branch)();
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
                $('#withPromotionLevel').val(form.withPromotionLevel);
                $('#sincePromotionLevel').val(form.sincePromotionLevel);
                $('#withValidationStamp').val(form.withValidationStamp);
                $('#withValidationStampStatus').val(form.withValidationStampStatus);
                $('#sinceValidationStamp').val(form.sinceValidationStamp);
                $('#sinceValidationStampStatus').val(form.sinceValidationStampStatus);
                $('#limit').val(form.limit);
            },
            submitFn: function (closeFn) {
                // Gets the values
                var form = Application.values('filter-form');
                // Submitting the query
                filterWithForm(form);
                // OK
                closeFn();
            }
        });
	}

	function closeFilter () {
	    $('#filter-form').dialog('close');
	}

	function clearFilter () {
        // Clears the form
        $('#withPromotionLevel').val('');
        $('#sincePromotionLevel').val('');
        $('#withValidationStamp').val('');
        $('#withValidationStampStatus').val('');
        $('#sinceValidationStamp').val('');
        $('#sinceValidationStampStatus').val('');
        $('#limit').val('10');
	}

	function diffAction (btn) {
	    // Action attributes
	    var project = $(btn).attr('project');
	    var branch = $(btn).attr('branch');
	    var path = $(btn).attr('path');
	    // From & to
	    var from = $('input[name="buildFrom"]:checked').val();
	    var to = $('input[name="buildTo"]:checked').val();
	    // Logging
	    // console.log('project={0},branch={1},path={2},from={3},to={4}'.format(project, branch, path, from, to));
	    // URL
	    var url = '{0}?project={1}&branch={2}&from={3}&to={4}'.format(path, project, branch, from, to);
	    // Go
	    location = url;
	}

    function promoteBuild (project, branch, build) {
        // Gets the list of promotions
        AJAX.get({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(project, branch),
            successFn: function (promotionLevels) {
                // Displays the dialog
                Application.dialog({
                    id: 'build-promotion',
                    title: loc('build.promote.title', build),
                    openFn: function () {
                        // No description by default
                        $('#build-promotion-description').val('');
                        // List of promotion levels
                        $('#build-promotion-promotionLevel').empty();
                        $.each(promotionLevels, function (index, promotionLevel) {
                            var option = $('<option></option>')
                                .attr('value', promotionLevel.name)
                                .text(promotionLevel.name);
                            $('#build-promotion-promotionLevel').append(option);
                        });
                        // TODO Date picker to externalize (see #160)
                        // Date picker
                        var now = new Date();
                        $('#build-promotion-creation').datepicker('destroy');
                        $('#build-promotion-creation').datepicker({
                            showOtherMonths: true,
                            selectOtherMonths: true
                        });
                        $('#build-promotion-creation').datepicker('setDate', now);
                        // Time picker
                        $('#build-promotion-creation-time').empty();
                        var currentHour = now.getHours();
                        var currentQuarter = Math.round(now.getMinutes() / 15);
                        for (var hour = 0 ; hour < 24 ; hour++) {
                            var formattedHours = formatTimePart(hour);
                            for (var quarter = 0 ; quarter < 4 ; quarter++) {
                                var minutes = quarter * 15;
                                var formattedMinutes = formatTimePart(minutes);
                                var formattedTime = "{0}:{1}".format(formattedHours, formattedMinutes);
                                var option = $('<option></option>')
                                    .attr('value', hour * 60 + minutes)
                                    .text(formattedTime);
                                if (currentHour == hour && currentQuarter == quarter) {
                                    option.attr('selected', 'selected');
                                }
                                $('#build-promotion-creation-time').append(option);
                            }
                        }
                    },
                    submitFn: function (closeFn) {
                        // Collects the data
                        var promotionLevel = $('#build-promotion-promotionLevel').val();
                        var description = $('#build-promotion-description').val();
                        // Data to send
                        var data = {
                            description: description
                        };
                        // Date
                        var time;
                        var creation = $('#build-promotion-creation').datepicker('getDate');
                        if (creation != null) {
                            time = creation.getTime();
                        } else {
                            time = new Date().getTime();
                        }
                        // Time
                        var selectedTimeInMinutes = Number($('#build-promotion-creation-time').val());
                        data.creation = time + selectedTimeInMinutes * 60 * 1000;
                        // Call
                        AJAX.post({
                            url: 'ui/control/project/{0}/branch/{1}/build/{2}/promotion_level/{3}'.format(project, branch, build, promotionLevel),
                            data: data,
                            loading: {
                                el: $('#build-promotion-submit')
                            },
                            successFn: function (result) {
                                closeFn();
                                location.reload();
                            }
                        });
                    }
                });
            }
        });
    }
	
	return {
	    // Templating
		buildTemplate: buildTemplate,
		buildValidationStampTemplate: buildValidationStampTemplate,
		buildPromotionLevelsTemplate: buildPromotionLevelsTemplate,
		generateTableBranchBuilds: generateTableBranchBuilds,
        // Dialogs
        promoteBuild: promoteBuild,
		// Filter management
		showFilter: showFilter,
		filterFormTemplate: filterFormTemplate,
		closeFilter: closeFilter,
		clearFilter: clearFilter,
		// Actions
		diffAction: diffAction
	};
	
} ();


Handlebars.registerHelper('statusLabel', function(options) {
  return loc('status.' + options.fn(this));
});
