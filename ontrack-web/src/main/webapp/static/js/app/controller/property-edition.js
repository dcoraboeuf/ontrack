define(['render','ajax'], function (render, ajax) {

    function hideEditionBox(id) {
        $('#' + id + '-loading').hide();
        $('#' + id + '-error').hide();
        $('#' + id + '-field').hide();
    }

    function showEditionBox (config) {
        // No error
        $('#' + config.id + '-error').hide();
        // Loading the edition box
        ajax.get({
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

    function onPropertySelected (config, dropbox) {
        var value = $(dropbox).val();
        if (value != "") {
            // Gets the extension and the name
            var hash = value.indexOf('#');
            var extension = value.substring(0, hash);
            var name = value.substring(hash + 1);
            // Entity
            var entity = config.entity;
            var entityId = config.entityid;
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
        url: function (config) {
            return 'ui/property/{0}/{1}/editable'.format(config.entity, config.entityid);
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
        })
    }
});