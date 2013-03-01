var subscriptionDataGrid = new DataGrid({
    "tableId": "subscription-details-table",
    "root" : "subscriptionDetails",
    "rows": 10
});

$(document).ready(function() {
    resetForm();
    fetchAndDisplay();
});

var fetchAndDisplay = function() {
    $("#msisdn_form").submit(function(event) {
        event.preventDefault();
        var msisdn = $("#msisdn").val();
        if (validateMsisdn(msisdn)) {
            initDataGrids(msisdn);
        }
    });
}

var showSubscriptionDetails = function(data) {
    $("#subscriber-details").show();
    subscriptionDataGrid.initWithData(data);
    $("#subscription-details-table").show();
    $("#subscriber-error").hide();
}


var showAllDetails = function(data) {
    if (data.subscriberError) {
        $("#subscriber-details").hide();
        $("#subscriber-error").html(data.subscriberError);
        $("#subscriber-error").show();
    } else {
        showSubscriptionDetails(data);
    }
}

var initDataGrids = function(msisdn) {
    resetForm();

    $.ajax({
        url: "admin/inquiry/data",
        data: 'msisdn=' + msisdn,
        dataType: 'json'

    }).done(showAllDetails);
}

var resetForm = function() {
    $("#subscriber-error").hide();
    $("#subscriber-details").show();
}

var validateMsisdn = function (msisdn) {
//    var isValid = msisdn.match(/^(91)?[1-9]\d{9}$/) != null;
//    var infoLabel = $("#msisdn_info");
//
//    if (!isValid) {
//        infoLabel.text("Enter a valid msisdn. eg 9988776655 or 91998776655")
//        infoLabel.addClass("label-warning");
//    } else if (infoLabel.hasClass("label-warning")) {
//        infoLabel.text("Enter MSISDN")
//        infoLabel.removeClass("label-warning");
//    }
//    return isValid;
    return true;
}