define(['render', 'jquery'], function (render, $) {

    function selectExtension(extension) {

    }

    function unselectExtension(extension) {

    }

    function setup(config) {
        // Indexation
        var extensionIndex = {};
        $.each(extensions, function (index, extension) {
            extensionIndex[extension.name] = extension;
        });
        // TODO Checkbox initialization
        // Actions
        $('.extension').each(function (index, input) {
            var extension = $(input).attr('data-extension');
            $(input).change(function () {
                if ($(input).is(':checked')) {
                    selectExtension(extension);
                } else {
                    unselectExtension(extension);
                }
            });
        });
    }

    return {
        url: 'ui/admin/extensions',
        render: render.asTableTemplate('extensions', setup)
    }

})