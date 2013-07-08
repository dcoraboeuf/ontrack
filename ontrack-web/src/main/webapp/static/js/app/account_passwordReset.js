define(['jquery', 'ajax', 'common'], function ($, ajax, common) {

    // Updating the account
    function accountPasswordReset() {
        var accountId = $('#accountId').val();
        var password = $('#password').val();
        var confirm = $('#confirmPassword').val();
        if (password != confirm) {
            $('#account-form-error').text('account.password.confirm.incorrect'.loc());
            $('#account-form-error').show();
        } else {
            ajax.put({
                url: 'ui/admin/accounts/{0}/passwordReset'.format(accountId),
                data: {
                    password: password
                },
                successFn: function () {
                    'gui/admin/accounts'.goto();
                },
                errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#account-form-error')))
            });
        }
        // Does not send
        return false;
    }

    $('#account-form').submit(accountPasswordReset);

});