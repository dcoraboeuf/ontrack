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
	
	function dialogAndSubmit (dialogId, dialogTitle, method, url) {
		dialog (
			dialogId,
			dialogTitle,
			function (successFn) {
				submit (dialogId, method, url, successFn, function (message) {
			  		$('#' + dialogId + '-error').html(message.htmlWithLines());
			  		$('#' + dialogId + '-error').show();
				});
			});
	}
	
	function dialog (dialogId, dialogTitle, onSubmitFn) {
		// Sets the submit function
		$('#' + dialogId).unbind('submit');
		$('#' + dialogId).submit(function () {
			onSubmitFn(function () {
				$('#' + dialogId).dialog('close');
			});
			return false;
		});
		// Sets the cancel button (if any)
		$('#' + dialogId + '-cancel').unbind('click');
		$('#' + dialogId + '-cancel').click(function () {
			$('#' + dialogId).dialog('close');
		});
		// Shows the dialog
		$('#' + dialogId).dialog({
			title: dialogTitle,
			width: 450,
			modal: true
		});
	}
	
	function values (baseId) {
		var data = {};
		$('#' + baseId + ' input').each (function (index, field) {
			var name = field.getAttribute('name');
			var value = field.value;
			data[name] = value;
		});
		$('#' + baseId + ' textarea').each (function (index, field) {
			var name = field.getAttribute('name');
			var value = field.value;
			data[name] = value;
		});
		return data;
	}
	
	function submit (baseId, method, url, successFn, errorMessageFn) {
		// Collects all fields values
		var data = values (baseId);
		// Call
		ajax (method, url, data, successFn, errorMessageFn);
	}
	
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
			  	if (errorMessageFn && jqXHR.status == 500 && jqXHR.responseText && jqXHR.responseText != '') {
			  		// Error callback
			  		errorMessageFn(jqXHR.responseText);
			  	} else {
			  		Application.displayAjaxError (loc('client.error.general'), jqXHR, textStatus, errorThrown);
			  	}
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
	
	return {
		dialog: dialog,
		submit: submit,
		dialogAndSubmit: dialogAndSubmit,
		confirmAndCall: confirmAndCall,
		confirmIDAndCall: confirmIDAndCall,
		displayError: displayError,
		displayAjaxError: displayAjaxError,
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
		loading: loading
	};
	
} ();