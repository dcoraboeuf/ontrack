define(['common','jquery','require','render','text!template/dynamic-section.html','ajax'], function (common, $, require, render, sectionTemplate, AJAX) {

    $('.dynamic').each(function (index, section) {
        // Gets the data from the section
        var id = $(section).attr('id');
        var controllerId = $(section).attr('dynamic-controller');
        // Checks the controller
        if (!controllerId || controllerId == '') {
            throw '[dynamic] Controller is not defined for section "{0}".'.format(id);
        }
        // Loads the controller
        var controller = require('app/controller/' + controllerId);
        // TODO Checks the controller
        // if (typeof controller.load === 'undefined') {
        //    throw '[dynamic] Controller {1} defined for section "{0}" does not have any "load" method.'.format(controllerId, id);
        // }
        // Creates the configuration
        var config = {
            id: id,
            section: section,
            controller: controller
        };
        $(section.attributes).each(function (aindex, a) {
            if (a.nodeName.match(/^dynamic-/)) {
                var name = a.nodeName.substring(8);
                if (name != 'controller') {
                    config[name] = a.nodeValue;
                }
            }
        });
        // Default configuration
        config = $.extend({}, {
            refresh: false,
            refreshInterval: 30000, // 30 seconds
            offset: 0,
            count: 10,
            more: false,
            showLoading: true,
            render: render.defaultRender,
            dataLength: function (data) {
                return data.length;
            },
            placeholder: 'general.empty'.loc()
        }, config);
        // Initialisation
        init(config);
    });

    function init (config) {
        // Associates the template definition with the ID
        $(config.section).data('dynamic-config', config);
        // Sections
        render.renderInto(config.section, sectionTemplate, config);
        // Loading
        load(config, false);
        // Reloading?
        if (config.refresh) {
            setInterval(function () {
                reload(config);
            }, config.refreshInterval);
        }
    }

    function getUrl(config) {
        if (config.url) {
            return config.url;
        } else if (config.controller.getUrl) {
            return config.controller.getUrl(config);
        } else {
            throw '[dynamic] Cannot get the URL for section "{0}"'.format(config.id);
        }
    }

    function getConfig(config, name) {
        return config.controller[name] || config[name];
    }

    function display (config, append, data) {
        var container = $('#' + config.id + '-content');
        var render = getConfig(config, 'render');
        if (render) {
            var preProcessingFn = getConfig(config, 'preProcessingFn');
            // Preprocessing?
            if (preProcessingFn) {
                data = preProcessingFn(config, data, append);
            }
            // Rendering
            render(container, append, config, data);
            // Post rendering
            var postRenderFn = getConfig(config, 'postRenderFn');
            if (postRenderFn) {
                postRenderFn(config);
            }
        } else {
            throw '[dynamic] "{0}" section does not define any "render" function.'.format(config.id);
        }
    }

    function moreStatus(config, data) {
        if (config.more) {
            var dataCount = config.dataLength(data);
            config.offset += dataCount;
            var hasMore = (dataCount >= config.count);
            if ($.isFunction(config.more)) {
                config.more(config, data, hasMore);
            } else {
                if (hasMore) {
                    $('#'+ config.id + '-more-section').show();
                } else {
                    $('#'+ config.id + '-more-section').hide();
                }
            }
        }
    }

    function load (config, append) {
        // Gets the loading information
        var url = getUrl(config);
        if (url) {
            // Offset and count
            if (config.more) {
                url += '&offset=' + config.offset;
                url += '&count=' + config.count;
            }
            // Starts loading
            $('#' + config.id + '-error').hide();
            // Call
            if (config.data) {
                AJAX.post ({
                    url: url,
                    data: config.data,
                    loading: {
                        mode: 'container',
                        el: '#' + id + '-loading'
                    },
                    successFn: function (data) {
                        // Uses the data
                        try {
                            display(config, append, data);
                            // Management of the 'more'
                            moreStatus(config, data);
                        } catch (message) {
                            AJAX.elementErrorMessageFn('#' + config.id + '-error')(message);
                        }
                    },
                    errorFn: AJAX.simpleAjaxErrorFn(AJAX.elementErrorMessageFn('#' + config.id + '-error'))
                });
            } else {
                AJAX.get({
                    url: url,
                    loading: {
                        mode: config.showLoading ? 'container' : 'none',
                        el: '#' + config.id + '-loading'
                    },
                    successFn: function (data) {
                        // Uses the data
                        try {
                            display(config, append, data);
                            // Management of the 'more'
                            moreStatus(config, data);
                        } catch (message) {
                            AJAX.elementErrorMessageFn('#' + config.id + '-error')(message);
                        }
                    },
                    errorFn: AJAX.simpleAjaxErrorFn(AJAX.elementErrorMessageFn('#' + config.id + '-error'))
                });
            }
        } else {
            throw '[dynamic] No "url" is defined for dynamic section "{0}"'.format(config.id);
        }
    }

});