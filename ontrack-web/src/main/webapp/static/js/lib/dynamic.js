define(['common','jquery','require','render','text!template/dynamic-section.html'], function (common, $, require, render) {

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
            // TODO render: defaultRender,
            dataLength: function (data) {
                return data.length;
            }
            // TODO placeholder: loc('general.empty')
        }, config);
        // Logging
        if (console) {
            console.log(config);
        }
        // Initialisation
        init(config);
    });

    function init (config) {
        // Associates the template definition with the ID
        $(config.section).data('dynamic-config', config);
        // Sections
        render.renderInto(config.section, 'dynamic-section', config);
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
            $('#' + id + '-error').hide();
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
                            display(id, append, config, data);
                            // Management of the 'more'
                            moreStatus(id, config, data);
                        } catch (message) {
                            error(id, message);
                        }
                    },
                    errorFn: AJAX.simpleAjaxErrorFn(function (message) {
                        error(id, message);
                    })
                });
            } else {
                AJAX.get({
                    url: url,
                    loading: {
                        mode: config.showLoading ? 'container' : 'none',
                        el: '#' + id + '-loading'
                    },
                    successFn: function (data) {
                        // Uses the data
                        try {
                            display(id, append, config, data);
                            // Management of the 'more'
                            moreStatus(id, config, data);
                        } catch (message) {
                            error(id, message);
                        }
                    },
                    errorFn: AJAX.simpleAjaxErrorFn(function (message) {
                        error(id, message);
                    })
                });
            }
        } else {
            throw '[dynamic] No "url" is defined for dynamic section "{0}"'.format(config.id);
        }
    }

});