var Branches = function () {
	
	function createBranch (project) {
		Application.dialogAndSubmit(
			'branch-create-dialog',
			loc('branch.create.title'),
			'POST',
			'ui/manage/branch/' + project,
			function (data) {
				location = 'gui/branch/' + project + '/' + data.name;
			});
	}
	
	function deleteBranch (id) {
		Application.deleteEntity('branch', id, '');
	}
	
	function branchTemplate (items) {
		return Template.links(items, 'gui/branch');
	}
	
	return {
		createBranch: createBranch,
		deleteBranch: deleteBranch,
		branchTemplate: branchTemplate
	};
	
} ();