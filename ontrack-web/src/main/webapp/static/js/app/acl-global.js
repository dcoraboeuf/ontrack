define(['jquery', 'ajax', 'app/component/account-role-selection', 'dynamic'], function ($, ajax, accountRoleSelection, dynamic) {

    Handlebars.registerHelper('aclRole', function (role) {
        return $('<i></i>')
            .append(' ' + 'globalFunction.{0}'.format(role).loc())
            .html();
    });

    function addACL(config, id, fn) {
        ajax.put({
            url: 'ui/admin/acl/global/{0}/{1}'.format(id, fn),
            successFn: function () {
                dynamic.reloadSection('acl-global-list')
            }
        })
    }

    // Gets the list of global functions
    ajax.get({
        url: 'ui/admin/acl/global/fn',
        successFn: function (fns) {
            // Account selection & global functions
            accountRoleSelection.init(
                $('#acl-account-form'),
                {
                    roles: fns,
                    submitFn: addACL
                }
            )
        }
    })


});