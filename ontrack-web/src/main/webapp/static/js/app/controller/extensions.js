define(['render', 'jquery', 'ajax', 'dynamic'], function (render, $, ajax, dynamic) {

    function selectExtension(extension) {
        ajax.put({
            url: 'ui/admin/extensions/{0}'.format(extension),
            successFn: function () {
                dynamic.reloadSection('extensions')
            }
        })
    }

    function unselectExtension(extension) {
        ajax.del({
            url: 'ui/admin/extensions/{0}'.format(extension),
            successFn: function () {
                dynamic.reloadSection('extensions')
            }
        })
    }

    function setup(config, extensions) {
        // Indexation
        var extensionIndex = {};
        $.each(extensions, function (index, extension) {
            extensionIndex[extension.name] = extension;
        });
        // Actions
        $('.extension').each(function (index, input) {
            var extension = $(input).attr('data-extension');
            // Checkbox initialization
            if (extensionIndex[extension].enabled) {
                $(input).attr('checked', 'checked');
            } else {
                $(input).removeAttr('checked');
            }
            // Actions
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