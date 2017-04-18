CP := cp
RM := rm -rf
AFPLAY := afplay
DEPLOY_DIR := /Users/build/Projects/www_inhouse
SOUND_FILE := /System/Library/Sounds/Glass.aiff

all: clean buildall deploy sound

sound:
	${AFPLAY} ${SOUND_FILE}

deploy:
	$(CP) ./app/build/outputs/apk/*.apk $(DEPLOY_DIR)/

beta:
	./gradlew assembleBeta

debug:
	./gradlew assembleDebug

release:
	./gradlew assembleRelease

buildall:
	./gradlew assemble

clean:
	$(RM) ./app/build/outputs/apk/*.apk
