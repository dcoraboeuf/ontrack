define(['render', 'jquery', 'ajax'], function (render, $, ajax) {

    function loadingError(error) {
        ajax.elementErrorMessageFn($('#project-validation-stamp-mgt-error'))(error.responseText)
    }

    function loadValidationStamps(config, branch) {
        return $.get('ui/manage/project/{0}/branch/{1}/validation_stamp'.format(config.project, branch))
    }

    function loadBranches(config, target) {
        var branch1 = config.branch1;
        var branch2 = config.branch2;
        if (branch1 != '' && branch2 != '' && branch1 != branch2) {
            // Two parallel AJAX calls
            var ajax1 = loadValidationStamps(config, branch1);
            var ajax2 = loadValidationStamps(config, branch2);
            // Loading...
            ajax.showLoading({
                mode: 'container',
                el: $('#project-validation-stamp-mgt-loading')
            }, true);
            // Completion
            $.when(ajax1, ajax2).then(
                function (result1, result2) {
                    console.log(result1[0], result2[0]);
                },
                loadingError
            ).done(
                function () {
                    ajax.showLoading({
                        mode: 'container',
                        el: $('#project-validation-stamp-mgt-loading')
                    }, false);
                }
            )
        }
    }

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch'.format(config.project)
        },
        render: render.asSimpleTemplate(
            'project-validation-stamp-mgt',
            render.sameDataFn,
            function (config, branches, target) {
                config.branch1 = '';
                config.branch2 = '';
                // On selection of a branch
                target.find('select.project-validation-stamp-mgt-branch').change(function (e) {
                    var branchTarget = $(this).attr('data-target');
                    var branch = $(this).val();
                    if (branchTarget == '1') {
                        config.branch1 = branch;
                    } else {
                        config.branch2 = branch;
                    }
                    loadBranches(config, target);
                })
            }
        )
    }

});