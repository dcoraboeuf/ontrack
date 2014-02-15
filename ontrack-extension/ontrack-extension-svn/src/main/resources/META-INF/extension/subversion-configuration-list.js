define(['crud', 'ajax', 'dialog', 'common'], function (crud, ajax, dialog, common) {

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
                                        successFn: dialog.closeFn,
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
        itemDeletePromptKey: 'subversion.configuration.delete.prompt'
    })

});