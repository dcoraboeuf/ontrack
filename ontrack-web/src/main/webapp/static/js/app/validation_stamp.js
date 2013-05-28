define(['jquery', 'ajax', 'dialog', 'application', 'dynamic'], function ($, ajax, dialog, application, dynamic) {

    var project = $('#project').val();
    var branch = $('#branch').val();
    var validationStamp = $('#validation_stamp').val();

    // Updating the validation stamp
    function updateValidationStamp() {
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(project, branch, validationStamp),
            successFn: function (summary) {
                dialog.show({
                    title: 'validation_stamp.update'.loc(),
                    templateId: 'validation-stamp-update',
                    initFn: function (config) {
                        config.form.find('#validation-stamp-name').val(summary.name);
                        config.form.find('#validation-stamp-description').val(summary.description);
                    },
                    submitFn: function (config) {
                        ajax.put({
                            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(project, branch, validationStamp),
                            data: {
                                name: config.form.find('#validation-stamp-name').val(),
                                description: config.form.find('#validation-stamp-description').val()
                            },
                            successFn: function (updatedValidationStamp) {
                                config.closeFn();
                                location.href = 'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(
                                    updatedValidationStamp.branch.project.name,
                                    updatedValidationStamp.branch.name,
                                    updatedValidationStamp.name);
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
        });
    }

    // Adding a comment
    function addComment() {
        $('#validation-stamp-comment-form').show();
        $('#validation-stamp-comment').focus();
    }

    // Cancelling the comment form
    function cancelComment() {
        $('#validation-stamp-comment-form').hide();
    }

    // Sending the form
    function sendComment() {
        ajax.post({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/comment'.format(
                project,
                branch,
                validationStamp
            ),
            data: {
                comment: $('#validation-stamp-comment').val()
            },
            loading: {
                el: $('#validation-stamp-comment-submit')
            },
            successFn: function () {
                $('#validation-stamp-comment').val('');
                dynamic.reloadSection('validation-stamp-comments');
                dynamic.reloadSection('audit');
            }
        });
        // OK
        return false;
    }

    $('#validation-stamp-update').click(updateValidationStamp);

    $('#validation-stamp-comment-cancel').click(cancelComment);

    $('#validation-stamp-comment-add').click(addComment);

    $('#validation-stamp-comment-form').submit(sendComment);

});