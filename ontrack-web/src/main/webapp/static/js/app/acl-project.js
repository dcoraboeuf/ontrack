define(['jquery', 'ajax', 'app/component/account-role-selection', 'dynamic'], function ($, ajax, accountRoleSelection, dynamic) {

    Handlebars.registerHelper('aclRole', function (role) {
        return $('<i></i>')
            .append(' ' + 'projectRole.{0}'.format(role).loc())
            .html();
    });

    function addACL(config, id, fn) {
        ajax.put({
            url: 'ui/admin/acl/project/{0}/{1}'.format(id, fn),
            successFn: function () {
                dynamic.reloadSection('acl-project-list')
            }
        })
    }

    // Gets the list of project functions
    ajax.get({
        url: 'ui/admin/acl/project/role',
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