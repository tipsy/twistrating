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
        $(".sortable-twist-element").each(function () {
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
        }).done(function(data){
            updateChart(data.id, data.likeCount, data.neutralCount, data.dislikeCount);
        });
    }

    function statsVisible($twist){
        return $twist.find(".twist-stats").is(":visible");
    }
    
    function buildChart($twist, $twistStats) {
        var id = $twist.data("id");
        $.getJSON( "/twist/"+id, function(twistData) {
            var twist = twistData,
                data  = {
                    labels: ["Nam", "Hm", "Æsj"],
                    datasets: [
                        {
                            label: "",
                            fillColor: "rgba(33,29,30,1)",
                            highlightFill: "rgba(33,29,30,0.85)",
                            data: [twist.likeCount, twist.neutralCount, twist.dislikeCount]
                        }
                    ]
                },
                options = {
                    animation: true,
                    barShowStroke : false
                };
            global_chartArray[id] = new Chart($twistStats.get(0).getContext('2d')).Bar(data, options);
        }).done(function() {
                console.log( "Successfully built chart from JSON data" );
        }).fail(function() {
                console.log( "Fetching of JSON chart data failed" );
        });
    }

    function updateChart(id, loves, sosos, hates) {
        var chart = global_chartArray[id];
        chart.datasets[0].bars[0].value = loves;
        chart.datasets[0].bars[1].value = sosos;
        chart.datasets[0].bars[2].value = hates;
        chart.update();
    }
    
    function toggleStats($twist) {
        var $twistImage = $($twist.find(".top-image")),
            $twistStats = $($twist.find(".twist-stats")),
            currentWidth =  $twist.find(".image-wrapper").width();
        
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
    
    function createRatingSmileyClickListeners() {
        $(".btn-twist").click(function () {
            var button = $(this);
            var id     = button.data("id"),
                value  = button.data("value"),
                $twist = $("#"+id);
            button.addClass("pressed").siblings().removeClass("pressed");
            if(!statsVisible($twist)){
                toggleStats($twist);
                setTimeout(function(){ //let the chart build before sending rating and updating
                    sendRatingJSON(id, value);
                }, 1200);
            } else {
                sendRatingJSON(id, value);
            }
        });
    }
    
    function createRatePage(twistData) {
        $("#twist-overview-template-output").html(twistOverviewTemplate(twistData));
        createRatingSmileyClickListeners();
        createNameAndImageWrapperClickListeners();
        setPressed();
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

    function createNameAndImageWrapperClickListeners() {
        $(".twist-name, .image-wrapper").click(function(){
            toggleStats($("#"+$(this).data("id")));
        });
    }

    function getRatingFromCookie(){
        var rawCookie = document.cookie;
        var rawData = rawCookie.substring(rawCookie.indexOf('-') + 1, rawCookie.length -1);
        var session = {};
        rawData.split(";").forEach(function(rawPair){
            var pair = rawPair.split('=');
            session[pair[0]] = decodeURIComponent(pair[1]);
        });
        return JSON.parse(session['ratings'].substr(0, session['ratings'].length-1));
    }

    function setPressed(){
        var ratings = getRatingFromCookie();
        for(var key in ratings){
            var button;
            console.log(ratings[key]);
            if (ratings[key] === 1) button = " .btn-love";
            else if (ratings[key] === 0) button = " .btn-soso";
            else if (ratings[key] === -1) button = " .btn-hate";

            console.log('#' + key + button);
            $('#' + key + button).addClass('pressed');
        }

    }

    createFacebookShareButton();
    createCopyLinkButton();
    downloadTwistsAndBuildSite();

    setInterval(function() {  //update opened twists every 5 seconds
        $(".twist-wrapper").each(function() {
            if ( $(this).find(".twist-stats").is(":visible") ) {
                var id = ($(this).data("id"));
                $.getJSON( "/twist/"+id, function(data) {
                    var twist = data;
                    updateChart(data.id, data.likeCount, data.neutralCount, data.dislikeCount);
                }).done(function() {
                        console.log( "Liveupdate successful" );
                    }).fail(function() {
                        console.log( "Liveupdate failed" );
                    });
            }
        });
    }, 5000);

});
