define(['render', 'app/component/account-selection', 'common'], function (render, accountSelection, common) {

    Handlebars.registerHelper('aclRole', function (role) {
        return $('<i></i>')
            .append(' ' + 'globalFunction.{0}'.format(role).loc())
            .html();
    });

    function onSubmit(config) {
        var role = config.container.find('.acl-role-selection').val();
        var account = accountSelection.val(config.accountSelector);
        if (account) {
            var accountId = account.id;
            config.submitFn(config, accountId, role);
        }
    }

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
            },
            function () {
                // Account selection
                config.accountSelector = accountSelection.init(container.find('.acl-account-selection'));
                // Add button
                container.find('.acl-form').submit(function () {
                    try {
                        onSubmit(config);
                    } catch (e) {
                        common.error('account-role-selection')(e)
                    }
                    return false;
                })
            }
        )
    }

    return {
        init: init
    }

});