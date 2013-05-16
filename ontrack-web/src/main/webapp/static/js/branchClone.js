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
        // TODO Collects the validation stamp expressions
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
                branchProperties: branchProperties
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