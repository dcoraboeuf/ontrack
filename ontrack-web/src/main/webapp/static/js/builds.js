var Builds = function () {
	
	function buildTemplate (project, branch) {
		return function (items) {
			return Template.links(items, 'build/{0}/{1}'.format(project,branch));
		};
	}
	
	return {
		buildTemplate: buildTemplate
	};
	
} ();