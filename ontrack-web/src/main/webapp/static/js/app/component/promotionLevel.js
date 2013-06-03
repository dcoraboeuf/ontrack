define(['ajax','dialog'], function (ajax, dialog) {

    function createPromotionLevel (project, branch, successFn) {
        dialog.show({
            title: 'promotion_level.create'.loc(),
            templateId: 'promotion-level-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(project, branch),
                    data: {
                        name: $('#promotion-level-name').val(),
                        description: $('#promotion-level-description').val()
                    },
                    successFn: function (summary) {
                        config.closeFn();
                        successFn(summary);
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    return {
        createPromotionLevel: createPromotionLevel
    }

});