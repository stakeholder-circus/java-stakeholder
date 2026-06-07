package com.stakeholder.experimental;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

final class ExperimentalStore {
    private static final ObjectMapper MAPPER =
            new ObjectMapper().findAndRegisterModules().enable(SerializationFeature.INDENT_OUTPUT);
    private static final TypeReference<LinkedHashMap<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final Path stateRoot;
    private final Path secretDir;
    private final Path dataDir;
    private final Path runtimeDir;
    private final byte[] secretKey;

    ExperimentalStore(Path requestedStateRoot) throws IOException {
        this.stateRoot = requestedStateRoot == null ? resolveStateRoot() : requestedStateRoot;
        this.secretDir = stateRoot.resolve(".secrets");
        this.dataDir = stateRoot.resolve("encrypted");
        this.runtimeDir = stateRoot.resolve("runtime");
        Files.createDirectories(secretDir);
        Files.createDirectories(dataDir);
        Files.createDirectories(runtimeDir);
        ensureMetadata();
        this.secretKey = resolveSecretKey();
    }

    Path stateRoot() {
        return stateRoot;
    }

    List<ProviderProfile> listProviderProfiles() throws IOException {
        List<ProviderProfile> stored =
                readEncryptedList(dataDir.resolve("provider-profiles.json.enc"), ProviderProfile.class);
        List<ProviderProfile> merged =
                overlayByKey(ExperimentalDefaults.defaultProviderProfiles(), stored, ProviderProfile::id);
        writeEncryptedList(dataDir.resolve("provider-profiles.json.enc"), merged);
        return merged;
    }

    ProviderProfile getProviderProfile(String idOrProvider) throws IOException {
        if (idOrProvider == null || idOrProvider.isBlank()) {
            return listProviderProfiles().stream().findFirst().orElse(null);
        }
        return listProviderProfiles().stream()
                .filter(profile -> idOrProvider.equals(profile.id()) || idOrProvider.equals(profile.provider()))
                .findFirst()
                .orElse(null);
    }

    List<PromptAsset> listPromptAssets() throws IOException {
        List<PromptAsset> stored = readEncryptedList(dataDir.resolve("prompt-assets.json.enc"), PromptAsset.class);
        List<PromptAsset> merged = overlayByKey(ExperimentalDefaults.defaultPromptAssets(), stored, PromptAsset::id);
        writeEncryptedList(dataDir.resolve("prompt-assets.json.enc"), merged);
        return merged;
    }

    PromptAsset getPromptAsset(String id) throws IOException {
        if (id == null || id.isBlank()) {
            return listPromptAssets().stream().findFirst().orElse(null);
        }
        return listPromptAssets().stream()
                .filter(asset -> id.equals(asset.id()))
                .findFirst()
                .orElse(null);
    }

    List<PromptAssetVersion> listPromptAssetVersions() throws IOException {
        List<PromptAssetVersion> stored =
                readEncryptedList(dataDir.resolve("prompt-asset-versions.json.enc"), PromptAssetVersion.class);
        List<PromptAssetVersion> merged = overlayByKey(
                ExperimentalDefaults.defaultPromptAssetVersions(),
                stored,
                version -> version.assetId() + ":" + version.version());
        writeEncryptedList(dataDir.resolve("prompt-asset-versions.json.enc"), merged);
        return merged;
    }

    PromptAssetVersion getPromptAssetVersion(String assetId, String version) throws IOException {
        return listPromptAssetVersions().stream()
                .filter(entry -> assetId.equals(entry.assetId()) && version.equals(entry.version()))
                .findFirst()
                .orElse(null);
    }

    List<PersonalizationProfile> listPersonalizationProfiles() throws IOException {
        List<PersonalizationProfile> stored =
                readEncryptedList(dataDir.resolve("personalization-profiles.json.enc"), PersonalizationProfile.class);
        List<PersonalizationProfile> merged =
                overlayByKey(ExperimentalDefaults.defaultPersonalizationProfiles(), stored, PersonalizationProfile::id);
        writeEncryptedList(dataDir.resolve("personalization-profiles.json.enc"), merged);
        return merged;
    }

