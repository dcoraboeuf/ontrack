define(['jquery', 'ajax', 'app/component/account-role-selection'], function ($, ajax, accountRoleSelection) {

    function addACL(config, id, fn) {
        ajax.put({
            url: 'ui/admin/acl/global/{0}/{1}'.format(id, fn),
            successFn: function () {
                // TODO Refreshes the list of ACLs
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