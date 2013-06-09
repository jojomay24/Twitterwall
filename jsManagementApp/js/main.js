const STATE_OPEN = 0;
const STATE_ACKED = 1;
const STATE_BLOCKED = 2;

const PAGE_OPEN_TWEETS = 'Open Tweets';
const PAGE_ACKED_TWEETS = 'Acked Tweets';
const PAGE_BLOCKED_TWEETS = 'Blocked Tweets';

const TIME_INTERVAL = 120;

var currentPage;
var myCounter;

/* ---------------------------------------- helper -------------------------------*/
function unixTime() {
    return Math.round(+new Date()/1000);
}


/* --------------- timer ---------------------------------------*/
function Countdown(options) {
    var timer, 
    instance = this,
    secondsStart = options.seconds || TIME_INTERVAL,
    seconds = options.seconds || TIME_INTERVAL,
    updateStatus = options.onUpdateStatus || function() { },
    counterEnd = options.onCounterEnd || function() { };

    function decrementCounter() {
        updateStatus(seconds);
        if (seconds === 0) {
            instance.stop();
            counterEnd();
        }
        seconds--;
    }

    this.restart = function() {
        console.log("Restarting Counter ...");
        seconds = TIME_INTERVAL;
        clearInterval(timer);
        timer = 0;
        timer = setInterval(decrementCounter, 1000);
    };
    this.start = function() {
        clearInterval(timer);
        timer = 0;
        seconds = options.seconds;
        timer = setInterval(decrementCounter, 1000);
    };

    this.stop = function() {
        clearInterval(timer);
    };
}

/* ------------ SERVER Calls ------------------------------------------------*/
function changeAckStateOnServer(data) {
    var serverUrl = $('#serverUrl').val();
    $.ajax({
        type : "PUT",
        url : serverUrl + "/ack",

        data : JSON.stringify(data),
        contentType : 'application/json',
        dataType : "json",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Failed changing the ack state!");
            alert("Failed changing AckState!")
        },
        success : function(data) {
            console.log("Success changing the ack state!");
        }
    });
}

function getTweetsFromServer(list, state, offset) {
    var serverUrl = $('#serverUrl').val();
    offset = ( offset == undefined) ? 0 : offset;
//    offset = 25;
    list.empty();
    console.log(unixTime() +" calling getTweetsFromServer");
    $.ajax({
        type : "GET",
        url : serverUrl + "/tweets",
        dataType : "json",
        data : "ackState=" + state + "&offset=" + offset,
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Fail: " + data.text);
        },
        success : function(data) {
            console.log(unixTime() +" Received Answer for getTweetsFromServer:" + data);
            console.log("Found total nr:" + data.totalFoundNr);
            console.log("Actually loaded nr: " + data.containedNr);
            console.log(data.tweets.length);
            updateSearchResultBar(data);
            appendTweetsToList(data.tweets, list);
            console.log(unixTime() +" Appended to list");
        }
    });

}

function getMinAutoAckAge() {
    var serverUrl = $('#serverUrl').val();
    console.log(unixTime() +" calling getMinAutoAckAge");
    $.ajax({
        type : "GET",
        url : serverUrl + "/minAutoAckAge",
        dataType : "json",
        data : "",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Fail: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function(data) {
            console.log(unixTime() +" Received Answer for getMinAutoAckAge:" + data);
            $('#minAgeAutoAck').val(data);
        }
    });
}

function setMinAutoAckAge(age) {
    var serverUrl = $('#serverUrl').val();
    console.log(unixTime() +" calling setMinAutoAckAge with age: " + age);
    $.ajax({
        type : "PUT",
        url : serverUrl + "/minAutoAckAge",
        data : JSON.stringify(parseInt(age)),
        contentType : 'application/json',
        dataType : "json",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            alert("Failed setting MinAutoAckAge: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function() {
            console.log(unixTime() +" Received Successful Answer for setMinAutoAckAge");
        }
    });
}

function getRegexFilteringActive() {
    var serverUrl = $('#serverUrl').val();
    console.log(unixTime() +" calling getRegexFilteringActive");
    $.ajax({
        type : "GET",
        url : serverUrl + "/regexFilteringActive",
        dataType : "json",
        data : "",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Fail: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function(data) {
            console.log(unixTime() +" Received Answer for getRegexFilteringActive:" + data);
            $('#regexFilteringActive').val(data);
        }
    });
}

function setRegexFilteringState(state) {
    var serverUrl = $('#serverUrl').val();
    console.log(unixTime() +" calling setRegexFilteringState with State: " + state);
    var stateAsBoolean = (state === 'ACTIVE' ? true : false);
    
    $.ajax({
        type : "PUT",
        url : serverUrl + "/regexFilteringActive",
        data : JSON.stringify( stateAsBoolean),
        contentType : 'application/json',
        dataType : "json",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Fail: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function() {
            console.log(unixTime() +" Received Successful Answer for setRegexFilteringState");
            $('#regexFilteringActive').val(stateAsBoolean);
        }
    });
}

