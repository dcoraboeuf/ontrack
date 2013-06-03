define(['jquery','common'], function ($, common) {
    $('#indexation-full').click(function () {
        common.confirmAndCall(
            'subversion.indexation.full.confirmation'.loc(),
            function () {
                location.href = 'gui/extension/svn/indexation/full';
            }
        )
    });
});