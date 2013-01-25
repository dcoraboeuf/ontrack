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
		return Template.table().withClass("table").withClass("table-hover").withRow(
			Template.row()
				//.cell("$id")
				.cell("$name", {title: "$description", 'class': "action"})
		).render(items);
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();