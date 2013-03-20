var PromotionLevels = function () {
	
	function createPromotionLevel (project, branch) {
		Application.dialogAndSubmit({
			id: 'promotion_level-create-dialog',
			title: loc('promotion_level.create'),
			url: 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(project,branch),
			successFn: function (data) {
				location = 'gui/project/{0}/branch/{1}/promotion_level_manage'.format(project,branch);
			}
		});
	}
	
	function deletePromotionLevel(project, branch, name) {
		Application.deleteEntity('project/{0}/branch/{1}/promotion_level'.format(project,branch), name, '');
	}

	function promotionLevelImage (project, branch, promotionLevel) {
	    return '<img width="24" title="{2}" src="gui/project/{0}/branch/{1}/promotion_level/{2}/image" />'.format(
               					project.html(),
               					branch.html(),
               					promotionLevel.name.html()
               					);
	}
	
	function promotionLevelTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(project,branch),
	        render: Template.asTableTemplate('promotionLevelTemplate')
	    });
	}
	
	function editImage () {
		$('#promotion_level-image-form').toggle();
	}
	
	function editImageCancel() {
		$('#promotion_level-image-form').hide();
	}
	
	return {
		createPromotionLevel: createPromotionLevel,
		deletePromotionLevel: deletePromotionLevel,
		promotionLevelTemplate: promotionLevelTemplate,
		promotionLevelImage: promotionLevelImage,
		editImage: editImage,
		editImageCancel: editImageCancel
	};
	
} ();