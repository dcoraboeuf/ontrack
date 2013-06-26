define(['jquery', 'ajax', 'render', 'common', 'plot'], function ($, ajax, render, common, plot) {

    Handlebars.registerHelper('git_changetype', function (key, options) {
        return 'git.changelog.files.changeType.{0}'.format(key).loc();
    });

    var commits = null;
    var files = null;
    var extensionDataIndex = {};

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

    function loadExtension(extension, extensionName) {
        var extensionId = extension + '-' + extensionName;
        location.hash = extensionId;
        var extensionData = extensionDataIndex[extensionId];
        if (extensionData == null) {
            // UUID for the change log
            var uuid = $('#changelog').val();
            // Loads the data
            ajax.get({
                url: 'ui/extension/{0}/{1}/{2}'.format(extension, extensionName, uuid),
                loading: {
                    el: '#{0}'.format(extensionId),
                    mode: 'appendText'
                },
                successFn: function (data) {
                    displayExtension(extension, extensionName, data);
                },
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
        // Extensions
        $('.changelog-extension').each(function (index, def) {
            var extension = $(def).attr('data-extension');
            var extensionName = $(def).attr('data-extension-name');
            $(def).on('show', function () {
                loadExtension(extension, extensionName);
            });
        });
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