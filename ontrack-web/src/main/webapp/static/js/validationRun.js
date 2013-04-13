var ValidationRun = function () {

    function statusUpdateDataTemplate (validationRunId) {
        return Template.config({
            url: 'ui/manage/validation_run/{0}/statusUpdateData'.format(validationRunId),
            render: Template.asSimpleTemplate('statusUpdateDataTemplate')
        });
    }

    function historyTemplate (validationRunId, buildId) {
        return Template.config({
            url: 'ui/manage/validation_run/{0}/history?u=1'.format(validationRunId),
            more: true,
            preProcessingFn: function (validationRunEvents) {
                var list = [];
                var currentRun = true;
                var currentBuild = true;
                list.push({
                    header: true,
                    title: loc('validationRun.history.thisRun')
                });
                $.each (validationRunEvents, function (index, validationRunEvent) {
                    // This run?
                    if (validationRunEvent.validationRun.id == validationRunId) {
                        validationRunEvent.thisBuild = true;
                        validationRunEvent.thisRun = true;
                    } else if (validationRunEvent.validationRun.build.id == buildId) {
                        if (currentRun) {
                            currentRun = false;
                            list.push({
                                header: true,
                                title: loc('validationRun.history.thisBuild'),
                                link: 'gui/project/{0}/branch/{1}/build/{2}'.format(
                                    validationRunEvent.validationRun.build.branch.project.name.html(),
                                    validationRunEvent.validationRun.build.branch.name.html(),
                                    validationRunEvent.validationRun.build.name.html()
                                )
                            });
                        }
                        validationRunEvent.thisBuild = true;
                        validationRunEvent.thisRun = false;
                    } else {
                        if (currentBuild) {
                            currentBuild = false;
                            list.push({
                                header: true,
                                title: loc('validationRun.history.allBuilds')
                            });
                        }
                        validationRunEvent.thisBuild = false;
                        validationRunEvent.thisRun = false;
                    }
                    list.push(validationRunEvent);
                });
                return list;
            },
            render: Template.asTableTemplate('historyItemTemplate')
        });
    }

    function sendStatus (status) {
        // Checks the description
        var description = $('#description').val();
        if (description.trim() == '') {
            $('#description').focus();
            $('#description-line').addClass('error');
            return false;
        }
        // Run ID stored as a hidden field
        var runId = $('#validationRunId').val();
        // Collects the properties and their values
        var properties = [];
        $('.update-status-property').each (function (index, cell) {
            var extension = $(cell).attr('extension');
            var name = $(cell).attr('property');
            var inputId = 'extension-{0}-{1}'.format(extension, name);
            var value = $('#' + inputId).val();
            if (value != '') {
                properties.push({
                    extension: extension,
                    name: name,
                    value: value
                });
            }
        });
        // Sends the form
        AJAX.post({
            url: 'ui/manage/validation_run/{0}/comment'.format(runId),
            data: {
                status: status,
                description: description,
                properties: properties
            },
            successFn: function (data) {
                // Closes the dialog
                $('#statusUpdate-form').hide();
                // Reloads the page
                location.reload();
            },
            errorFn: AJAX.simpleAjaxErrorFn(function (message) {
                Application.error('statusUpdate-error', message);
            })
        });
        // Does not submit
        return false;
    }

    function updateStatus () {
        $('#statusUpdate-error').hide();
        $('#statusUpdate-form').show();
        if ($('#statusUpdate-form').is(':visible')) {
            $('#description').focus();
        }
    }

    function cancelUpdateStatus () {
        $('#statusUpdate-form').hide();
    }

    return {
        sendStatus: sendStatus,
        updateStatus: updateStatus,
        cancelUpdateStatus: cancelUpdateStatus,
        historyTemplate: historyTemplate,
        statusUpdateDataTemplate: statusUpdateDataTemplate
    };

} ();