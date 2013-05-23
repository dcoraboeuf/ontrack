define(['common','handlebars'], function (common, handlebars) {


    function render (template, model) {
        return Handlebars.compile(template)(model);
    }

    function renderInto (target, template, model) {
        $(target).html(render(template, model));
    }

    function defaultRender (target, append, config, data) {
        tableInto(target, false, config, data, function (item) {
            var value;
            if (item.name) {
                value = item.name.html();
            } else {
                value = String(item).html();
            }
            return '<tr><td>{0}</td></tr>'.format(value);
        });
    }

    function fill (contentFn) {
        return function (target, append, config, items) {
            var html = contentFn(items, append);
            $(target).empty();
            $(target).append(html);
        }
    }

    /**
     * Uses a {{handleBars}} template for rendering.
     * If <code>dataFn</code> is defined and is:
     * <ul>
     *     <li>a String - the data for the template is {$dataFn: items}</li>
     *     <li>a Function - the data for the template is dataFn(items)
     * </ul>
     * In any other case, data = items
     */
    function asSimpleTemplate (template, dataFn) {
        return fill (function (items, append) {
            var data;
            if (dataFn) {
                if ($.isFunction(dataFn)) {
                    data = dataFn(items);
                } else {
                    data = {};
                    data[dataFn] = items;
                }
            } else {
                data = items;
            }
            return render (template, data);
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
                common.log('render')('Rendering generated table {0} into '.format(config.id), target);
                // Direct filling of the container
                $(target).empty();
                $(target).append(generateTable(items, itemFn));
            }
            // No items
            else {
                common.log('render')('Rendering empty table {0} into '.format(config.id), target);
                $(target).empty();
                $(target).append('<div class="alert">{0}</div>'.format(config.placeholder));
            }
        }
    }

    function asTable (itemFn) {
        return function (target, append, config, items) {
            tableInto(target, append, config, items, itemFn);
        };
    }

    function asTableTemplate (rowTemplate) {
        return asTable (function (item) {
            return render (rowTemplate, item);
        });
    }

    return {
        // Low level rendering using templates
        render: render,
        renderInto: renderInto,
        // Defaults
        defaultRender: defaultRender,
        // Basic templating
        asSimpleTemplate: asSimpleTemplate,
        // Table rendering
        tableInto: tableInto,
        asTableTemplate: asTableTemplate
    }

});