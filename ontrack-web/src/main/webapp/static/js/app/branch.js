define(['application', 'jquery', 'dialog', 'ajax', 'dynamic', 'app/component/promotionLevel'], function (application, $, dialog, ajax, dynamic, promotionLevelComponent) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    // Delete the branch
    function deleteBranch() {
        application.deleteEntity('project/{0}/branch'.format(project), branch, function () {
            'gui/project/{0}'.format(project).goto();
        });
    }

    // Updating the branch
    function updateBranch() {
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
                                'gui/project/{0}/branch/{1}'.format(updatedBranch.project.name, updatedBranch.name).goto();
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
        });
    }

    // Create a promotion level
    function createPromotionLevel() {
        promotionLevelComponent.createPromotionLevel(project, branch, function (summary) {
            'gui/project/{0}/branch/{1}/promotion_level/{2}'.format(project, branch, summary.name).goto();
        });
    }

    // Create a validation stamp
    function createValidationStamp() {
        dialog.show({
            title: 'validation_stamp.create'.loc(),
            templateId: 'validation-stamp-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(project, branch),
                    data: {
                        name: $('#validation-stamp-name').val(),
                        description: $('#validation-stamp-description').val()
                    },
                    successFn: function (summary) {
                        config.closeFn();
                        'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(project, branch, summary.name).goto();
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    // Clean-up configuration
    function cleanupBranch() {
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/cleanup'.format(project, branch),
            successFn: function (cleanup) {
                dialog.show({
                    title: 'build.cleanup'.loc(),
                    templateId: 'branch-build-cleanup',
                    data: cleanup,
                    submitFn: function (config) {
                        var retention = config.form.find('#build-cleanup-retention').val();
                        var excludedPromotionLevels = [];
                        config.form.find('input.promotion-level').each(function (index, input) {
                            if ($(input).is(':checked')) {
                                excludedPromotionLevels.push(Number($(input).attr('data-promotionlevel')));
                            }
                        });
                        ajax.put({
                            url: 'ui/manage/project/{0}/branch/{1}/cleanup'.format(project, branch),
                            data: {
                                retention: retention,
                                excludedPromotionLevels: excludedPromotionLevels
                            },
                            successFn: function () {
                                config.closeFn();
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
        });
    }

    $('#command-branch-delete').click(deleteBranch);
    $('#command-branch-update').click(updateBranch);
    $('#command-branch-build-cleanup').click(cleanupBranch);
    $('#promotion-level-create-button').click(createPromotionLevel);
    $('#validation-stamp-create-button').click(createValidationStamp);

});