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

	function validationStampImage (project, branch, stamp) {
	    return '<img width="24" title="{2}" src="gui/validation_stamp/{0}/{1}/{2}/image" />'.format(
               					project.html(),
               					branch.html(),
               					stamp.name.html()
               					);
	}
	
	function validationStampTemplate (project, branch) {
		return function (containerId, append, items) {
		    return Template.table(containerId, append, items, function (stamp) {
				var html = '';
				html += validationStampImage (project, branch, stamp);
				html += ' <a href="gui/validation_stamp/{0}/{1}/{2}" title="{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
				return html;
			});
		};
	}
	
	function editImage () {
		$('#validation_stamp-image-form').toggle();
	}
	
	function editImageCancel() {
		$('#validation_stamp-image-form').hide();
	}
	
	return {
		createValidationStamp: createValidationStamp,
		deleteValidationStamp: deleteValidationStamp,
		validationStampTemplate: validationStampTemplate,
		validationStampImage: validationStampImage,
		editImage: editImage,
		editImageCancel: editImageCancel
	};
	
} ();