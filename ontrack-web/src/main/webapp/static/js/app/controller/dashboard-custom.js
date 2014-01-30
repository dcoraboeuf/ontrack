define(['render'], function (render) {

    return {
        url: 'ui/manage/dashboard',
        render: render.asTableTemplate('dashboard-custom-line')
    }

});