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
	
	return {
		createProjectGroup: createProjectGroup,
		// Template for the list of groups
		projectGroupTemplate: Template.config({
            url: 'ui/manage/projectgroup/all',
            render: Template.asTable(Template.asLink('gui/projectgroup')),
            placeholder: loc('projectgroup.empty')
		})
	};
	
} ();