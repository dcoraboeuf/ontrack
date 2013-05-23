define(['render','text!template/project-row.html'], function (render, template) {

    return {
        getUrl: function () {
            return 'ui/manage/project';
        },
        render: render.asTableTemplate(template)
    }

});