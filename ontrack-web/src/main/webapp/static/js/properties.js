var Properties = function () {

    function editablePropertiesTemplate (entity, entityId) {
        return Template.config({
            url: 'ui/property/{0}/{1}/editable'.format(entity, entityId),
            render: Template.asSimpleTemplate('property-edition-template', 'properties')
        });
    }

    function propertiesTemplate (entity, entityId) {
        return Template.config({
            url: 'ui/property/{0}/{1}'.format(entity, entityId),
            render: Template.asSimpleTemplate('property-values-template', 'properties')
        });
    }

    function addProperties () {
        hideEditionBox('property-add');
        $('#property-add-select').val('');
        $('#property-add-section').show();
    }

    function editProperty (extension, name) {
        var entity = $('#entity').val();
        var entityId = $('#entityId').val();
        addProperties();
        $('#property-add-select').val('{0}#{1}'.format(extension, name));
        onPropertySelected($('#property-add-select'));
    }

    function hideEditionBox (id) {
        $('#' + id + '-loading').hide();
        $('#' + id + '-error').hide();
        $('#' + id + '-field').hide();
    }

    return {
        propertiesTemplate: propertiesTemplate,
        editablePropertiesTemplate: editablePropertiesTemplate,
        addProperties: addProperties,
        cancelAddProperties: cancelAddProperties,
        addProperty: addProperty,
        editProperty: editProperty,
        onPropertySelected: onPropertySelected
    };

} ();