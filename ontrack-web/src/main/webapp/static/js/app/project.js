define(['application','dialog','ajax','jquery'], function(application, dialog, ajax, $) {

    /**
     * Deletion of a project
     */
    function deleteProject() {
        application.deleteEntity('project', $('#project').val(), function () {
            location.href = '';
        });
    }

    /**
     * Creation of a branch
     */
    function createBranch() {
        var project = $('#project').val();
        dialog.show({
            title: 'branch.create.title'.loc(),
            templateId: 'branch-create',
            submitFn: function (config) {
                ajax.post({
                    url: 'ui/manage/project/{0}/branch'.format(project),
                    data: {
                        name: $('#branch-name').val(),
                        description: $('#branch-description').val()
                    },
                    successFn: function (branch) {
                        config.closeFn();
                        location.href = 'gui/project/{0}/branch/{1}'.format(project, branch.name.html());
                    },
                    errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                });
            }
        });
    }

    // Create branch
    $('#branch-create-button').click(createBranch);

    // Delete project
    $('#command-project-delete').click(deleteProject);

    // TODO Update project

});