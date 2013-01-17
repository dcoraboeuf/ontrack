var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialogAndSubmit(
			'projectgroup-create-dialog',
			loc('projectgroup.create.title'),
			'POST',
			'gui/projectgroup');
	}
	
	return {
		createProjectGroup: createProjectGroup
	};
	
} ();