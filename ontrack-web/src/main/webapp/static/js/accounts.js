var Accounts = function () {

    function createAccount () {
		Application.dialogAndSubmit(
			'account-create-dialog',
			loc('account.new'),
			'POST',
			'ui/admin/accounts',
			function (data) {
				location = 'gui/admin/accounts';
			});
    }

    return {
        createAccount: createAccount
    };

} ();