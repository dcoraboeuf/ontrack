define(['jquery','ajax'], function ($, ajax) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    $('#branch-clone-form').submit(function () {
        // Collects the branch properties
        var branchProperties = [];
        $('.branch-property').each (function (index, el) {
            var extension = $(el).attr('extension');
            var property = $(el).attr('property');
            var value = $('#extension-{0}-{1}'.format(extension, property)).val();
            branchProperties.push({
                extension: extension,
                name: property,
                value: value
            });
        });
        // Collects the validation stamp expressions
        var validationStampExpressions = [];
        $('.validation-stamp-property').each(function (index, el) {
            var extension = $(el).attr('extension');
            var property = $(el).attr('property');
            var regex = $('#validation-stamp-{0}-{1}-regex'.format(extension, property)).val();
            var replacement = $('#validation-stamp-{0}-{1}-replacement'.format(extension, property)).val();
            if (regex != '') {
                validationStampExpressions.push({
                    extension: extension,
                    name: property,
                    regex: regex,
                    replacement: replacement
                });
            }
        });
        // Collects the promotion level expressions
        var promotionLevelExpressions = [];
        $('.promotion-level-property').each(function (index, el) {
            var extension = $(el).attr('extension');
            var property = $(el).attr('property');
            var regex = $('#promotion-level-{0}-{1}-regex'.format(extension, property)).val();
            var replacement = $('#promotion-level-{0}-{1}-replacement'.format(extension, property)).val();
            if (regex != '') {
                promotionLevelExpressions.push({
                    extension: extension,
                    name: property,
                    regex: regex,
                    replacement: replacement
                });
            }
        });
        // AJAX call
        ajax.post({
            url: 'ui/manage/project/{0}/branch/{1}/clone'.format(project, branch),
            loading: {
                el: $('#clone-submit')
            },
            data: {
                name: $('#name').val(),
                description: $('#description').val(),
                branchProperties: branchProperties,
                validationStampReplacements: validationStampExpressions,
                promotionLevelReplacements: promotionLevelExpressions
            },
            successFn: function (summary) {
                'gui/project/{0}/branch/{1}'.format(project, summary.name).goto();
            },
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#clone-error')))
        });
        // No regular form submit
        return false;
    });

});