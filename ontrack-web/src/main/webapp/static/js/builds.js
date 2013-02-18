var Builds = function () {

    function runs(buildValidationStamp) {
        var html = '';
        $.each(buildValidationStamp.runs, function (index, run) {
            html += ' <p class="validation-run status-{0}">'.format(run.status);
            html += '<a href="gui/validation_run/{0}"><i class="icon-play"></i> <span class="validation-run-status">{1}</span></a>'.format(run.runId, run.status.html());
            html += ' <span class="validation-run-description">{0}</span>'.format(run.statusDescription.html());
            html += '</p>';
        });
        return html;
    }
	
	function buildTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/build/{0}/{1}?u=1'.format(project, branch),
	        more: true,
	        moreFn: function (branchBuilds) {
	            return branchBuilds.builds.length >= 10;
	        },
	        placeholder: loc('branch.nobuild'),
	        render: Template.fill(function (branchBuilds, append) {
                if (branchBuilds.builds.length == 0) {
                    return '<div>&nbsp;</div><div class="alert">{0}</div>'.format(loc('branch.nobuild'));
                }
                var html = '<table class="table table-hover"><thead>';
                // Header
                html += '<tr>';
                    html += '<th rowspan="2">{0}</th>'.format(loc('model.build'));
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
                $.each (branchBuilds.builds, function (index, buildCompleteStatus) {
                    html += '<tr>';
                        html += '<td class="branch-build">';
                            html += '<a href="gui/build/{0}/{1}/{2}">{2}</a>'.format(project.html(),branch.html(),buildCompleteStatus.name.html());
                        html += '</td>';
                        $.each(branchBuilds.validationStamps, function (index, validationStamp) {
                            var buildValidationStamp = buildCompleteStatus.validationStamps[validationStamp.name];
                            html += '<td>';
                            if (buildValidationStamp) {
                                if (buildValidationStamp.run) {
                                    html += runs(buildValidationStamp);
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
                // End
                html += '</tbody></table>';
                return html;
            })
         });
	}

	function buildValidationStampTemplate (project, branch, build) {
	    return Template.config({
	        url: 'ui/manage/build/{0}/{1}/{2}/validationStamps'.format(project, branch, build),
	        render: Template.asTable(function (stamp) {
                var pClass;
                if (!stamp.run) {
                    pClass = 'validation-stamp-norun';
                }
                var html = '<div class="{0}">'.format(pClass);
                html += ValidationStamps.validationStampImage(project, branch, stamp);
                html += ' <a href="gui/validation_stamp/{0}/{1}/{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
                if (stamp.run) {
                    html += runs(stamp);
                }
                html += '</div>';
                return html;
	        })
	    });
	}
	
	return {
		buildTemplate: buildTemplate,
		buildValidationStampTemplate: buildValidationStampTemplate
	};
	
} ();