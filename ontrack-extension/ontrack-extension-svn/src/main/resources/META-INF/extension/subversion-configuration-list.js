define(['jquery', 'crud', 'ajax', 'dialog', 'common'], function ($, crud, ajax, dialog, common) {

    function indexationDialog(repositoryId) {
        ajax.get({
            url: 'ui/extension/svn/indexation/{0}'.format(repositoryId),
            successFn: function (lastRevisionInfo) {
                dialog.show({
                    title: 'subversion.indexation'.loc(),
                    templateId: 'extension/subversion-indexation-dialog',
                    data: {
                        lastRevisionInfo: lastRevisionInfo
                    },
                    buttons: [
                        {
                            text: 'general.cancel'.loc(),
                            action: 'cancel'
                        }
                    ],
                    initFn: function (dialog) {
                        // TODO Indexation from latest
                        // TODO Range indexation
                        // Full indexation
                        dialog.form.find('#subversion-indexation-dialog-full-submit').click(function () {
                            common.confirmAndCall(
                                'subversion.indexation.full.confirmation'.loc(),
                                function () {
                                    ajax.post({
                                        url: 'ui/extension/svn/indexation/{0}/full'.format(repositoryId),
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                dialog.closeFn()
                                            } else {
                                                dialog.errorFn('subversion.indexation.alreadyrunning'.loc())
                                            }
                                        },
                                        errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                                    })
                                }
                            )
                        })
                    }
                })
            }
        })
    }

    function fillConfigurationCbo(serviceName, configurationCbo, selection) {
        configurationCbo.empty();
        // No entry
        $('<option></option>').attr('value', '').html('&nbsp;').appendTo(configurationCbo);
        if (serviceName && serviceName != '') {
            ajax.get({
                url: 'ui/extension/issue/service/{0}/configurations'.format(serviceName),
                successFn: function (configurations) {
                    // Fills the `select` with the configuration names
                    $.each(configurations, function (i, configuration) {
                        $('<option></option>')
                            .attr('value', configuration.id)
                            .text(configuration.name)
                            .appendTo(configurationCbo)
                    });
                    // Selection?
                    if (selection) {
                        configurationCbo.val(selection);
                    }
                }
            })
        }
    }

    function deleteRepository(svnRepositoryDeletion, dynamicConfig, cfg, itemId, item) {
        if (svnRepositoryDeletion.projects.length == 0) {
            crud.defaultItemDeleteFn(dynamicConfig, cfg, itemId, item)
        } else {
            dialog.show({
                title: 'subversion.configuration.delete'.loc(),
                templateId: 'extension/subversion-repository-deletion',
                data: svnRepositoryDeletion,
                submitFn: function (dialog) {
                    dialog.closeFn();
                    crud.deleteItem(dynamicConfig, cfg, itemId);
                }
            })
        }
    }

    return crud.create({
        url: 'ui/extension/svn/configuration',
        itemName: 'subversion.configuration'.loc(),
        itemTemplateId: 'extension/subversion-repository-row',
        itemNewFn: function () {
            return {
                branchPattern: '.*/branches/.*',
                tagPattern: '.*/tags/.*',
                indexationInterval: 0,
                indexationStart: 1
            }
        },
        itemDialogTemplateId: 'extension/subversion-repository-dialog',
        itemDialogWidth: 800,
        itemDialogFieldPrefix: 'subversion-repository-',
        itemFields: [ 'name', 'url', 'user', 'password', 'branchPattern', 'tagPattern', 'tagFilterPattern', 'browserForPath', 'browserForRevision', 'browserForChange', 'indexationInterval', 'indexationStart' ],
        commands: [ crud.createCommand('subversion.configuration.create'.loc()) ],
        itemCommands: [
            crud.updateItemCommand(),
            crud.deleteItemCommand(),
            {
                iconCls: 'icon-time',
                title: 'subversion.indexation'.loc(),
                action: function (btn, dynamicConfig, cfg, itemId) {
                    // Indexation dialog
                    indexationDialog(itemId)
                }
            }
        ],
        itemDeletePromptKey: 'subversion.configuration.delete.prompt',
        itemDialogInitFn: function (cfg, dialog, item) {
            var serviceCbo = dialog.form.find('#subversion-repository-issueServiceName');
            var configurationCbo = dialog.form.find('#subversion-repository-issueServiceConfigId');
            // Loading of the issue services
            ajax.get({
                url: 'ui/extension/issue/service',
                successFn: function (services) {
                    serviceCbo.empty();
                    // No entry
                    $('<option></option>').attr('value', '').html('&nbsp;').appendTo(serviceCbo);
                    // Fills the `select` with the services name
                    $.each(services, function (i, service) {
                        $('<option></option>')
                            .attr('value', service.id)
                            .text(service.name)
                            .appendTo(serviceCbo)
                    });
                    // Initial selection
                    var itemServiceName = item.issueService ? item.issueService.id : '';
                    serviceCbo.val(itemServiceName);
                    fillConfigurationCbo(itemServiceName, configurationCbo, item.issueServiceConfig ? item.issueServiceConfig.id : '');
                }
            });
            // Selection of the name triggers the selection of the configurations for this service
            serviceCbo.change(function () {
                var serviceName = serviceCbo.val();
                fillConfigurationCbo(serviceName, configurationCbo, '');
            });
        },
        itemDialogReadFn: function (cfg, dialog, form) {
            var serviceCbo = dialog.form.find('#subversion-repository-issueServiceName');
            var configurationCbo = dialog.form.find('#subversion-repository-issueServiceConfigId');
            form.issueServiceName = serviceCbo.val();
            if (form.issueServiceName && form.issueServiceName != '') {
                form.issueServiceConfigId = configurationCbo.val()
            } else {
                delete form.issueServiceConfigId
            }
        },
        itemDeleteFn: function (dynamicConfig, cfg, itemId, item) {
            ajax.get({
                url: 'ui/extension/svn/configuration/{0}/deletion'.format(itemId),
                successFn: function (deletion) {
                    deleteRepository(deletion, dynamicConfig, cfg, itemId, item)
                }
            })
        }
    })

});