define(function () {

    String.prototype.format = function() {
        var args = arguments;
        return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function(m, n) {
            if (m == "{{") {
                return "{";
            }
            if (m == "}}") {
                return "}";
            }
            return args[n];
        });
    };

    String.prototype.html = function() {
        return $('<i></i>').text(this).html();
    };

    String.prototype.htmlWithLines = function() {
        var text = this.html();
        return text.replace(/\n/g, '<br/>');
    };

});