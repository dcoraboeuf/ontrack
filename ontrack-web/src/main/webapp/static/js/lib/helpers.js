define(['application', 'common', 'handlebars'], function (application) {

    Handlebars.registerHelper('loc', function (key, options) {
        return key.loc();
    });

    Handlebars.registerHelper('securityRole', function (role) {
        return 'account.role.{0}'.format(role).loc();
    });

    Handlebars.registerHelper('projectRole', function (role) {
        return $('<i></i>')
            .append(
                $('<img/>').attr('src', application.staticPathTo('images/projectRole-{0}.png'.format(role)))
            )
            .append(' ' + 'projectRole.{0}'.format(role).loc())
            .html();
    });

});