    PersonalizationProfile getPersonalizationProfile(String id) throws IOException {
        if (id == null || id.isBlank()) {
            return listPersonalizationProfiles().stream().findFirst().orElse(null);
        }
        return listPersonalizationProfiles().stream()
                .filter(profile -> id.equals(profile.id()))
                .findFirst()
                .orElse(null);
    }

    ConsumerSessionRecord importConsumerSession(Path sessionFile, String profileId, String provider, String source)
            throws IOException {
        Map<String, Object> material = MAPPER.readValue(sessionFile.toFile(), MAP_TYPE);
        return saveConsumerSession(profileId, provider, material, source);
    }

    ConsumerSessionRecord saveConsumerSession(
            String profileId, String provider, Map<String, Object> material, String source) throws IOException {
        List<ConsumerSessionRecord> sessions =
                readEncryptedList(dataDir.resolve("consumer-sessions.json.enc"), ConsumerSessionRecord.class);
        ConsumerSessionRecord record = new ConsumerSessionRecord(
                sha256(profileId + ":" + provider + ":" + Instant.now()),
                profileId,
                provider,
                material,
                source,
                Instant.now().toString());
        List<ConsumerSessionRecord> next = new ArrayList<>();
        next.add(record);
        next.addAll(sessions.stream()
                .filter(existing -> !existing.id().equals(record.id()))
                .toList());
        writeEncryptedList(dataDir.resolve("consumer-sessions.json.enc"), next);
        return record;
    }

    ConsumerSessionRecord getLatestConsumerSession(String profileId) throws IOException {
        return readEncryptedList(dataDir.resolve("consumer-sessions.json.enc"), ConsumerSessionRecord.class).stream()
                .filter(entry -> profileId == null || profileId.equals(entry.profileId()))
                .findFirst()
                .orElse(null);
    }

    CacheRecord getCache(String cacheKey) throws IOException {
        return readJsonList(runtimeDir.resolve("cache-entries.json"), CacheRecord.class).stream()
                .filter(entry -> cacheKey.equals(entry.cacheKey()))
                .findFirst()
                .orElse(null);
    }

    void putCache(CacheRecord entry) throws IOException {
        List<CacheRecord> entries = readJsonList(runtimeDir.resolve("cache-entries.json"), CacheRecord.class);
        List<CacheRecord> next = new ArrayList<>();
        next.add(entry);
        next.addAll(entries.stream()
                .filter(existing -> !existing.cacheKey().equals(entry.cacheKey()))
                .toList());
        writeJsonList(runtimeDir.resolve("cache-entries.json"), next);
    }

    void putProvenance(Map<String, Object> snapshot) throws IOException {
        Path file = runtimeDir.resolve("provenance-snapshots.json");
        List<Map<String, Object>> entries = readJsonMaps(file);
        List<Map<String, Object>> next = new ArrayList<>();
        next.add(snapshot);
        next.addAll(entries);
        writeJsonMaps(file, next);
    }

    private void ensureMetadata() throws IOException {
        Path metadataFile = stateRoot.resolve("metadata.json");
        if (Files.exists(metadataFile)) {
            return;
        }
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("createdAt", Instant.now().toString());
        metadata.put("repo", "java-stakeholder");
        metadata.put("mode", "experimental-live-provider");
        Files.writeString(metadataFile, MAPPER.writeValueAsString(metadata), StandardCharsets.UTF_8);
    }

    private byte[] resolveSecretKey() throws IOException {
        String envValue = System.getenv(ExperimentalDefaults.SECRET_ENV);
        if (envValue != null && !envValue.isBlank()) {
            return sha256Bytes(envValue);
        }
        Path secretFile = secretDir.resolve("master.key");
        if (Files.exists(secretFile)) {
            return Base64.getDecoder()
                    .decode(Files.readString(secretFile, StandardCharsets.UTF_8).trim());
        }
        byte[] generated = java.security.SecureRandom.getSeed(32);
        Files.writeString(secretFile, Base64.getEncoder().encodeToString(generated), StandardCharsets.UTF_8);
        trySetPrivate(secretFile);
        return generated;
    }

