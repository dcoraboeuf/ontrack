var AccountUpdate = function () {

    function updateAccount () {
        Application.submit({
            id: 'account-form',
            method: 'PUT',
            url: 'ui/admin/accounts/{0}/update'.format($('#accountId').val()),
            successFn: function () {
                location.href = 'gui/admin/accounts';
            },
            errorMessageFn: AJAX.elementErrorMessageFn('#account-form-error')
        });
        // Does not send
        return false;
    }

    return {
        updateAccount: updateAccount
    };

} ();