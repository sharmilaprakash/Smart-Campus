if (window.console) {
    console.log("Welcome to your Play application's JavaScript!");
}

function getEventsFromBeacon(id) {
    // setTimeout(function () {
    //     $(".events").load("/beacon/"+id);
    // },2000);
}

$(document).ready(function () {
    getEventsFromBeacon(1);

    $('.reload-events').on('click',function () {
        $('.events').html("<div class=\"col-xs-12 event-loading\"><div>Loading ...</div> <img src=\"/assets/images/loading.gif\" alt=\"\" width=\"20px\"></div>");
        getEventsFromBeacon(1);
    });
});

