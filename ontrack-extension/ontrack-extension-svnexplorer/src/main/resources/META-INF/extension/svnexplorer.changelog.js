var ChangeLog = function () {

    var revisions = null;
    var issues = null;

    function toggleMergedRevision (parentRevision) {
        $('tr[parent="{0}"]'.format(parentRevision)).toggle();
    }

    function displayRevisions (data) {
        // Stores the revisions (local cache for display purpose only)
        revisions = data;
        // Computation for the layout
        var currentLevel = 0;
        $.each (revisions.list, function (index, entry) {
            // Merge management
            entry.merge = false;
            entry.merged = false;
            var level = entry.level;
            if (level > currentLevel) {
                var previous = revisions.list[index - 1];
                // The previous entry is a merge
                previous.merge = true;
                // Parent for this entry
                entry.merged = true;
                entry.mergeParent = previous.revision;
            } else if (level < currentLevel) {
                // Merge section done
            } else {
                // Same level
                // Copies properties from previous item
                if (index > 0) {
                    var previous = revisions.list[index - 1];
                    entry.merged = previous.merged;
                    entry.mergeParent = previous.mergeParent;
                }
            }
            // Change the current level
            currentLevel = level;
        });
        // Rendering
        $('#revisions').html(Template.render('revisions-template', revisions));
    }

    function displayIssues (data) {
        // FIXME displayIssues
    }

    function loadRevisions () {
        if (revisions == null) {
            // UUID for the change log
            var uuid = $('#changelog').val();
            // Loads the revisions
            AJAX.get({
                url: 'ui/extension/svnexplorer/changelog/{0}/revisions'.format(uuid),
                loading: {
                    el: '#revisions',
                    mode: 'appendText'
                },
                successFn: displayRevisions,
                errorFn: changelogErrorFn()
            });
        }
    }

    function loadIssues () {
        if (issues == null) {
            // UUID for the change log
            var uuid = $('#changelog').val();
            // Loads the issues
            AJAX.get({
                url: 'ui/extension/svnexplorer/changelog/{0}/issues'.format(uuid),
                loading: {
                    el: '#issues',
                    mode: 'appendText'
                },
                successFn: displayIssues,
                errorFn: changelogErrorFn()
            });
        }
    }

    function changelogErrorFn () {
        return AJAX.simpleAjaxErrorFn(AJAX.elementErrorMessageFn('#changelog-error'));
    }

    function init () {
        $('#revisions-tab').on('show', loadRevisions);
        $('#issues-tab').on('show', loadIssues);
    }

    return {
        init: init,
        toggleMergedRevision: toggleMergedRevision
    };

} ();

$(document).ready(ChangeLog.init);