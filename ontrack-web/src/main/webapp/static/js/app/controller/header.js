define(['jquery'], function ($) {

    function changeLanguage(lang) {
        if (location.search.indexOf("language") > -1) {
            location.search = location.search.replace(/language=[a-z][a-z]/, "language=" + lang);
        } else if (location.search == "") {
            location.search = "language=" + lang;
        } else {
            location.search += "&language=" + lang;
        }
    }

    $('#header-signin').click(function () {
        'login?callbackUrl={0}'.format(encodeURIComponent(location.href)).goto();
    });

    $('.language-selection').each(function (index, action) {
        var language = $(action).attr('data-language');
        $(action).click(function () {
            changeLanguage(language);
        });
    });

});