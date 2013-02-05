var Builds = function () {
	
	function buildTemplate (project, branch) {
		return function (items) {
			return Template.links(items, 'gui/build/{0}/{1}'.format(project,branch));
		};
	}

	function buildValidationStampTemplate (project, branch) {
	    return function (items) {
            return Template.list(items, function (stamp) {
                var pClass = '';
                if (!stamp.run) {
                    pClass += ' validation-stamp-norun';
                }
                var html = '<p class="{0}">'.format(pClass);
                html += '<img width="24" title="{2}" src="gui/validation_stamp/{0}/{1}/{2}/image" />'.format(
                    project.html(),
                    branch.html(),
                    stamp.name.html()
                    );
                html += ' <a href="gui/validation_stamp/{0}/{1}/{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
                if (stamp.run) {
                    html += ' <a href="gui/validation_run/{0}"><i class="icon-play"></i></a>'.format(stamp.runId);
                }
                html += '</p>';
                return html;
            });
        };
	}
	
	return {
		buildTemplate: buildTemplate,
		buildValidationStampTemplate: buildValidationStampTemplate
	};
	
} ();