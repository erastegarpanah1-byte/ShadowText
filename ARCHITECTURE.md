# ShadowText Architecture

## Overview

ShadowText is an offline text steganography engine for Android that hides arbitrary files inside ordinary-looking text using invisible Unicode characters.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                           │
│  ┌──────────┐  ┌──────────────┐  ┌──────────────┐              │
│  │ Screens  │  │  ViewModels   │  │  Navigation  │              │
│  │ Compose  │  │  StateFlows   │  │  NavHost     │              │
│  └──────────┘  └──────────────┘  └──────────────┘              │
├─────────────────────────────────────────────────────────────────┤
│                       DOMAIN LAYER                               │
│  ┌────────────────────┐  ┌──────────────────────┐               │
│  │  EncodeFileUseCase │  │  DecodeTextUseCase    │               │
│  │  SaveAndShare      │  │                       │               │
│  └────────────────────┘  └──────────────────────┘               │
├─────────────────────────────────────────────────────────────────┤
│                        DATA LAYER                                │
│  ┌──────────────────┐                                           │
│  │  FileRepository  │  URI → ByteArray, ByteArray → File       │
│  └──────────────────┘                                           │
├─────────────────────────────────────────────────────────────────┤
│                     CORE ENGINE LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐     │
│  │  StegoEncoder│  │ StegoDecoder │  │ CarrierTextProvider│    │
│  └──────┬───────┘  └──────┬───────┘  └───────────────────┘     │
│         │                  │                                     │
│  ┌──────┴──────────────────┴───────┐                            │
│  │         InvisibleEncoder        │  ← Interface (swappable)   │
│  │   ┌────────────────────────┐    │                            │
│  │   │   ZeroWidthEncoder     │    │  ← Default implementation  │
│  │   │   (U+200B/C/D, U+FEFF) │    │                            │
│  │   └────────────────────────┘    │                            │
│  └─────────────────────────────────┘                            │
│         │                  │                                     │
│  ┌──────┴──────────────────┴───────┐                            │
│  │        Binary Packet Format      │                           │
│  │  ┌──────────────────────┐       │                            │
│  │  │  PacketSerializer    │       │                            │
│  │  │  PacketDeserializer  │       │                            │
│  │  │  PacketFormat        │       │                            │
│  │  └──────────────────────┘       │                            │
│  └─────────────────────────────────┘                            │
├─────────────────────────────────────────────────────────────────┤
│                    DI / HILT MODULES                              │
│  ┌──────────────┐                                                │
│  │  CoreModule  │  Provides all engine components                │
│  └──────────────┘                                                │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

### Encoding Pipeline

```
User picks file (any type)
         │
         ▼
FileRepository.readUri() ──→  ByteArray + MIME + filename
         │
         ▼
Packet(magic, version, payloadType, payload, metadata)
         │
         ▼
PacketSerializer ──→  Binary blob (24-byte header + metadata + payload + CRC32)
         │
         ▼
InvisibleEncoder.encode() ──→  String of invisible chars (ZWSP, ZWNJ, ZWJ, BOM)
         │
         ▼
CarrierTextProvider.provide() ──→  Visible English text
         │
         ▼
Embed invisible chars into carrier text
         │
         ▼
Output: Stego Text (copiable, pastable, shareable)
```

### Decoding Pipeline

```
User receives/pastes stego text
         │
         ▼
InvisibleEncoder.containsEncodedData() ──→  Detection
         │
         ▼
InvisibleEncoder.extractInvisible() ──→  Invisible-only string
         │
         ▼
InvisibleEncoder.decode() ──→  Binary blob
         │
         ▼
PacketDeserializer.deserialize() ──→  Packet (validated: magic, CRC32)
         │
         ▼
Extract payload + metadata
         │
         ▼
FileRepository.writeToTempFile() ──→  Reconstructed original file
```

## Binary Packet Format

```
Offset  Size  Field
────────────────────────────────────
0       4     Magic Number   0x53544458 ("STDX" LE)
4       2     Version        Currently 1
6       1     Flags          Reserved
7       1     Payload Type   Enum (see PacketFormat.PayloadType)
8       8     Payload Size   Uncompressed bytes (little-endian)
16      4     Metadata Len   UTF-8 JSON metadata length
20      4     Reserved       Future use
24      N     Metadata       UTF-8 JSON key-value map
24+N    M     Payload        Raw file bytes
24+N+M  4     Checksum       CRC32 of all preceding bytes
```

## Invisible Character Encoding

### Zero-Width Characters (current default)

| Bits | Character | Codepoint | Name                      |
|------|-----------|-----------|---------------------------|
| 00   | ​         | U+200B    | ZERO WIDTH SPACE          |
| 01   | ‌         | U+200C    | ZERO WIDTH NON-JOINER     |
| 10   | ‍         | U+200D    | ZERO WIDTH JOINER         |
| 11   | ﻿         | U+FEFF    | ZERO WIDTH NO-BREAK SPACE |

- 2 bits per character
- 4 invisible chars per byte
- Framing: START marker (ZWSP ZWSP ZWSP ZWNJ), END marker (ZWSP ZWSP ZWSP ZWJ)
- Sentinel: U+2063 INVISIBLE SEPARATOR

## Module Design

### Core Engine (`core/`)
- **format/** — Binary packet serialization. Completely independent. Does not know about encoding or encryption.
- **encoding/** — Invisible character encoding. `InvisibleEncoder` interface allows plugging in new schemes.
- **engine/** — Orchestrators (`StegoEncoder`, `StegoDecoder`) that compose format + encoding.

### Data (`data/`)
- **repository/** — File I/O. Reads URIs into ByteArrays, writes ByteArrays to temp files.

### Domain (`domain/`)
- **usecase/** — Single-purpose use cases. Each does one thing: encode, decode, save/share.

### Presentation (`ui/`)
- **screens/** — Compose screens (Home, Encode, Decode, Result)
- **viewmodel/** — StateFlow-based ViewModels with Hilt injection

### DI (`di/`)
- **CoreModule** — Wires all engine components. Add new encoders here.

## Key Design Decisions

1. **Encoder is an interface** — `InvisibleEncoder` is not coupled to any implementation. Add Tag Characters, Variation Selectors, or custom schemes by implementing the interface and registering in `CoreModule`.

2. **Encryption slot is reserved** — Between `PacketSerializer` and `InvisibleEncoder`. Phase 2 will insert encryption here without touching either side.

3. **Packet format is versioned** — The binary format has a version field. Future versions can add fields without breaking backward compatibility (old decoders check version before parsing).

4. **No file-type coupling** — Input is always `ByteArray`. The engine doesn't care what the bytes represent. MIME type is stored as metadata for reconstruction only.

5. **Offline by design** — The manifest declares no INTERNET permission. No network calls exist in the codebase.

## Future Phases

### Phase 2 — Encryption (planned, not implemented)
- Insert between PacketSerializer and InvisibleEncoder
- AES-256-GCM
- Password-based key derivation (PBKDF2/Argon2)
- Public/private key mode
- Digital signatures
- New flags byte in packet header to indicate encryption

### Phase 3 — Optimization (planned, not implemented)
- Compression (zlib/LZ4 before encoding)
- Streaming for large files
- Additional encoding schemes
- Performance benchmarking
- Memory optimization
- Batch processing
