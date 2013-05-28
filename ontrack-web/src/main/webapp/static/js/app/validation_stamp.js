define(['jquery', 'ajax', 'dialog', 'application', 'dynamic'], function ($, ajax, dialog, application, dynamic) {

    var project = $('#project').val();
    var branch = $('#branch').val();
    var validationStamp = $('#validation_stamp').val();

    // Adding a comment
    $('#validation-stamp-comment-add').click(function () {
        $('#validation-stamp-comment-form').show();
        $('#validation-stamp-comment').focus();
    });

    // Cancelling the comment form
    $('#validation-stamp-comment-cancel').click(function () {
        $('#validation-stamp-comment-form').hide();
    });

    // Sending the form
    $('#validation-stamp-comment-form').submit(function () {
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
    });

});