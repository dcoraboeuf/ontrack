define(['require','common','jquery','render','ajax'], function (require, common, $, render, AJAX) {

    $('.dynamic').each(function (index, section) {
        // Gets the data from the section
        var id = $(section).attr('id');
        var controllerId = $(section).attr('dynamic-controller');
        log('[{0}] Starting dynamic section'.format(id));
        // Checks the controller
        if (!controllerId || controllerId == '') {
            dynamicError({id: id}, 'Controller is not defined');
        } else {
            log('[{0}] Using controller "{1}"'.format(id, controllerId));
            // Loads the controller
            require(['app/controller/' + controllerId], function (controller) {
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
        }
    });

    function log (message, args) {
        common.log('dynamic')(message, args);
    }

    function dynamicError(config, message) {
        var displayMessage = '[id={0}] {1}'.format(config.id, message);
        $('#' + config.id).empty();
        $('#' + config.id)
            .addClass('alert')
            .addClass('alert-error')
            .text('[dynamic]' + displayMessage)
            .show();
        log(displayMessage);
    }

    function init (config) {
        log('[{0}] Initializing section with: '.format(config.id), config);
        // Sections
        log('[{0}] Initializing section content into: '.format(config.id), config.section);
        render.renderInto(config.section, 'dynamic-section', config, function () {
            // Loading
            load(config, false);
            // More item
            $('#' + config.id + '-more').click(function () {
                load(config, true);
            });
            // Reloading?
            if (config.refresh) {
                setInterval(function () {
                    reload(config);
                }, config.refreshInterval);
            }
        });
    }

    function getUrl(config) {
        var url = getConfig(config, 'url');
        if (url) {
            if ($.isFunction(url)) {
                return url(config);
            } else {
                return url;
            }
        } else {
            dynamicError(config, 'Cannot get the URL');
        }
    }

    function getConfig(config, name) {
        var value;
        if (config.controller) {
            value = config.controller[name];
        }
        if (value) {
            return value;
        } else {
            return config[name];
        }
    }

    function display (config, append, data) {
        log('[{0}] Displaying data: '.format(config.id), data);
        var container = $('#' + config.id + '-content');
        var render = getConfig(config, 'render');
        if (render) {
            log('[{0}] Rendering'.format(config.id));
            var preProcessingFn = getConfig(config, 'preProcessingFn');
            // Preprocessing?
            if (preProcessingFn) {
                data = preProcessingFn(config, data, append);
                log('[{0}] Preprocessed data: '.format(config.id), data);
            }
            // Rendering
            render(container, append, config, data);
            // Post rendering
            var postRenderFn = getConfig(config, 'postRenderFn');
            if (postRenderFn) {
                log('[{0}] Post rendering'.format(config.id));
                postRenderFn(config);
            }
        } else {
            dynamicError('"{0}" section does not define any "render" function.'.format(config.id));
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
        log('[{0}] Loading section'.format(config.id));
        // Gets the loading information
        var url = getUrl(config);
        if (url) {
            log('[{0}] Loading section with URL [{1}]'.format(config.id, url));
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
        }
    }

});