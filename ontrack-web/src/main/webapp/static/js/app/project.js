define(['application'], function(application) {

    /**
     * Deletion of a project
     */
    function deleteProject() {
        application.deleteEntity('project', $('#project').val(), function () {
            location.href = '';
        });
    }

    // Delete project
    $('#command-project-delete').click(deleteProject);

    // TODO Update project

});