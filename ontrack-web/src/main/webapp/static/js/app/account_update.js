define(['jquery', 'ajax', 'common'], function ($, ajax, common) {

    // Updating the account
    function accountUpdate() {
        var accountId = $('#accountId').val();
        ajax.put({
            url: 'ui/admin/accounts/{0}/update'.format(accountId),
            data: common.values($('#account-form')),
            successFn: function () {
                location.href = 'gui/admin/accounts';
            },
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#account-form-error')))
        });
        // Does not send
        return false;
    }

    $('#account-form').submit(accountUpdate);

});