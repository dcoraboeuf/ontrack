define(['jquery','render'], function ($, render) {

    function renderSections (config, data) {
        $.each(data.sections, function (index, section) {
            render.renderInto(
                $('#' + section.uuid),
                section.templateId,
                section.data
            )
        });
    }

    return {
        url: function (config) {
            return 'ui/dashboard/project/{0}/branch/{1}'.format(config.project, config.branch)
        },
        render: render.asSimpleTemplate('dashboard-branch', render.sameDataFn, renderSections)
    }

})