define(['application','dialog','ajax','jquery'], function(application, dialog, ajax, $) {

    var project = $('#project').val();

    /**
     * Deletion the project
     */
    function deleteProject() {
        application.deleteEntity('project', project, function () {
            location.href = '';
        });
    }

    /**
     * Update the project
     */
    function updateProject() {
        ajax.get({
            url: 'ui/manage/project/{0}'.format(project),
            successFn: function (summary) {
                dialog.show({
                    title: 'project.update'.loc(),
                    templateId: 'project-update',
                    initFn: function (config) {
                        config.form.find('#project-name').val(summary.name);
                        config.form.find('#project-description').val(summary.description);
                    },
                    submitFn: function (config) {
                        ajax.put({
                            url: 'ui/manage/project/{0}'.format(project),
                            data: {
                                name: config.form.find('#project-name').val(),
                                description: config.form.find('#project-description').val()
                            },
                            successFn: function (updatedProject) {
                                config.closeFn();
                                location.href = 'gui/project/{0}'.format(updatedProject.name);
                            },
                            errorFn: ajax.simpleAjaxErrorFn(config.errorFn)
                        });
                    }
                });
            }
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

    // Update project
    $('#command-project-update').click(updateProject);

});