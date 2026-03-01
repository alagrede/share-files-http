# Share Files HTTP

> Because configuring Windows/Mac folder sharing is a nightmare — this is just way easier.

Share files between any devices on your local network via a simple web interface. No SMB, no Samba, no Bonjour headaches. Just run a JAR file and drag & drop.

![Stable version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-8+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6-green)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## Why this exists

Every time I need to transfer files between my Windows and Mac machines, I waste 20 minutes trying to get network folder sharing to work. Wrong workgroup, firewall blocking, SMB version mismatch... you know the drill.

This tool fixes that. Launch it on any machine, open a browser on any other device, and you're done.

---

## Features

- **Upload** files from any browser — phone, tablet, PC, Mac
- **Download** files with streaming support (no size limit headaches)
- **Swing UI** tray app for quick start/stop and folder selection
- **Zero config** — works out of the box on your local network
- **Lightweight** — a single JAR, no installation needed

---

## Screenshots

### Web Interface
![Web Interface](https://raw.githubusercontent.com/alagrede/share-files-http/refs/heads/master/docs/screenshot.png)

### Desktop App (Swing UI)
![Swing UI](https://raw.githubusercontent.com/alagrede/share-files-http/refs/heads/master/docs/screenshot-ui.png)

---

## Getting Started

### Option 1 — Download the JAR (easiest)

Grab the latest release: [share-upload.jar](https://github.com/alagrede/share-files-http/releases)

```bash
java -jar share-upload.jar
```

### Option 2 — Build from source

```bash
./mvnw clean package
java -jar target/share-upload.jar
```

---

## Usage

1. Run the JAR on the machine that **has the files**
2. Open `http://<machine-ip>:8080` on **any other device** on the same network
3. Upload or download files — that's it

> Tip: the Swing UI shows your local IP address so you don't have to look it up.

---

## Requirements

- Java 8 or higher
- Devices on the same local network (WiFi or Ethernet)

---

## License

MIT
