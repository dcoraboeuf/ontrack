define(['dialog','ajax','common'], function (dialog, ajax, common) {

    // Creating an account
    function createAccount() {
        dialog.show({
            title: 'account.new'.loc(),
            templateId: 'account-create',
            initFn: function (config) {
                config.form.find('#mode').unbind('change');
                config.form.find('#mode').change(function () {
                    var mode = config.form.find('#mode').val();
                    var password = (mode == 'builtin');
                    if (password) {
                        config.form.find('.account-password').show();
                        config.form.find('#password').attr('required', 'required');
                        config.form.find('#passwordConfirm').attr('required', 'required');
                    } else {
                        config.form.find('.account-password').hide();
                        config.form.find('#password').removeAttr('required');
                        config.form.find('#passwordConfirm').removeAttr('required');
                    }
                });
            },
            submitFn: function (config) {
                // Validation
                var success;
                var mode = config.form.find('#mode').val();
                if (mode == 'builtin') {
                    var password = config.form.find('#password').val();
                    var confirm = config.form.find('#passwordConfirm').val();
                    if (password != confirm) {
                        config.errorFn('account.password.confirm.incorrect'.loc());
                        success = false;
                    } else {
                        success = true;
                    }
                } else {
                    success = true;
                }
                // In case of success
                if (success) {
                    ajax.post({
                        url: 'ui/admin/accounts',
                        data: common.values(config.form),
                        successFn: function () {
                            location.reload();
                        },
                        errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                    });
                }
                // Does not submit in any case
                return false;
            }
        });
    }

    $('#account-create').click(createAccount);

});