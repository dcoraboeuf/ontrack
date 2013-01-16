var ProjectGroups = function () {
	
	function createProjectGroup () {
		Application.dialog({
			title: loc('projectgroup.create.title'),
			content: 'form/projectgroup/create',
			actions: [{
				text: loc('general.create')
			}, {
				text: loc('general.cancel')
			}]
		});
	}
	
	return {
		createProjectGroup: createProjectGroup
	};
	
} ();