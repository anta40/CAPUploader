This is a port of Stefan Braicu's jcManager to Android.

His code used to be found at this link:
http://www.brokenmill.com/2010/03/java-secure-card-manager/

Seems that now it's dead. Fortunately, I found a backup in one of my
HDs. Now it has been mirrored:
https://github.com/anta40/jcManager

All the heavy works are credited to Stefan. I just wrote the UI and implement
the NFC part. 

This work was done on Eclipse years ago. Since Eclipse for Android development was deprecated,
and people use Android Studio these days, I decide to migrate this project to Android Studio. There's an error though:
error: unknown element <intent-filter> found.
Message{kind=ERROR, text=error: unknown element <intent-filter> found., sources=[C:\Users\Cipta-NB\StudioProjects\CAPUploader\app\build\intermediates\manifests\full\debug\AndroidManifest.xml:35], original message=, tool name=Optional.of(AAPT)}

Maybe I'll fix that if in the mood. 