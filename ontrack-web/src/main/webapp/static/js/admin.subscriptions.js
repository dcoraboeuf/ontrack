var Subscriptions = function () {

    function unsubscribe(userId, entity, entityId) {
        AJAX.del({
            url: 'ui/admin/subscriptions/{0}/{1}/{2}'.format(userId, entity, entityId),
            successFn: function (ack) {
                if (ack.success) {
                    location.reload();
                }
            }
        });
    }

    return {
        unsubscribe: unsubscribe
    };

}();