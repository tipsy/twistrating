$(function() {
    
    createFacebookShareButton();
    createCopyLinkButton();
    var twistOverviewTemplate = Handlebars.compile( $("#twist-overview-template").html() );
    var twistListTemplate = Handlebars.compile( $("#twist-list-template").html() );

    $.getJSON( "http://twistrating.apiary-mock.com/twists", function(data) {
        createList(data);
        createOverview(data);
    })
    .done(function() {
        console.log("Lastet ned twist fra APIet");
    })
    .fail(function() {
        alert( "Fant ingen twist i APIet... :/" );
    });

    function createList(twistData) {
        var order = $(location).attr('href').split("#")[1] || "fail";
        sortedTwists = sortTwists(twistData, order);
        if(sortedTwists.twists.length === twistData.twists.length) {
            twistData = sortedTwists;
            goToOrderPage();
        }else{
            goToOverviewPage();
        }

        $("#twist-list-template-output").html(twistListTemplate(twistData));
        $("#sortable-twist-list").sortable();
    }

    function goToOrderPage() {
        $("#rate,#rate-tab").addClass("active in"); 
    }

    function goToOverviewPage() {
        $("#overview,#overview-tab").addClass("active in");
    }

    function sortTwists(twistData, order) {
        twists = twistData.twists;
        var sortedTwists = [];
        var usedCharIds = [];
        for (var i = 0; i < order.length; i++) {
            var charId = order.charAt(i);
            var foundTwists = twists.filter(function (twist) {
                return (twist.charId === charId);
            });
            if (foundTwists.length === 1 && usedCharIds.indexOf(charId) === -1) {
                sortedTwists.push(foundTwists[0]);
                usedCharIds.push(charId);
            }
        }
        return { twists : sortedTwists };
    }

    function createOverview(twistData) {
        $("#twist-overview-template-output").html(twistOverviewTemplate(twistData));
        $(".twist-wrapper").last().css("margin-bottom", "70px"); //hack, not sure why needed...
        $(".btn-twist").click(function() {
            var id = $(this).data("id");
            var rating = $(this).data("value");
            $.ajax({
              url: "http://twistrating.apiary-mock.com/twists",
              type: "POST",
              data: JSON.stringify({ "id" : id, "rating" : rating } ),
              contentType:"application/json; charset=utf-8",
              dataType:"json",
            });
        });
    }

    function createCopyLinkButton(){
        $(".copy-link").click(function(){
            $(".link-container").text("http://twistrating.no#"+getTwistOrder());
            $(".link-container").attr("href", "http://twistrating.no#"+getTwistOrder());
        });
    }

    function createFacebookShareButton() {
        $(".share-on-facebook").click(function(){
            console.log("TwistRating: " + getTwistOrder());
            FB.ui({
                method: 'share',
                href: 'http://twistrating.no#'+getTwistOrder(),
            }, function(response){});
        });
    }

    function getTwistOrder(){
        var order = "";
        $("#sortable-twist-list li").each(function(){
            order += $(this).data("charid");
        });
        return order;
    }
    
});
