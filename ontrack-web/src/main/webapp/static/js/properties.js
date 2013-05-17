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

    function addProperty () {
        var property = $('#property-add-select').val();
        var hash = property.indexOf('#');
        var extension = property.substring(0, hash);
        var name = property.substring(hash + 1);
        var value = $('#extension-{0}-{1}'.format(extension, name)).val();
        var entity = $('#entity').val();
        var entityId = $('#entityId').val();
        // No error
        $('#property-add-error').hide();
        // Loading the edition box
		AJAX.post({
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
                Template.reload('property-values');
			},
			errorFn: AJAX.simpleAjaxErrorFn(function (message) {
                $('#property-add-error-message').text(message);
                $('#property-add-error').show();
			})
		});
        // Does not submit the normal way
        return false;
    }

    function cancelAddProperties () {
        hideEditionBox('property-add');
        $('#property-add-section').hide();
    }

    function hideEditionBox (id) {
        $('#' + id + '-loading').hide();
        $('#' + id + '-error').hide();
        $('#' + id + '-field').hide();
    }

    function showEditionBox (config) {
        // No error
        $('#' + config.id + '-error').hide();
        // Loading the edition box
		AJAX.get({
			url: 'ui/property/{0}/{1}/edit/{2}/{3}'.format(config.entity, config.entityId, config.extension, config.name),
			responseType: 'html',
			loading: {
			    mode: 'toggle',
			    el: '#' + config.id + '-loading'
			},
			successFn: function (html) {
                // Display
                $('#' + config.id + '-field').html(html);
                // Adjusting the label
                // Showing the edition box
                $('#' + config.id + '-field').show();
			},
			errorFn: AJAX.simpleAjaxErrorFn(function (message) {
                // Error
                $('#' + config.id + '-error-message').text(message);
                $('#' + config.id + '-error').show();
            })
		});
    }

    function onPropertySelected (dropbox) {
        var value = $(dropbox).val();
        if (value != "") {
            // Gets the extension and the name
            var hash = value.indexOf('#');
            var extension = value.substring(0, hash);
            var name = value.substring(hash + 1);
            // Entity
            var entity = $('#entity').val();
            var entityId = $('#entityId').val();
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