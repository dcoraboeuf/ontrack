define(['jquery','ajax'], function ($, ajax) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    // Promotion level ordering
    function order (direction, promotionLevel) {
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Ajax to perform the re-ordering
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/{3}'.format(
                project,
                branch,
                promotionLevel,
                direction
            ),
            loading: {
                mode: 'container',
                el: $(promotionLevelLoadingIndicator)
            },
            successFn: function (data) {
                if (data.success) {
                    location.reload();
                }
            }
        });
    }

    // Promotion level up
    function promotionUp(promotionLevel) {
        order('up', promotionLevel);
    }

    // Promotion level down
    function promotionDown(promotionLevel) {
        order('down', promotionLevel);
    }

    // Actions

    $('.icon-arrow-up').each(function (index, action) {
        var promotionLevel = $(action).attr('data-promotionLevel');
        $(action).click(function () {
            promotionUp(promotionLevel);
        });
    });

    $('.icon-arrow-down').each(function (index, action) {
        var promotionLevel = $(action).attr('data-promotionLevel');
        $(action).click(function () {
            promotionDown(promotionLevel);
        });
    });

});