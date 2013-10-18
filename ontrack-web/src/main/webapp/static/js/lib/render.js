define(['common', 'handlebars'], function (common, handlebars) {

    Handlebars.registerHelper('loc', function (key, options) {
        return key.loc();
    });

    function withTemplate(templateId, templateFn) {
        require(['text!template/' + templateId + '.html'], function (rawTemplate) {
            templateFn(Handlebars.compile(rawTemplate));
        });
    }

    function render(templateId, model, templateFn) {
        withTemplate(templateId, function (compiledTemplate) {
            templateFn(compiledTemplate(model));
        });
    }

    function renderInto(target, templateId, model, callbackFn) {
        render(templateId, model, function (template) {
            $(target).html(template);
            if (callbackFn) {
                callbackFn();
            }
        });
    }

    function defaultRender(target, append, config, data) {
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

    /**
     * Uses a {{handleBars}} template for rendering.
     * If <code>dataFn</code> is defined and is:
     * <ul>
     *     <li>a String - the data for the template is {$dataFn: items}</li>
     *     <li>a Function - the data for the template is dataFn(items)
     * </ul>
     * In any other case, data = items
     */
    function asSimpleTemplate(templateId, dataFn, callbackFn) {
        return function (target, append, config, items) {
            var data;
            if (dataFn) {
                if ($.isFunction(dataFn)) {
                    data = dataFn(items, config);
                } else {
                    data = {};
                    data[dataFn] = items;
                }
            } else {
                data = items;
            }
            renderInto(target, templateId, data, function () {
                if (callbackFn) {
                    callbackFn(config, data, target);
                }
            });
        }
    }

    function generateTableRows(items, rowFn) {
        var html = '';
        $.each(items, function (index, item) {
            html += rowFn(item);
        });
        return html;
    }

    function generateTable(items, rowFn) {
        var html = '<table class="table table-hover"><tbody>';
        html += generateTableRows(items, rowFn);
        html += '</tbody></table>';
        return html;
    }

    function tableInto(target, append, config, items, itemFn) {
        if (append === true && $(target).has('tbody').length) {
            $(target).find('tbody').append(generateTableRows(items, itemFn));
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

    function asTable(itemFn) {
        return function (target, append, config, items) {
            tableInto(target, append, config, items, itemFn);
        };
    }

    function asTableTemplate(rowTemplateId, callbackFn) {
        return function (target, append, config, items) {
            withTemplate(rowTemplateId, function (compiledTemplate) {
                tableInto(target, append, config, items, function (item) {
                    return compiledTemplate(item);
                });
                if (callbackFn) {
                    callbackFn(config, items, target);
                }
            });
        }
    }

    function sameDataFn (data, config) {
        return data;
    }

    return {
        // Template mgt
        withTemplate: withTemplate,
        // Low level rendering using templates
        render: render,
        renderInto: renderInto,
        // Defaults
        defaultRender: defaultRender,
        sameDataFn: sameDataFn,
        // Basic templating
        asSimpleTemplate: asSimpleTemplate,
        // Table rendering
        asTableTemplate: asTableTemplate
    }

});