package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.entity.PackageFile;
import be.technobel.parsemaster.enumeration.CompressionEncoding;
import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.service.declaration.CompressionService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompressionService implements CompressionService {
  @Override
  public CompressionEncoding getEncoding() {
    return CompressionEncoding.GZIP;
  }

  @Override
  public byte[] readAndValidate(InputStream input, long limit) {
    final byte[] encoded = read(input, limit);
    validate(new ByteArrayInputStream(encoded));
    return encoded;
  }

  private byte[] read(InputStream input, long limit) {
    final byte[] buffer = new byte[65536];
    int bytesRead, totalRead = 0;
    try(final var output = new ByteArrayOutputStream()) {
      while(true) {
        bytesRead = input.read(buffer);
        if(bytesRead <= 0) break;
        totalRead += bytesRead;
        if(totalRead > limit) throw Exceptions.TOO_BIG_PAYLOAD.create();
        output.write(buffer, 0, bytesRead);
      }
      return output.toByteArray();
    }
    catch(Exception ex) {
      throw Exceptions.INVALID_PAYLOAD.create();
    }
  }

  @Override
  public byte[] encode(InputStream input) {
    final byte[] buffer = new byte[65536];
    int bytesRead;
    try(
      final var output = new ByteArrayOutputStream();
      final var gzipOutput = new GZIPOutputStream(output)
    ) {
      while(true) {
        bytesRead = input.read(buffer);
        if(bytesRead <= 0) break;
        gzipOutput.write(buffer, 0, bytesRead);
      }
      gzipOutput.close();
      return output.toByteArray();
    }
    catch(IOException e) {
      throw Exceptions.INVALID_PAYLOAD.create();
    }
  }

  @Override
  public byte[] decode(InputStream input) {
    final byte[] buffer = new byte[65536];
    int bytesRead;
    try(
      final var output = new ByteArrayOutputStream();
      final var gzipInput = new GZIPInputStream(input)
    ) {
      while(true) {
        bytesRead = gzipInput.read(buffer);
        if(bytesRead <= 0) break;
        output.write(buffer, 0, bytesRead);
      }
      return output.toByteArray();
    }
    catch(IOException e) {
      throw Exceptions.INVALID_PAYLOAD.create();
    }
  }

  @Override
  public void validate(InputStream input) {
    final byte[] buffer = new byte[65536];
    try(final var gzipInput = new GZIPInputStream(input)) {
      while(gzipInput.read(buffer) > 0);
    }
    catch(IOException e) {
      throw Exceptions.INVALID_PAYLOAD.create();
    }
  }

  @Override
  public PackageFile createFile() {
    try(
      final var output = new ByteArrayOutputStream();
      final var gzip = new GZIPOutputStream(output)
    ) {
      gzip.write(new byte[0]);
      gzip.close();
      final var file = new PackageFile(output.toByteArray());
      file.setEncoding(CompressionEncoding.GZIP);
      return file;
    }
    catch(IOException e) {
      throw Exceptions.SERVER_ERROR.create();
    }
  }
}
