define(['jquery', 'dialog', 'dynamic', 'ajax', 'common'], function ($, dialog, dynamic, ajax, common) {

    var self = {};

    // TODO Test button
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
                        ajax.put({
                            url: 'ui/extension/jira/configuration/{0}'.format(id),
                            data: editedJiraConfiguration,
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

    self.deleteConfiguration = function (id) {
        ajax.get({
            url: 'ui/extension/jira/configuration/{0}/deletion'.format(id),
            successFn: function (jiraConfigurationDeletion) {
                // Deletion function
                var deleteFn = function () {
                    ajax.del({
                        url: 'ui/extension/jira/configuration/{0}'.format(id),
                        successFn: function () {
                            dynamic.reloadSection('jira-configuration-list');
                        }
                    })
                };
                // If no entity is impacted, just delete it after prompt
                if (jiraConfigurationDeletion.subscribers.length == 0) {
                    common.confirmAndCall(
                        'jira.configuration.delete.prompt'.loc(),
                        deleteFn
                    );
                } else {
                    dialog.show({
                        title: 'jira.configuration.delete'.loc(),
                        templateId: 'extension/jira-configuration-deletion',
                        data: jiraConfigurationDeletion,
                        submitFn: function (dialog) {
                            dialog.closeFn();
                            deleteFn();
                        }
                    })
                }
            }
        })
    };

    $('#jira-configuration-create').click(function () {
        self.createConfiguration();
    });

    return self;

});