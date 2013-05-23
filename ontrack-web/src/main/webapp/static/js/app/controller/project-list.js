define(['render','text!template/project-row.html'], function (render, template) {

    return {
        url: 'ui/manage/project',
        render: render.asTableTemplate(template)
    }

});