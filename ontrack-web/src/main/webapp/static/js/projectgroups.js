var ProjectGroups = function () {

	function doCreateProjectGroup () {
		alert('Create');
		return true;
	}
	
	function createProjectGroup () {
		Application.dialog('projectgroup-create-dialog', doCreateProjectGroup);
	}
	
	return {
		createProjectGroup: createProjectGroup
	};
	
} ();