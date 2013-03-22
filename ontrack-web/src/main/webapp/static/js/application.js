// Prototypes

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

// Global functions

function loc () {
	var code = arguments[0];
	var text = l[code];
	if (text != null) {
		var params = []	;
		for (var i = 1 ; i < arguments.length ; i++) {
			params.push(arguments[i]);
		}
		return text.format (params);
	} else {
		return "##" + code + "##";
	}
}

// Application singleton

var Application = function () {

    function login() {
        location = 'login?callbackUrl={0}'.format(encodeURIComponent(location.href));
    }

	/**
	 * Opens a dialog and manages the submit through a AJAX request.
	 * Configuration:
	 * @param id Dialog HTML ID
	 * @param title Dialog title
	 * @param method HTTP method (defaults to 'POST')
	 * @param url URL to post to
	 * @param successFn Function to call when the submit is OK. This function takes the returned JSON data as a unique parameter
	 * @param openFn Function to call when the dialog is opended. This defaults to no action
	 * @param validateFn Function to call when validating before submitting. It must return false when
	 *    the validation is not OK. The default returns always true.
	 */
	function dialogAndSubmit (config) {
	    config = $.extend({
	        method: 'POST',
	        width: 450,
	        openFn: $.noop,
	        validateFn: function () {
	            return true;
	        }
	    }, config);
	    // Dialog
	    dialog ({
	        id: config.id,
	        title: config.title,
	        openFn: config.openFn,
	        width: config.width,
	        submitFn: function (closeFn) {
	            if (config.validateFn()) {
                    submit ({
                        id: config.id,
                        method: config.method,
                        url: config.url,
                        successFn: function (data) {
                            // Does something with the data
                            config.successFn(data);
                            // Closes the dialog
                            closeFn();
                        },
                        errorMessageFn: function (message) {
                            dialogError(config.id, message);
                        }
                    });
                }
	        }
	    });
	}

	/**
	 * Displays an error in a dialog. If the message is null or not defined, clears the error box.
	 */
	function dialogError (id, message) {
	    error(id + '-error');
	}

	/**
	 * Displays an error message in a container
	 */
	function error (id, message) {
	    if (message && message != '') {
            $('#' + id).html(message.htmlWithLines());
            $('#' + id).show();
        } else {
            $('#' + id).hide();
        }
	}
	
	/**
	 * Opens a dialog defined by the <code>dialogId</code> HTML fragment.
	 * @param id ID of a HTML section
	 * @param title Title of the dialog
	 * @param submitFn Function to call when the <code>submit</code> button
	 * is triggered. The <code>submitFn</code> is called with a function
	 * (no argument) which is responsible to close the dialog. Defaults to just
	 * closing the dialog
	 * @param openFn Function to call when the dialog is opended. This defaults to no action
	 */
	function dialog (config) {
	    config = $.extend({
	        submitFn: function (closeFn) {
	            closeFn();
	        },
	        openFn: $.noop,
	        width: 450
	    }, config);
		// Sets the submit function
		$('#' + config.id).unbind('submit');
		$('#' + config.id).submit(function () {
			config.submitFn(function () {
				$('#' + config.id).dialog('close');
			});
			return false;
		});
		// Sets the cancel button (if any)
		$('#' + config.id + '-cancel').unbind('click');
		$('#' + config.id + '-cancel').click(function () {
			$('#' + config.id).dialog('close');
		});
		// Clears any error message
		dialogError();
		// Initialization
		config.openFn();
		// Shows the dialog
		$('#' + config.id).dialog({
			title: config.title,
			width: config.width,
			modal: true
		});
	}

	/**
	 * Collects all values in a form
	 */
	function values (baseId) {
		var data = {};
		$('#' + baseId + ' input,textarea,select').each (function (index, field) {
			var name = field.getAttribute('name');
			var value = field.value;
			data[name] = value;
		});
		// console.log(JSON.stringify(data));
		return data;
	}
	
	/**
	 * Submit a set of data defined in a form.
	 * @param id ID of the HTML form
	 * @param method HTTP method to use ('GET', 'POST'...)
	 * @param url URL to call
	 * @param successFn <code>(JSON => .)</code> Function called in case of success.
	 * @param errorMessageFn <code>(String => .)</code> Function called in case of error.
	 * @see #ajax
	 */
	function submit (config) {
	    config = $.extend({
	        method: 'POST',
	        successFn: $.noop,
	        errorMessageFn: function (message) {
	            displayError(message);
	        }
	    }, config);
		// Collects all fields values
		var data = values (config.id);
		// Call
		ajax (config.method, config.url, data, config.successFn, config.errorMessageFn);
	}
	
	function onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn) {
	  	if (errorMessageFn) {
	  		if (jqXHR.status == 500 && jqXHR.responseText && jqXHR.responseText != '') {
	  			errorMessageFn(jqXHR.responseText);
	  		} else {
	  			var message = getAjaxError(loc('client.error.general'), jqXHR, textStatus, errorThrown);
	  			errorMessageFn(message);
	  		}
	  	} else {
	  		Application.displayAjaxError (loc('client.error.general'), jqXHR, textStatus, errorThrown);
	  	}
	}
	
	/**
	 * @param method HTTP method to use ('GET', 'POST'...)
	 * @param url URL to call
	 * @param data Data to send as JSON
	 * @param successFn <code>(JSON => .)</code> Function called in case of success.
	 * @param errorMessageFn <code>(String => .)</code> Function called in case of error.
	 */
	function ajax (method, url, data, successFn, errorMessageFn) {
		$.ajax({
			type: method,
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(data),
			dataType: 'json',
			success: function (data) {
				successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn);
			}
		});
	}
	
	function ajaxGet (url, successFn, errorMessageFn) {
		$.ajax({
			type: 'GET',
			url: url,
			dataType: 'json',
			success: function (data) {
				successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn);
			}
		});
	}
	
	function ajaxDelete (url, successFn, errorMessageFn) {
		$.ajax({
			type: 'DELETE',
			url: url,
			dataType: 'json',
			success: function (data) {
				successFn(data);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				onAjaxError(jqXHR, textStatus, errorThrown, errorMessageFn);
			}
		});
	}
	
	function confirmAndCall (text, callback) {
		$('<div>{0}</div>'.format(text)).dialog({
			title: loc('general.confirm.title'),
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
	
	function confirmIDAndCall (id, callback) {
		var text = document.getElementById(id).value;
		confirmAndCall(text, callback);
	}
	
	function displayError (text) {
		$('<div>{0}</div>'.format(text.htmlWithLines())).dialog({
			title: loc('client.error.title'),
			modal: true,
            buttons: {
                Ok: function() {
                    $( this ).dialog( "close" );
                }
            }
		});
	}
	
	function getAjaxError (message, jqXHR, textStatus, errorThrown) {
		return '{0}\n[{1}] {2}'.format(message, jqXHR.status, jqXHR.statusText);
	}
	
	function displayAjaxError (message, jqXHR, textStatus, errorThrown) {
		var text = getAjaxError(message, jqXHR, textStatus, errorThrown);
		displayError(text);
	}
	
	function changeLanguage (lang) {
		if (location.search.indexOf("language") > -1) {
	    	location.search = location.search.replace(/language=[a-z][a-z]/, "language=" + lang);
		} else if (location.search == "") {
			var url = location.href;
			if (url.substr(url.length - 1) == '?') {
				location.href += "language=" + lang;
			} else {
				location.href += "?language=" + lang;
			}
		} else {
			location.href += "&language=" + lang;
		}
	}

	function validate (selector, test) {
		if (test) {
			$(selector).removeClass("invalid");
			return true;
		} else {
			$(selector).addClass("invalid");
			return false;
		}
	}
	
	function loading (selector, loading) {
		$(selector).empty();
		if (loading) {
			$(selector).append('<div class="loading">{0}</div>'.format(loc('general.loading')));
		}
	}

	function extractEntity (value) {
	    var pos = value.indexOf('/');
	    if (pos > 0) {
	        return value.substring(0, pos);
	    } else {
	        return value;
	    }
	}
	
	function deleteEntity (entity, id, backUrl) {
		var url = 'ui/manage/{0}/{1}'.format(entity, id);
		ajaxGet (
			url,
			function (o) {
				confirmAndCall(
					loc('{0}.delete.prompt'.format(extractEntity(entity)), o.name),
					function () {
						ajaxDelete (
							url,
							function () {
								location = backUrl;
							},
							displayError);
					});
			},
			displayError);
	}

	function tooltips () {
        $('.tooltip-source').tooltip({
            placement: 'bottom'
        });
    }
	
	return {
	    login: login,
		dialog: dialog,
		submit: submit,
		values: values,
		dialogAndSubmit: dialogAndSubmit,
		dialogError: dialogError,
		error: error,
		confirmAndCall: confirmAndCall,
		confirmIDAndCall: confirmIDAndCall,
		displayError: displayError,
		displayAjaxError: displayAjaxError,
		onAjaxError: onAjaxError,
		getAjaxError: getAjaxError,
		changeLanguage: changeLanguage,
		validateTextAsName: function (selector) {
			var value = $(selector).val();
			var trimmedValue = value.trim();
			return validate (selector, trimmedValue == value && trimmedValue != "");
		},
		validateTextAsTrimmed: function (selector) {
			var value = $(selector).val();
			var trimmedValue = value.trim();
			return validate (selector, trimmedValue == value);
		},
		validateConfirmation: function (source, confirmation) {
			var value = $(source).val();
			var confirmValue = $(confirmation).val();
			return validate (confirmation, confirmValue == value);
		},
		loading: loading,
		deleteEntity: deleteEntity,
		ajaxGet: ajaxGet,
		ajax: ajax,
		tooltips: tooltips
	};
	
} ();