$(document).ready(function () {

    $('#fileData').click(
        function(){
                $('#uploadAction').attr('disabled',false);
                $('.alert').remove();
        }
    );


    $('#csvUploadForm').submit(function (event) {
        var form = $(event.target);
        result = true;

        $('#uploadAction').attr('disabled', true);

        form.find('.required').each(function (index, element) {
            element = $(element);
            if (element.val() == "") {
                removeErrorMsg(element);
                element.parents('.control-group').addClass('error');
                element.parents('.controls').append('<span class="help-inline error-help">This is required.</span>');
                removeErrorMsgOnChange(element);
                result = false;
            }
        });

        form.find('#fileData').each(function(index, element){
            element = $(element);
            var fileName = element.val();
            var fileNameArray = fileName.split(".");
            if(fileNameArray[fileNameArray.length-1] != "csv" && fileName.length != 0)
            {
                removeErrorMsg(element);
                element.parents('.control-group').addClass('error');
                element.parents('.controls').append('<span class="help-inline error-help">Invalid file format.</span>');
                removeErrorMsgOnChange(element);
                result = false;
            }
        });

        return result;
    })

    var removeErrorMsgOnChange = function () {
        var elements = arguments;
        $(elements).each(function (i, targetEle) {
            $(targetEle).change(function () {
                $(elements).each(function (j, ele) {
                    removeErrorMsg($(ele));
                });
            });
        });
    }

    var removeErrorMsg = function (element) {
        element = $(element);
        element.parents('.control-group').removeClass('error');
        element.parents('.controls').children('.error-help').remove();
    }
})