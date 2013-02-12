var Settings = function () {

    function ldapEnabled () {
        var ldapEnabled = $('#ldap-enabled').is(':checked');
        if (ldapEnabled) {
            $('#settings-ldap').show();
            $('.settings-ldap').attr('required', 'required');
        } else {
            $('#settings-ldap').hide();
            $('.settings-ldap').removeAttr('required');
        }
    }

    function ldapSetup () {
        ldapEnabled();
        $('#ldap-enabled').click(ldapEnabled);
    }

    return {
        init: function () {
            ldapSetup();
        }
    };

} ();

$(document).ready(Settings.init);
