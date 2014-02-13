/**
 * Defines a standard behaviour for a CRUD table.
 */
define(['jquery'], function ($) {

    /**
     * This service
     */
    var self = {};

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
     *
     * The client will call the method by doing:
     *
     * <pre>
     *     return crud.create({...})
     * </pre>
     */
    self.create = function (config) {

        /**
         * TODO Defaults
         */
        var cfg = $.extend({}, {

        }, config);

        /**
         * Control of requisites
         */
        if (!cfg.url) throw '"url" parameter is required.';

        /**
         * Returns a standard `dynamic` configuration:
         * - the URL for the source
         * - the rendering & post-rendering methods
         */
        return {
            url: cfg.url
        }

    };

    /**
     * Returns the CRUD service.
     */
    return self;

});