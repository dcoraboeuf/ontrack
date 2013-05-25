define(['ajax','common'], function (ajax, common) {

    function deleteEntity (entityPath, id, callbackFn) {
        var url = 'ui/manage/{0}/{1}'.format(entityPath, id);
        ajax.get ({
            url: url,
            successFn: function (o) {
                common.confirmAndCall(
                    '{0}.delete.prompt'.format(extractEntity(entityPath)).loc(o.name),
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
        var pos = value.indexOf('/');
        if (pos > 0) {
            return value.substring(0, pos);
        } else {
            return value;
        }
    }

    return {
        deleteEntity: deleteEntity
    }

});