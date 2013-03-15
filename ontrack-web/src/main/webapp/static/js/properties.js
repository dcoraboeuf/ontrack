var Properties = function () {

    function propertiesTemplate (entity, entityId) {
        return Template.config({
            url: 'gui/property/{0}/{1}'.format(entity, entityId),
            type: 'html'
        });
    }

    return {
        propertiesTemplate: propertiesTemplate
    };

} ();