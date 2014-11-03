all:

-include android-paths.mk

XW_APPS = \
	xwperf_social \
	$(NULL)
JAVA_APPS = \
	notxw_hello_world \
	notxw_list \
	$(NULL)

XW_APKS = $(patsubst %,%_x86.apk,$(XW_APPS))
JAVA_APKS = $(patsubst %,%.apk,$(JAVA_APPS))

all: $(XW_APKS) $(JAVA_APKS)

$(XW_APKS): %_x86.apk: always
	test -d dist || mkdir dist
	rm -fr build-$*/
	mkdir build-$*/
	rm -fr tmp-$*/
	mkdir tmp-$*/
	cp -a avatars build-$*/
	cp -a $*/manifest.json build-$*/
	cp -a LICENSE.txt build-$*/
	cd $* && vulcanize --csp --inline --strip -o ../build-$*/index.html \
		index.html
	cd crosswalk && python ./make_apk.py \
		--package=com.collabora.xwperf.$* \
		--manifest=$(CURDIR)/build-$*/manifest.json \
		--enable-remote-debugging \
		--target-dir=$(CURDIR)/tmp-$* \
		$(NULL)
	mv tmp-$*/*_arm.apk dist/$*_arm.apk
	mv tmp-$*/*_x86.apk dist/$*_x86.apk

$(JAVA_APKS): %.apk: always
	cd $* && ant debug
	mv $*/bin/$*-debug.apk dist/$*.apk

.PHONY: always
