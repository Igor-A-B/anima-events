This is a Kotlin Multiplatform project targeting Android, iOS, Web, Server.

* [/app/iosApp](./app/iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/app/shared](./app/shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./app/shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./app/shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./app/shared/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/core](./core/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./core/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :app:androidApp:assembleDebug`
- Server: `./gradlew :server:run`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :app:webApp:jsBrowserDevelopmentRun`
- iOS app: open the [/app/iosApp](./app/iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).
## API url on Android

- Emulator: works with no setup (`http://10.0.2.2:8080`).
- Debug on a real phone (USB): works with no setup. Every debug build runs `adb reverse tcp:8080 tcp:8080`, so the phone's `localhost:8080` reaches this machine.
- Debug over Wi-Fi: set `api.baseUrl=http://<your-LAN-IP>:8080` in `local.properties` and open port 8080 in your firewall (e.g. `sudo ufw allow 8080/tcp`). Debug builds allow cleartext http. Also open port 9199 (`sudo ufw allow 9199/tcp`): image urls use the api host, see below.
- Release: pass `API_BASE_URL=https://...` as a Gradle property or env var. The build fails without it, and release builds are https only.
- Logs: `adb logcat -s System.out` shows the requests and failures.

## Recreating the events table

Hibernate runs with `ddl-auto=update`, which adds new columns but never changes or drops existing ones. After a schema change to `EventEntity`, drop the table and let the server recreate it (subscriptions are dropped with it, since they reference events):

```
docker exec -i anima-database sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"' < server/db/recreate-events.sql
docker compose restart server
```

With `SPRING_PROFILES_ACTIVE=dev` the seeder puts the sample events back on the next start.

## Firebase Storage emulator (event images, profile photos)

Local storage for uploaded images runs on the Firebase Storage emulator, project `anima-events` (storage only).

- Start it: `docker compose up -d firebase` (the `server` service starts it too). Storage is on `:9199`, the emulator UI on http://localhost:4000/storage.
- Data persists in `.local-emulator/` (gitignored). It is imported on start and exported when the container stops, so stop it with `docker compose stop firebase` rather than killing it.
- Running the server outside docker: set `STORAGE_EMULATOR_HOST=http://127.0.0.1:9199` (see `.env.example`). Without it the server uses the real bucket with application default credentials.
- Uploads go through the backend only (`storage.rules` denies client writes). Files are stored under `events/{eventId}/` and `users/{userId}/`.
- Upload an event image (curator only, max 10MB, jpeg/png/webp):
  `curl -H "Authorization: Bearer $TOKEN" -F "file=@photo.jpg;type=image/jpeg" localhost:8080/events/{id}/images`
- Image urls come from the server as `STORAGE_PUBLIC_BASE_URL` (default `http://127.0.0.1:9199`, the server's own loopback). The app rewrites a loopback image host to the api host (`deviceImageUrl`), so:
  - Android emulator: images load from `10.0.2.2:9199`, no setup.
  - USB phone: `adb reverse tcp:9199 tcp:9199` runs on every debug build (and `8080` for the api), so `127.0.0.1` works.
  - Wi-Fi debug (`api.baseUrl=http://<LAN-IP>:8080`): images go to `<LAN-IP>:9199`; open 9199 in the firewall. Setting `STORAGE_PUBLIC_BASE_URL=http://<LAN-IP>:9199` for the server also works.
  - iOS simulator, desktop, web: `127.0.0.1:9199` is reachable as is. iOS: `Info.plist` has no ATS exception (`NSAllowsLocalNetworking`), so plain-http loads (API and images alike) may be blocked; unverified, no Mac here.
  - Web: the browser fetches images, so the emulator must answer CORS; unverified.
- Images are not thumbnailed: the server stores the original (up to 10MB) and the app downloads it whole, decoding at the cover's layout size (bounded). Server-side thumbnails are a follow-up. Failures (404, offline) leave the gradient and log `E/EventCover`; coil does not retry on its own.
