define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/property/{0}/{1}/editable'.format(config.entity, config.entityid);
        },
        render: render.asSimpleTemplate('property-edition', 'properties')
    }
});