define(['jquery', 'ajax', 'render', 'common', 'plot'], function ($, ajax, render, common, plot) {

    Handlebars.registerHelper('git_changetype', function (key, options) {
        return 'git.changelog.files.changeType.{0}'.format(key).loc();
    });

    var commits = null;
    var files = null;

    function displayCommits(data) {
        // Stores the commits (local cache for display purpose only)
        commits = data;
        // Rendering
        render.renderInto(
            $('#commits'),
            'extension/git-changelog-commits',
            commits.log,
            function () {
                // Plotting
                plot.draw(document.getElementById('commits-canvas'), commits.log.plot);
                // Tooltips
                common.tooltips();
            }
        );
    }

    function displayFiles(data) {
        // Stores the files (local cache for display purpose only)
        files = data;
        // Rendering
        render.renderInto(
            $('#files'),
            'extension/git-changelog-files',
            files,
            function () {
                // Tooltips
                common.tooltips();
            }
        );
    }

    function loadSummary() {
        // Nothing to load, just adjust the hash
        location.hash = "";
    }

    function loadCommits() {
        location.hash = "commits";
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

    function loadFiles() {
        location.hash = "files";
        if (files == null) {
            // UUID for the change log
            var uuid = $('#changelog').val();
            // Loads the files
            ajax.get({
                url: 'ui/extension/git/changelog/{0}/files'.format(uuid),
                loading: {
                    el: '#files',
                    mode: 'appendText'
                },
                successFn: displayFiles,
                errorFn: changelogErrorFn()
            });
        }
    }

    function changelogErrorFn() {
        return ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn('#changelog-error'));
    }

    function init() {
        $('#summary-tab').on('show', loadSummary);
        $('#commits-tab').on('show', loadCommits);
        $('#files-tab').on('show', loadFiles);
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