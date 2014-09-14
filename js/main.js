/*global $:false, Handlebars:false, console:false, FB: false, alert: false */
$(function () {
    'use strict';
    var twistOverviewTemplate = Handlebars.compile($("#twist-overview-template").html()),
        twistListTemplate     = Handlebars.compile($("#twist-list-template").html());
    
    function getTwistOrder() {
        var order = "";
        $("#sortable-twist-list li").each(function () {
            order += $(this).data("charid");
        });
        return order;
    }
    
    function sortTwists(twistData, order) {
        var twists = twistData.twists,
            sortedTwists = [];
        $(order.split("")).each(function (i) {
            var charId = this[0],
                foundTwists = twists.filter(function (twist) {
                    return (twist.charId === charId);
                });
            sortedTwists.push(foundTwists[0]);
        });
        return { twists : sortedTwists };
    }
    
    function goToOrderPage() {
        $("#rate,#rate-tab").addClass("active in");
    }

    function goToOverviewPage() {
        $("#overview,#overview-tab").addClass("active in");
    }

    function createList(twistData) {
        var order = $(location).attr('href').split("#")[1] || "fail",
            sortedTwists = sortTwists(twistData, order);
        if (sortedTwists.twists.length === twistData.twists.length) {
            twistData = sortedTwists;
            goToOrderPage();
        } else {
            goToOverviewPage();
        }
        $("#twist-list-template-output").html(twistListTemplate(twistData));
        $("#sortable-twist-list").sortable();
    }
    
    function createCopyLinkButton() {
        $(".copy-link").click(function () {
            $(".link-container").text("http://twistrating.no#" + getTwistOrder());
            $(".link-container").attr("href", "http://twistrating.no#" + getTwistOrder());
        });
    }

    function createFacebookShareButton() {
        $(".share-on-facebook").click(function () {
            console.log("TwistRating: " + getTwistOrder());
            FB.ui({
                method: 'share',
                href: 'http://twistrating.no#' + getTwistOrder()
            }, function (response) {});
        });
    }
    
    function sendRatingJSON(clickEvent) {
        var id     = $(clickEvent).data("id"),
            rating = $(clickEvent).data("value");
        $.ajax({
            url: "http://twistrating.apiary-mock.com/twists",
            type: "POST",
            data: JSON.stringify({ "id" : id, "rating" : rating }),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        });
    }
    
    function showStats(clickEvent) {
        var $topImage   = $(clickEvent.parent().parent().find(".top-image")),
            $twistStats = $(clickEvent.parent().parent().find(".twist-stats"));
        $topImage.animate({height: "130px"}, 1000, function () {
            $twistStats.show();    
        });
    }
    
    function createRatingButtons(twistData) {
        $(".btn-twist").click(function () {
            sendRatingJSON($(this));
            showStats($(this));
        });
    }
    
    function createOverview(twistData) {
        $("#twist-overview-template-output").html(twistOverviewTemplate(twistData));
        $(".twist-wrapper").last().css("margin-bottom", "70px"); //hack, not sure why needed...
        createRatingButtons();
    }
    
    function downloadTwistsAndBuildSite() {
        $.getJSON("http://twistrating.apiary-mock.com/twists", function (data) {
            createList(data);
            createOverview(data);
        }).done(function () {
            console.log("Lastet ned twist fra APIet");
        }).fail(function () {
            alert("Fant ingen twist i APIet... :/");
        });
    }
    
    createFacebookShareButton();
    createCopyLinkButton();
    downloadTwistsAndBuildSite();
    
});
