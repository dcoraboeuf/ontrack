define(['jquery', 'application'], function ($, application) {

    var project = $('#project').val();
    var branch = $('#branch').val();
    var build = $('#build').val();
    var validationStamp = $('#validationStamp').val();
    var validationRunOrder = $('#validationRunOrder').val();

    /**
     * Deletion the project
     */
    function deleteValidationRun() {
        application.deleteEntity(
            'project/{0}/branch/{1}/build/{2}/validation_stamp/{3}/validation_run'.format(
                project,
                branch,
                build,
                validationStamp),
            validationRunOrder,
            function () {
                ''.goto();
            },
            function (data) {
                return data.runOrder;
            }
        );
    }

    $('#validation-run-delete').click(deleteValidationRun);

})