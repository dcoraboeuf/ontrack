var Builds = function () {

    function runs(project, branch, build, buildValidationStamp) {
        var html = '';
        $.each(buildValidationStamp.runs, function (index, run) {
            html += ' <p class="validation-run status-{0}">'.format(run.status);
            html += '<a href="gui/validation_run/{0}/{1}/{2}/{3}/{4}"><i class="icon-play"></i> <span class="validation-run-status">{5}</span></a>'
                .format(
                    project.html(),
                    branch.html(),
                    build.html(),
                    buildValidationStamp.name.html(),
                    run.runOrder,
                    run.status.html());
            html += ' <span class="validation-run-description">{0}</span>'.format(run.statusDescription.html());
            html += '</p>';
        });
        return html;
    }

    function generateTableBuildRows (project, branch, branchBuilds) {
        var html = '';
        $.each (branchBuilds.builds, function (index, buildCompleteStatus) {
            html += '<tr>';
                html += '<td class="branch-build">';
                    html += '<a href="gui/build/{0}/{1}/{2}">{2}</a>'.format(project.html(),branch.html(),buildCompleteStatus.name.html());
                    html += '<br/><span class="signature" title="{1}">{0}</span>'.format(buildCompleteStatus.signature.elapsedTime, buildCompleteStatus.signature.formattedTime);
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
            html += '<a href="gui/validation_stamp/{0}/{1}/{2}" title="{2}">'.format(project.html(), branch.html(), validationStamp.name.html());
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
	        url: 'ui/manage/build/{0}/{1}?u=1'.format(project, branch),
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
	        url: 'ui/manage/build/{0}/{1}/{2}/validationStamps'.format(project, branch, build),
	        refresh: true,
	        render: Template.asTable(function (stamp) {
                var pClass;
                if (!stamp.run) {
                    pClass = 'validation-stamp-norun';
                }
                var html = '<div class="{0}">'.format(pClass);
                html += ValidationStamps.validationStampImage(project, branch, stamp);
                html += ' <a href="gui/validation_stamp/{0}/{1}/{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
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
                    html += '<img width="24" src="gui/promotion_level/{0}/{1}/{2}/image" />'.format(
                        project.html(),
                        branch.html(),
                        promotion.name
                    );
                    // Link to the promotion level
                    html += ' <a href="gui/promotion_level/{0}/{1}/{2}">{2}</a>'.format(
                        project.html(),
                        branch.html(),
                        promotion.name
                    );
                }
            }
            return html;
        };
	}

	function buildPromotionLevelsTemplate (project, branch, build) {
        return Template.config({
            url: 'ui/manage/build/{0}/{1}/{2}/promotionLevels'.format(project, branch, build),
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