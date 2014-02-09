define(['render'], function (render) {

    return {
        url: 'ui/extension/jira/configuration',
        render: render.asTableTemplate('extension/jira-configuration-list-row')
    }

});