define(['render', 'jquery', 'ajax', 'common'], function (render, $, ajax, common) {

    function loadingError(error) {
        ajax.elementErrorMessageFn($('#project-validation-stamp-mgt-error'))(error.responseText)
    }

    function sendUpdates(config) {
        var project = config.project;
        var branch1 = config.branch1;
        var branch2 = config.branch2;
        // 1 - collect the selected validation stamp names
        var names = $('.project-validation-stamp-mgt-chk').filter(':checked').map(function (i, e) {
            return $(e).attr('data-name')
        }).toArray();
        // 2 - collect the replacement expression
        var replacements = [];
        $('div.project-validation-stamp-mgt-property').each(function (i, div) {
            var extension = $(div).attr('data-extension');
            var name = $(div).attr('data-name');
            var oldRegex = $(div).find('.project-validation-stamp-mgt-property-old').val();
            var newRegex = $(div).find('.project-validation-stamp-mgt-property-new').val();
            if (oldRegex != '') {
                replacements.push({
                    extension: extension,
                    name: name,
                    regex: oldRegex,
                    replacement: newRegex
                })
            }
        });
        // Sends the update
        ajax.post({
            url: 'ui/manage/project/{0}/validation-stamp-mgt'.format(project),
            data: {
                branch1: branch1,
                branch2: branch2,
                stamps: names,
                replacements: replacements
            },
            loading: {
                el: $('#project-validation-stamp-mgt-submit')
            },
            successFn: function () {
                // TODO Feedback & exit
            }
        })
    }

    function loadValidationStamps(config, branch) {
        return $.get('ui/manage/project/{0}/branch/{1}/validation_stamp'.format(config.project, branch))
    }

    function loadEditableProperties(config, branch) {
        return $.get('ui/manage/project/{0}/branch/{1}/clone'.format(config.project, branch))
    }

    function prepareCommands(config, indexedStamps2, clone1, clone2, target) {
        // Select missing only
        $('#project-validation-stamp-mgt-select-missing').click(function () {
            $('.project-validation-stamp-mgt-chk').each(function (index, chk) {
                var name = $(chk).attr('data-name');
                var checked = indexedStamps2[name] ? false : true;
                $(chk).prop('checked', checked)
            })
        });
        // Select none
        $('#project-validation-stamp-mgt-select-none').click(function () {
            $('.project-validation-stamp-mgt-chk').prop('checked', false)
        });
        // Select all
        $('#project-validation-stamp-mgt-select-all').click(function () {
            $('.project-validation-stamp-mgt-chk').prop('checked', true)
        });
        // Shows the commands
        target.find('.commands').show();

        // Assemble the properties
        var propertiesIndex = {};
        for (var i = 0; i < clone1.validationStampProperties.length; i++) {
            var property = clone1.validationStampProperties[i];
            var key = property.extension + '-' + property.name;
            propertiesIndex[key] = property;
        }
        for (var i = 0; i < clone2.validationStampProperties.length; i++) {
            var property = clone2.validationStampProperties[i];
            var key = property.extension + '-' + property.name;
            propertiesIndex[key] = property;
        }

        // Gets the values only
        var properties = [];
        for (var key in propertiesIndex) {
            properties.push(propertiesIndex[key])
        }

        // Renders the properties
        render.renderInto(
            target.find('.project-validation-stamp-mgt-properties'),
            'project-validation-stamp-mgt-properties',
            {
                properties: properties
            }
        );

        // Submit button
        $('#project-validation-stamp-mgt-submit').click(function () {
            sendUpdates(config)
        });
    }

    function displayBranches(config, stamps1, stamps2, clone1, clone2, target) {
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
                prepareCommands(config, indexedStamps2, clone1, clone2, target)
            }
        )
    }

    function loadBranches(config, target) {
        var branch1 = config.branch1;
        var branch2 = config.branch2;
        if (branch1 != '' && branch2 != '' && branch1 != branch2) {
            // Parallel AJAX calls
            var ajaxStamps1 = loadValidationStamps(config, branch1);
            var ajaxStamps2 = loadValidationStamps(config, branch2);
            var ajaxProperties1 = loadEditableProperties(config, branch1);
            var ajaxProperties2 = loadEditableProperties(config, branch2);
            // Loading...
            ajax.showLoading({
                mode: 'container',
                el: $('#project-validation-stamp-mgt-loading')
            }, true);
            // Completion
            $.when(ajaxStamps1, ajaxStamps2, ajaxProperties1, ajaxProperties2).then(
                function (resultStamps1, resultStamps2, resultProperties1, resultProperties2) {
                    displayBranches(config, resultStamps1[0], resultStamps2[0], resultProperties1[0], resultProperties2[0], target);
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