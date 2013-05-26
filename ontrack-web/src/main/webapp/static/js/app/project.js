define(['application'], function(application) {

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
        // TODO Create branch
        alert('TODO');
    }

    // Create branch
    $('#branch-create-button').click(createBranch);

    // Delete project
    $('#command-project-delete').click(deleteProject);

    // TODO Update project

});