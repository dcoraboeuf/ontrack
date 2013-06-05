define(['jquery'], function ($) {

    $(document).ready(function () {
        $('#header-signin').click(function () {
            location.href = 'login?callbackUrl={0}'.format(encodeURIComponent(location.href));
        });
    });

});