var Properties = function () {

    function propertiesTemplate (entity, entityId) {
        return Template.config({
            url: 'gui/property/{0}/{1}'.format(entity, entityId),
            type: 'html'
        });
    }

    function addProperties () {
        hideEditionBox('property-add');
        $('#property-add-select').val('');
        $('#property-add-section').show();
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
        // Loading
        $('#' + config.id + '-loading').show();
        // Loading the edition box
		$.ajax({
			type: 'GET',
			url: 'ui/property/{0}/{1}/edit/{2}/{3}'.format(config.entity, config.entityId, config.extension, config.name),
			dataType: 'html',
			success: function (html) {
                // Loading...
                $('#' + config.id + '-loading').hide();
                // Display
                $('#' + config.id + '-field').html(html);
                // Adjusting the label
                // Showing the edition box
                $('#' + config.id + '-field').show();
			},
			error: function (jqXHR, textStatus, errorThrown) {
				Application.onAjaxError(jqXHR, textStatus, errorThrown, function (message) {
                    // Error
                    $('#' + config.id + '-error-message').text(message);
                    $('#' + config.id + '-error').show();
                    // Loading...
                    $('#' + config.id + '-loading').hide();
				});
			}
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
        addProperties: addProperties,
        cancelAddProperties: cancelAddProperties,
        onPropertySelected: onPropertySelected
    };

} ();