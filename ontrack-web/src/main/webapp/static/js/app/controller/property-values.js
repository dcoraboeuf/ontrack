define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/property/{0}/{1}'.format(config.entity, config.entityid);
        },
        render: render.asSimpleTemplate('property-values', 'properties')
    }
});