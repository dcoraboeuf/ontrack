define(['jquery','ajax'], function ($, ajax) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    function onSelectValidationStamp(input) {
        var validationStamp = $(input).attr('data-validation-stamp');
        var validationStampId = $(input).attr('data-validation-stamp-id');
        if (input.is(':checked')) {
            ajax.put({
                url: 'ui/dashboard/project/{0}/branch/{1}/validation_stamp/{2}'.format(
                    project,
                    branch,
                    validationStamp
                ),
                successFn: function (ack) {
                    if (ack.success) {
                        $('#validation-stamp-' + validationStampId).addClass('checked');
                    }
                }
            });
        } else {
            ajax.del({
                url: 'ui/dashboard/project/{0}/branch/{1}/validation_stamp/{2}'.format(
                    project,
                    branch,
                    validationStamp
                ),
                successFn: function (ack) {
                    if (ack.success) {
                        $('#validation-stamp-' + validationStampId).removeClass('checked');
                    }
                }
            });
        }
    }

    $('.validation-stamp').each(function(index, input) {
        $(input).change(function () {
            onSelectValidationStamp($(input));
        });
    });

})