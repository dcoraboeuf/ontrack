var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialog(
			'projectgroup-create-dialog',
			loc('projectgroup.create.title'),
			function (successFn) {
				Application.submit('projectgroup-create-dialog', 'POST', 'gui/projectgroup', successFn);
			}
		);
	}
	
	return {
		createProjectGroup: createProjectGroup
	};
	
} ();