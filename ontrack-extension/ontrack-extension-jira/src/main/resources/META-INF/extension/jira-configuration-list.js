define(['render', 'jquery', 'extension/jira-configuration'], function (render, $, jiraConfiguration) {

    return {
        url: 'ui/extension/jira/configuration',
        render: render.asTableTemplate('extension/jira-configuration-list-row', function () {
            // Edition
            $('.jira-configuration-edit').each(function (index, btn) {
                var id = $(btn).attr('data-id');
                $(btn).click(function () {
                    jiraConfiguration.editConfiguration(id);
                });
            });
            // TODO Deletion
        })
    }

});