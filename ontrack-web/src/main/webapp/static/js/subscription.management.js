var Subscriptions = function () {

    function unsubscribe(entity, entityId) {
        AJAX.del({
            url: 'ui/admin/subscriptions/{0}/{1}'.format(entity, entityId),
            successFn: function (ack) {
                if (ack.success) {
                    $('#subscription-{0}-{1}'.format(entity, entityId)).remove();
                    if ($('#container').find('.subscription').length == 0) {
                        $('#container-table').remove();
                        $('#container').append(
                            '<div class="alert alert-info">{0}</div>'.format(
                                loc('subscription.management.none')
                            )
                        );
                    }
                }
            }
        });
    }

    return {
        unsubscribe: unsubscribe
    };

}();