var ChangeLog = function () {

    var revisions = null;

    function loadRevisions () {
        if (revisions == null) {
            // UUID for the change log
            var uuid = $('#changelog').val();
            // FIXME Loads the revisions
            AJAX.get({
                url: 'ui/extension/svnexplorer/changelog/{0}/revisions'.format(uuid),
                loading: {
                    el: '#revisions',
                    mode: 'appendText'
                },
                successFn: $.noop,
                errorFn: changelogErrorFn()
            });
        }
    }

    function changelogErrorFn () {
        return AJAX.simpleAjaxErrorFn(AJAX.elementErrorMessageFn('#changelog-error'));
    }

    function init () {
        $('#revisions-tab').on('show', loadRevisions);
    }

    return {
        init: init
    };

} ();

$(document).ready(ChangeLog.init);