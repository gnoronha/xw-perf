// Copyright 2014 Intel Corporation. All rights reserved.
//
// License: BSD-3-clause-Intel, see LICENSE.txt

(function() {
'use strict';

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
  toggleMenu: function() {
    this.$['my-drawer-panel'].togglePanel();
  },
  toggleAlwaysAnimate: function() {
    window.MyPerfBoxGlobal.alwaysAnimate = this.$.alwaysAnimateCheck.checked;
  },
  data: [],
  favorites: [],
  ready: function() {
    var data = this.$.data.data;

    // Ideally we'd make the core-list be directly backed by the
    // database rather than copying, but core-list wants to know
    // the item count, and perform bidirection mapping between
    // dense 0-based index and raw data.
    // TODO: future work: fork core-list as my-db-list?
    window.performance.mark('mark_before_db_access');
    passThroughDatabase(data, (function (data, favorites) {
      window.performance.mark('mark_after_db_access');
      window.performance.measure('measure_db_access',
          'mark_before_db_access', 'mark_after_db_access');
      this.data = data;
      this.favorites = favorites;
      this.$.allList.refresh();
      this.$.favoritesList.refresh();
    }).bind(this));

    this.$.favoritesList.scrollTarget = this.$['my-header-panel'];
    this.$.allList.scrollTarget = this.$['my-header-panel'];

    var myTabs = this.$['my-tabs'];
    var myPages = this.$['my-pages'];
    myTabs.addEventListener('core-select', function() {
      myPages.selected = myTabs.selected;
    });
  },
  itemActivated: function(e) {
    this.$.card.model = e.detail.item.model;
    this.$.modePages.selected = 1;
  },
  backToLists: function(e) {
    this.$.modePages.selected = 0;
  },
  settingsActivated: function(e) {
  },

  goToPerf: function() {
    window.document.getElementById('perfBox').openPopup();
  },
});

})();

// vim:set sw=2 sts=2 et:
