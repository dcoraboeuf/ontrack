define(['application','jquery','dialog','ajax','dynamic'], function(application, $, dialog, ajax, dynamic) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    // Delete the branch
    $('#command-branch-delete').click(function () {
        application.deleteEntity('project/{0}/branch'.format(project), branch, function () {
            location.href = 'gui/project/{0}'.format(project);
        });
    });

    // Updating the branch
    $('#command-branch-update').click(function () {
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}'.format(project, branch),
            successFn: function (summary) {
                dialog.show({
                    title: 'branch.update'.loc(),
                    templateId: 'branch-update',
                    initFn: function (config) {
                        config.form.find('#branch-name').val(summary.name);
                        config.form.find('#branch-description').val(summary.description);
                    },
                    submitFn: function (config) {
                        ajax.put({
                            url: 'ui/manage/project/{0}/branch/{1}'.format(project, branch),
                            data: {
                                name: config.form.find('#branch-name').val(),
                                description: config.form.find('#branch-description').val()
                            },
                            successFn: function (updatedBranch) {
                                config.closeFn();
                                location.href = 'gui/project/{0}/branch/{1}'.format(updatedBranch.project.name, updatedBranch.name);
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
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

    // Create a validation stamp
    $('#validation-stamp-create-button').click(function () {
        dialog.show({
            title: 'validation_stamp.create'.loc(),
            templateId: 'validation-stamp-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(project,branch),
                    data: {
                        name: $('#validation-stamp-name').val(),
                        description: $('#validation-stamp-description').val()
                    },
                    successFn: function (summary) {
                        config.closeFn();
                        location.href = 'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(project, branch, summary.name);
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    });

});