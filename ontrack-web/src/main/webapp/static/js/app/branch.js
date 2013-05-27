define(['application','jquery','dialog','ajax'], function(application, $, dialog, ajax) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    // Delete the branch
    $('#command-branch-delete').click(function () {
        application.deleteEntity('project/{0}/branch'.format(project), branch, function () {
            location.href = 'gui/project/{0}'.format(project);
        });
    });

    // Create a promotion level
    $('#promotion-level-create-button').click(function () {
        dialog.show({
            title: 'promotion_level.create'.loc(),
            templateId: 'promotion-level-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(project,branch),
                    data: {
                        name: $('#promotion-level-name').val(),
                        description: $('#promotion-level-description').val()
                    },
                    successFn: function (summary) {
                        config.closeFn();
                        location.href = 'gui/project/{0}/branch/{1}/promotion_level/{2}'.format(project, branch, summary.name);
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    });

});