define(['ajax','jquery','render'], function (ajax, $, render) {

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
    function refresh () {
        // Gets the current branch
        var page = index % (branches.length);
        var branch = branches[page];
        // Refreshes the content of the branch
        $('#branch-content').text(branch.project + '/' + branch.branch);
        // Next
        index++;
    }

    // Refreshes the dashboard content
    refresh();
    setInterval(refresh, 10000);

})