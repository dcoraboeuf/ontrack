var SVNIndexation = function () {

    function indexationFull () {
        Application.confirmAndCall(
            loc('subversion.indexation.full.confirmation'),
            function () {
                location = 'gui/extension/svn/indexation/full';
            }
        );
    }

    return {
        indexationFull: indexationFull
    };

} ();