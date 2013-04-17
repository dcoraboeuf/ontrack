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

	function updateProject (name) {
	    var url = 'ui/manage/project/{0}'.format(name);
	    AJAX.get({
            url: url,
            successFn: function (project) {
                Application.dialogAndSubmit({
                    id: 'project-update-dialog',
                    title: loc('project.update'),
                    url: url,
                    method: 'PUT',
                    openFn: function () {
                        $('#project-update-dialog-name').val(project.name);
                        $('#project-update-dialog-description').val(project.description);
                    },
                    successFn: function (data) {
                            location = 'gui/project/' + $('#project-update-dialog-name').val();
                        }
                    });
            }
	    });
	}
	
	function deleteProject (name) {
		Application.deleteEntity('project', name, '');
	}
	
	return {
		createProject: createProject,
		deleteProject: deleteProject,
		updateProject: updateProject,
		projectTemplate: Template.config({
		    url: 'ui/manage/project',
		    render: Template.asTableTemplate('projectTemplate'),
		    placeholder: loc('project.empty')
		})
	};
	
} ();