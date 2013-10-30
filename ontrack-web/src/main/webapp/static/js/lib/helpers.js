define(['common', 'handlebars'], function () {

    Handlebars.registerHelper('loc', function (key, options) {
        return key.loc();
    });

    Handlebars.registerHelper('securityRole', function (role) {
        return 'account.role.{0}'.format(role).loc();
    });

});