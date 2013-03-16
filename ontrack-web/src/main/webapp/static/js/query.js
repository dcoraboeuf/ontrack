var Query = function () {

    /**
     * Launches the query asynchronously
     */
    function launch () {
        // Hides the error
        $('#query-error').hide();
        // Loading...
        $('#query-loading').show();
        // Form values
        var form = Application.values('query-form');
        // Conversion into a BuildFilter
        var filter = {
            sincePromotionLevel: form.sincePromotionLevel,
            withPromotionLevel: form.withPromotionLevel,
            limit: form.limit
        };
        // sinceValidationStamps
        if (form.sinceValidationStamp != '') {
            var statuses = [];
            if (form.sinceValidationStampStatus != '') {
                statuses.push(form.sinceValidationStampStatus);
            }
            filter.sinceValidationStamps = [{
                validationStamp: form.sinceValidationStamp,
                statuses: statuses
            }];
        }
        // withValidationStamps
        if (form.withValidationStamp != '') {
            var statuses = [];
            if (form.withValidationStampStatus != '') {
                statuses.push(form.withValidationStampStatus);
            }
            filter.withValidationStamps = [{
                validationStamp: form.withValidationStamp,
                statuses: statuses
            }];
        }
        // Branch
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Ajax call
        Application.ajax (
            'POST',
            'ui/manage/project/{0}/branch/{1}/query'.format(project, branch),
            filter,
            function (branchBuilds) {
                // Loading...
                $('#query-loading').hide();
                // Display
                $('#query-result').html(Builds.generateTableBranchBuilds(project, branch, branchBuilds));
                $('#query-result').show();
            },
            function (message) {
                // Error
                $('#query-error-message').text(message);
                $('#query-error').show();
                // Loading...
                $('#query-loading').hide();
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