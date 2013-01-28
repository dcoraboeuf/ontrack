var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit(
			'projectgroup-create-dialog',
			loc('projectgroup.create.title'),
			'POST',
			'ui/manage/projectgroup',
			function (data) {
				Application.load('projectgroups', projectGroupTemplate);
			});
	}
	
	function projectGroupTemplate (items) {
		var html = '<table class="table table-hover"><tbody>';
		$.each (items, function (index, item) {
			html += '<tr><td>';
				html += '<a href="gui/projectgroup/{0}" title="{2}">{1}</a>'.format(item.id, item.name.html(), item.description.html());
				html += ' ';
				html += '<span class="item action">';
					html += '<i class="icon-pencil"></i>';
					html += '<i class="icon-trash"></i>';
				html += '</span>';
			html += '</td></tr>';
		});
		html += '</tbody></table>';
		return html;
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();