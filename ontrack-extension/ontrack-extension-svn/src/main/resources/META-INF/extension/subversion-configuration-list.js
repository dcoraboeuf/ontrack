define(['crud'], function (crud) {

    return crud.create({
        url: 'ui/extension/svn/configuration',
        itemName: 'subversion.configuration'.loc(),
        itemTemplateId: 'extension/subversion-repository-row',
        itemDialogTemplateId: 'extension/subversion-repository-dialog',
        itemDialogFieldPrefix: 'subversion-repository-',
        itemFields: [ 'name', 'url', 'user', 'password', 'branchPattern', 'tagPattern', 'tagFilterPattern', 'browserForPath', 'browserForRevision', 'browserForChange' ],
        commands: [ crud.createCommand('subversion.configuration.create'.loc()) ]
    })

});