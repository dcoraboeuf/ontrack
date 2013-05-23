define(['require','handlebars'], function (require, handlebars) {


    function render (templateId, model) {
        var template = require('text!template/' + templateId + '.html');
        return Handlebars.compile(template)(model);
    }

    function renderInto (target, templateId, model) {
        $(target).html(render(templateId, model));
    }

    function defaultRender (container, append, config, data) {
        tableInto(container, false, config, data, function (item) {
            var value;
            if (item.name) {
                value = item.name.html();
            } else {
                value = String(item).html();
            }
            return '<tr><td>{0}</td></tr>'.format(value);
        });
    }

    function generateTableRows (items, rowFn) {
        var html = '';
        $.each (items, function (index, item) {
            html += rowFn(item);
        });
        return html;
    }

    function generateTable (items, rowFn) {
        var html = '<table class="table table-hover"><tbody>';
        html += generateTableRows(items, rowFn);
        html += '</tbody></table>';
        return html;
    }

    function tableInto (target, append, config, items, itemFn) {
        if (append === true && $(target).has("tbody").length) {
            $(target + " tbody").append(generateTableRows(items, itemFn));
        } else {
            // No table defined, or no need to append
            // Some items
            if (items.length && items.length > 0) {
                // Direct filling of the container
                $(target).empty();
                $(target).append(generateTable(items, itemFn));
            }
            // No items
            else {
                $(target).empty();
                $(target).append('<div class="alert">{0}</div>'.format(config.placeholder));
            }
        }
    }

    return {
        // Low level rendering using templates
        render: render,
        renderInto: renderInto,
        // Defaults
        defaultRender: defaultRender,
        // Table rendering
        tableInto: tableInto
    }

});