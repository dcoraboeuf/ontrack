define(['ajax','common'], function (ajax, common) {

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
        deleteEntity: deleteEntity
    }

});