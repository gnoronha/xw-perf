APPS = \
	xwperf_paper_elements \
	$(NULL)

APKS = $(patsubst %,%_x86.apk,$(APPS))

$(APKS): %_x86.apk: always
	cd crosswalk && python ./make_apk.py \
		--package=com.collabora.$* \
		--manifest=$(CURDIR)/$*/manifest.json \
		--target-dir=$(CURDIR) \
		$(NULL)

.PHONY: always
