define(['jquery', 'ajax', 'app/component/account-role-selection'], function ($, ajax, accountRoleSelection) {

    // Gets the list of global functions
    ajax.get({
        url: 'ui/admin/acl/global',
        successFn: function (fns) {
            // Account selection & global functions
            accountRoleSelection.init(
                $('#acl-account-form'),
                {
                    roles: fns
                }
            )
        }
    })


});