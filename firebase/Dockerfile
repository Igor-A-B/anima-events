# trixie, bookworm has no java 21
FROM node:22-trixie-slim

# the storage rules runtime needs java, firebase-tools requires 21+
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-21-jre-headless \
    && rm -rf /var/lib/apt/lists/*

RUN npm install -g firebase-tools

WORKDIR /firebase

# firebase.json, .firebaserc, storage.rules and .local-emulator are mounted by docker compose
EXPOSE 9199 4000 4400

# --import fails if the folder is missing on the first run
CMD mkdir -p .local-emulator && exec firebase emulators:start \
    --only storage \
    --project anima-events \
    --import=./.local-emulator \
    --export-on-exit=./.local-emulator
