var PromotionLevelManagement = function () {

    function autoPromote (button) {
        // Gets the promotion level
        var project = $('#project').val();
        var branch = $('#branch').val();
        var promotionLevel = $(button).attr('data-promotionLevel');
        // Current state of the autopromotion
        var currentAutoPromote = $(button).attr('data-autoPromote');
        // URL
        var url = 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/autopromote'.format(
            project.html(),
            branch.html(),
            promotionLevel.html()
        );
        if (currentAutoPromote == 'true') {
            url += '/unset';
        } else {
            url += '/set';
        }
        // Call
        AJAX.put({
            url: url,
            loading: {
                el: button
            },
            successFn: function (flag) {
                if (flag.set) {
                    $(button).attr('data-autoPromote', 'true');
                    $(button).text(loc('promotion_level.management.notauto'));
                } else {
                    $(button).attr('data-autoPromote', 'false');
                    $(button).text(loc('promotion_level.management.auto'));
                }
                initAutoPromote();
            }
        });
    }

    function init () {
    }

    return {

        init: init,
        up: up,
        down: down,
        autoPromote: autoPromote

    };

} ();

$(document).ready(PromotionLevelManagement.init)