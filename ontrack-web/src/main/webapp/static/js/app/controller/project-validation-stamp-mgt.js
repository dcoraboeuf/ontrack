define(['render', 'jquery', 'ajax', 'common'], function (render, $, ajax, common) {

    function loadingError(error) {
        ajax.elementErrorMessageFn($('#project-validation-stamp-mgt-error'))(error.responseText)
    }

    function loadValidationStamps(config, branch) {
        return $.get('ui/manage/project/{0}/branch/{1}/validation_stamp'.format(config.project, branch))
    }

    function prepareCommands(model, indexedStamps1, indexedStamps2, target) {
        target.find('.commands').show();
    }

    function displayBranches(config, stamps1, stamps2, target) {
        var project = config.project;
        var branch1 = config.branch1;
        var branch2 = config.branch2;

        // Indexation of validation stamps by names
        var stampNameFn = function (stamp) {
            return stamp.name;
        }
        var indexedStamps1 = common.uniqueIndex(stamps1, stampNameFn);
        var indexedStamps2 = common.uniqueIndex(stamps2, stampNameFn);

        // Gets all names in a sorted list
        var names = [];
        for (var name in indexedStamps1) {
            if (names.indexOf(name) < 0) {
                names.push(name);
            }
        }
        for (var name in indexedStamps2) {
            if (names.indexOf(name) < 0) {
                names.push(name);
            }
        }
        names.sort();

        // Creating the model
        var model = {
            project: project,
            branch1: branch1,
            branch2: branch2,
            stamps: []
        };
        $.each(names, function (index, name) {
            var line = {
                name: name
            };
            var stamp1 = indexedStamps1[name];
            if (stamp1) {
                line.stamp1 = stamp1;
            }
            var stamp2 = indexedStamps2[name];
            if (stamp2) {
                line.stamp2 = stamp2;
            }
            model.stamps.push(line);
        });

        // Rendering
        render.renderInto(
            target.find('.content'),
            'project-validation-stamp-mgt-list',
            model,
            function () {
                prepareCommands(model, indexedStamps1, indexedStamps2, target)
            }
        )
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
                    displayBranches(config, result1[0], result2[0], target);
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