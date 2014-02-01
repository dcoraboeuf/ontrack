define(['jquery', 'render', 'app/component/dashboard-custom-component'], function ($, render, dashboardCustom) {

    return {
        url: 'ui/manage/dashboard',
        render: render.asTableTemplate('dashboard-custom-line', function (config, items, target) {
            // Edition buttons
            $('.dashboard-custom-edit').each(function (index, el) {
                $(el).click(function () {
                    dashboardCustom.editDashboard($(el).attr('data-id'), config.admin == 'true')
                })
            });
            // Deletion buttons
            $('.dashboard-custom-delete').each(function (index, el) {
                if (config.admin == 'true') {
                    $(el).click(function () {
                        dashboardCustom.deleteDashboard($(el).attr('data-id'))
                    })
                } else {
                    $(el).remove();
                }
            });
        })
    }

});