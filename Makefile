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

JAVA_APPS = \
	notxw_hello_world \
	notxw_list \
	notxw_starter \
	$(NULL)

# We produce standalone .apks for the apps, but only shim .apks for the
# demos.
XW_APKS = \
	$(patsubst %,dist/%.apk,$(XW_APPS)) \
	$(patsubst %,dist/%_arm.apk,$(XW_APPS)) \
	$(patsubst %,dist/%_x86.apk,$(XW_APPS)) \
	$(patsubst %,dist/%.apk,$(XW_DEMOS)) \
	$(NULL)
JAVA_APKS = $(patsubst %,dist/%.apk,$(JAVA_APPS))

all: xw java
.PHONY: all
xw: $(XW_APKS)
.PHONY: xw
java: $(JAVA_APKS)
.PHONY: java

# no real dependency tracking yet so use "always"
tmp-%/main.js: always
	rm -fr build-$*/
	mkdir build-$*/
	rm -fr tmp-$*/
	mkdir tmp-$*/
	cp -a avatars build-$*/
	cp -a $*/manifest.json build-$*/
	cp -a LICENSE.txt build-$*/
	cd $* && vulcanize --csp --inline --strip -o ../tmp-$*/main.html \
		main.html
	mv tmp-$*/main.html build-$*/main.html
	uglifyjs tmp-$*/main.js --screw-ie8 --compress --mangle \
		--output build-$*/main.js
	if [ -e $*/index.html ]; then \
		set -e; \
		cp $*/index.html build-$*/; \
		mkdir -p build-$*/bower_components/my-utils/; \
		cp my-utils/my-perf* \
			build-$*/bower_components/my-utils/; \
	fi
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

$(filter %_x86.apk,$(XW_APKS)): dist/%_x86.apk: tmp-%/main.js
	$(call build_apk,$*,$@,--arch=x86)

$(filter %_arm.apk,$(XW_APKS)): dist/%_arm.apk: tmp-%/main.js
	$(call build_apk,$*,$@,--arch=arm)

$(filter-out %_x86.apk %_arm.apk,$(XW_APKS)): dist/%.apk: tmp-%/main.js
	$(call build_apk,$*,$@,--mode=shared)

$(JAVA_APKS): dist/%.apk: always
	cd $* && ant debug
	mv $*/bin/$*-debug.apk dist/$*.apk

.PHONY: always
