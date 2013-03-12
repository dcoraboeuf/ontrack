var Builds = function () {

    function runs(project, branch, build, buildValidationStamp) {
        var html = '';
        $.each(buildValidationStamp.runs, function (index, run) {
            html += ' <p class="validation-run status-{0}">'.format(run.status);
            // /gui/project/{project:[A-Z0-9_\.]+}/branch/{branch:[A-Z0-9_\.]+}/build/{build:[A-Za-z0-9_\.]+}/validation_stamp/{validationStamp:[A-Z0-9_\.]+}/validation_run/{run:[0-9]+}
            html += '<a href="gui/project/{0}/branch/{1}/build/{2}/validation_stamp/{3}/validation_run/{4}" title="{8} - {6} - {7}"><i class="icon-play"></i> <span class="validation-run-status">{5}</span></a>'
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
        return html;
    }

    function generateTableBuildRows (project, branch, branchBuilds) {
        var html = '';
        $.each (branchBuilds.builds, function (index, buildCompleteStatus) {
            html += '<tr>';
                html += '<td class="branch-build">';
                    html += '<a href="gui/project/{0}/branch/{1}/build/{2}" title="{3} - {4} - {5}">{2}</a>'.format(
                        project.html(), // 0
                        branch.html(), // 1
                        buildCompleteStatus.name.html(), // 2
                        buildCompleteStatus.description.html(), // 3
                        buildCompleteStatus.signature.elapsedTime, // 4
                        buildCompleteStatus.signature.formattedTime // 5
                        );
                html += '</td><td>';
                    html += generateBuildPromotionLevels(project,branch)(buildCompleteStatus.promotionLevels);
                html += '</td>';
                $.each(branchBuilds.validationStamps, function (index, validationStamp) {
                    var buildValidationStamp = buildCompleteStatus.validationStamps[validationStamp.name];
                    html += '<td>';
                    if (buildValidationStamp) {
                        if (buildValidationStamp.run) {
                            html += runs(project, branch, buildCompleteStatus.name, buildValidationStamp);
                        } else {
                            html += '<span class="muted">{0}</span>'.format(loc('validationRun.notRun'));
                        }
                    } else {
                        html += '-';
                    }
                    html += '</td>';
                });
            html += '</tr>';
        });
        return html;
    }

    function generateTableBranchBuilds (project, branch, branchBuilds) {
        var html = '<table class="table table-hover"><thead>';
        // Header
        html += '<tr>';
            html += '<th rowspan="2">{0}</th>'.format(loc('model.build'));
            html += '<th rowspan="2">{0}</th>'.format(loc('branch.promotion_levels'));
            html += '<th colspan="{1}">{0}</th>'.format(loc('branch.validation_stamps'), branchBuilds.validationStamps.length);
        html += '</tr>';
        html += '<tr>';
        $.each(branchBuilds.validationStamps, function (index, validationStamp) {
            html += '<th align="center">';
            html += '<a href="gui/project/{0}/branch/{1}/validation_stamp/{2}" title="{2}">'.format(project.html(), branch.html(), validationStamp.name.html());
            html += ValidationStamps.validationStampImage(project, branch, validationStamp);
            html += '</a>';
            html += '</th>';
        });
        html += '</tr>';
        // Items
        html += '</thead><tbody>';
        html += generateTableBuildRows(project, branch, branchBuilds);
        // End
        html += '</tbody></table>';
        return html;
    }
	
	function buildTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/build?u=1'.format(project, branch),
	        more: true,
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
                var html = '<div class="{0}">'.format(pClass);
                html += ValidationStamps.validationStampImage(project, branch, stamp);
                html += ' <a href="gui/project/{0}/branch/{1}/validation_stamp/{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
                if (stamp.run) {
                    html += runs(project, branch, build, stamp);
                }
                html += '</div>';
                return html;
	        })
	    });
	}

	function generateBuildPromotionLevels (project, branch) {
	    return function (promotionLevels) {
            var html = '';
            var count = promotionLevels.length;
            if (count == 0) {
                html += '<span class="muted">{0}</span>'.format(loc('build.promotion_levels.none'));
            } else {
                for (var i = 0 ; i < count ; i++) {
                    // Promotion
                    var promotion = promotionLevels[i];
                    // Separator
                    if (i > 0) {
                        html += ' <i class="icon-arrow-right"></i> ';
                    }
                    // Image of the promotion level
                    html += '<img width="24" src="gui/project/{0}/branch/{1}/promotion_level/{2}/image" />'.format(
                        project.html(),
                        branch.html(),
                        promotion.name
                    );
                    // Link to the promotion level
                    html += ' <a href="gui/project/{0}/branch/{1}/promotion_level/{2}" title="{3} - {4}">{2}</a>'.format(
                        project.html(),
                        branch.html(),
                        promotion.name,
                        promotion.signature.elapsedTime,
                        promotion.signature.formattedTime
                    );
                }
            }
            return html;
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
		buildPromotionLevelsTemplate: buildPromotionLevelsTemplate
	};
	
} ();