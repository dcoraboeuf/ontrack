define(['text!template/information-message.html','render'], function(template,render) {
    return {
        url: 'ui/info/message',
        render: render.asSimpleTemplate(template)
    }
});