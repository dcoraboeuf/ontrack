define(['crud'], function (crud) {

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
                    crud.onItemGet(btn, cfg, itemId, function (item) {
                        // TODO Indexation dialog
                    })
                }
            }
        ],
        itemDeletePromptKey: 'subversion.configuration.delete.prompt'
    })

});