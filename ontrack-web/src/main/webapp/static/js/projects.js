var Projects = function () {
	
	function createProject () {
		Application.dialogAndSubmit(
			'project-create-dialog',
			loc('project.create.title'),
			'POST',
			'ui/manage/project',
			function (data) {
				Application.load('projects', projectTemplate);
			});
	}
	
	function projectTemplate (items) {
		return Template.links(items, 'gui/project');
	}
	
	return {
		createProject: createProject,
		projectTemplate: projectTemplate
	};
	
} ();