var Accounts = function () {

    function createAccount () {
		Application.dialogAndSubmit({
			id: 'account-create-dialog',
			title: loc('account.new'),
			url: 'ui/admin/accounts',
			successFn: function (data) {
				location = 'gui/admin/accounts';
			}
		});
    }

    return {
        createAccount: createAccount
    };

} ();