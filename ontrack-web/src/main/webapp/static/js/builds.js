var Builds = function () {

    function runs(project, branch, build, buildValidationStamp, compact) {
        var html = '';
        if (compact) {
            // Run?
            if (buildValidationStamp.run) {
                // Gets the first run only
                var run = buildValidationStamp.runs[0];
                // Status 'ball'
                html += '<a class="tooltip-source" data-toggle="tooltip" href="gui/project/{0}/branch/{1}/build/{2}/validation_stamp/{3}/validation_run/{4}" title="{8} - {6} - {7}"><img width="24" src="static/images/status-{5}.png" /></a>'.format(
                    project.html(), // 0
                    branch.html(), // 1
                    build.html(), // 2
                    buildValidationStamp.name.html(), // 3
                    run.runOrder, // 4
                    run.status.html(), // 5
                    run.signature.elapsedTime, // 6
                    run.signature.formattedTime, // 7
                    run.statusDescription.html() // 8
                );
            } else {
                html += '<img class="tooltip-source" width="24" height="24" src="static/images/status-NONE.png" title="{0}" />'.format(loc('validationRun.notRun'));
            }
        } else {
            $.each(buildValidationStamp.runs, function (index, run) {
                html += ' <p class="validation-run status-{0}">'.format(run.status);
                html += '<a class="tooltip-source" href="gui/project/{0}/branch/{1}/build/{2}/validation_stamp/{3}/validation_run/{4}" title="{8} - {6} - {7}"><i class="icon-play"></i> <span class="validation-run-status">{5}</span></a>'
                    .format(
                        project.html(), // 0
                        branch.html(), // 1
                        build.html(), // 2
                        buildValidationStamp.name.html(), // 3
                        run.runOrder, // 4
                        run.status.html(), // 5
                        run.signature.elapsedTime, // 6
                        run.signature.formattedTime, // 7
                        run.statusDescription.html()); // 8
                html += '</p>';
            });
        }
        return html;
    }

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
	        more: function (id, config, data, hasMore) {
	            if (hasMore) {
	                $('#more-builds').show();
	            } else {
	                $('#more-builds').hide();
	            }
	        },
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
                var pClass;
                if (!stamp.run) {
                    pClass = 'validation-stamp-norun';
                }
                var html = '<tr><td><div class="{0}">'.format(pClass);
                html += ValidationStamps.validationStampImage(project, branch, stamp);
                html += ' <a class="tooltip-source" href="gui/project/{0}/branch/{1}/validation_stamp/{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
                if (stamp.run) {
                    html += runs(project, branch, build, stamp, false);
                }
                html += '</div></td></tr>';
                return html;
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
	
	return {
		buildTemplate: buildTemplate,
		buildValidationStampTemplate: buildValidationStampTemplate,
		buildPromotionLevelsTemplate: buildPromotionLevelsTemplate,
		generateTableBranchBuilds: generateTableBranchBuilds
	};
	
} ();