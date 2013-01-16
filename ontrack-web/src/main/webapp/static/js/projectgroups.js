var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialog('projectgroup-create-dialog');
	}
	
	return {
		createProjectGroup: createProjectGroup
	};
	
} ();