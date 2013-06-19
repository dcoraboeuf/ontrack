define(['jquery', 'ajax', 'render', 'common'], function ($, ajax, render, common) {

    var commits = null;

    function displayCommits(data) {
        // Stores the commits (local cache for display purpose only)
        commits = data;
        // TODO Extracts the plotting in a separate module
        // Size
        var canvas = document.getElementById('commits-canvas');
        canvas.width = commits.plot.width;
        canvas.height = commits.plot.height;
        // Context
        var ctx = canvas.getContext('2d');
        // All items
        $.each(commits.plot.items, function (index, item) {
            drawItem(ctx, item);
        });
    }

    var COLORS = [
        'black',
        'red',
        'green',
        'blue'
    ];

    function getColor(item) {
        if (item.color) {
            return COLORS[item.color.index % COLORS.length];
        } else {
            return 'black';
        }
    }

    function drawLine(context, item) {
        context.beginPath();
        context.moveTo(item.a.x, item.a.y);
        context.lineTo(item.b.x, item.b.y);
        context.lineWidth = item.width;
        context.strokeStyle = getColor(item);
        context.stroke();
    }

    function drawItem(ctx, item) {
        if ('line' == item.type) {
            drawLine(ctx, item);
        } else {
            common.log('plot')('Unknown item type: {0}', item.type);
        }
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

    function changelogErrorFn() {
        return ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn('#changelog-error'));
    }

    function init() {
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