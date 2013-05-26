var Projects = function () {

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
                            location = 'gui/project/' + data.name;
                        }
                    });
            }
	    });
	}
	
} ();