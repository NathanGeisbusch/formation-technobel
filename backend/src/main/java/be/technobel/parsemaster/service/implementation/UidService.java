package be.technobel.parsemaster.service.implementation;

import org.springframework.stereotype.Service;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Base64;

@Service
public class UidService {
  private record Uid(long timestamp, long counter) {}
  private final Map<String, Uid> sequences = new HashMap<>();
  private final byte[] SECRET;

  public UidService() throws NoSuchAlgorithmException {
    SecureRandom.getInstanceStrong().nextBytes(this.SECRET = new byte[16]);
  }

  private synchronized Uid generateIdPair(String seqName) throws IllegalStateException {
    final long v1, v2;
    final var pair = sequences.get(seqName);
    if(pair == null) {
      v1 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
      v2 = 0L;
    } else {
      v1 = pair.timestamp();
      v2 = pair.counter();
    }
    if(v1 == Long.MAX_VALUE && v2 == Long.MAX_VALUE) {
      throw new IllegalStateException(seqName+": no_uid_available");
    }
    final var id = v2 == Long.MAX_VALUE ?
      new Uid(v1 + 1, 0L) :
      new Uid(v1, v2 + 1);
    sequences.put(seqName, id);
    return id;
  }

  public String generateId(String seqName) throws IllegalStateException {
    final Uid pair = generateIdPair(seqName);
    final long v1 = pair.timestamp();
    final long v2 = pair.counter();
    final var buffer = ByteBuffer.allocate(16);
    buffer.putLong(v1);
    buffer.putLong(v2);
    final byte[] bytes = buffer.array();
    for(int i = 0; i < bytes.length; ++i) bytes[i] ^= SECRET[i];
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
