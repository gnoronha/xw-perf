all:

-include android-paths.mk

XW_APPS = \
	xwperf_paper_elements \
	$(NULL)
JAVA_APPS = \
	notxw_hello_world \
	$(NULL)

XW_APKS = $(patsubst %,%_x86.apk,$(XW_APPS))
JAVA_APKS = $(patsubst %,%.apk,$(JAVA_APPS))

all: $(XW_APKS) $(JAVA_APKS)

$(XW_APKS): %_x86.apk: always
	cd crosswalk && python ./make_apk.py \
		--package=com.collabora.xwperf.$* \
		--manifest=$(CURDIR)/$*/manifest.json \
		--target-dir=$(CURDIR) \
		$(NULL)

$(JAVA_APKS): %.apk: always
	cd $* && ant debug
	mv $*/bin/$*-debug.apk $*.apk

.PHONY: always
