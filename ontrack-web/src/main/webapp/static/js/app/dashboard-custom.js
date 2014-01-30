define(['jquery','ajax','dialog','dynamic'], function ($, ajax, dialog, dynamic) {

    // Dashboard creation
    function createDashboard() {
        dialog.show({
            title: 'dashboard.custom'.loc(),
            templateId: 'dashboard-custom-dialog',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/manage/dashboard',
                    data: {
                        name: $('#dashboard-custom-name').val()
                    },
                    successFn: function () {
                        config.closeFn();
                        dynamic.reloadSection('dashboard-custom');
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    $('#dashboard-custom-create').click(createDashboard);

});