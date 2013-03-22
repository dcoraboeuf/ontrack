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
            var oBuildValidationStamp = oBuild.validationStamps[this.name];
            // First run
            var firstRun;
            if (oBuildValidationStamp.run) {
                firstRun = oBuildValidationStamp.runs[0];
            }
            // Rendering
            return Template.render('compactValidationStampTemplate', {
                validationStamp: this,
                project: project,
                branch: branch,
                build: build,
                buildValidationStamp: oBuildValidationStamp,
                firstRun: firstRun
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
                totalColspan: branchBuilds.validationStamps.length + 2,
                rowFn: function (text, renderFn) {
                    return generateTableBuildRows (project, branch, branchBuilds);
                }
            }
        );
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
	
	function buildTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/build?u=1'.format(project, branch),
	        more: false, // Managed by the filter
	        refresh: true,
	        dataLength: function (branchBuilds) {
	            return branchBuilds.builds.length;
	        },
	        render: function (containerId, append, config, branchBuilds) {
                var containerSelector = '#' + containerId;
                if (append === true && $(containerSelector).has("tbody").length) {
                    $(containerSelector + " tbody").append(generateTableBuildRows(project, branch, branchBuilds));
                } else {
                    // No table defined, or no need to append
                    // Some items
                    if (branchBuilds.builds.length > 0) {
                        // Direct filling of the container
                        $(containerSelector).empty();
                        $(containerSelector).append(generateTableBranchBuilds(project, branch, branchBuilds));
                    }
                    // No items
                    else {
                        $(containerSelector).empty();
                        $(containerSelector).append('<div class="alert">{0}</div>'.format(loc('branch.nobuild')));
                    }
                }
	        },
	        postRenderFn: function () {
	            Application.tooltips();
	            gridHoverSetup();
	        }
         });
	}

	function buildValidationStampTemplate (project, branch, build) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/build/{2}/validationStamps'.format(project, branch, build),
	        refresh: true,
	        render: Template.asTable(function (stamp) {
	            return Template.render('buildValidationStampsTemplate', {
                    project: project,
                    branch: branch,
                    build: build,
                    stamp: stamp
	            });
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

	function showFilter () {
        Application.dialog({
            id: 'filter-form',
            title: loc('query'),
            width: 800,
            openFn: function () {
                // TODO Preselection of fields
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
        $('#limit').val('20');
	}
	
	return {
	    // Templating
		buildTemplate: buildTemplate,
		buildValidationStampTemplate: buildValidationStampTemplate,
		buildPromotionLevelsTemplate: buildPromotionLevelsTemplate,
		generateTableBranchBuilds: generateTableBranchBuilds,
		// Filter management
		showFilter: showFilter,
		filterFormTemplate: filterFormTemplate,
		closeFilter: closeFilter,
		clearFilter: clearFilter
	};
	
} ();