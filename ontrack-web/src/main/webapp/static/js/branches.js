var Branches = function () {
	
	function createBranch (project) {
		Application.dialogAndSubmit({
			id: 'branch-create-dialog',
			title: loc('branch.create.title'),
			url: 'ui/manage/project/' + project + '/branch',
			successFn: function (data) {
				location = 'gui/project/{0}/branch/{1}'.format(project, data.name);
			}
	    });
	}
	
	function deleteBranch (project,name) {
		Application.deleteEntity('project/{0}/branch'.format(project), name, '');
	}
	
	function branchTemplate (project) {
        return Template.config({
            url: 'ui/manage/project/{0}/branch'.format(project.html()),
            render: Template.asTableTemplate('branchTemplate'),
            placeholder: loc('branch.empty')
        });
	}
	
	return {
		createBranch: createBranch,
		deleteBranch: deleteBranch,
		branchTemplate: branchTemplate
	};
	
} ();