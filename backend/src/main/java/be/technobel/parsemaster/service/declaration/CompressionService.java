package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.entity.PackageFile;
import be.technobel.parsemaster.enumeration.CompressionEncoding;
import java.io.InputStream;

public interface CompressionService {
  /** Returns the compression encoding supported by the service */
  CompressionEncoding getEncoding();

  /**
   * Reads bytes from stream and verifies if it corresponds to valid data
   * (based on service supported encoding).
   * @param input input byte stream to read
   * @param limit maximum amount to read before throwing an exception
   * @return the bytes that have been read
   */
  byte[] readAndValidate(InputStream input, long limit);

  /**
   * Reads bytes from stream and encodes them in the supported encoding of the service.
   * @param input input byte stream to read
   * @return the encoded bytes
   */
  byte[] encode(InputStream input);

  /**
   * Reads bytes from stream and decodes them from the supported encoding of the service.
   * @param input input byte stream to read
   * @return the decoded bytes
   */
  byte[] decode(InputStream input);

  /**
   * Reads bytes from stream and verifies if it corresponds to valid data
   * (based on service supported encoding) or throws an exception.
   * @param input input byte stream to read
   */
  void validate(InputStream input);

  /** Creates an empty package file whose content
   * is compressed in the supported encoding of the service. */
  PackageFile createFile();
}
