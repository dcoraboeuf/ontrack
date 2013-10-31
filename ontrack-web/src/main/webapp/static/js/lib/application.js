define(['ajax','common','dialog'], function (ajax, common, dialog) {

    function deleteDialog(config) {
        config = $.extend({
            title: 'general.delete'.loc(),
            templateId: 'dialog-delete'
        }, config);
        dialog.show({
            title: config.title,
            templateId: config.templateId,
            data: {
                message: config.message
            },
            buttons:[{
                text: 'general.delete'.loc(),
                action: 'submit'
            }, {
                text: 'general.cancel'.loc(),
                action: 'cancel'
            }],
            submitFn: function (dialog) {
                ajax.del({
                    url: config.url,
                    loading: {
                        el: dialog.controls['submit']
                    },
                    successFn: config.successFn
                })
            }
        })
    }

    function deleteEntity (entityPath, id, callbackFn, nameFn) {
        var url = 'ui/manage/{0}/{1}'.format(entityPath, id);
        ajax.get ({
            url: url,
            successFn: function (o) {
                var name;
                if (nameFn) {
                    name = nameFn(o);
                } else {
                    name = o.name;
                }
                common.confirmAndCall(
                    '{0}.delete.prompt'.format(extractEntity(entityPath)).loc(name),
                    function () {
                        ajax.del({
                            url: url,
                            successFn: function () {
                                callbackFn();
                            }
                        });
                    });
            }
        });
    }

    function extractEntity (value) {
        var pos = value.lastIndexOf('/');
        if (pos > 0) {
            return value.substring(pos + 1);
        } else {
            return value;
        }
    }

    return {
        deleteEntity: deleteEntity,
        deleteDialog: deleteDialog
    }

});