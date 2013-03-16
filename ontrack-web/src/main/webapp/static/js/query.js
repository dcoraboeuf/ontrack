var Query = function () {

    /**
     * Launches the query asynchronously
     */
    function launch () {
        // Hides the error
        $('#query-error').hide();
        // Loading...
        $('#query-loading').show();
        // Ajax call
        Application.submit({
            id: 'query-form',
            url: 'ui/manage/project/{0}/branch/{1}/query'.format(
                                 $('#project').val(),
                                 $('#branch').val()
                             ),
            successFn: function (data) {
                alert(JSON.stringify(data));
            },
            errorMessageFn: function (message) {
                // Error
                $('#query-error-message').text(message);
                $('#query-error').show();
                // Loading...
                $('#query-loading').hide();
            }
        });
        // No submit
        return false;
    }

    /**
     * Initialization of the page
     */
    function init () {
        // Hides the loading component
        $('#query-loading').hide();
        // Hides the results
        $('#query-result').hide();
        // Hides the error
        $('#query-error').hide();
        // TODO Launches a search if form initialized
    }

    return {
        init: init,
        launch: launch
    };

} ();

$(document).ready(Query.init);