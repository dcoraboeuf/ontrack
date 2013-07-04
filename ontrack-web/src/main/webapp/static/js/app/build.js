define(['jquery', 'dialog', 'ajax', 'time', 'common'], function ($, dialog, ajax, time, common) {

    var project = $('#project').val();
    var branch = $('#branch').val();
    var build = $('#build').val();

    /**
     * Deletion of the build
     */
    function buildDelete() {
        common.confirmAndCall(
            'build.delete.prompt'.loc(build),
            function () {
                ajax.del({
                    url: 'ui/manage/project/{0}/branch/{1}/build/{2}'.format(project, branch, build),
                    successFn: function () {
                        'gui/project/{0}/branch/{1}'.format(project, branch).goto();
                    }
                })
            }
        );
    }

    /**
     * Promotion for a build
     */
    function buildPromote() {
        // Gets the list of promotions
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(project, branch),
            successFn: function (promotionLevels) {
                // Displays the dialog
                dialog.show({
                    title: 'build.promote.title'.loc(build),
                    templateId: 'build-promotion',
                    initFn: function (config) {
                        // No description by default
                        config.form.find('#build-promotion-description').val('');
                        // List of promotion levels
                        config.form.find('#build-promotion-promotionLevel').empty();
                        $.each(promotionLevels, function (index, promotionLevel) {
                            var option = $('<option></option>')
                                .attr('value', promotionLevel.name)
                                .text(promotionLevel.name);
                            config.form.find('#build-promotion-promotionLevel').append(option);
                        });
                        // Date / time picker
                        config.form.find('#build-promotion-creation').datetime({});
                    },
                    submitFn: function (config) {
                        // Collects the data
                        var promotionLevel = config.form.find('#build-promotion-promotionLevel').val();
                        var description = config.form.find('#build-promotion-description').val();
                        // Data to send
                        var data = {
                            description: description
                        };
                        // Date
                        data.creation = config.form.find('#build-promotion-creation').datetime('getDateTime');
                        // Call
                        ajax.post({
                            url: 'ui/control/project/{0}/branch/{1}/build/{2}/promotion_level/{3}'.format(project, branch, build, promotionLevel),
                            data: data,
                            loading: {
                                el: $('#build-promotion-submit')
                            },
                            successFn: function (result) {
                                config.closeFn();
                                location.reload();
                            }
                        });
                    }
                });
            }
        });
    }

    // Updating the build
    function buildUpdate() {
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/build/{2}'.format(project, branch, build),
            successFn: function (summary) {
                dialog.show({
                    title: 'build.update'.loc(),
                    templateId: 'build-update',
                    initFn: function (config) {
                        config.form.find('#build-name').val(summary.name);
                        config.form.find('#build-description').val(summary.description);
                    },
                    submitFn: function (config) {
                        ajax.put({
                            url: 'ui/manage/project/{0}/branch/{1}/build/{2}'.format(project, branch, build),
                            data: {
                                name: config.form.find('#build-name').val(),
                                description: config.form.find('#build-description').val()
                            },
                            successFn: function (updatedBuild) {
                                config.closeFn();
                                'gui/project/{0}/branch/{1}/build/{2}'.format(updatedBuild.branch.project.name, updatedBuild.branch.name, updatedBuild.name).goto();
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
        });
    }

    $('#build-update').click(buildUpdate);
    $('#build-promote').click(buildPromote);
    $('#build-delete').click(buildDelete);

});