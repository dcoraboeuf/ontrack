define(['jquery'], function($) {

    $('#password-form').submit(function () {
        var newPassword = $('#newPassword').val();
        var newPasswordConfirm = $('#newPasswordConfirm').val();
        if (newPassword == newPasswordConfirm) {
            return true;
        } else {
            $('#password-error').text('password.confirmationNok'.loc());
            $('#password-error').show();
            $('newPasswordConfirm').focus();
            return false;
        }
    });

});