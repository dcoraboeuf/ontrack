var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit({
			id: 'projectgroup-create-dialog',
			title: loc('projectgroup.create.title'),
			url: 'ui/manage/project_group',
			successFn: function (data) {
				location.reload();
			}
	    });
	}
	
	return {
		createProjectGroup: createProjectGroup,
		// Template for the list of groups
		projectGroupTemplate: Template.config({
            url: 'ui/manage/project_group',
            render: Template.asTable(Template.asLink('gui/project_group')),
            placeholder: loc('projectgroup.empty')
		})
	};
	
} ();