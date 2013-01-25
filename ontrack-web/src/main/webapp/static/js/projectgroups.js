var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit(
			'projectgroup-create-dialog',
			loc('projectgroup.create.title'),
			'POST',
			'ui/manage/projectgroup');
	}
	
	function projectGroupTemplate (items) {
		return Template.table().lines(items, Template.td("id").td("name").td("description").row()).render();
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();