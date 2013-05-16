var BranchClone = function () {

    function clone (project, branch) {
        AJAX.post({
            url: 'ui/manage/project/{0}/branch/{1}/clone'.format(project, branch),
            loading: {
                el: $('#clone-submit')
            },
            data: {
                name: $('#name').val(),
                description: $('#description').val()
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