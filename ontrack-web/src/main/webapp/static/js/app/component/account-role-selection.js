define(['render'], function (render) {

    Handlebars.registerHelper('aclRole', function (role) {
        return $('<i></i>')
            .append(' ' + 'globalFunction.{0}'.format(role).loc())
            .html();
    });

    function init(container, config) {
        // ID
        config.id = container.attr('id');
        // Default values
        config = $.extend({
            id: 'acl-form'
        }, config);
        // Container link
        config.container = container;
        // Initialization of the form
        render.renderInto(
            container,
            'account-role-selection',
            {
                config: config
            }
        )
    }

    return {
        init: init
    }

});