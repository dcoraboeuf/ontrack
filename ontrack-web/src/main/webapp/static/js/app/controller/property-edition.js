define(['render'], function (render) {

    function hideEditionBox(id) {
        $('#' + id + '-loading').hide();
        $('#' + id + '-error').hide();
        $('#' + id + '-field').hide();
    }

    function propertyEdition(config) {
        hideEditionBox('property-add');
        $('#property-add-select').val('');
        $('#property-add-section').show();
    }

    return {
        url: function (config) {
            return 'ui/property/{0}/{1}/editable'.format(config.entity, config.entityid);
        },
        render: render.asSimpleTemplate('property-edition', 'properties', function (config) {
            $('#property-edition-button').click(function () {
                propertyEdition(config);
            });
        })
    }
});