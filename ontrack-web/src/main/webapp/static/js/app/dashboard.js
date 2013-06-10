define(['ajax', 'jquery', 'render'], function (ajax, $, render) {

    // Gets the list of branches
    var branches = [];
    $('.branch-info').each(function (index, info) {
        branches.push({
            project: $(info).attr('project'),
            branch: $(info).attr('branch')
        });
    });

    // Current page
    var index = 0;

    // Refresh function
    function refresh() {
        // Gets the current branch
        var page = index % (branches.length);
        var branch = branches[page];
        // Refreshes the content of the branch
        ajax.get({
            url: 'ui/dashboard/project/{0}/branch/{1}'.format(branch.project, branch.branch),
            loading: {
                mode: 'toggle',
                el: $('#branch-content-loading')
            },
            successFn: function (dashboard) {

            },
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#branch-content-error')))
        });
        // Next
        index++;
    }

    // Refreshes the dashboard content
    refresh();
    setInterval(refresh, 10000);

})