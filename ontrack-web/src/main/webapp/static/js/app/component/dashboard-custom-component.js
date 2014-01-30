define(['jquery', 'ajax', 'dialog', 'dynamic'], function ($, ajax, dialog, dynamic) {

    var self = {};

    self.createDashboard = function (btn) {
        showDashboardDialog({
            loading: {
                el: $(btn)
            },
            dashboard: {
                name: '',
                branches: []
            },
            successFn: function (form, dialog) {
                ajax.post({
                    url: 'ui/manage/dashboard',
                    data: form,
                    successFn: function () {
                        dialog.closeFn();
                        dynamic.reloadSection('dashboard-custom');
                    },
                    errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                });
            }
        });
    };

    self.editDashboard = function (dashboardId) {
        ajax.get({
            url: 'ui/manage/dashboard/{0}'.format(dashboardId),
            successFn: function (dashboardConfig) {
                showDashboardDialog({
                    dashboard: dashboardConfig,
                    successFn: function (form, dialog) {
                        ajax.put({
                            url: 'ui/manage/dashboard/{0}'.format(dashboardId),
                            data: form,
                            successFn: function () {
                                dialog.closeFn();
                                dynamic.reloadSection('dashboard-custom');
                            },
                            errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                        })
                    }
                })
            }
        })
    };

    function showDashboardDialog(dialogConfig) {
        ajax.get({
            url: 'ui/manage/branch',
            loading: dialogConfig.loading,
            successFn: function (branches) {
                dialog.show({
                    title: 'dashboard.custom'.loc(),
                    templateId: 'dashboard-custom-dialog',
                    data: {
                        branches: branches
                    },
                    initFn: function (config) {
                        // Dashboard name
                        config.form.find('#dashboard-custom-name').val(dialogConfig.dashboard.name);
                        // List of branches
                        config.form.find('.dashboard-custom-dialog-branch').each(function (i, tr) {
                            var branchId = $(tr).attr('id');
                            // TODO Initial state
                            var selected = false;
                            $.each(dialogConfig.dashboard.branches, function (index, branch) {
                                if (branch.id == branchId) {
                                    selected = true;
                                }
                            });
                            $(tr).data('branch-selected', selected);
                            if (selected) {
                                $(tr).addClass('dashboard-custom-branch-selected');
                            }
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
                        // Posting
                        dialogConfig.successFn({
                            name: $('#dashboard-custom-name').val(),
                            branches: branches
                        }, config);
                    }
                });
            }
        });
    }

    return self;

});