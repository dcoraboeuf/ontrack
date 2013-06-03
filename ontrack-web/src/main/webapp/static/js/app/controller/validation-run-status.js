define(['render','jquery'], function (render, $) {

    Handlebars.registerHelper('statusLabel', function(options) {
        return ('status.' + options.fn(this)).loc();
    });

    function updateStatus() {
        $('#validation-run-status-error').hide();
        $('#validation-run-status-form').show();
        if ($('#validation-run-status-form').is(':visible')) {
            $('#validation-run-status-description').focus();
        }
    }

    function init (config) {
        $('#validation-run-status-button').click(updateStatus);
    }

    return {
        url: function (config) {
            return 'ui/manage/validation_run/{0}/statusUpdateData'.format(config.run)
        },
        render: render.asSimpleTemplate(
            'validation-run-status',
            render.sameDataFn,
            init
        )
    }

})