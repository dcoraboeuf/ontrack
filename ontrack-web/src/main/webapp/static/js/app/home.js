define(['dialog', 'jquery'], function(dialog, $) {

    /**
     * Creating a project
     */
    function createProject() {
        dialog.show({
            title: 'project.create.title'.loc(),
            templateId: 'project-create'
        });
    }

    //

    $('#project-create-button').click(createProject);

});