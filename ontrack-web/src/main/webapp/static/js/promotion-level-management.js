var PromotionLevelManagement = function () {

    function init () {
        $('span.validationStamp.free').draggable({
            revert: true
        });
    }

    return {

        init: init

    };

} ();

$(document).ready(PromotionLevelManagement.init);