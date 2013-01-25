var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit(
			'projectgroup-create-dialog',
			loc('projectgroup.create.title'),
			'POST',
			'ui/manage/projectgroup');
	}
	
	function projectGroupTemplate (items) {
		var html = Template.table().lines(items, function (item) {
			return item.name;
		}).render();
		return html;
		// return Template.table().lines(items, td("id").td("name").td("description").line()).render();
	}
	
	return {
		createProjectGroup: createProjectGroup,
		projectGroupTemplate: projectGroupTemplate
	};
	
} ();