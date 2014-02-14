define(['crud', 'ajax', 'dialog'], function (crud, ajax, dialog) {

    function indexationDialog(repositoryId) {
        ajax.get({
            url: 'ui/extension/svn/indexation/{0}'.format(repositoryId),
            successFn: function (lastRevisionInfo) {
                dialog.show({
                    title: 'subversion.indexation'.loc(),
                    templateId: 'extension/subversion-indexation-dialog',
                    data: {
                        repositoryId: repositoryId,
                        lastRevisionInfo: lastRevisionInfo
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