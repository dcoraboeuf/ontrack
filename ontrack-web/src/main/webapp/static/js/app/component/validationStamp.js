define(['ajax','dialog'], function (ajax, dialog) {

    function changeOwner (project, branch, stamp, successFn) {
        // Gets the details of the validation stamp
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(
                project,
                branch,
                stamp
            ),
            successFn: function (summary) {
                // Gets the current owner
                var currentOwner = summary.owner ? summary.owner.id : 0;
                // Gets the list of potential owners
                ajax.get({
                    url: 'ui/admin/accounts',
                    successFn: function (accounts) {
                        dialog.show({
                            templateId: 'validation-stamp-owner-dialog',
                            title: 'validation_stamp.owner.change'.loc(),
                            initFn: function (config) {
                                // Validation stamp name
                                config.form.find('#validation_stamp-owner-dialog-name').val(stamp);
                                // List of accounts
                                var ownerSelect = config.form.find('#validation_stamp-owner-dialog-owner');
                                ownerSelect.empty();
                                ownerSelect
                                    .append($("<option></option>")
                                        .attr("value", "")
                                        .text('validation_stamp.owner.none'.loc()));
                                $.each(accounts, function (index, account) {
                                    var option = $("<option></option>")
                                        .attr("value", account.id)
                                        .text('{0} - {1}'.format(account.name, account.fullName));
                                    if (account.id == currentOwner) {
                                        option.attr('selected', 'selected');
                                    }
                                    ownerSelect.append(option);
                                });
                            },
                            submitFn: function (config) {
                                // Gets the selected owner
                                var owner = config.form.find('#validation_stamp-owner-dialog-owner').val();
                                // Removes the owner
                                if (owner == '') {
                                    ajax.del({
                                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/owner'.format(
                                            project,
                                            branch,
                                            stamp
                                        ),
                                        loading: {
                                            el: config.form.find('[type="submit"]')
                                        },
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                // Closes the dialog
                                                config.closeFn();
                                                // OK
                                                successFn();
                                            }
                                        }
                                    });
                                }
                                // Changes the owner
                                else {
                                    ajax.put({
                                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/owner/{3}'.format(
                                            project,
                                            branch,
                                            stamp,
                                            owner
                                        ),
                                        loading: {
                                            el: config.form.find('[type="submit"]')
                                        },
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                // Closes the dialog
                                                config.closeFn();
                                                // OK
                                                successFn();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    return {
        changeOwner: changeOwner
    }

});