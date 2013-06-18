define(['jquery','ajax','render','common'], function ($, ajax, render, common) {

    var commits = null;

    function displayCommits(data) {
        // Stores the commits (local cache for display purpose only)
        commits = data;
    }

    function loadSummary() {
        // Nothing to load, just adjust the hash
        location.hash = "";
    }

    function loadCommits() {
        location.hash = "revisions";
        if (commits == null) {
            // UUID for the change log
            var uuid = $('#changelog').val();
            // Loads the revisions
            ajax.get({
                url: 'ui/extension/git/changelog/{0}/commits'.format(uuid),
                loading: {
                    el: '#revisions',
                    mode: 'appendText'
                },
                successFn: displayCommits,
                errorFn: changelogErrorFn()
            });
        }
    }

    function changelogErrorFn () {
        return ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn('#changelog-error'));
    }

    function init () {
        $('#summary-tab').on('show', loadSummary);
        $('#commits-tab').on('show', loadCommits);
        // Initial tab
        $(document).ready(function () {
            var hash = location.hash;
            if (hash != '' && hash != '#') {
                var tab = hash.substring(1);
                $('#{0}-tab'.format(tab)).tab('show');
            }
        });
    }

    init();

});