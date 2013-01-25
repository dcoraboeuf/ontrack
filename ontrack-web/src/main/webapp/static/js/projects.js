var Projects = function () {
	
	function createProject () {
		Application.dialogAndSubmit(
			'project-create-dialog',
			loc('project.create.title'),
			'POST',
			'ui/manage/project',
			function (data) {
				Application.load('projects', projectGroupTemplate);
			});
	}
	
	function projectTemplate (items) {
		return Template.table().withClass("table").withClass("table-hover").withRow(
			Template.row()
				//.cell("$id")
				.cell("$name", {title: "$description", 'class': "action"})
		).render(items);
	}
	
	return {
		createProject: createProject,
		projectTemplate: projectTemplate
	};
	
} ();