define(['jquery'], function ($) {

    function changeLanguage(lang) {
        if (location.search.indexOf("language") > -1) {
            location.search = location.search.replace(/language=[a-z][a-z]/, "language=" + lang);
        } else if (location.search == "") {
            var url = location.href;
            if (url.substr(url.length - 1) == '?') {
                location.href += "language=" + lang;
            } else {
                location.href += "?language=" + lang;
            }
        } else {
            location.href += "&language=" + lang;
        }
    }

    $('#header-signin').click(function () {
        location.href = 'login?callbackUrl={0}'.format(encodeURIComponent(location.href));
    });

    $('.language-selection').each(function (index, action) {
        var language = $(action).attr('data-language');
        $(action).click(function () {
            changeLanguage(language);
        });
    });

});