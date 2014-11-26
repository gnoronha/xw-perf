all:

-include android-paths.mk

XW_APPS = \
	xwperf_contacts \
	xwperf_social \
	$(NULL)

XW_DEMOS = \
	xwperf_app_widgets \
	xwperf_transition_grid \
	xwperf_transition_list \
	xwperf_transition_music \
	xwperf_transition_nested \
	xwperf_transition_news \
	xwperf_transition_quiz1 \
	xwperf_transition_simple \
	$(NULL)

ANT_APPS = \
	notxw_hello_world \
	notxw_list \
	notxw_starter \
	$(NULL)

GRADLE_APPS = \
	notxw_social \
	$(NULL)

# We produce standalone .apks for the apps, but only shim .apks for the
# demos.
XW_APKS = \
	$(patsubst %,dist/%.apk,$(XW_APPS)) \
	$(patsubst %,dist/%_arm.apk,$(XW_APPS)) \
	$(patsubst %,dist/%_x86.apk,$(XW_APPS)) \
	$(patsubst %,dist/%.apk,$(XW_DEMOS)) \
	$(NULL)
ANT_APKS = $(patsubst %,dist/%.apk,$(ANT_APPS))
GRADLE_APKS = $(patsubst %,dist/%.apk,$(GRADLE_APPS))

all: xw java
.PHONY: all
xw: $(XW_APKS)
.PHONY: xw
java: $(ANT_APKS) $(GRADLE_APKS)
.PHONY: java

# no real dependency tracking yet so use "always"
tmp-%/vulcanized.js: always
	rm -fr build-$*/
	mkdir build-$*/
	rm -fr tmp-$*/
	mkdir tmp-$*/
	cp -a avatars build-$*/
	cp -a $*/manifest.json build-$*/
	cp -a LICENSE.txt build-$*/
	cp -aL $*/* tmp-$*
	cd tmp-$* && vulcanize --csp --inline --strip \
		--config=../vulcanize.json \
		-o vulcanized.html \
		main.html
	mv tmp-$*/vulcanized.html build-$*/main.html
	uglifyjs tmp-$*/vulcanized.js --screw-ie8 --compress --mangle \
		--output build-$*/vulcanized.js
	if [ -e $*/placeholder.png ]; then \
		cp $*/placeholder.png build-$*/; \
	fi
	if [ -e $*/index.html ]; then \
		cp $*/index.html build-$*/; \
	fi
	mkdir -p build-$*/bower_components/my-utils/
	cp -aL my-utils/my-perf* build-$*/bower_components/my-utils/
	if [ -e $*/index.js ]; then \
		uglifyjs $*/index.html --screw-ie8 --compress --mangle \
			--output build-$*/index.js; \
	fi
	if [ -e $*/index.css ]; then \
		cssmin < $*/index.css > build-$*/index.css; \
	fi

define build_apk =
	test -d dist || mkdir dist
	cd crosswalk && python ./make_apk.py \
		--package=com.collabora.xwperf.$1 \
		--manifest=$(CURDIR)/build-$1/manifest.json \
		--enable-remote-debugging \
		--target-dir=$(CURDIR)/tmp-$1 \
		$3 \
		$(NULL)
	mv tmp-$1/*.apk $2
endef

$(filter %_x86.apk,$(XW_APKS)): dist/%_x86.apk: tmp-%/vulcanized.js
	$(call build_apk,$*,$@,--arch=x86)

$(filter %_arm.apk,$(XW_APKS)): dist/%_arm.apk: tmp-%/vulcanized.js
	$(call build_apk,$*,$@,--arch=arm)

$(filter-out %_x86.apk %_arm.apk,$(XW_APKS)): dist/%.apk: tmp-%/vulcanized.js
	$(call build_apk,$*,$@,--mode=shared)

$(ANT_APKS): dist/%.apk: always
	cd $* && ant debug
	mv $*/bin/$*-debug.apk dist/$*.apk

$(GRADLE_APKS): dist/%.apk: always
	cd $* && ./gradlew build -x lint
	mv $*/app/build/outputs/apk/app-debug.apk dist/$*.apk

xwperf_social/placeholder.png: placeholders.xcf Makefile
	xcf2png -o $@ $< Social --percent 100 Social.Markers --percent 0 Contacts.Markers --percent 0 Contacts --percent 0

xwperf_contacts/placeholder.png: placeholders.xcf Makefile
	xcf2png -o $@ $< Social --percent 0 Social.Markers --percent 0 Contacts.Markers --percent 0 Contacts --percent 100

.PHONY: always
