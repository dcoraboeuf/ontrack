var Accounts = function () {

    function createAccount () {
		Application.dialogAndSubmit({
			id: 'account-create-dialog',
			title: loc('account.new'),
			url: 'ui/admin/accounts',
			openFn: function () {
                $('#mode').unbind('change');
                $('#mode').change(function () {
                    var mode = $('#mode').val();
                    var password = (mode == 'builtin');
                    if (password) {
                        $('#password-line').show();
                        $('#password').attr('required', 'required');
                        $('#passwordConfirm-line').show();
                        $('#passwordConfirm').attr('required', 'required');
                    } else {
                        $('#password-line').hide();
                        $('#password').removeAttr('required');
                        $('#passwordConfirm-line').hide();
                        $('#passwordConfirm').removeAttr('required');
                    }
                });
			},
			successFn: function (data) {
				location = 'gui/admin/accounts';
			}
		});
    }

    return {
        createAccount: createAccount
    };

} ();