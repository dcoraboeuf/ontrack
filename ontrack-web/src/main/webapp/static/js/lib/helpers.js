define(['common', 'handlebars'], function (common) {

    Handlebars.registerHelper('loc', function (key, options) {
        return key.loc();
    });

    Handlebars.registerHelper('securityRole', function (role) {
        return 'account.role.{0}'.format(role).loc();
    });

    Handlebars.registerHelper('static', function (path) {
        return common.staticPathTo(path);
    });

    Handlebars.registerHelper('projectRole', function (role) {
        return $('<i></i>')
            .append(
                $('<img/>').attr('src', common.staticPathTo('images/projectRole-{0}.png'.format(role)))
            )
            .append(' ' + 'projectRole.{0}'.format(role).loc())
            .html();
    });

});