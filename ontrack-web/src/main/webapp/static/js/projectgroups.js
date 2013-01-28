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
		return Template.links(items, 'gui/projectgroup');
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();