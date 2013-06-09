const STATE_ACKED = 1;
const TIME_INTERVAL = 15;

var serverUrl = "http://localhost:9998/twitter";
var knownTweets = {};
var forceDisplayTweets = false;

var minimumTweetIdToRequestFromServer=1;       //used for the server as minimum age for the tweets

$(function() {
    console.log("getting tweets from server");
    getTweetsFromServer($("#tweetsList"), STATE_ACKED);
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
        }, // callback for each second
        onCounterEnd : function() {
//            console.log('counter ended ...');
            getTweetsFromServer($("#tweetsList"), STATE_ACKED);
            myCounter.restart();
        }
    });

    myCounter.start();
}


function getTweetStatesFromServer(tweets) {
  var tweetIdList = [];
  $.each(tweets, function(i, item) {
      tweetIdList.push(item.tweetId);
  });
  var stringified = JSON.stringify({"tweetIds": tweetIdList });

    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: serverUrl + "/tweetStates",
        dataType: "json",
        data: stringified,
        
        success: function(data, textStatus, jqXHR){
//          console.log("answer:" + Object.keys(data).length );
          $.each(data, function(key, ackState) {
              if (ackState == 2) {
                  console.log("Found recently blocked Tweet: " + key + "! Removing it from wall ...");
                  delete knownTweets[key];
                  forceDisplayTweets = true;
              }
          });

        },
        error: function(jqXHR, textStatus, errorThrown){
            console.log("error getting TweetStates: " + textStatus);
        }
    });
}

function getTweetsFromServer(list, state) {
    $.ajax({
        type : "GET",
        url : serverUrl + "/tweets",
        dataType : "json",
        data : {ackState: state},

        error : function(data) {
            console.log("Failed getting tweets: " + data);
        },
        success : function(data) {
            console.log("Received " + Object.keys(data.tweets).length + " open tweets from server");
            var receivedTweets = {};

            $.each(data.tweets, function(i, item) {
                    receivedTweets[item.tweetId] = item;
//                    console.log("received: " + item.tweetId + "," + item.ackState);
            });
            
           
            console.log("check new tweets");
            var newTweets = extractNewTweets(receivedTweets);

            // dont do anything if no new tweets 
            if (Object.keys(newTweets).length == 0 && !forceDisplayTweets && !checkOutdatedTweets(receivedTweets)  ) {       //&& !checkOutdatedTweets(receivedTweets)
                return;
            }
            console.log("ForceDisplay: " + forceDisplayTweets);
            forceDisplayTweets = false;
            
            var newTweetsSortedList = getAsDescendingSortedList(newTweets);
            console.log("new tweets found");
            
            //fill with old tweets if necessessary
            var nrOfOldFillUp = (10 - newTweetsSortedList.length);
            var fillUpList = [];
            if ( nrOfOldFillUp > 0 ) {
                var fillUpCompleteList = getAsDescendingSortedList(knownTweets);
                
                if (fillUpCompleteList.length > nrOfOldFillUp) {
                    fillUpList = fillUpCompleteList.slice(0, (nrOfOldFillUp-1));
                } else {
                    fillUpList = fillUpCompleteList.slice(0);
                }
            }
            console.log("Filling Up newTweets with "+  fillUpList.length );
            newTweetsSortedList = newTweetsSortedList.concat(fillUpList);
            newTweetsSortedList.sort(function(a,b){return b.tweetId-a.tweetId});
            console.log("Size of sortedTweetsList for display: " + newTweetsSortedList.length);
            
            //refresh display of twitterwall
            var c1Id = $('.c1 .tweetId').val();
            var c2Id = $('.c2 .tweetId').val();
            var c3Id = $('.c3 .tweetId').val();
            var c4Id = $('.c4 .tweetId').val();
            var c5Id = $('.c5 .tweetId').val();
            var c6Id = $('.c6 .tweetId').val();
            var c7Id = $('.c7 .tweetId').val();
            var c8Id = $('.c8 .tweetId').val();
            var c9Id = $('.c9 .tweetId').val();
            
            if (!(
                    (c1Id==newTweetsSortedList[0].tweetId) && (c2Id==newTweetsSortedList[1].tweetId) && (c3Id==newTweetsSortedList[2].tweetId) &&
                    (c4Id==newTweetsSortedList[3].tweetId) && (c5Id==newTweetsSortedList[4].tweetId) && (c6Id==newTweetsSortedList[5].tweetId) &&
                    (c7Id==newTweetsSortedList[6].tweetId) && (c8Id==newTweetsSortedList[7].tweetId) && (c9Id==newTweetsSortedList[8].tweetId)
            )) {
                displayTweets(newTweetsSortedList)
            }
            
            //add valid received Tweets to known tweets
            $.extend(knownTweets, receivedTweets);
            
            //check most current tweets if they are still ACKED! -> May be they need to be removed from the wall!
            getTweetStatesFromServer(newTweetsSortedList);

        }
    });
}

