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
	
	function deleteBranch (project,name) {
		Application.deleteEntity('branch/' + project, name, '');
	}
	
	function branchTemplate (project) {
		return function (items) {
			return Template.links(items, 'gui/branch/' + project);
		};
	}
	
	return {
		createBranch: createBranch,
		deleteBranch: deleteBranch,
		branchTemplate: branchTemplate
	};
	
} ();