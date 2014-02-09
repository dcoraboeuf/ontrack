define(['jquery', 'dialog', 'dynamic', 'ajax'], function ($, dialog, dynamic, ajax) {

    var self = {};

    self.createConfiguration = function () {
        dialog.show({
            title: 'jira.configuration'.loc(),
            width: 600,
            templateId: 'extension/jira-configuration-dialog',
            submitFn: function (dialog) {
                ajax.post({
                    url: 'ui/extension/jira/configuration',
                    data: {
                        name: dialog.form.find('#jira-configuration-name').val(),
                        url: dialog.form.find('#jira-configuration-url').val(),
                        user: dialog.form.find('#jira-configuration-user').val(),
                        password: dialog.form.find('#jira-configuration-password').val(),
                        // TODO Excluded projects
                        excludedProjects: [],
                        // TODO Excluded issues
                        excludedIssues: []
                    },
                    successFn: function () {
                        dialog.closeFn();
                        dynamic.reloadSection('jira-configuration-list');
                    },
                    errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                });
            }
        });
    };

    $('#jira-configuration-create').click(function () {
        self.createConfiguration();
    });

    return self;

});