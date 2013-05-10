var ValidationStamps = function () {

	function createValidationStamp (project, branch) {
		Application.dialogAndSubmit({
			id: 'validation_stamp-create-dialog',
			title: loc('validation_stamp.create.title'),
			url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(project,branch),
			successFn: function (data) {
				location = 'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(project,branch,data.name);
			}
		});
	}

	function updateValidationStamp (project, branch, validationStamp) {
	    var url = 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(project, branch, validationStamp);
	    AJAX.get({
            url: url,
            successFn: function (summary) {
                Application.dialogAndSubmit({
                    id: 'validation_stamp-update-dialog',
                    title: loc('validation_stamp.update'),
                    url: url,
                    method: 'PUT',
                    openFn: function () {
                        $('#validation_stamp-update-dialog-name').val(summary.name);
                        $('#validation_stamp-update-dialog-description').val(summary.description);
                    },
                    successFn: function (summary) {
                            location = 'gui/project/{0}/branch/{1}/validation_stamp/{2}'.format(summary.branch.project.name, summary.branch.name, summary.name);
                        }
                    });
            }
	    });
	}

    function up (project, branch, name) {
        AJAX.put({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/up'.format(project, branch, name),
            loading: {
                el: $('#validation-stamp-{0}-order-loading'.format(name)),
                mode: 'container'
            },
            successFn: function () {
                Template.reload('validation_stamps');
            }
        })
    }

    function down (project, branch, name) {
        AJAX.put({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/down'.format(project, branch, name),
            loading: {
                el: $('#validation-stamp-{0}-order-loading'.format(name)),
                mode: 'container'
            },
            successFn: function () {
                Template.reload('validation_stamps');
            }
        })
    }

    function changeOwnerInList (project, branch, name) {
        changeOwner(project, branch, name, function () {
            // Refreshes the validation stamps
            Template.reload('validation_stamps');
        });
    }

    function changeOwnerInPage (project, branch, name) {
        changeOwner(project, branch, name, function () {
            // Refreshes the page
            location.reload();
        });
    }

    function changeOwner (project, branch, name, successFn) {
        // Gets the details of the validation stamp
        AJAX.get({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}'.format(
                project,
                branch,
                name
            ),
            successFn: function (summary) {
                // Gets the current owner
                var currentOwner = summary.owner ? summary.owner.id : 0;
                // Gets the list of potential owners
                AJAX.get({
                    url: 'ui/admin/accounts',
                    successFn: function (accounts) {
                        Application.dialog({
                            id: 'validation_stamp-owner-dialog',
                            title: loc('validation_stamp.owner.change'),
                            openFn: function () {
                                // Validation stamp name
                                $('#validation_stamp-owner-dialog-name').val(name);
                                // List of accounts
                                $('#validation_stamp-owner-dialog-owner').empty();
                                $('#validation_stamp-owner-dialog-owner')
                                    .append($("<option></option>")
                                        .attr("value", "")
                                        .text(loc('validation_stamp.owner.none')));
                                $.each(accounts, function (index, account) {
                                    var option = $("<option></option>")
                                        .attr("value", account.id)
                                        .text('{0} - {1}'.format(account.name, account.fullName));
                                    if (account.id == currentOwner) {
                                        option.attr('selected', 'selected');
                                    }
                                    $('#validation_stamp-owner-dialog-owner').append(option);
                                });
                            },
                            submitFn: function (closeFn) {
                                // Gets the selected owner
                                var owner = $('#validation_stamp-owner-dialog-owner').val();
                                // Removes the owner
                                if (owner == '') {
                                    AJAX.del({
                                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/owner'.format(
                                            project,
                                            branch,
                                            name
                                        ),
                                        loading: {
                                            el: $('#validation_stamp-owner-dialog-submit')
                                        },
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                // Closes the dialog
                                                closeFn();
                                                // OK
                                                successFn();
                                            }
                                        }
                                    });
                                }
                                // Changes the owner
                                else {
                                    AJAX.put({
                                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/owner/{3}'.format(
                                            project,
                                            branch,
                                            name,
                                            owner
                                        ),
                                        loading: {
                                            el: $('#validation_stamp-owner-dialog-submit')
                                        },
                                        successFn: function (ack) {
                                            if (ack.success) {
                                                // Closes the dialog
                                                closeFn();
                                                // OK
                                                successFn();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

	function deleteValidationStamp(project, branch, name) {
		Application.deleteEntity('project/{0}/branch/{1}/validation_stamp'.format(project,branch), name, '');
	}

	function validationStampImage (project, branch, stamp) {
	    return '<img class="tooltip-source" width="24" title="{2}" src="gui/project/{0}/branch/{1}/validation_stamp/{2}/image" />'.format(
               					project.html(),
               					branch.html(),
               					stamp.name.html()
               					);
	}

	function validationStampTemplate (project, branch) {
	    return Template.config({
	        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(project,branch),
            preProcessingFn: function (stamps) {
                var count = stamps.length;
                $.each(stamps, function (index, stamp) {
                    if (index == 0) {
                        stamp.first = true;
                    }
                    if (index == count - 1) {
                        stamp.last = true;
                    }
                });
                return stamps;
            },
	        render: Template.asTableTemplate('validationStampTemplate')
	    });
	}

    function commentsTemplate (project, branch, validationStamp) {
        return Template.config({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/comment?u=1'.format(project, branch, validationStamp),
            more: true,
            preProcessingFn: function (comments) {
                $.each(comments, function (index, comment) {
                    comment.comment = comment.comment.html().replace(/\n/g, '<br/>');
                });
                return comments;
            },
            render: Template.asTableTemplate('validation-stamp-comment-template')
        });
    }

    function validationStampHistoryTemplate (project, branch, validationStamp) {
        return Template.config({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/validation_run?u=1'.format(project, branch, validationStamp),
            more: true,
            render: Template.asTableTemplate('validation-stamp-history-template')
        });
    }

	function editImage () {
		$('#validation_stamp-image-form').toggle();
	}

	function editImageCancel() {
		$('#validation_stamp-image-form').hide();
	}

    function addComment() {
        AJAX.post({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/comment'.format(
                $('#validation-stamp-comment-project').val(),
                $('#validation-stamp-comment-branch').val(),
                $('#validation-stamp-comment-validationStamp').val()
            ),
            data: {
                comment: $('#validation-stamp-comment').val()
            },
            loading: {
                el: $('#validation-stamp-comment-submit')
            },
            successFn: function () {
                $('#validation-stamp-comment').val('');
                Template.reload('validation-stamp-comments');
                Template.reload('audit');
            }
        });
        // No direct submit
        return false;
    }

	return {
		createValidationStamp: createValidationStamp,
		deleteValidationStamp: deleteValidationStamp,
		updateValidationStamp: updateValidationStamp,
		validationStampTemplate: validationStampTemplate,
		validationStampImage: validationStampImage,
		editImage: editImage,
		editImageCancel: editImageCancel,
        up: up,
        down: down,
        changeOwnerInList: changeOwnerInList,
        changeOwnerInPage: changeOwnerInPage,
        addComment: addComment,
        commentsTemplate: commentsTemplate,
        validationStampHistoryTemplate: validationStampHistoryTemplate
	};

} ();

$(document).ready(Application.tooltips);