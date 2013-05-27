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

	function updatePromotionLevel (project, branch, promotionLevel) {
	    var url = 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}'.format(project, branch, promotionLevel);
	    AJAX.get({
            url: url,
            successFn: function (summary) {
                Application.dialogAndSubmit({
                    id: 'promotion_level-update-dialog',
                    title: loc('promotion_level.update'),
                    url: url,
                    method: 'PUT',
                    openFn: function () {
                        $('#promotion_level-update-dialog-name').val(summary.name);
                        $('#promotion_level-update-dialog-description').val(summary.description);
                    },
                    successFn: function (summary) {
                            location = 'gui/project/{0}/branch/{1}/promotion_level/{2}'.format(summary.branch.project.name, summary.branch.name, summary.name);
                        }
                    });
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
	
	function editImage () {
		$('#promotion_level-image-form').toggle();
	}
	
	function editImageCancel() {
		$('#promotion_level-image-form').hide();
	}

    function promotionsTemplate (project, branch, promotionLevel) {
        return Template.config({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/promotions?u=1'.format(project, branch, promotionLevel),
            more: true,
            render: Template.asTableTemplate('promotionTemplate')
        });
    }
	
	return {
		createPromotionLevel: createPromotionLevel,
		deletePromotionLevel: deletePromotionLevel,
		updatePromotionLevel: updatePromotionLevel,
		promotionLevelTemplate: promotionLevelTemplate,
		promotionLevelImage: promotionLevelImage,
		editImage: editImage,
		editImageCancel: editImageCancel,
        promotionsTemplate: promotionsTemplate
	};
	
} ();