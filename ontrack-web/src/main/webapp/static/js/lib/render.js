define(['require','handlebars'], function (require, handlebars) {

    function render (templateId, model) {
        var template = require('text!template/' + templateId + '.html');
        return Handlebars.compile(template)(model);
    }

    function renderInto (target, templateId, model) {
        $(target).html(render(templateId, model));
    }

    return {
        render: render,
        renderInto: renderInto
    }

});