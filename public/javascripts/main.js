/*global $:false, Handlebars:false, console:false, FB: false, alert: false, Chart: false*/
$(function () {
    'use strict';

//    var apiBaseUrl = "http://twistrating.apiary-mock.com";
    var apiBaseUrl            = window.location.protocol + "//" + window.location.host,
        publicBaseUrl         = window.location.protocol + "//" + window.location.host,
        twistOverviewTemplate = Handlebars.compile($("#twist-overview-template").html()),
        twistListTemplate     = Handlebars.compile($("#twist-list-template").html()),
        global_chartArray     = new Array();
    
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
    
    function gotToListPage() {
        $("#list-page,#list-tab").addClass("active in");
    }

    function goToVotePage() {
        $("#vote-tab,#vote-page").addClass("active in");
    }

    function createListPage(twistData) {
        var order = $(location).attr('href').split("#")[1] || "fail",
            sortedTwists = sortTwists(twistData, order);
        if (sortedTwists.twists.length === twistData.twists.length) {
            twistData = sortedTwists;
            gotToListPage();
        } else {
            goToVotePage();
        }
        $("#twist-list-template-output").html(twistListTemplate(twistData));
        new Sortable(document.getElementById("sortable-twist-list"));
    }
    
    function createCopyLinkButton() {
        $(".copy-link").click(function () {
            var $shareLinkDiv = $(".link-container");
            $shareLinkDiv.text(publicBaseUrl + "#" + getTwistOrder());
            $shareLinkDiv.attr("href", publicBaseUrl + "#" + getTwistOrder());
        });
    }

    function createFacebookShareButton() {
        $(".share-on-facebook").click(function () {
            FB.ui({
                method: 'share',
                href: publicBaseUrl + '#' + getTwistOrder()
            }, function (response) {});
        });
    }
    
    function sendRatingJSON(id, rating) {
        $.ajax({
            url: apiBaseUrl + "/twists",
            type: "POST",
            data: JSON.stringify({ "id" : id, "rating" : rating }),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        });
    }

    function statsVisible($twist){
        return $twist.find(".twist-stats").is(":visible");
    }
    
    function buildChart($twist, $twistStats) {
        var loves = $twistStats.data("loves"),
            sosos = $twistStats.data("sosos"),
            hates = $twistStats.data("hates"),
            id    = $twist.data("id"),
            data  = {
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
            };
            global_chartArray[id] = new Chart($twistStats.get(0).getContext('2d')).Bar(data, options);
    }
    
    function toggleStats($twist) {
        var $twistImage = $($twist.find(".top-image")),
            $twistStats = $($twist.find(".twist-stats")),
            currentWidth = $twist.find(".image-wrapper").width();
        
        if (!statsVisible($twist)) {
            $twistImage.animate({height: "130px"}, 1000);
            setTimeout(function () {
                $twistStats.fadeIn(200);
                buildChart($twist, $twistStats);
            }, 400);
        } else {
            $twistStats.fadeOut(600);
            $twistImage.animate({height: currentWidth + "px"}, 1000);
        }
    }
    
    function createRatingButtons() {
        $(".btn-twist").click(function () {
            var button = $(this);
            var id     = button.data("id"),
                value  = button.data("value"),
                $twist = $("#"+id);
            button.addClass("pressed").siblings().removeClass("pressed");
            sendRatingJSON(id, value);
            if(!statsVisible($twist)){
                toggleStats($twist);
            }
        });
    }
    
    function createRatePage(twistData) {
        $("#twist-overview-template-output").html(twistOverviewTemplate(twistData));
        createRatingButtons();
    }
    
    function downloadTwistsAndBuildSite() {
        $.getJSON(apiBaseUrl + "/twists", function (data) {
            createListPage(data);
            createRatePage(data);
        }).done(function () {
            console.log("Lastet ned twist fra APIet");
        }).fail(function () {
            alert("Noen har spist all twisten i APIet... :/");
        });
    }

    function createTwistNameClickListeners() {
        $(".twist-name").click(function(){
            toggleStats($(this));
        });
    }
    
    createFacebookShareButton();
    createCopyLinkButton();
    createTwistNameClickListeners();
    downloadTwistsAndBuildSite();
    
});
