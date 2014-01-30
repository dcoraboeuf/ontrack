define(['jquery', 'app/component/dashboard-custom-component'], function ($, dashboardCustom) {

    // Dashboard creation
    $('#dashboard-custom-create').click(function () {
        dashboardCustom.createDashboard(this);
    });

});