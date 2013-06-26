define(['render', 'common'], function (render, common) {

    function display(target, data) {
        render.renderInto(
            target,
            'extension/github-issues',
            {issues: data},
            function () {
                // Tooltips
                common.tooltips();
            }
        );
    }

    return {
        display: display
    }

})