var Projects = function () {
	
	function createProject () {
		Application.dialogAndSubmit({
			id: 'project-create-dialog',
			title: loc('project.create.title'),
			url: 'ui/manage/project',
			successFn: function (data) {
				    location = 'gui/project/' + data.name;
			    }
			});
	}
	
	function deleteProject (name) {
		Application.deleteEntity('project', name, '');
	}
	
	return {
		createProject: createProject,
		deleteProject: deleteProject,
		projectTemplate: Template.config({
		    url: 'ui/manage/project/all',
		    render: Template.asTable(Template.asLink('gui/project')),
            placeholder: loc('project.empty')
		})
	};
	
} ();