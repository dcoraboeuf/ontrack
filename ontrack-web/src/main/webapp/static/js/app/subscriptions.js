define(['jquery', 'ajax'], function ($, ajax) {

    function unsubscribe(entity, entityId) {
        ajax.del({
            url: 'ui/admin/subscriptions/{0}/{1}'.format(entity, entityId),
            successFn: function (ack) {
                if (ack.success) {
                    $('#subscription-{0}-{1}'.format(entity, entityId)).remove();
                    if ($('#container').find('.subscription').length == 0) {
                        $('#container-table').remove();
                        $('<div></div>')
                            .addClass('alert')
                            .addClass('alert-info')
                            .text('subscription.management.none'.loc())
                            .appendTo($('#container'));
                    }
                }
            }
        });
    }

    $('.unsubscribe-action').each(function (index, action) {
        var entity = $(action).attr('entity');
        var entityId = $(action).attr('entityid');
        $(action).click(function () {
            unsubscribe(entity, entityId);
        });
    });

});