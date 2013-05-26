define(['ajax','jquery'], function(ajax, $) {

    function subscribe (filter) {
        ajax.get({
            loading: {
                el: '#subscription'
            },
            url: 'gui/event/subscribe?u=1' + filter,
            successFn: function (data) {
                $('#subscription').attr('src', 'static/images/unsubscription.png');
                $('#subscription').attr('title', 'subscription.disable'.loc());
                $('#subscription').unbind('click');
                $('#subscription').click(function () {
                    unsubscribe(filter);
                });
                // Notification
                $('#subscription-notification-message').text('subscription.enabled'.loc());
                $('#subscription-notification').show();
            }
        });
    }

    function unsubscribe (filter) {
        ajax.get({
            loading: {
                el: '#subscription'
            },
            url: 'gui/event/unsubscribe?u=1' + filter,
            successFn: function (data) {
                $('#subscription').attr('src', 'static/images/subscription.png');
                $('#subscription').attr('title', 'subscription.enable'.loc());
                $('#subscription').unbind('click');
                $('#subscription').click(function () {
                    subscribe(filter);
                });
                // Notification
                $('#subscription-notification-message').text('subscription.disabled'.loc());
                $('#subscription-notification').show();
            }
        });
    }

    function init (filter) {
        // Subscription item
        var link = $('#subscription');
        var subscribed = link.attr('audit-subscribed');
        if (subscribed) {
            link.click(function () {
                unsubscribe(filter);
            });
        } else {
            link.click(function () {
                subscribe(filter);
            });
        }
    }

    return {
        init: init
    }

});