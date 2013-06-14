define(['render','./property-edition', 'jquery'], function (render, propertyEdition, $) {

    return {
        url: function (config) {
            return 'ui/property/{0}/{1}'.format(config.entity, config.entityId);
        },
        render: render.asSimpleTemplate('property-values', 'properties', function (config) {
            $('.property-editor-button').each(function (index, button) {
                $(button).click(function () {
                    propertyEdition.editProperty(
                        $(button).attr('edition-extension'),
                        $(button).attr('edition-property')
                    );
                });
            });
        })
    }
});