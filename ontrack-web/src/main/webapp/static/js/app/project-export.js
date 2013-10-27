define(['jquery', 'ajax', 'app/component/export'], function ($, ajax, exp) {

    var projectExportUID = $('#projectExportUID').val();

    exp.check({
        container: $('#project-export-progress'),
        uid: projectExportUID
    })

})