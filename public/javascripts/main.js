/*global $:false, Handlebars:false, console:false, FB: false, alert: false, Chart: false*/
$(function () {
    'use strict';
    
    var apiBaseUrl = window.location.protocol + "//" + window.location.host;
//    var apiBaseUrl = "http://twistrating.apiary-mock.com";
    var publicBaseUrl = window.location.protocol + "//" + window.location.host;

    var firebase = new Firebase("https://radiant-heat-8671.firebaseio.com/twists");
    firebase.on("value", function (dataSnapshot) {
        console.log(dataSnapshot.val());
    });
    
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
        new Sortable(document.getElementById("sortable-twist-list"));
    }
    
    function createCopyLinkButton() {
        $(".copy-link").click(function () {
            $(".link-container").text(publicBaseUrl + "#" + getTwistOrder());
            $(".link-container").attr("href", publicBaseUrl + "#" + getTwistOrder());
        });
    }

    function createFacebookShareButton() {
        $(".share-on-facebook").click(function () {
            console.log("TwistRating: " + getTwistOrder());
            FB.ui({
                method: 'share',
                href: publicBaseUrl + '#' + getTwistOrder()
            }, function (response) {});
        });
    }
    
    function sendRatingJSON(clickEvent) {
        var id     = $(clickEvent).data("id"),
            rating = $(clickEvent).data("value");
        $.ajax({
            url: apiBaseUrl + "/twists",
            type: "POST",
            data: JSON.stringify({ "id" : id, "rating" : rating }),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        });
    }
    
    function buildChart(clickEvent) {
        var $twistStats = $(clickEvent.parent().parent().find(".twist-stats")),
            loves = $twistStats.data("loves"),
            sosos = $twistStats.data("sosos"),
            hates = $twistStats.data("hates"),
            data = {
                labels: ["Nam", "Hm", "Æsj"],
                datasets: [
                    {
                        label: "",
                        fillColor: "rgba(33,29,30,1)",
                        highlightFill: "rgba(33,29,30,0.85)",
                        data: [loves, sosos, hates]
                    }
                ]
            },
            options = {
                animation: true,
                barShowStroke : false
            },
            myNewChart = new Chart($twistStats.get(0).getContext('2d')).Bar(data, options);
    }
    
    function showStats(clickEvent) {
        var $topImage   = $(clickEvent.parent().parent().find(".top-image")),
            $twistStats = $(clickEvent.parent().parent().find(".twist-stats")),
            currentWidth = clickEvent.parent().parent().find(".image-wrapper").width();
        
        if (!$twistStats.is(":visible")) {
            $topImage.animate({height: "130px"}, 1000);
            setTimeout(function () {
                $twistStats.fadeIn(200);
                buildChart(clickEvent);
            }, 400);
        } else {
            //$twistStats.fadeOut(600);
            //$topImage.animate({height: currentWidth + "px"}, 1000);
        }
    }
    
    function createRatingButtons(twistData) {
        $(".btn-twist").click(function () {
            $(this).addClass("pressed");
            $(this).siblings().removeClass("pressed");
            sendRatingJSON($(this));
            showStats($(this));
        });
    }
    
    function createOverview(twistData) {
        $("#twist-overview-template-output").html(twistOverviewTemplate(twistData));
        createRatingButtons();
    }
    
    function downloadTwistsAndBuildSite() {
        $.getJSON(apiBaseUrl + "/twists", function (data) {
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
