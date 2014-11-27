// Copyright 2014 Intel Corporation. All rights reserved.
//
// License: BSD-3-clause-Intel, see LICENSE.txt

(function() {
'use strict';

var N_TABS = 3;
var MIN_DRAG = 8; // px

function passThroughDatabase(data, cb) {
  var db = null;
  var loadedData = [];

  function onerror(err) {
    performance.mark('mark_contacts_db_error');
    console.log('DB error: ' + err);
  };

  performance.mark('mark_contacts_open_begin');
  var req = indexedDB.open('contacts', 1);
  req.onupgradeneeded = function(e) {
    performance.mark('mark_contacts_onupgradeneeded_begin');
    db = e.target.result;
    e.target.transaction.onerror = onerror;
    try {
      db.deleteObjectStore('contacts');
    } catch (e) {
    }
    var store = db.createObjectStore('contacts', {
      autoIncrement: true,
    });
    performance.mark('mark_contacts_onupgradeneeded_end');
    performance.measure('measure_contacts_onupgradeneeded',
        'mark_contacts_onupgradeneeded_begin',
        'mark_contacts_onupgradeneeded_end');
  };

  req.onsuccess = function(e) {
    performance.mark('mark_contacts_open_end');
    performance.measure('measure_contacts_open',
        'mark_contacts_open_begin',
        'mark_contacts_open_end');
    db = e.target.result;
    performance.mark('mark_contacts_insert_begin');
    var txn = db.transaction(['contacts'], 'readwrite');
    var store = txn.objectStore('contacts');
    store.clear();
    for (var i = 0; i < data.length; i++) {
      store.put(data[i]);
    }
    txn.onerror = onerror;
    txn.oncomplete = function(e) {
      performance.mark('mark_contacts_insert_end');
    performance.measure('measure_contacts_insert',
        'mark_contacts_insert_begin',
        'mark_contacts_insert_end');
      performance.mark('mark_contacts_read_begin');
      var txn = db.transaction(['contacts']);
      var store = txn.objectStore('contacts');
      var readReq = store.openCursor();
      readReq.onsuccess = function(e) {
        var cursor = event.target.result;
        if (cursor) {
          loadedData.push(cursor.value);
          cursor.continue();
        }
        else {
          performance.mark('mark_contacts_read_end');
          performance.measure('measure_contacts_read',
              'mark_contacts_read_begin',
              'mark_contacts_read_end');
          performance.mark('mark_contacts_copy_begin');
          var favorites = [];
          for (var i = 0; i < loadedData.length; i++) {
            if (loadedData[i].favorite) {
              favorites.push(loadedData[i]);
            }
          }
          performance.mark('mark_contacts_copy_end');
          performance.measure('measure_contacts_copy',
              'mark_contacts_copy_begin',
              'mark_contacts_copy_end');
          cb(data, favorites);
        }
      };
      readReq.onerror = onerror;
    };
  };
  req.onerror = onerror;
}

Polymer({
  publish: {
    searching: {
      default: false,
      reflect: true,
    },
  },

  toggleMenu: function() {
    this.fire('toggle-menu');
  },

  data: [],
  favorites: [],
  filteredData: [],
  filteredFavorites: [],

  created: function () {
    performance.mark('mark_contacts_list_view_created');
  },

  ready: function() {
    performance.mark('mark_contacts_list_view_ready');

    var myTabs = this.$['my-tabs'];
  },

  setData: function(data, favorites) {
    this.data = data;
    this.favorites = favorites;
    this.updateSearch();
  },

  attached: function () {
    performance.mark('mark_contacts_list_view_attached');
  },

  itemActivated: function(e) {
    this.fire('item-activated', e.target.model);
  },

  toggleSearch: function() {
    if (this.searching) {
      this.searching = false;
      this.clearSearch();
    } else {
      this.searching = true;
      this.async(function() {
        this.$.searchInput.focus();
      });
    }
  },

  clearSearch: function() {
    this.$.searchInput.inputValue = this.$.searchInput.value = '';
    this.$.searchInput.commit();
    this.updateSearch();
  },

  updateSearch: function() {
    var key = this.$.searchInput.inputValue;
    if (this.searching && key) {
      var filteredData = [];
      var filteredFavorites = [];

      for (var i = 0; i < this.data.length; i++) {
        var c = this.data[i];

        if (c.name.toLocaleLowerCase().indexOf(key.toLocaleLowerCase()) > -1) {
          filteredData.push(c);

          if (c.favorite)
            filteredFavorites.push(c);
        }
      }

      this.filteredData = filteredData;
      this.filteredFavorites = filteredFavorites;
    } else {
      this.filteredData = this.data;
      this.filteredFavorites = this.favorites;
    }

    this.$.allList.refresh();
    this.$.favoritesList.refresh();
  },

  favoriteChanged: function (e) {
    var model = e.detail.model;
    var i;

    if (model.favorite) {
      i = this.favorites.indexOf(model);
      if (i >= 0) {
        this.favorites.splice(i, 1);
      }
    } else {
      // re-filter the favorites to get them in the correct order
      var favorites = [];
      for (i = 0; i < this.data.length; i++) {
        var data_i = this.data[i];
        if (data_i.favorite) {
          favorites.push(data_i);
        }
      }
      this.favorites = favorites;
    }

    if (this.filteredFavorites !== this.favorites)
      this.updateSearch();
  },

  // Based on app-dismissable-item
  enteredView: function () {
    var that = this;
    var tabsContainer = this.$.tabsContainer;
    var dragOffset = 0;

    this.controller = new DismissController({
      target: this.$.tabsViewport,
      curve: 'ease-in-out',

      onStart: function() {
      },

      onMove: function(newPos) {
        that.moveTabs(newPos, true);
      },

      onCanceled: function() {
        that.moveTabs(0, false);
      },

      onDismiss: function(direction) {
        var sel = that.$.tabSelector.selected;

        if (direction == 'right') {
          sel -= 1;
        }
        if (direction == 'left') {
          sel += 1;
        }
        if (!sel || sel <= 0)
          sel = 0;
        if (sel >= N_TABS - 1)
          sel = N_TABS - 1;

        that.changingTab = true;
        that.$.tabSelector.selected = sel;
        that.moveTabs(0, false);
        that.changingTab = false;
      },

    });

    this.moveTabs(0, false);
  },

  moveTabs: function (offset, isMoving) {
    // force numeric
    var offs = offset || 0;

    if (this.$.tabSelector.selected == 0 && offs > 0)
      offs = 0;

    if (this.$.tabSelector.selected == 2 && offs < 0)
      offs = 0;

    var position = offs - (this.$.tabSelector.selected * this.$.tabsViewport.offsetWidth);

    if (isMoving && (offs > MIN_DRAG || offs < -MIN_DRAG)) {
      this.$.tabsClickBarrier.hidden = false;
    } else if (!isMoving) {
      this.$.tabsClickBarrier.hidden = true;
    }

    this.$.tabsContainer.style.transform = 'translate3d(' + position + 'px,0,0)';
  },

  changingTab: false,

  tabSelectorChanged: function () {
    if (this.changingTab)
      return;

    this.changingTab = true;
    this.$.tabsContainer.classList.toggle('tabbing', true);
    this.moveTabs(0, false);
    this.async(function () {
      this.$.tabsContainer.classList.toggle('tabbing', false);
    }.bind(this), null, 200);
    this.changingTab = false;
  },
});

})();

// vim:set sw=2 sts=2 et:
