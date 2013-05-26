const STATE_ACKED = 1;
const TIME_INTERVAL = 25;

var serverUrl = "http://192.168.1.52:9998/twitter";

var knownTweets = {};

$(function() {

    console.log("getting tweets from server");
//    getTweetsFromServer($("#tweetsList"), STATE_ACKED);
    initializeCounter();
});

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
//        console.log("Restarting Counter ...");
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

function initializeCounter() {
    myCounter = new Countdown({
        seconds : TIME_INTERVAL, // number of seconds to count down
        onUpdateStatus : function(sec) {

//            if (sec % 5 === 0) {
//                $('.timeInterval').text(sec);
//            }
        }, // callback for each second
        onCounterEnd : function() {
//            console.log('counter ended ...');
            getTweetsFromServer($("#tweetsList"), STATE_ACKED);
            myCounter.restart();
        }
    });

    myCounter.start();
}




function getTweetsFromServer(list, state) {
    $.ajax({
        type : "GET",
        url : serverUrl + "/tweets",
        dataType : "json",
        data : "ackState=" + state,

        error : function(data) {
            console.log("Failed getting tweets: " + data);
        },
        success : function(data) {
            var receivedTweets = {};
            $.each(data, function(i, item) {
                    receivedTweets[item.tweetId] = item;
            });

            
            var newTweets = extractNewTweets(receivedTweets);
            var removedTweets = extractRemovedTweets(receivedTweets);
            
            //hideRemovedItems
              displayNewTweets(newTweets,list);
              hideRemovedTweets(removedTweets,list);
            knownTweets = {};
            $.each(data, function(i, item) {
                knownTweets[item.tweetId] = item;
        });

            
        }
    });
}

function extractRemovedTweets(receivedTweets) {
    var removedTweets = {};
    
    $.each(knownTweets, function(key, item) {
        console.log("checking if " + key + " was removed");
        if (!(key in receivedTweets)) {
            console.log("Found removed tweet: " + item.tweetId);
            removedTweets[key] = item;
        }
        if (key in receivedTweets) {
            console.log("found " + key  + " in ReceivedTweets ..." );
        }
    });

    return removedTweets;
}

function extractNewTweets(receivedTweets) {
    var newTweets = {};
    
    $.each(receivedTweets, function(key, item) {
        var item;
        // TODO if isInvalid item continue; validateTweetItem(item);
        if (!(key in knownTweets)) {
            console.log("Found new tweet: " + item.tweetId);
            newTweets[item.tweetId] = item;
        }
    });

    return newTweets;
}

function hideRemovedTweets(removedTweets,list) {
    $.each(removedTweets, function(key, item) {
        $('#'+ key).hide();
    });
}

function displayNewTweets(newTweets, list) {
    var items = [];
    $.each(newTweets, function(i, item) {
        var item = validateTweetItem(item);
        var listEntryHtml = constructTweetsListItem(item);
        $(list).prepend(listEntryHtml);
    });
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
    listEntryHtml = '<li id="' + item.tweetId + '">';
    listEntryHtml += '<img src="'
            + item.twitterUser.profileImageUrl + '"></img>';
    listEntryHtml += '<p><strong>' + item.twitterUser.name + '</strong>  ('
            + new Date(item.createdAt)
            + ') :</p><p style="white-space: normal !important;">' + item.text
            + '</p>';
    listEntryHtml += '<hr /></li>';

    return listEntryHtml;
}



