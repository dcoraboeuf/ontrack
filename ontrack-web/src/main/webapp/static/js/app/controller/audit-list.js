define(['render'], function (render) {

    return {
        url: function (config) {
            return 'gui/event?u=1' + config.filter;
        },
        render: render.asTableTemplate('audit')
    }

});