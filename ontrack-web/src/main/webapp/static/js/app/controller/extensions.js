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