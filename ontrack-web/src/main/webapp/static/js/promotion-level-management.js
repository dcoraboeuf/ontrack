var PromotionLevelManagement = function () {

    function init () {
        // Free validation stamps are draggable
        $('span.validationStamp.free').draggable({
            revert: "invalid"
        });
        // Initialization
        // Droppable zones for the promotion levels
        $('.promotionLevelStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
            $( this )
                .addClass( "ui-state-highlight" )
                .find( "p" )
                .html( "Dropped!" );
         }
        });
    }

    return {

        init: init

    };

} ();

$(document).ready(PromotionLevelManagement.init);