function getAsDescendingSortedList(newTweets) {
    var newTweetIDsSortedArray = [];
    var newTweetsSortedArray = [];

    $.each(newTweets, function(key, item) {
        newTweetIDsSortedArray.push(key);
    });
    newTweetIDsSortedArray.sort(function(a,b){return b-a});
    for (var i = 0; i < newTweetIDsSortedArray.length; i++) {
        var id=newTweetIDsSortedArray[i];
        newTweetsSortedArray.push(newTweets[id])
//        console.log("t: " + newTweets[id]);
    }
    
    return newTweetsSortedArray;
}

function extractNewTweets(receivedTweets) {
    var newTweets = {};
    
    $.each(receivedTweets, function(key, item) {
        if (!isValid(item)) {
            return;
        }
        if (!(key in knownTweets)) {
//            console.log("Found new tweet: " + item.tweetId);
            newTweets[item.tweetId] = item;
        }
    });
    console.log("extracted " + Object.keys(newTweets).length + " new Tweets");
    
    return newTweets;
}

function checkOutdatedTweets(receivedTweets) {
    var ntSorted = getAsDescendingSortedList(knownTweets);
    var existingOutdatedTweets = false;
    if (ntSorted.length > 20) {
         console.log("Checking for old tweets");
         for (var i = 0; i<= 21; i++) {
             if (!(ntSorted[i].tweetId in receivedTweets)) {
                 console.log("BLOCKED OR OUTDATED TWEET: " + ntSorted[i].twitterUser.name);
                 existingOutdatedTweets = true;
             }
         }
    }
    return existingOutdatedTweets;
}

function isValid(item) {
    var result = false
    var validText = (item.text !== undefined); 
    var validUser = (item.twitterUser !== undefined);
    var validImage = (item.twitterUser.profileImageUrl !== undefined);
    var validName = (item.twitterUser.name !== undefined);

    return (validText && validUser && validImage && validName);
}

function displayTweets(tweets, list) {
    var items = [];
    var htmlTweets = [];
    $.each(tweets, function(i, item) {
        //TODO Validierung woanders
        var item = validateTweetItem(item);
        var listEntryHtml = constructTweetsListItem(item);
        htmlTweets.push(listEntryHtml);
    });
    
    //hideAllTableCells
    $("td.c div").each(function(){
        $(this).remove();
    });
    
    //replaceTableCells
    $.each(htmlTweets, function(i, item) {
        if (i > 8) return;
        var cellClass = ".c" + (i+1);
        $("td" + cellClass).html(item);
//        console.log($("td" + cellClass) + item);
    });
    
    //Show Table Cells
    $("td.c div").each(function(){
        $(this).fadeIn(3000);
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
//    console.log(item.tweetId);
    var tweetHtml= "";
    tweetHtml += '<div style="display:none;">';
    tweetHtml += '<input type="hidden" value="' + item.tweetId + '" class="tweetId" />';
    tweetHtml += '<img src="' + item.twitterUser.profileImageUrl + '"></img>';
    tweetHtml += '<p><strong>' + item.twitterUser.name + '</strong> '
  + formattedDate(new Date(item.createdAt))
  + ' </p><p style="white-space: normal !important;">' + item.text
  + '</p>';
    tweetHtml += '</div>';
    
    return tweetHtml;
}

var m_names = new Array("Januar", "Februar", "M&auml;rz", 
        "April", "Mai", "Juni", "Juli", "August", "September", 
        "Oktober", "November", "Dezember");

function formattedDate(d) {
    var curr_date = d.getDate();
    var curr_month = d.getMonth();
    var curr_year = d.getFullYear();
    return (d.getHours() +  ":" + d.getSeconds() + " Uhr, " + curr_date + ". " + m_names[curr_month] + " " + curr_year);
}




