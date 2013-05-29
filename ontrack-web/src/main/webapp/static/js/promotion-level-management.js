var PromotionLevelManagement = function () {

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