var BranchClone = function () {

    function clone (project, branch) {
        // Collects the branch properties
        var branchProperties = [];
        $('.branch-property').each (function (index, el) {
            var extension = $(el).attr('extension');
            var property = $(el).attr('property');
            var value = $('#extension-{0}-{1}'.format(extension, property)).val();
            branchProperties.push({
                extension: extension,
                property: property,
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
                    property: property,
                    regex: regex,
                    replacement: replacement
                });
            }
        });
        // TODO Collects the promotion level expressions
        // AJAX call
        AJAX.post({
            url: 'ui/manage/project/{0}/branch/{1}/clone'.format(project, branch),
            loading: {
                el: $('#clone-submit')
            },
            data: {
                name: $('#name').val(),
                description: $('#description').val(),
                branchProperties: branchProperties,
                validationStampExpressions: validationStampExpressions
            },
            successFn: function (summary) {
                location = 'gui/project/{0}/branch/{1}'.format(project, summary.name);
            },
            errorMessageFn: AJAX.simpleAjaxErrorFn(AJAX.elementErrorMessageFn($('#clone-error')))
        });
        return false;
    }

    return {
        clone: clone
    };

} ();