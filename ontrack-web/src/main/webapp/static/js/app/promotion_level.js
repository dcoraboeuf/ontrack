define(['jquery','ajax','dialog'], function ($, ajax, dialog) {

    var project = $('#project').val();
    var branch = $('#branch').val();
    var promotionLevel = $('#promotion_level').val();

    // Updating the promotion level
    $('#promotion-level-update').click(function () {
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}'.format(project, branch, promotionLevel),
            successFn: function (summary) {
                dialog.show({
                    title: 'promotion_level.update'.loc(),
                    templateId: 'promotion-level-update',
                    initFn: function (config) {
                        config.form.find('#promotion-level-name').val(summary.name);
                        config.form.find('#promotion-level-description').val(summary.description);
                    },
                    submitFn: function (config) {
                        ajax.put({
                            url: 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}'.format(project, branch, promotionLevel),
                            data: {
                                name: config.form.find('#promotion-level-name').val(),
                                description: config.form.find('#promotion-level-description').val()
                            },
                            successFn: function (updatedPromotionLevel) {
                                config.closeFn();
                                location.href = 'gui/project/{0}/branch/{1}/promotion_level/{2}'.format(
                                    updatedPromotionLevel.branch.project.name,
                                    updatedPromotionLevel.branch.name,
                                    updatedPromotionLevel.name);
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
        });
    });

});