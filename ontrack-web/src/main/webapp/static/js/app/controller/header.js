define(function () {

    $(document).ready(function () {
        $('#header-signin').click(function () {
            location = 'login?callbackUrl={0}'.format(encodeURIComponent(location.href));
        });
    });

});