var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit(
			'projectgroup-create-dialog',
			loc('projectgroup.create.title'),
			'POST',
			'ui/manage/projectgroup',
			function (data) {
				Application.reload('projectgroups');
				Application.reload('audit');
			});
	}

	function projectGroupTemplate (containerId, append, items) {
		return Template.table(containerId, append, items, Template.tableRowLink('gui/projectgroup'));
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();