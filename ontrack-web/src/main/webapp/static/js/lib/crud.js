/**
 * Defines a standard behaviour for a CRUD table.
 */
define(['jquery', 'render', 'dialog', 'dynamic', 'ajax'], function ($, render, dialog, dynamic, ajax) {

    /**
     * This service
     */
    var self = {};

    function setupGlobalCommands(dynamicConfig, container, cfg) {
        var rowId = 'crud-{0}-commands'.format(dynamicConfig.id);
        if ($('#' + rowId).length == 0) {
            var row = $('<div></div>').insertAfter(container).attr('id', rowId);
            // Commands?
            if (cfg.commands) {
                $.each(cfg.commands, function (i, command) {
                    var btn = $('<button/>').appendTo(row).addClass('btn');
                    // Additional classes
                    if (command.classes) {
                        $.each(command.classes, function (j, cls) {
                            btn.addClass(cls)
                        })
                    }
                    // Icon
                    if (command.iconCls) {
                        $('<i></i>').addClass(command.iconCls).appendTo(btn)
                    }
                    // Text
                    if (command.title) {
                        btn.append(' ' + command.title)
                    }
                    // Action
                    if (command.action) {
                        btn.click(function () {
                            command.action(btn, cfg, dynamicConfig)
                        })
                    }
                })
            }
        }
    }

    function setupItemCommand(dynamicConfig, cfg, cell, itemCommand, itemId) {
        var btn = $('<i></i>')
            .addClass(itemCommand.iconCls)
            .appendTo(cell);
        btn.click(function () {
            itemCommand.action(btn, dynamicConfig, cfg, itemId)
        });
    }

    function setupItemCommands(dynamicConfig, container, cfg) {
        if (cfg.itemCommands) {
            $(container).find('.crud-item-commands').each(function (i, commandContainer) {
                var itemId = $(commandContainer).attr('crud-id');
                var cell = $('<span></span>').appendTo($(commandContainer));
                cell.addClass('action').addClass('action-optional');
                $.each(cfg.itemCommands, function (j, itemCommand) {
                    setupItemCommand(dynamicConfig, cfg, cell, itemCommand, itemId)
                })
            });
        }
    }

    /**
     * Internal function used to set-up the table after the rendering of items
     */
    function setupTableAfterRender(dynamicConfig, items, container, cfg) {
        setupGlobalCommands(dynamicConfig, container, cfg);
        setupItemCommands(dynamicConfig, container, cfg);
    }

    /**
     * Create a CRUD controller.
     *
     * The `config` object has the following parameters:
     * - url: required, base URL used to access the objects:
     *      GET '' - list of items
     *      GET '/{id}' - get one item by id
     *      POST '' - creates an item
     *      PUT '/{id}' - updates an item
     *      DELETE '/{id}' - deletes an item
     * - itemId: optional, defaults to 'id'. Field that contains the ID in an item.
     * - itemName: required, name of the type of item
     * - itemTemplateId: required, template ID (see `render.js`) used to render the row for an item in the table
     * - itemDialogTemplateId: required, template ID used to render the
     * - itemDialogFieldPrefix, optional, defaults to ''. Prefix to add before the property name to get the field ID in the dialog
     * - itemNewFn, optional, defaults to a function that returns an empty object. Function that is called to
     *              get a new item to create
     * - itemDialogWidth, optional, defaults to 600, width of the dialog
     * - itemDialogInitFn, optional, defaults to doing nothing. Initializes the dialog before display.
     * - itemDialogReadFn, optional, defaults to doing nothing. Reads the dialog data before validation.
     * - itemDialogValidateFn, optional, default to doing nothing. Validation of the dialog before reading the dialog data.
     *
     * The client will call the method by doing:
     *
     * <pre>
     *     return crud.create({...})
     * </pre>
     */
    self.create = function (config) {

        /**
         * Defaults
         */
        var cfg = $.extend({}, {
            itemDialogFieldPrefix: '',
            itemNewFn: function () {
                return {}
            },
            itemDialogWidth: 600,
            itemDialogInitFn: $.noop,
            itemDialogReadFn: $.noop,
            itemDialogValidateFn: $.noop
        }, config);

        /**
         * Control of requisites
         */
        if (!cfg.url) throw '[crud] "url" parameter is required.';
        if (!cfg.itemTemplateId) throw '[crud] "itemTemplateId" parameter is required.';
        if (!cfg.itemName) throw '[crud] "itemName" parameter is required.';
        if (!cfg.itemDialogTemplateId) throw '[crud] "itemDialogTemplateId" parameter is required.';

        /**
         * Returns a standard `dynamic` configuration:
         * - the URL for the source
         * - the rendering & post-rendering methods
         */
        return {
            url: cfg.url,
            render: render.asTableTemplate(cfg.itemTemplateId, function (dynamicConfig, items, container) {
                setupTableAfterRender(dynamicConfig, items, container, cfg)
            })
        }

    };

    /**
     * Creates an 'update' command to use for the items
     */
    self.updateItemCommand = function () {
        return {
            iconCls: 'icon-pencil',
            action: function (btn, dynamicConfig, cfg, itemId) {
                self.updateItem(btn, cfg, dynamicConfig, itemId)
            }
        }
    };

    /**
     * Creates a 'create' command to use in the `commands` field of the configuration.
     */
    self.createCommand = function (title) {
        return {
            title: title,
            iconCls: 'icon-plus',
            action: function (btn, cfg, dynamicConfig) {
                self.createItem(btn, cfg, dynamicConfig)
            }
        }
    };

    /**
     * Update command
     */
    self.updateItem = function (btn, cfg, dynamicConfig, itemId) {
        ajax.get({
            url: '{0}/{1}'.format(cfg.url, itemId),
            loading: {
                el: $(btn)
            },
            successFn: function (item) {
                dialogItem(btn, cfg, {
                    data: item,
                    action: function (dialogBtn, dialog, form) {
                        ajax.put({
                            url: '{0}/{1}'.format(cfg.url, itemId),
                            data: form,
                            loading: {
                                el: $(dialogBtn)
                            },
                            successFn: function () {
                                dialog.closeFn();
                                dynamic.reloadSection(dynamicConfig.id);
                            },
                            errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                        })
                    }
                })
            }
        })
    }

    /**
     * Create command.
     */
    self.createItem = function (btn, cfg, dynamicConfig) {
        dialogItem(btn, cfg, {
            data: cfg.itemNewFn(),
            action: function (dialogBtn, dialog, form) {
                ajax.post({
                    url: cfg.url,
                    data: form,
                    loading: {
                        el: $(dialogBtn)
                    },
                    successFn: function () {
                        dialog.closeFn();
                        dynamic.reloadSection(dynamicConfig.id);
                    },
                    errorFn: ajax.simpleAjaxErrorFn(dialog.errorFn)
                })
            }
        })
    };

    /**
     * Generic create/update dialog
     */
    function dialogItem(btn, cfg, dialogConfig) {
        // Shows the dialog
        dialog.show({
            title: cfg.itemName,
            width: cfg.itemDialogWidth,
            templateId: cfg.itemDialogTemplateId,
            initFn: function (dialog) {
                // Gets the data
                var item = dialogConfig.data;
                // Standard fields
                if (cfg.itemFields) {
                    $.each(cfg.itemFields, function (f, itemField) {
                        if ($.isPlainObject(itemField)) {
                            var fieldName = itemField.field;
                            var itemProperty = itemField.property;
                            dialog.form.find('#' + fieldName).val(item[itemProperty]);
                        } else if ($.isFunction(itemField)) {
                            itemField(cfg, dialog, item, 'set');
                        } else {
                            dialog.form.find('#' + cfg.itemDialogFieldPrefix + itemField).val(item[itemField]);
                        }
                    })
                }
                // Custom init
                cfg.itemDialogInitFn(cfg, dialog, item);
            },
            submitFn: function (dialog) {
                // Collects the fields
                var form = {};
                // Extra validation method?
                var message = cfg.itemDialogValidateFn(cfg, dialog, form);
                if (message) {
                    dialog.errorFn(message)
                }
                // Standard fields
                if (cfg.itemFields) {
                    $.each(cfg.itemFields, function (f, itemField) {
                        if ($.isPlainObject(itemField)) {
                            var fieldName = itemField.field;
                            var itemProperty = itemField.property;
                            form[itemProperty] = dialog.form.find('#' + fieldName).val();
                        } else if ($.isFunction(itemField)) {
                            itemField(cfg, dialog, form, 'get');
                        } else {
                            form[itemField] = dialog.form.find('#' + cfg.itemDialogFieldPrefix + itemField).val();
                        }
                    })
                }
                // Custom read
                cfg.itemDialogReadFn(cfg, dialog, form)
                // Taking action now!
                dialogConfig.action(
                    // Clicked button
                    dialog.controls['submit'],
                    // The current opened dialog
                    dialog,
                    // The form that has been read
                    form
                );
            }
        })
    }

    /**
     * Returns the CRUD service.
     */
    return self;

});