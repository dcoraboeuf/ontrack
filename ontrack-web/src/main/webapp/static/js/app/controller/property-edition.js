define(['render', 'ajax', 'dynamic'], function (render, ajax, dynamic) {

    function hideEditionBox(id) {
        $('#' + id + '-loading').hide();
        $('#' + id + '-error').hide();
        $('#' + id + '-field').hide();
    }

    function showEditionBox(config) {
        // No error
        $('#' + config.id + '-error').hide();
        // Loading the edition box
        ajax.get({
            url: 'ui/property/{0}/{1}/edit/{2}/{3}'.format(config.entity, config.entityId, config.extension, config.name),
            loading: {
                mode: 'toggle',
                el: '#' + config.id + '-loading'
            },
            successFn: function (editableProperty) {
                // Display
                $('#' + config.id + '-field').html(editableProperty.htmlForEdit);
                // Adjusting the description
                $('#' + config.id + '-description').text(editableProperty.displayDescription);
                // Showing the edition box
                $('#' + config.id + '-field').show();
            },
            errorFn: ajax.simpleAjaxErrorFn(function (message) {
                // Error
                $('#' + config.id + '-error-message').text(message);
                $('#' + config.id + '-error').show();
            })
        });
    }

    function propertyEdition(config) {
        hideEditionBox('property-add');
        $('#property-add-select').val('');
        $('#property-add-section').show();
    }

    function onPropertySelected(config, dropbox) {
        var value = $(dropbox).val();
        if (value != "") {
            // Gets the extension and the name
            var hash = value.indexOf('#');
            var extension = value.substring(0, hash);
            var name = value.substring(hash + 1);
            // Entity
            var entity = config.entity;
            var entityId = config.entityId;
            // Prepares the edition box
            showEditionBox({
                id: 'property-add',
                extension: extension,
                name: name,
                entity: entity,
                entityId: entityId
            });
        } else {
            // Hides edition box
            hideEditionBox('property-add');
        }
    }

    function cancelAddProperties() {
        hideEditionBox('property-add');
        $('#property-add-section').hide();
    }

    function addProperty(config) {
        var property = $('#property-add-select').val();
        var hash = property.indexOf('#');
        var extension = property.substring(0, hash);
        var name = property.substring(hash + 1);
        var value = $('#extension-{0}-{1}'.format(extension, name)).val();
        var entity = config.entity;
        var entityId = config.entityId;
        // No error
        $('#property-add-error').hide();
        // Loading the edition box
        ajax.post({
            url: 'ui/property/{0}/{1}/edit/{2}/{3}'.format(entity, entityId, extension, name),
            data: {
                value: value
            },
            loading: {
                mode: 'toggle',
                el: '#property-add-loading'
            },
            successFn: function () {
                // OK - reloads the property container
                cancelAddProperties();
                // Reloads the property values
                dynamic.reloadSection('property-values');
            },
            errorFn: ajax.simpleAjaxErrorFn(function (message) {
                $('#property-add-error-message').text(message);
                $('#property-add-error').show();
            })
        });
        // Does not submit the normal way
        return false;
    }

    function editProperty (extension, name) {
        var config = dynamic.getSectionConfig('property-edition');
        if (config) {
            propertyEdition(config);
            $('#property-add-select').val('{0}#{1}'.format(extension, name));
            onPropertySelected(config, $('#property-add-select'));
        }
    }

    return {
        url: function (config) {
            return 'ui/property/{0}/{1}/editable'.format(config.entity, config.entityId);
        },
        render: render.asSimpleTemplate('property-edition', 'properties', function (config) {
            // Adding a property
            $('#property-edition-button').click(function () {
                propertyEdition(config);
            });
            // Selecting a property
            $('#property-add-select').change(function () {
                onPropertySelected(config, this);
            });
            // Submitting the form
            $(config.section).find('form').submit(function () {
                return addProperty(config);
            });
            // Cancelling the form
            $('#property-add-field-cancel').click(cancelAddProperties);
        }),
        editProperty: editProperty
    }
});