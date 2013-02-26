var PromotionLevelManagement = function () {

    function init () {
        // Free validation stamps are draggable
        $('span.validationStamp.free').draggable({
            revert: "invalid",
            cursor: "move"
        });
        // Initialization
        $('.promotionLevelStamps').each(function (index, e) {
            $(e).addClass('dropzone');
            $(e).html(loc('promotion_level.management.dropzone'));
        });
        // Droppable zones for the promotion levels
        $('.promotionLevelStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                alert(ui.draggable.attr('validationStamp'));
                ui.draggable.appendTo($(this));
                return false;
            }
        });
        // Droppable zone for the free validation stamps
        $('#freeValidationStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                alert(ui.draggable.attr('validationStamp'));
                return false;
            }
        });
    }

    return {

        init: init

    };

} ();

$(document).ready(PromotionLevelManagement.init);