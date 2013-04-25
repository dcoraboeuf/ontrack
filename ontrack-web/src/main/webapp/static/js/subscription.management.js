var Subscriptions = function () {

    function unsubscribe(entity, entityId) {
        AJAX.del({
            url: 'ui/admin/subscriptions/{0}/{1}'.format(entity, entityId),
            successFn: function (ack) {
                if (ack.success) {
                    $('#subscription-{0}-{1}'.format(entity, entityId)).remove();
                }
            }
        });
    }

    return {
        unsubscribe: unsubscribe
    };

}();