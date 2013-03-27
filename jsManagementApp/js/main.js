const STATE_OPEN = 0;
const STATE_ACKED = 1;
const STATE_BLOCKED = 2;

const PAGE_OPEN_TWEETS = 'Open Tweets';
const PAGE_ACKED_TWEETS = 'Acked Tweets';
const PAGE_BLOCKED_TWEETS = 'Blocked Tweets';

const TIME_INTERVAL = 40;

var currentPage;
var myCounter;

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

/* ---------------------- delegates ------------------------------ */
/* pageinit */
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
        $(list).append(listEntryHtml).listview("refresh").trigger("create");
    });

    list.slideDown();
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
    console.log(item.tweetId);
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

/* ------------ SERVER Calls ------------------------------------------------*/
function changeAckStateOnServer(data) {
    var serverUrl = $('#serverUrl').val();
    $.ajax({
        type : "PUT",
        url : serverUrl + "/ack",

        data : JSON.stringify(data),
        contentType : 'application/json',
        dataType : "json",

        error : function(data) {
            console.log("Failed changing the ack state!");
            alert("Failed changing AckState!")
        },
        success : function(data) {
            console.log("Success changing the ack state!");
        }
    });
}

function getTweetsFromServer(list, state) {
    var serverUrl = $('#serverUrl').val();
    list.empty();
    $.ajax({
        type : "GET",
        url : serverUrl + "/tweets",
        dataType : "json",
        data : "ackState=" + state,

        error : function(data) {
            alert("Fail: " + data.text);
            var list = data == null ? [] : (data instanceof Array ? data
                    : [ data ]);
        },
        success : function(data) {
            appendTweetsToList(data, list);
        }
    });

}
