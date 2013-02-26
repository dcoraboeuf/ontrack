var PromotionLevelManagement = function () {

    function link (validationStampItem, promotionLevelItem) {
        var promotionLevel = promotionLevelItem.attr('data-promotionLevel');
        // TODO Starts indicating the loading
        // TODO Ajax to perform the link
        // TODO Clears the DnD style for the validation stamp item
        // TODO Appends the validation stamp item to the promotion level
        // TODO Clears the drop zone label
    }

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
                var promotionLevelItem = $(this);
                var validationStampItem = ui.draggable;
                link(validationStampItem, promotionLevelItem);
                return false;
            }
        });
        // Droppable zone for the free validation stamps
        $('#freeValidationStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                alert(ui.draggable.attr('data-validationStamp'));
                return false;
            }
        });
    }

    return {

        init: init

    };

} ();

$(document).ready(PromotionLevelManagement.init);