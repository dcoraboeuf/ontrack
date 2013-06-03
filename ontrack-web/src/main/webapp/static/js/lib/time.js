define(['jquery', 'jquery-ui'], function ($) {

    // DateTime UI
    $.widget( 'ontrack.datetime', {
        // Default options
        options: {
            dateTime: new Date(),
            date: {
                showOtherMonths: true,
                selectOtherMonths: true
            }
        },
        // Constructor
        _create: function () {
            // Date picker
            this.datePicker = $('<input/>')
                .attr('type', 'text')
                .datepicker(this.options.date)
                .datepicker('setDate', this.options.dateTime)
                .appendTo(this.element);
            // Time picker
            this.timePicker = $('<select></select>');
            var currentHour = this.options.dateTime.getHours();
            var currentQuarter = Math.round(this.options.dateTime.getMinutes() / 15);
            for (var hour = 0; hour < 24; hour++) {
                var formattedHours = formatTimePart(hour);
                for (var quarter = 0; quarter < 4; quarter++) {
                    var minutes = quarter * 15;
                    var formattedMinutes = formatTimePart(minutes);
                    var formattedTime = "{0}:{1}".format(formattedHours, formattedMinutes);
                    var option = $('<option></option>')
                        .attr('value', hour * 60 + minutes)
                        .text(formattedTime);
                    if (currentHour == hour && currentQuarter == quarter) {
                        option.attr('selected', 'selected');
                    }
                    this.timePicker.append(option);
                }
            }
            this.timePicker.appendTo(this.element);
        },
        // Getting the selected date/time in ms since 1970 UTC
        getDateTime: function () {
            var ms;
            var date = this.datePicker.datepicker('getDate');
            if (date != null) {
                ms = date.getTime();
            } else {
                ms = new Date().getTime();
            }
            // Time
            var selectedTimeInMinutes = Number(this.timePicker.val());
            // OK
            return ms + selectedTimeInMinutes * 60 * 1000;
        }
    });

    function formatTimePart (n) {
        if (n < 10) {
            return '0' + n;
        } else {
            return '' + n;
        }
    }

    function calendarAt(target, initConfig) {
        var config = $.extend({
            showOtherMonths: true,
            selectOtherMonths: true,
            date: new Date()
        }, initConfig);
        $(target).datepicker('destroy');
        $(target).datepicker(config);
        // Initial date
        $(target).datepicker('setDate', config.date);
    }

    function timeAt(target, initConfig) {
        var config = $.extend({
            date: new Date()
        }, initConfig);
        // Initialization
        $(target).empty();
        var currentHour = config.date.getHours();
        var currentQuarter = Math.round(config.date.getMinutes() / 15);
        for (var hour = 0; hour < 24; hour++) {
            var formattedHours = formatTimePart(hour);
            for (var quarter = 0; quarter < 4; quarter++) {
                var minutes = quarter * 15;
                var formattedMinutes = formatTimePart(minutes);
                var formattedTime = "{0}:{1}".format(formattedHours, formattedMinutes);
                var option = $('<option></option>')
                    .attr('value', hour * 60 + minutes)
                    .text(formattedTime);
                if (currentHour == hour && currentQuarter == quarter) {
                    option.attr('selected', 'selected');
                }
                $(target).append(option);
            }
        }
    }

    // API
    return {
        calendarAt: calendarAt,
        timeAt: timeAt
    }

});