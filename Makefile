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
	./gradlew clean assembleBeta

debug:
	./gradlew clean assembleDebug

release:
	./gradlew clean assembleRelease

buildall:
	./gradlew clean assemble

clean:
	$(RM) ./app/build/outputs/apk/*.apk
