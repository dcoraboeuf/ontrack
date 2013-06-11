define(['jquery'], function ($) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    function onSelectValidationStamp(input) {
        var validationStamp = $(input).attr('data-validation-stamp');
        var validationStampId = $(input).attr('data-validation-stamp-id');
        if (input.is(':checked')) {
            // selectValidationStamp(validationStamp);
            $('#validation-stamp-' + validationStampId).addClass('checked');
        } else {
            // unselectValidationStamp(validationStamp);
            $('#validation-stamp-' + validationStampId).removeClass('checked');
        }
    }

    $('.validation-stamp').each(function(index, input) {
        $(input).change(function () {
            onSelectValidationStamp($(input));
        });
    });

})