define(['jquery','ajax'], function ($, ajax) {

    function unsubscribe(userId, entity, entityId) {
        ajax.del({
            url: 'ui/admin/subscriptions/{0}/{1}/{2}'.format(userId, entity, entityId),
            successFn: function (ack) {
                if (ack.success) {
                    location.reload();
                }
            }
        });
    }

    $('.unsubscribe-action').each(function (index, action) {
        var userId = $(action).attr('user');
        var entity = $(action).attr('entity');
        var entityId = $(action).attr('entityid');
        $(action).click(function () {
            unsubscribe(userId, entity, entityId);
        });
    });

});