define(['ajax','jquery'], function (ajax, $) {

    // Context
    var context = location.href.substring(location.href.indexOf('dashboard'));
    // Initial index
    var index = 0;

    // Display function
    function displayPage(page) {
        // Title
        $('#page-title').text(page.title);
    }

    // Refresh function
    function refresh () {
        // Gets the URL
        var url = 'ui/{0}/page/{1}'.format(context, index++);
        // Call
        ajax.get({
            url: url,
            loading: {
                el: $('#dashboard-loading'),
                mode: 'toggle'
            },
            successFn: displayPage,
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#dashboard-error')))
        })
    }

    // Refreshes the dashboard content
    refresh();
    setInterval(refresh, 10000);

})