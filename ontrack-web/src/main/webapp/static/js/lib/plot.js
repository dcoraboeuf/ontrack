define(function () {

    var COLORS = [
        'black',
        'red',
        'green',
        'blue'
    ];

    function getColor(item) {
        if (item.color) {
            return COLORS[item.color.index % COLORS.length];
        } else {
            return 'black';
        }
    }

    function drawLine(context, item) {
        context.beginPath();
        context.moveTo(item.a.x, item.a.y);
        context.lineTo(item.b.x, item.b.y);
        context.lineWidth = item.width;
        context.strokeStyle = getColor(item);
        context.stroke();
    }

    function drawItem(ctx, item) {
        if ('line' == item.type) {
            drawLine(ctx, item);
        } else {
            common.log('plot')('Unknown item type: {0}', item.type);
        }
    }

    function draw(canvas, plot) {
        // Size
        canvas.width = plot.width;
        canvas.height = plot.height;
        // Context
        var ctx = canvas.getContext('2d');
        // All items
        $.each(plot.items, function (index, item) {
            drawItem(ctx, item);
        });
    }

    return {
        draw: draw
    }

});