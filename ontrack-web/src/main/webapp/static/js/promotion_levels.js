var PromotionLevels = function () {
	
	function createPromotionLevel (project, branch) {
		Application.dialogAndSubmit({
			id: 'promotion_level-create-dialog',
			title: loc('promotion_level.create'),
			url: 'ui/manage/promotion_level/{0}/{1}'.format(project,branch),
			successFn: function (data) {
				location = 'gui/promotion_level/{0}/{1}/{2}'.format(project,branch,data.name);
			}
		});
	}
	
	function deletePromotionLevel(project, branch, name) {
		Application.deleteEntity('promotion_level/{0}/{1}'.format(project,branch), name, '');
	}

	function promotionLevelImage (project, branch, promotionLevel) {
	    return '<img width="24" title="{2}" src="gui/promotion_level/{0}/{1}/{2}/image" />'.format(
               					project.html(),
               					branch.html(),
               					promotionLevel.name.html()
               					);
	}
	
	function promotionLevelTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/promotion_level/{0}/{1}/all'.format(project,branch),
	        render: Template.asTable(function (stamp) {
                var html = '';
                html += promotionLevelImage (project, branch, stamp);
                html += ' <a href="gui/promotion_level/{0}/{1}/{2}" title="{2}">{2}</a>'.format(project.html(), branch.html(), stamp.name.html());
                return html;
	        })
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