var ChangeLog = function () {

    var revisions = null;

    function loadRevisions () {
        if (revisions == null) {
            // FIXME Loads the revisions
        }
    }

    function init () {
        $('#revisions').on('show', loadRevisions);
    }

    return {
        init: init
    };

} ();

$(document).ready(ChangeLog.init);