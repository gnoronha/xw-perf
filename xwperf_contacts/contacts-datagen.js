// Copyright 2013 The Polymer Authors. All rights reserved.
// Copyright 2014 Intel Corporation. All rights reserved.
//
// License: BSD-3-clause-Google and BSD-3-clause-Intel, see LICENSE.txt

    (function(exports) {
    'use strict';

    function repeat(s, n) {
      return Array(n + 1).join(s);
    }

    function getRandomItem(array) {
      return array[Math.floor(Math.random() * array.length)];
    }

    // from https://en.wikipedia.org/wiki/List_of_most_popular_given_names
    var forenames = [
      'Abigail',
      'Alexander',
      'Alfie',
      'Amelia',
      'Ava',
      'Charlie',
      'Daniel',
      'Dylan',
      'Elizabeth',
      'Ella',
      'Emily',
      'Emma',
      'Ethan',
      'George',
      'Harry',
      'Isabella',
      'Isla',
      'Jack',
      'Jacob',
      'James',
      'Jayden',
      'Jessica',
      'Lewis',
      'Liam',
      'Logan',
      'Lucas',
      'Lucy',
      'Madison',
      'Mason',
      'Mia',
      'Michael',
      'Millie',
      'Noah',
      'Oliver',
      'Olivia',
      'Oscar',
      'Poppy',
      'Riley',
      'Ruby',
      'Sophia',
      'Sophie',
      'Thomas',
      'William',
    ];

    // https://en.wikipedia.org/wiki/List_of_the_most_common_surnames_in_Europe#United_Kingdom
    var surnames = [
      'Alexander',
      'Ali',
      'Anderson',
      'Brown',
      'Campbell',
      'Clark',
      'Clarke',
      'Cox',
      'Davies',
      'Davis',
      'Doherty',
      'Driscoll',
      'Edwards',
      'Evans',
      'Graham',
      'Green',
      'Griffiths',
      'Hall',
      'Hamilton',
      'Hughes',
      'Jackson',
      'James',
      'Jenkins',
      'Johnson',
      'Johnston',
      'Jones',
      'Kelly',
      'Khan',
      'Lewis',
      'MacDonald',
      'Martin',
      'Mason',
      'McLaughlin',
      'Mitchell',
      'Moore',
      'Morgan',
      'Morrison',
      'Moss',
      'Murphy',
      'Murray',
      'Owen',
      'O\’Neill',
      'Patel',
      'Paterson',
      'Phillips',
      'Price',
      'Quinn',
      'Rees',
      'Reid',
      'Roberts',
      'Robertson',
      'Robinson',
      'Rodríguez',
      'Rose',
      'Ross',
      'Sanders',
      'Scott',
      'Smith',
      'Smyth',
      'Stewart',
      'Taylor',
      'Thomas',
      'Thompson',
      'Thomson',
      'Walker',
      'Watson',
      'White',
      'Williams',
      'Wilson',
      'Wood',
      'Wright',
      'Young',
    ];

    var domains = [
      'example.com',
      'example.net',
      'example.org',
    ];

    var emailPrefixes = [
      '',
      'me.',
      'mail.',
      'email.',
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

    Polymer({
      created: function () {
        this.data = [];

        for (var i = 0; i < 500; i++) {
          var forename = getRandomItem(forenames);
          var surname = getRandomItem(surnames);

          var email;
          if (Math.random() < 0.5) {
            email = forename.toLowerCase() + '@' + getRandomItem(emailPrefixes) + getRandomItem(domains);
          } else {
            email = forename.toLowerCase() + '.' + surname.toLowerCase().replace('\'', '').replace('í', 'i') + '@' + getRandomItem(emailPrefixes) + getRandomItem(domains);
          }

          var avatar = null;
          if (Math.random() < 0.5) {
            avatar = getRandomItem(possibleAvatars);
          }

          // https://en.wikipedia.org/wiki/Fictitious_telephone_number
          var phone = '+44 7700 900' + Math.floor(Math.random() * 999);

          this.data.push({
            name: forename + ' ' + surname,
            email: email,
            avatar: avatar,
            color: getRandomItem(possibleAvatarColors),
            phone: phone,
            favorite: (i < 10),
          });
          this.data.sort(function (a, b) {
            if (a.name > b.name)
              return 1;
            if (a.name < b.name)
              return -1;
            return 0;
          });
        }
      },
    });

    })(window);

// vim:set sw=2 sts=2 et:
