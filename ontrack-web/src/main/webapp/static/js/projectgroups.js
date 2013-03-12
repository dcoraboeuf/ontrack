var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit({
			id: 'projectgroup-create-dialog',
			title: loc('projectgroup.create.title'),
			url: 'ui/manage/projectgroup',
			successFn: function (data) {
				location.reload();
			}
	    });
	}
	
	return {
		createProjectGroup: createProjectGroup,
		// Template for the list of groups
		projectGroupTemplate: Template.config({
            url: 'ui/manage/projectgroup',
            render: Template.asTable(Template.asLink('gui/projectgroup')),
            placeholder: loc('projectgroup.empty')
		})
	};
	
} ();