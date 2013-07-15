define(['jquery', 'ajax'], function ($, ajax) {

    function changeProfileLanguage(lang) {
        ajax.put({
            url: 'ui/admin/language/{0}'.format(lang),
            successFn: function () {
                location.reload()
            }
        })
    }

    $('.profile-language').each(function (index, a) {
        var lang = $(a).attr('data-language');
        $(a).click(function () {
            changeProfileLanguage(lang)
        })
    })

})