define(['jquery','common'], function ($, common) {
    $('#indexation-full').click(function () {
        common.confirmAndCall(
            'subversion.indexation.full.confirmation'.loc(),
            function () {
                'gui/extension/svn/indexation/full'.goto();
            }
        )
    });
});