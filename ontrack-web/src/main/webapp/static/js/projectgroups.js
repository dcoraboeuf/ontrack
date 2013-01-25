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
		return Template.table().lines(items, Template.td("name").td("description").row()).render();
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();