function Colors() {
  var colors = ["#0C85C4", "#269650", "#955E3A", "#3C3C22", "#B2AD1B", "#F0FA1E", "#FA321E", "#7E07A9", "#95DFF3", "#EC7AD0", "#26FF00", "#FEE766"];
  var nextColorIndex = 0;

  return {
    next: function() {
      if (nextColorIndex >= colors.length) nextColorIndex = 0;
      nextColorIndex++;

      return colors[nextColorIndex - 1];
    }
  }
};

function Schedule(startDate, numberOfTimelinesToDraw) {
  var firstDay = startDate.getTime();

  var leftMargin = 25;
  var numberOfWeeksPerTimeline = 20;
  var widthOfEachWeek = 60;
  var heigthOfEachTimeline = 200;

  var MILLIS_PER_DAY = 86400 * 1000;
  var MILLIS_PER_TIMELINE = (MILLIS_PER_DAY * 7 * numberOfWeeksPerTimeline);

  var colors = new Colors();

  var paper = Raphael(0, 40, leftMargin + (numberOfWeeksPerTimeline * widthOfEachWeek) + 100, numberOfTimelinesToDraw * heigthOfEachTimeline + 300);



  var previouslySeenIdentifier = "";
  var stepNumber = 0;
  var legendIndex = 0;

  var textStartingAt = function(startX, startY, text) {
    var text = paper.text(startX, startY, text);
    var textBox = text.getBBox(1);
    return text.translate((textBox.width / 2), 0);
  };

  var drawTimeline = function(weekOffset, yOffset) {
    var xEndOfTimeline = leftMargin + (numberOfWeeksPerTimeline * widthOfEachWeek) - widthOfEachWeek;
    paper.path("M " + leftMargin + " " + yOffset + " H " + xEndOfTimeline);

    for(var i = 0; i < numberOfWeeksPerTimeline; i++) {
      var xPosition = (leftMargin + (i * widthOfEachWeek));
      var yPosition = yOffset - 10;

      var weekNotch = paper.path("M " + xPosition + " " + yPosition + " V " + (yPosition + 20));
      var week = new Date(firstDay + (86400000 * 7 * (i + weekOffset)));

      var text = textStartingAt(xPosition, yPosition - 20, week.toDateString());
      var textBox = text.getBBox(1);
      text.rotate(-30, textBox.x, textBox.y);

      var weeksAfterLMP = paper.text(xPosition, yPosition + 30, i + weekOffset + 1);
    };
  };

  var messagePosition = function(date) {
    var offsetDaysFromStartOfTimeline = ((date - firstDay) % MILLIS_PER_TIMELINE) / MILLIS_PER_DAY;

    var timelineForThisDate = Math.floor((date - firstDay) / MILLIS_PER_TIMELINE);
    var yOffsetBasedOnTimeline = ((heigthOfEachTimeline / 2) * (2 * timelineForThisDate + 1)) + 40;

    var xOffsetPosition = leftMargin + (offsetDaysFromStartOfTimeline * widthOfEachWeek / 7.0);

    return {x: xOffsetPosition, y: yOffsetBasedOnTimeline};
  };

  var addMessage = function(date, color) {
    var position = messagePosition(date);
    paper.circle(position.x, position.y + ((stepNumber - 1) * 14), 7).attr("fill", color).attr("stroke", "#fff");
  };


  var addToLegend = function(color, legendText) {
    var legendStartPosition = (numberOfTimelinesToDraw * heigthOfEachTimeline) + legendIndex * 20;
    legendIndex++;

    var circle = paper.circle(leftMargin, legendStartPosition, 7).attr("fill", color).attr("stroke", "#fff");
    textStartingAt(leftMargin + circle.getBBox(1).width, legendStartPosition, legendText);
  };

  var updateStepNumber = function(identifier) {
    if (identifier != previouslySeenIdentifier) {
      stepNumber++;
      previouslySeenIdentifier = identifier;
    }
  };


  var addMessages = function(identifier, legendText, dates) {
      updateStepNumber(identifier);

      var color = colors.next();
      addToLegend(color, legendText);
      for (var dateIndex in dates) {
        addMessage(dates[dateIndex], color);
      }
  };

  return {

    clear: function() {
        paper.clear();
    },

    draw: function(campaign) {
      for (var i = 0; i < numberOfTimelinesToDraw; i++) {
        drawTimeline(20 * i, (heigthOfEachTimeline / 2) * ((2 * i) + 1));
      }

      for(var schIndex=0; schIndex < campaign.schedules.length; schIndex++) {
        var schedule = campaign.schedules[schIndex];

        var dates = new Array();
        for(var dtIndex=0; dtIndex < schedule.messages.length; dtIndex++) {
          dates[dtIndex] = new Date(eval(schedule.messages[dtIndex]));
        }

        addMessages(schedule.mid, schedule.mid, dates);
      }

      return this;
    }
  };
};