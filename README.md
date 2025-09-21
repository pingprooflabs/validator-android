# Validator Android App

The **Validator Android App** is part of the [Ping Proof Labs](https://pingproof.io) network — a decentralized uptime monitoring system powered by mobile validators worldwide.

Android devices connect to the PingProof backend via MQTT, perform lightweight API uptime and latency checks, and report results in real time. Rewards are handled on Solana-compatible wallets.

## Features
- 🔗 Phantom & Solflare wallet integration
- 📡 Real-time task dispatch with MQTT
- 📊 API uptime/latency validation
- 🌍 Geo-distributed participation
- ⚡ Background execution support

## Project Structure (high-level)
```
app/
  src/main/java/com/example/ping_proof/...
  src/main/res/...
  AndroidManifest.xml
keystore/                      # (optional, not committed)
.gradle/, .idea/, .kotlin/     # IDE/build metadata
```

## Requirements
- Android Studio (Arctic Fox or newer)
- Java 17+
- Android SDK 24+

## Quick Start
1) **Clone**
```bash
git clone https://github.com/pingprooflabs/validator-android.git
cd validator-android
```
3) **Open in Android Studio** and run on a device/emulator.

## 🔧 Configuration

All runtime configuration is kept in [`Environment.kt`](app/src/main/java/com/example/ping_proof/Environment.kt).

By default, the file contains **only empty strings or safe Devnet values**.  
This means you can build and run the app without worrying about leaking secrets.

If you want to run against a real backend or MQTT broker:

1. Open `app/src/main/java/com/example/ping_proof/Environment.kt`
2. Replace the empty strings with your actual values:
   - `baseUrl` → your backend API URL
   - `MQTT_BROKER_URL` → your MQTT broker address
   - `MQTT_USERNAME` → broker username
   - `MQTT_PASSWORD` → broker password
   - `SOLANA_CLUSTER` → choose from `RpcCluster.Devnet`, `RpcCluster.Testnet`, or `RpcCluster.MainnetBeta`

3. Rebuild and run the app.

⚠️ **Do not commit your updated values** if they contain secrets. Always reset them before pushing changes.


### Environment switching
The app includes an `Environment.kt` enum with `DEV` and `PROD`. Values are sourced from `BuildConfig` so you can reuse the same APK with different configs.

See `app/src/main/java/com/example/ping_proof/Environment.kt` (provided below) for the final version.


## Contributing
We welcome contributions! Please:
1. Fork the repo
2. Create a feature branch
3. Run lint/tests
4. Open a Pull Request

Read [CONTRIBUTING.md](CONTRIBUTING.md) for coding standards and process, and our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## Security
If you discover a vulnerability, please follow our [SECURITY.md](SECURITY.md) policy and **do not** open a public issue.

## License
Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).

## About PingProof
PingProof is a decentralized uptime monitoring platform where companies register APIs and validators ensure availability across regions. Validators earn rewards for their contributions.

- 🌐 Website: https://pingproof.io
- 🐦 Twitter: https://twitter.com/pingproof_io
- 💼 LinkedIn: https://www.linkedin.com/company/pingprooflabs
