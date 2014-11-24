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
    var that = this;
    var req = {
      isRefresh: isRefresh,
    }

    performance.mark('mark_feed_load_begin_' + numberOfItems);

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

    var onFileSystemFailure = function (e) {
      console.log(e);

      that.async(function() {
        performance.mark('mark_legacy_save_begin');
        localStorage.setItem('posts', JSON.stringify(tmp));
        performance.mark('mark_legacy_save_end');
        performance.measure('measure_legacy_save', 'mark_legacy_save_begin',
            'mark_legacy_save_end');

        that.async(function() {
          performance.mark('mark_legacy_load_begin');
          that.loadFromJson(req, localStorage.getItem('posts'));
          performance.mark('mark_legacy_load_end');
          performance.measure('measure_legacy_load', 'mark_legacy_load_begin',
              'mark_legacy_load_end');
        });
      });
    }

    var onError = function (ev) {
      performance.mark('mark_feed_error');
      onFileSystemFailure(ev.target.error);
    }

    var onLoadEnd = function (ev) {
      performance.mark('mark_feed_read_as_text_end');
      performance.measure('measure_feed_read_as_text',
          'mark_feed_read_as_text_begin',
          'mark_feed_read_as_text_end');
      that.loadFromJson(req, ev.target.result);
    }

    var onGetFileForRead = function (f) {
      performance.mark('mark_feed_get_file_for_read_end');
      performance.measure('measure_feed_get_file_for_read',
          'mark_feed_get_file_for_read_begin',
          'mark_feed_get_file_for_read_end');
      var fr = new FileReader();
      fr.addEventListener('loadend', onLoadEnd);
      fr.addEventListener('error', onError);
      performance.mark('mark_feed_read_as_text_begin');
      fr.readAsText(f, 'utf-8');
    }

    var onGetFileEntryForRead = function (f) {
      performance.mark('mark_feed_get_entry_for_read_end');
      performance.measure('measure_feed_get_entry_for_read',
          'mark_feed_get_entry_for_read_begin',
          'mark_feed_get_entry_for_read_end');
      performance.mark('mark_feed_get_file_for_read_begin');
      f.file(onGetFileForRead, onFileSystemFailure);
    }

    var onWriteEnd = function (ev) {
      performance.mark('mark_feed_writeend');
      var w = ev.target;

      if (w.error) {
        // should have been handled by onError already
        return;
      }

      if (w.position == 0) {
        // We've done the truncate but not the write. Come back to this
        // callback when the write has finished
        performance.measure('measure_feed_truncate',
            'mark_feed_truncate_begin', 'mark_feed_writeend');
        performance.mark('mark_feed_json_stringify_begin');
        var json = JSON.stringify(tmp);
        performance.mark('measure_feed_json_stringify',
            'mark_feed_json_stringify_begin');
        performance.mark('mark_feed_write_begin');
        w.write(new Blob([json]));
        return;
      }

      performance.measure('measure_feed_write',
          'mark_feed_write_begin', 'mark_feed_writeend');

      performance.mark('mark_feed_get_entry_for_read_begin');
      that.fileSystem.root.getFile('latest.json', {}, onGetFileEntryForRead,
          onFileSystemFailure);
    }

    var onCreateWriter = function (w) {
      performance.mark('mark_feed_create_writer_end');
      performance.measure('measure_feed_create_writer',
          'mark_feed_create_writer_begin', 'mark_feed_create_writer_end');
      w.addEventListener('writeend', onWriteEnd);
      w.addEventListener('error', onError);
      performance.mark('mark_feed_truncate_begin');
      w.truncate(0);
    }

    var onGetFileEntryForWrite = function (f) {
      performance.mark('mark_feed_create_file_end');
      performance.measure('measure_feed_create_file',
          'mark_feed_create_file_begin');
      performance.mark('mark_feed_create_writer_begin');
      f.createWriter(onCreateWriter, onFileSystemFailure);
    }

    var onRequestFileSystem = function (fs) {
      if (!that.fileSystem) {
        performance.mark('mark_feed_get_file_system_end');
        performance.measure('measure_feed_get_file_system',
            'mark_feed_get_file_system_begin', 'mark_feed_get_file_system_end');
      }
      that.fileSystem = fs;
      performance.mark('mark_feed_create_file_begin');
      fs.root.getFile('latest.json', {create: true},
          onGetFileEntryForWrite, onFileSystemFailure);
    }

    if (that.fileSystem) {
      onRequestFileSystem(that.fileSystem);
      return;
    }

    // according to tradition, this ought to be enough for anybody
    var quota = 640 * 1024;
    // we are actually only storing temporary data, but we want to instruct
    // the engine to behave as though we were storing it permanently,
    // because that's the functionality we're aiming to emulate
    var mode = PERSISTENT;

    var doRequestFileSystem = function () {
      var rfs = window.requestFileSystem || window.webkitRequestFileSystem;

      if (rfs) {
        performance.mark('mark_feed_get_file_system_begin');
        rfs(mode, quota, onRequestFileSystem,
            onFileSystemFailure);
      }
      else {
        onFileSystemFailure(new Error('no webkitRequestFileSystem'));
      }
    }

    var onQuota = function (grantedBytes) {
      performance.mark('mark_feed_request_quota_end');
      performance.measure('measure_feed_request_quota',
          'mark_feed_request_quota_begin', 'mark_feed_request_quota_end');
      if (grantedBytes < quota) {
        performance.mark('mark_feed_not_enough_quota');
        console.log('only got ' + grantedBytes +
            ' bytes of quota, falling back to temporary storage');
        mode = TEMPORARY;
      }
      doRequestFileSystem();
    }

    if (navigator.webkitPersistentStorage) {
      performance.mark('mark_feed_request_quota_begin');
      navigator.webkitPersistentStorage.requestQuota(quota, onQuota,
          onFileSystemFailure);
    } else {
      mode = TEMPORARY;
      doRequestFileSystem();
    }
  },

  loadFromJson: function(req, json) {
    var tmp = JSON.parse(json);
    performance.mark('mark_feed_load_end_' + tmp.length);
    performance.measure('measure_feed_load_' + tmp.length,
        'mark_feed_load_begin_' + tmp.length,
        'mark_feed_load_end_' + tmp.length);

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
