// Copyright 2013 The Polymer Authors. All rights reserved.
// Copyright 2014 Intel Corporation. All rights reserved.
// License: BSD-3-clause-Google and BSD-3-clause-Intel, see LICENSE.txt

(function(exports) {
'use strict';

function repeat(s, n) {
  return Array(n + 1).join(s);
}

function getRandomItem(array) {
  return array[Math.floor(Math.random() * array.length)];
}

// popular names in England, from
// https://en.wikipedia.org/wiki/List_of_most_popular_given_names
var possibleParticipants = [
  'Amelia',
  'Olivia',
  'Emily',
  'Jessica',
  'Ava',
  'Isla',
  'Poppy',
  'Isabella',
  'Sophie',
  'Mia',
  'Oliver',
  'Jack',
  'Harry',
  'Jacob',
  'Charlie',
  'Thomas',
  'Oscar',
  'William',
  'James',
  'George',
];

var emoticons = [
  '^_^',
  'o_O',
  '\\o/',
  '/o\\',
  ':)',
  ':(',
  ':)',
  ':(',
  ':/',
  ':D',
  '&lt;3',
];

var possibleAvatars = [
  'bench.jpg',
  'cactus.jpg',
  'helicopter.jpg',
  'hot-chocolate.jpg',
  'leucadendron-plumosum.jpg',
  'lighthouse.jpg',
  'pencils.jpg',
  'ram.jpg',
  'roses.jpg',
  'ship.jpg',
  'strawberries.jpg',
  'usb.jpg',
];

// The "700" colours from
// http://www.google.com/design/spec/style/color.html
// each followed by a suitable text colour
var possibleAvatarColors = [
  { 'background-color': '#d01716', color: 'white'},
  { 'background-color': '#c2185b', color: 'white'},
  { 'background-color': '#7b1fa2', color: 'white'},
  { 'background-color': '#512da8', color: 'white'},
  { 'background-color': '#303f9f', color: 'white'},
  { 'background-color': '#455ede', color: 'white'},
  { 'background-color': '#0288d1', color: 'white'},
  { 'background-color': '#0097a7', color: 'white'},
  { 'background-color': '#00796b', color: 'white'},
  { 'background-color': '#0a7e07', color: 'white'},
  { 'background-color': '#689f38', color: 'black'},
  { 'background-color': '#afb42b', color: 'black'},
  { 'background-color': '#fbc02d', color: 'black'},
  { 'background-color': '#ffa000', color: 'black'},
  { 'background-color': '#f57c00', color: 'black'},
  { 'background-color': '#e64a19', color: 'white'},
  { 'background-color': '#5d4037', color: 'white'},
  { 'background-color': '#616161', color: 'white'},
  { 'background-color': '#455a64', color: 'white'},
];

var people = [];

var j = 0;
for (var i = 0; i < possibleParticipants.length; i++) {
  people[i] = {
    name: possibleParticipants[i],
    login: '@' + possibleParticipants[i].toLowerCase(),
    avatarColor: getRandomItem(possibleAvatarColors),
  };
  if (Math.random() < 0.5) {
    people[i].avatar = possibleAvatars[j];
    j = (j + 1) % possibleAvatars.length;
  }
}

function generateFakeItem() {
  // Used to generate gibberish. Deliberately omit the vowels
  // to minimize the chance that we generate something offensive.
  var possibleLetters = 'qwrtyzxcvbnmsdfghjkl';
  var s = '';
  var nWords = Math.floor(Math.random() * 10) + 3;
  var i;

  for (i = 0; i < nWords; i++) {
    if (Math.random() < 0.1) {
      s = s + ' <a href="javascript:console.log(&quot;link clicked&quot;)">@';
      s = s + getRandomItem(possibleParticipants).toLowerCase();
      s = s + '</a>';
    }
    else if (Math.random() < 0.05) {
      s = s + ' ' + getRandomItem(emoticons);
    }
    else {
      var nLetters = Math.floor(Math.random() * 10) + 1;
      var j;
      s = s + ' ';
      for (j = 0; j < nLetters; j++) {
        s = s + getRandomItem(possibleLetters);
      }
    }
  }

  nWords = Math.floor(Math.random() * 3);

  for (i = 0; i < nWords; i++) {
    var nLetters = Math.floor(Math.random() * 10) + 3;
    var j;
    s = s + ' <a href="javascript:console.log(&quot;link clicked&quot;)">#';
    for (j = 0; j < nLetters; j++) {
      s = s + getRandomItem(possibleLetters);
    }
    s = s + '</a>';
  }

  // starts with a space
  return s.substring(1);
}

// Don't use arbitrarily much RAM. The official Twitter app's "infinite
// scroll" isn't infinite either
var MAX_ITEMS = 1000;

var INITIAL_ITEMS_BATCH = 50;
var EXTRA_ITEMS_BATCH = 20;
var REFRESH_BATCH = 5;

Polymer('my-feed', {
  startedInitialLoad: false,
  lastDismissed: null,
  created: function () {
    this.data = [];
  },

  loadMore: function () {
    this.addFakeListData(EXTRA_ITEMS_BATCH);
  },

  refresh: function () {
    this.addFakeListData(
        this.startedInitialLoad ? REFRESH_BATCH : INITIAL_ITEMS_BATCH,
        true);
    this.startedInitialLoad = true;
  },

  addFakeListData: function(numberOfItems, isRefresh) {
    var tmp = [];
    for (var i = 0; i < numberOfItems; ++i) {
      var who = getRandomItem(people);
      tmp[i] = {
        id: i,
        avatarColors: who.avatarColor,
        avatarLetter: who.name[0].toUpperCase(),
        avatar: who.avatar,
        who: who.name,
        login: who.login,
        // one tweet per 42 seconds
        when: new Date(Date.now() - (i * 42000)),
        html: generateFakeItem(),
      };
    }

    var req = {
      timeBefore: +(new Date()),
      isRefresh: isRefresh,
    }

    console.log('async-saving JSON...');
    this.async(function() {
      console.log('saving JSON...');
      localStorage.setItem('posts', JSON.stringify(tmp));

      console.log('async-loading JSON...');
      this.async(function() {
        console.log('loading JSON...');
        this.loadFromJson(req, localStorage.getItem('posts'));
      });
    });
  },

  loadFromJson: function(req, json) {
    var tmp = JSON.parse(json);

    var dt = +(new Date()) - req.timeBefore;
    console.log('serialized and parsed ' + json.length + ' JSON records in ' + dt + ' ms');

    for (var i = 0; i < tmp.length; i++) {
      if (this.data.length > MAX_ITEMS)
        break;

      if (req.isRefresh)
        this.data.unshift(tmp[tmp.length - i - 1]);
      else
        this.data.push(tmp[i]);
    }
  },
});

})(window);
// vim:set sw=2 sts=2 et: