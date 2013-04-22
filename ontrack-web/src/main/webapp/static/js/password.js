var Password = function () {

    function controlAndSubmit () {
        var newPassword = $('#newPassword').val();
        var newPasswordConfirm = $('#newPasswordConfirm').val();
        if (newPassword == newPasswordConfirm) {
            return true;
        } else {
            Application.error('password-error', loc('password.confirmationNok'));
            $('newPasswordConfirm').focus();
            return false;
        }
    }

    return {
        controlAndSubmit: controlAndSubmit
    };

} ();