function getRegexList() {
    var serverUrl = $('#serverUrl').val();
    console.log(unixTime() +" calling getRegexList");
    $.ajax({
        type : "GET",
        url : serverUrl + "/regexExpressions",
        dataType : "json",
        data : "",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Fail: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function(data) {
            console.log(unixTime() +" Received Answer for getRegexList:" + data);
            for ( var i = 0; i < 5; i++) {
                $('#regex' + (i+1)).val(data[i]);
            }
        }
    });
}

function setRegexList() {
    var serverUrl = $('#serverUrl').val();
    console.log(unixTime() +" calling setRegexList");

    var regexes = [];
    for ( var i = 0; i < 5; i++) {
        regexes[i] =$('#regex' + (i+1)).val(); 
    }
    
    $.ajax({
        type : "PUT",
        url : serverUrl + "/regexExpressions",
        data : JSON.stringify(regexes),
        contentType : 'application/json',
        dataType : "json",
        beforeSend: function() { $.mobile.showPageLoadingMsg(); }, //Show spinner
        complete: function() { $.mobile.hidePageLoadingMsg() }, //Hide spinner
        error : function(data) {
            console.log("Fail: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function() {
            console.log(unixTime() +" Received Successful Answer for setRegexList");
//            $('#regexFilteringActive').val(stateAsBoolean);
        }
    });
}
/* ---------------------- delegates ------------------------------ */
/* pageinit */
$(document).delegate("#pageConfig", "pageinit", function() {
    currentPage = document.title;
    $( "#getRegexListBtn" ).bind( "click", function() {
        getRegexList();
      });
    $( "#setRegexListBtn" ).bind( "click", function() {
        setRegexList();
    });
    $( "#getMinAgeAutoAckBtn" ).bind( "click", function() {
        getMinAutoAckAge();
    });
    $( "#setMinAgeAutoAckBtn" ).bind( "click", function() {
        var newValue = $('#minAgeAutoAck').val();
        var intRegex = /^\d+$/;
        if(intRegex.test(newValue)) {
            setMinAutoAckAge(newValue);
        } else {
            alert('Please enter a valid positive integer!');
        }
    });
    $( "#getRegexFilteringActiveBtn" ).bind( "click", function() {
        getRegexFilteringActive();
    });
    $( "#activateRegexFilteringBtn" ).bind( "click", function() {
        setRegexFilteringState('ACTIVE');
    });
    $( "#deactivateRegexFilteringBtn" ).bind( "click", function() {
        setRegexFilteringState('INACTIVE');
    });
});
$(document).delegate("#pageConfig", "pagebeforeshow", function() {
	//prefill config page input elements
	getRegexList();
	getRegexFilteringActive();
	getMinAutoAckAge();
});
$(document).delegate("#pageOpen", "pageinit", function() {
    currentPage = document.title;
    $("#updateOpenTweetsBtn").click(function() {
        getTweetsFromServer($("#openTweetsList"), STATE_OPEN);
    });
    if (myCounter === undefined) {initializeCounter()};
});
$(document).delegate("#pageAcked", "pageinit", function() {
    currentPage = document.title;
    $("#updateAckedTweetsBtn").click(function() {
        getTweetsFromServer($("#ackedTweetsList"), STATE_ACKED);
    });
    if (myCounter === undefined) {initializeCounter()};
});
$(document).delegate("#pageBlocked", "pageinit", function() {
    currentPage = document.title;
    $("#updateBlockedTweetsBtn").click(function() {
        getTweetsFromServer($("#blockedTweetsList"), STATE_BLOCKED);
    });
    if (myCounter === undefined) {initializeCounter()};
});
/* pagebeforeshow */
$(document).delegate('#pageOpen', 'pagebeforeshow', function() {
    currentPage = PAGE_OPEN_TWEETS;
    $("#openTweetsList").hide();
    getTweetsFromServer($("#openTweetsList"), STATE_OPEN);
    myCounter.restart();

});
$(document).delegate('#pageAcked', 'pagebeforeshow', function() {
    currentPage = PAGE_ACKED_TWEETS;
    $("#ackedTweetsList").hide();
    getTweetsFromServer($("#ackedTweetsList"), STATE_ACKED);
    myCounter.restart();
});
$(document).delegate('#pageBlocked', 'pagebeforeshow', function() {
    currentPage = PAGE_BLOCKED_TWEETS;
     
    $("#blockedTweetsList").hide();
    getTweetsFromServer($("#blockedTweetsList"), STATE_BLOCKED);
    myCounter.restart();
});
/* end delegates */

function initializeCounter() {
    myCounter = new Countdown({
        seconds : TIME_INTERVAL, // number of seconds to count down
        onUpdateStatus : function(sec) {

            if (sec % 5 === 0) {
                $('.timeInterval').text(sec);
            }
        }, // callback for each second
        onCounterEnd : function() {
            console.log('counter ended ...');
            switch (currentPage)
            {
            case PAGE_OPEN_TWEETS:
                getTweetsFromServer($("#openTweetsList"), STATE_OPEN);
                break;
            case PAGE_ACKED_TWEETS:
                getTweetsFromServer($("#ackedTweetsList"), STATE_ACKED);
                break;
            case PAGE_BLOCKED_TWEETS:
                getTweetsFromServer($("#blockedTweetsList"), STATE_BLOCKED);
                break;
            }
            myCounter.restart();
        }
    });

    myCounter.start();
}

function acceptTweetOnServer(element) {
    var tweetId = element.id.toString().slice(11); // tweetItemA_xxxxxxx
    var map = {};
    map[tweetId] = STATE_ACKED + "";
    hideElement(element);
    changeAckStateOnServer(map);
}

function blockTweetOnServer(element) {
    var tweetId = element.id.toString().slice(11); // tweetItemA_xxxxxxx
    var map = {};
    map[tweetId] = STATE_BLOCKED + "";
    hideElement(element);
    changeAckStateOnServer(map);
}

function hideElement(element) {
    $(element).parent().parent().siblings().fadeOut(200);
    $(element).parent().parent().fadeOut(200);
    $(element).parent().parent().parent().slideUp(300, "linear");
}

function appendTweetsToList(data, list) {
    list.hide();
    var items = [];
    $.each(data, function(i, item) {
        var item = validateTweetItem(item);
        var listEntryHtml = constructTweetsListItem(item);
        $(list).append(listEntryHtml);
    });
    $(list).listview("refresh").trigger("create");

    list.slideDown();
}

function updateSearchResultBar(data) {
    var currentOffset = { 1 :'#ackedTweetsCurrentOffset',0:'#openTweetsCurrentOffset',2:'#blockedTweetsCurrentOffset'};
    var pageBack = {1:'#ackedTweetsPageBack',0:'#openTweetsPageBack',2:'#blockedTweetsPageBack'};
    var pageForward = {1:'#ackedTweetsPageForward',0:'#openTweetsPageForward',2:'#blockedTweetsPageForward'};
    var total = {1:'.totallyAckedTweets',0:".totallyOpenTweets",2:".totallyBlockedTweets"};
    var minTweet = {1:'.tweetAckedMin',0:'.tweetOpenMin',2:'.tweetBlockedMin'};
    var maxTweet = {1:'.tweetAckedMax',0:'.tweetOpenMax',2:'.tweetBlockedMax'};
    
    $(currentOffset[data.ackState]).val(data.offset);
    if (data.offset === 0) {
        $(pageBack[data.ackState]).hide();
    } else {
        $(pageBack[data.ackState]).show();
    }
    
    if (data.totalFoundNr >= data.offset + 25) {
        $(pageForward[data.ackState]).show();
    } else {
        $(pageForward[data.ackState]).hide();
    }

    $(total[data.ackState]).text(data.totalFoundNr);
    var min = data.offset;
    var max = min + data.tweets.length;
    $(minTweet[data.ackState]).text(min);
    $(maxTweet[data.ackState]).text(max);
}

function validateTweetItem(item) {
    item.text = (item.text !== undefined) ? item.text : " - Empty - ";
    var twitterUser = item.twitterUser;
    if (item.twitterUser == undefined) {
        item.twitterUser = new Object();
    }
    item.twitterUser.profileImageUrl = (item.twitterUser.profileImageUrl !== undefined) ? item.twitterUser.profileImageUrl
            : "";
    item.twitterUser.name = (item.twitterUser.name !== undefined) ? item.twitterUser.name
            : "Author unknown";

    return item;
}

function constructTweetsListItem(item) {
//    console.log(item.tweetId);
    var listEntryHtml;
    listEntryHtml = '<li>';
    listEntryHtml += '<img style="width: 100%;" src="'
            + item.twitterUser.profileImageUrl + '"></img>';
    listEntryHtml += '<p><strong>' + item.twitterUser.name + '</strong>  ('
            + new Date(item.createdAt)
            + ') :</p><p style="white-space: normal !important;">' + item.text
            + '</p>';
    listEntryHtml += '<div  data-role="controlgroup" class="buttonGroup" data-type="horizontal" >';
    if (currentPage === PAGE_OPEN_TWEETS || currentPage === PAGE_BLOCKED_TWEETS) {
        listEntryHtml += '<button data-role="button" data-icon="plus" data-iconpos="left" id="tweetItemA_'
                + item.tweetId
                + '" onclick="acceptTweetOnServer(this)">Accept</button>';
    }
    if (currentPage === PAGE_OPEN_TWEETS || currentPage === PAGE_ACKED_TWEETS) {
        listEntryHtml += '<button data-role="button" data-icon="delete" data-iconpos="right" id="tweetItemB_'
                + item.tweetId
                + '" onclick="blockTweetOnServer(this)">Block</button>';
    }
    listEntryHtml += '</div>';
    listEntryHtml += '</li>';

    return listEntryHtml;
}

