/*global $:false, Handlebars:false, console:false, FB: false, alert: false, Chart: false*/
$(function () {
    'use strict';

    var firebaseUrl           = "https://radiant-heat-8671.firebaseio.com/twists",
        apiBaseUrl            = window.location.protocol + "//" + window.location.host,
        publicBaseUrl         = window.location.protocol + "//" + window.location.host,
        twistOverviewTemplate = Handlebars.compile($("#twist-overview-template").html()),
        twistListTemplate     = Handlebars.compile($("#twist-list-template").html()),
        global_chartArray     = {};

    function getTwistOrder() {
        var order = "";
        $("#sortable-twist-list li").each(function () {
            order += $(this).data("charid");
        });
        return order;
    }
    
    function sortTwists(twists, order) {
        var sortedTwists = [];
        order.split("").forEach(function (charId) {
            var foundTwists = twists.filter(function (twist) {
                    return (twist.charId === charId);
                });

            if (foundTwists.length === 1) {
                sortedTwists.push(foundTwists[0]);
            }
        });
        return sortedTwists;
    }
    
    function gotToListPage() {
        $("#list-page,#list-tab").addClass("active in");
    }

    function goToVotePage() {
        $("#vote-tab,#vote-page").addClass("active in");
    }

    function createListPage(twistData) {
        var order = $(location).attr('href').split("#")[1] || "fail",
            sortedTwists = sortTwists(twistData.twists, order);
        if (sortedTwists.length === twistData.length) {
            twistData = {twists: sortedTwists};
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
    
    function buildChart($twistStats) {
        var data = {
                labels: ["Nam", "Hm", "Æsj"],
                datasets: [
                    {
                        label: "",
                        fillColor: "rgba(33,29,30,1)",
                        highlightFill: "rgba(33,29,30,0.85)",
                        data: [0, 0, 0]
                    }
                ]
            },
            options = {
                animation: true,
                barShowStroke : false
            };
        return new Chart($twistStats.get(0).getContext('2d')).Bar(data, options);
    }
    
    function toggleStats($twist) {
        var $twistImage = $($twist.find(".top-image")),
            $twistStats = $($twist.find(".twist-stats")),
            currentWidth = $twist.find(".image-wrapper").width();
        
        if (!statsVisible($twist)) {
            $twistImage.animate({height: "130px"}, 1000);
            setTimeout(function () {
                $twistStats.fadeIn(200);
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
            sendRatingJSON(id, value);
            if(!statsVisible($twist)){
                toggleStats($twist);
            }
        });
    }
    
    function createRatePage(twistData) {
        $("#twist-overview-template-output").html(twistOverviewTemplate(twistData));
        createRatingSmileyClickListeners();
        createNameAndImageWrapperClickListeners();
        createCharts();
    }

    function createNameAndImageWrapperClickListeners() {
        $(".twist-name, .image-wrapper").click(function(){
            toggleStats($("#"+$(this).data("id")));
        });
    }

    function createCharts() {
        $(".twist-name, .image-wrapper").each(function (){
            var $twist = $("#"+$(this).data("id"));
            var $twistStats = $($twist.find(".twist-stats"));
            var chart = buildChart($twistStats);
            global_chartArray[$twist.data("id")] = chart;
        });
    }

    function needsUpdating(chart, data) {
        return chart.datasets[0].bars[0].value !== data.likeCount
            || chart.datasets[0].bars[1].value !== data.neutralCount
            || chart.datasets[0].bars[2].value !== data.dislikeCount;
    }

    function updateChart(chart, data) {
        chart.datasets[0].bars[0].value = data.likeCount;
        chart.datasets[0].bars[1].value = data.neutralCount;
        chart.datasets[0].bars[2].value = data.dislikeCount;
        chart.update();
    }
    
    createFacebookShareButton();
    createCopyLinkButton();

    var firebase = new Firebase(firebaseUrl);

    var siteBuilt = false;

    firebase.on("value", function (dataSnapshot) {
        var snapshotValue = dataSnapshot.val();
        var newTwistData = Object.keys(snapshotValue).map(function (key) {
            return snapshotValue[key];
        });

        if (!siteBuilt) {
            var wrappedTwists = {twists: newTwistData};
            createListPage(wrappedTwists);
            createRatePage(wrappedTwists);
            siteBuilt = true;
        }

        newTwistData.forEach(function (newData) {
            var chart = global_chartArray[newData.id];
            if(needsUpdating(chart, newData)){
                updateChart(chart, newData);
            }

        });
    });
});
