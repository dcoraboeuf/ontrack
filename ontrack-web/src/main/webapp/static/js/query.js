var Query = function () {

    function adaptLocation (form) {
        // Form as parameters
        var params = $.param(form);
        // Sets as hash
        location.hash = params;
    }

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
                // Adjust the location according to this form
                adaptLocation(form);
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

    function load () {
        var hash = location.hash;
        if (hash && hash != '' && hash != '#') {
            // Parses the hash (starts with the
            var form = $.deparam(hash.substring(1));
            // Initializes the form
            $('#withPromotionLevel').val(form.withPromotionLevel);
            $('#sincePromotionLevel').val(form.sincePromotionLevel);
            $('#withValidationStamp').val(form.withValidationStamp);
            $('#withValidationStampStatus').val(form.withValidationStampStatus);
            $('#sinceValidationStamp').val(form.sinceValidationStamp);
            $('#sinceValidationStampStatus').val(form.sinceValidationStampStatus);
            $('#limit').val(form.limit);
            // Launches the search
            launch();
        }
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
        // Launches a search if form initialized
        load();
    }

    return {
        init: init,
        launch: launch
    };

} ();

$(document).ready(Query.init);