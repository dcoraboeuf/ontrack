define(function () {

    var logging = false;

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

    String.prototype.loc = function (args) {
        var code = this;
        var text = l[code];
        if (text != null) {
            return text.format(args);
        } else {
            return "##" + code + "##";
        }
    };

    function log (context) {
        return function (message, args) {
            if (logging && console) {
                if (args) {
                    console.log('[{1}] {0}'.format(message, context), args);
                } else {
                    console.log('[{1}] {0}'.format(message, context));
                }
            }
        }
    }

    function confirmAndCall (text, callback) {
        $('<div>{0}</div>'.format(text)).dialog({
            title: 'general.confirm.title'.loc(),
            dialogClass: 'confirm-dialog',
            modal: true,
            buttons: {
                Ok: function () {
                    $( this ).dialog( "close" );
                    callback();
                },
                Cancel: function () {
                    $( this ).dialog( "close" );
                }
            }
        });
    }

    return {
        log: log,
        confirmAndCall: confirmAndCall
    }

});