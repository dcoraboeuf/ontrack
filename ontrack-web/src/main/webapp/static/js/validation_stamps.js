var ValidationStamps = function () {
	
	function createValidationStamp (project, branch) {
		Application.dialogAndSubmit(
			'validation_stamp-create-dialog',
			loc('validation_stamp.create.title'),
			'POST',
			'ui/manage/validation_stamp/{0}/{1}'.format(project,branch),
			function (data) {
				location = 'gui/validation_stamp/{0}/{1}/{2}'.format(project,branch,data.name);
			});
	}
	
	function deleteValidationStamp(project, branch, name) {
		Application.deleteEntity('validation_stamp/{0}/{1}'.format(project,branch), name, '');
	}
	
	function validationStampTemplate (project, branch) {
		return function (items) {
			return Template.links(items, 'gui/validation_stamp/{0}/{1}'.format(project,branch));
		};
	}
	
	return {
		createValidationStamp: createValidationStamp,
		deleteValidationStamp: deleteValidationStamp,
		validationStampTemplate: validationStampTemplate
	};
	
} ();