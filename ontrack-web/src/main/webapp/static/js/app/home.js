define(['dialog', 'jquery'], function(dialog, $) {

    /**
     * Creating a project
     */
    function createProject() {
        dialog.show({
            title: 'project.create.title'.loc(),
            templateId: 'project-create',
            submitFn: function (config) {
                config.closeFn();
            }
        });
    }

    //

    $('#project-create-button').click(createProject);

});