    private <T> List<T> readEncryptedList(Path file, Class<T> elementType) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }
        try {
            String payload = Files.readString(file, StandardCharsets.UTF_8);
            String decrypted = decrypt(payload);
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, elementType);
            return MAPPER.readValue(decrypted, type);
        } catch (GeneralSecurityException exception) {
            throw new IOException("Failed to decrypt " + file.getFileName(), exception);
        }
    }

    private <T> void writeEncryptedList(Path file, List<T> value) throws IOException {
        try {
            String serialized = MAPPER.writeValueAsString(value);
            Files.writeString(file, encrypt(serialized), StandardCharsets.UTF_8);
            trySetPrivate(file);
        } catch (GeneralSecurityException exception) {
            throw new IOException("Failed to encrypt " + file.getFileName(), exception);
        }
    }

    private <T> List<T> readJsonList(Path file, Class<T> elementType) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, elementType);
        return MAPPER.readValue(file.toFile(), type);
    }

    private void writeJsonList(Path file, List<?> value) throws IOException {
        Files.writeString(file, MAPPER.writeValueAsString(value), StandardCharsets.UTF_8);
    }

    private List<Map<String, Object>> readJsonMaps(Path file) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }
        JavaType type = MAPPER.getTypeFactory()
                .constructCollectionType(
                        List.class, MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        return MAPPER.readValue(file.toFile(), type);
    }

    private void writeJsonMaps(Path file, List<Map<String, Object>> value) throws IOException {
        Files.writeString(file, MAPPER.writeValueAsString(value), StandardCharsets.UTF_8);
    }

    private String encrypt(String payload) throws GeneralSecurityException, IOException {
        byte[] iv = java.security.SecureRandom.getSeed(12);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "AES"), new GCMParameterSpec(128, iv));
        byte[] cipherText = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        byte[] encrypted = new byte[cipherText.length - 16];
        byte[] tag = new byte[16];
        System.arraycopy(cipherText, 0, encrypted, 0, encrypted.length);
        System.arraycopy(cipherText, encrypted.length, tag, 0, tag.length);
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("algorithm", "aes-256-gcm");
        envelope.put("iv", Base64.getEncoder().encodeToString(iv));
        envelope.put("tag", Base64.getEncoder().encodeToString(tag));
        envelope.put("ciphertext", Base64.getEncoder().encodeToString(encrypted));
        return MAPPER.writeValueAsString(envelope);
    }

    private String decrypt(String payload) throws IOException, GeneralSecurityException {
        Map<String, Object> envelope = MAPPER.readValue(payload, MAP_TYPE);
        byte[] iv = Base64.getDecoder().decode(String.valueOf(envelope.get("iv")));
        byte[] tag = Base64.getDecoder().decode(String.valueOf(envelope.get("tag")));
        byte[] cipherText = Base64.getDecoder().decode(String.valueOf(envelope.get("ciphertext")));
        byte[] combined = new byte[cipherText.length + tag.length];
        System.arraycopy(cipherText, 0, combined, 0, cipherText.length);
        System.arraycopy(tag, 0, combined, cipherText.length, tag.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "AES"), new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(combined), StandardCharsets.UTF_8);
    }

    private static Path resolveStateRoot() {
        String override = System.getenv("STAKEHOLDER_STATE_DIR");
        if (override != null && !override.isBlank()) {
            return Path.of(override).resolve("java-stakeholder");
        }
        return Path.of(System.getProperty("user.home"), ".stakeholder", "java-stakeholder");
    }

    private static <T> List<T> overlayByKey(
            List<T> defaults, List<T> stored, java.util.function.Function<T, String> keyFn) {
        LinkedHashMap<String, T> merged = new LinkedHashMap<>();
        defaults.forEach(entry -> merged.put(keyFn.apply(entry), entry));
        stored.forEach(entry -> merged.put(keyFn.apply(entry), entry));
        return List.copyOf(merged.values());
    }

    private static void trySetPrivate(Path file) {
        try {
            Set<PosixFilePermission> permissions =
                    Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(file, permissions);
        } catch (Exception ignored) {
            // best effort on non-posix filesystems
        }
    }

    private static String sha256(String value) {
        return ExperimentalDefaults.sha256(value);
    }

    private static byte[] sha256Bytes(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 should be available", exception);
        }
    }
}
