define(['render'], function (render) {

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