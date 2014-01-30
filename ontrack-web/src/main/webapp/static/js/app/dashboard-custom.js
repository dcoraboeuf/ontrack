define(['jquery', 'ajax', 'dialog', 'dynamic'], function ($, ajax, dialog, dynamic) {

    // Dashboard creation
    function createDashboard(btn) {
        ajax.get({
            url: 'ui/manage/branch',
            loading: {
                el: $(btn)
            },
            successFn: function (branches) {
                dialog.show({
                    title: 'dashboard.custom'.loc(),
                    templateId: 'dashboard-custom-dialog',
                    data: {
                        branches: branches
                    },
                    initFn: function (config) {
                        config.form.find('.dashboard-custom-dialog-branch').each(function (i, tr) {
                            var branchId = $(tr).attr('id');
                            // TODO Initial state
                            // TODO Checks for authz
                            // Selection of branches
                            $(tr).click(function () {
                                $(tr).data('branch-selected', !$(tr).data('branch-selected'));
                                if ($(tr).data('branch-selected')) {
                                    $(tr).addClass('dashboard-custom-branch-selected');
                                } else {
                                    $(tr).removeClass('dashboard-custom-branch-selected');
                                }
                            })
                        })
                    },
                    submitFn: function (config) {
                        // Selected branches
                        var branches = [];
                        config.form.find('.dashboard-custom-dialog-branch').each(function (i, tr) {
                            var branchId = $(tr).attr('id');
                            if ($(tr).data('branch-selected')) {
                                branches.push(branchId);
                            }
                        });
                        // Posting the creation
                        ajax.post({
                            url: 'ui/manage/dashboard',
                            data: {
                                name: $('#dashboard-custom-name').val(),
                                branches: branches
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
        });
    }

    $('#dashboard-custom-create').click(function () {
        createDashboard(this);
    });

});