define(['render', 'common'], function (render, common) {

    function display(target, data, uuid) {
        render.renderInto(
            target,
            'extension/github-issues',
            {issues: data, uuid: uuid},
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