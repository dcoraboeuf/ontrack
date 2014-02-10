define(['jquery', 'dialog', 'dynamic', 'ajax'], function ($, dialog, dynamic, ajax) {

    var self = {};

    function showDialog(config) {
        dialog.show({
            title: 'jira.configuration'.loc(),
            width: 600,
            templateId: 'extension/jira-configuration-dialog',
            initFn: function (dialog) {
                dialog.form.find('#jira-configuration-name').val(config.jiraConfiguration.name);
                dialog.form.find('#jira-configuration-url').val(config.jiraConfiguration.url);
                dialog.form.find('#jira-configuration-user').val(config.jiraConfiguration.user);
                dialog.form.find('#jira-configuration-password').val(config.jiraConfiguration.password);
                // TODO Excluded projects
                // TODO Excluded issues
            },
            submitFn: function (dialog) {
                config.successFn(config, dialog, {
                    name: dialog.form.find('#jira-configuration-name').val(),
                    url: dialog.form.find('#jira-configuration-url').val(),
                    user: dialog.form.find('#jira-configuration-user').val(),
                    password: dialog.form.find('#jira-configuration-password').val(),
                    // TODO Excluded projects
                    excludedProjects: [],
                    // TODO Excluded issues
                    excludedIssues: []
                });
            }
        })
    }

    self.createConfiguration = function () {
        showDialog({
            jiraConfiguration: {
                name: '',
                url: '',
                user: '',
                password: '',
                excludedProjects: [],
                excludedIssues: []
            },
            successFn: function (config, dialog, jiraConfiguration) {
                ajax.post({
                    url: 'ui/extension/jira/configuration',
                    data: jiraConfiguration,
                    successFn: function () {
                        dialog.closeFn();
                        dynamic.reloadSection('jira-configuration-list');
                    },
                    errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                });
            }
        });
    };

    self.editConfiguration = function (id) {
        ajax.get({
            url: 'ui/extension/jira/configuration/{0}'.format(id),
            successFn: function (jiraConfiguration) {
                showDialog({
                    jiraConfiguration: jiraConfiguration,
                    successFn: function (config, dialog, editedJiraConfiguration) {
                        ajax.post({
                            url: 'ui/extension/jira/configuration/{0}'.format(id),
                            data: jiraConfiguration,
                            successFn: function () {
                                dialog.closeFn();
                                dynamic.reloadSection('jira-configuration-list');
                            },
                            errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                        });
                    }
                })
            }
        })
    };

    $('#jira-configuration-create').click(function () {
        self.createConfiguration();
    });

    return self;

});