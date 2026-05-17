package eg.document;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

//--Eadgyth--//
import eg.utils.FileUtils;

/**
 * The charset of a file, including how it was determined,
 * as UTF-8 or as fallback.
 */
public class FileCharset {

    private Charset charset = StandardCharsets.UTF_8;
    private Charset userFallbackCharset = null;
    private File file;
    private byte[] bytes;
    private boolean failedFallback = false;
    private boolean hasBOM = false;

    /**
     * @param file  the file whose charset is do be defined
     * @param userFallbackCharset  the fallback charset that may
     * have been selected in the File menu. Null means that a
     * fallback is determind via system or JVM default, if possible
     */
    public FileCharset(File file, Charset userFallbackCharset) {
       this.file = file;
       this.userFallbackCharset = userFallbackCharset;
    }

    public FileCharset() {}

   /**
    * Returns the charset, which is UTF-8 or maybe a fallback
    * charset if the file is detected to be invalid UTF-8
    *
    * @return  the charset
    */
   public Charset charset() {
      return charset;
   }

   /**
    * Reads the bytes of the specified file if this
    * <code>FileCharset</code> was created without a file.
    *
    * @param file  the file whose bytes are read
    */
   public void readBytes(File file) {
      this.file = file;
      readBytes();
   }

   /**
    * Reads the bytes of this file.
    */
   public void readBytes() {
      if (file == null) {
         throw new IllegalStateException("No file has been set.");
      }
      try {
         bytes = Files.readAllBytes(file.toPath());
      }
      catch (IOException e) {
         FileUtils.log(e);
      }
   }

   /**
    * Returns if this file has a binary encoding by searching for
    * a null byte over the first 4096 bytes. This method also
    * detects text files encoded with UTF-16/32.
    *
    * @return  true if a null byte was found, false otherwise
    */
   public boolean isBinary() {
      if (bytes == null) {
         throw new IllegalStateException("No bytes have been read yet.");
      }
      int limit = Math.min(bytes.length, 4096);
      for (int i = 0; i < limit; i++) {
         if (bytes[i] == 0) {
            return true;
         }
      }
      return false;
   }

   /**
    * Checks if this file is in UTF-8; looks for a fallback if fails.
    *
    * A fallback is based on the system default charset (OS or
    * Java runtime). If system default is UTF-8, a fallback is
    * not possible.
    */
   public void checkUtf8Encoding() {
      if (bytes == null || file == null) {
          throw new IllegalStateException("No bytes have been read yet");
      }
      try {
         CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
               .onMalformedInput(CodingErrorAction.REPORT)
               .onUnmappableCharacter(CodingErrorAction.REPORT);

         decoder.decode(ByteBuffer.wrap(bytes));
         hasBOM = detectBOM();
      }
      catch (CharacterCodingException e) {
         if (userFallbackCharset != null) {
            charset = userFallbackCharset;
         }
         else {
            charset = charsetBySystemDef();
         }
         if (charset.displayName().equalsIgnoreCase("UTF-8")) {
            failedFallback = true;
         }
      }
   }

   /**
    * Returns the bytes read from this file
    *
    * @return  the bytes
    */
   public byte[] getBytes() {
      return bytes;
   }

   /**
    * Returns if this file has a byte order mark, given it is
    * encoded with UTF-8.
    *
    * The BOM detection does not apply to UTF-16/32.
    *
    * @return true for BOM, false otherwise
    */
   public boolean hasBOM() {
      return hasBOM;
   }

   /**
    * Returns if a file was detected as invalid UTF-8 and a
    * fallback encoding was assumed.
    *
    * @return  true if a fallback was assumed, false otherwise
    */
   public boolean isFallback() {
      return !charset.displayName().equalsIgnoreCase("UTF-8");
   }

   /**
    * Returns if a file was detected as invalid UTF-8 and a
    * user defined fallback charset was assumed.
    *
    * @return  true for user fallback, false otherwise
    */
   public boolean isUserFallback() {
      return userFallbackCharset != null;
   }

   /**
    * Returns if a file was detected as invalid UTF-8 and a
    * fallback encoding was not assumed.
    *
    * @return  true for failed fallback, false otherwise
    */
   public boolean isFailedFallback() {
      return failedFallback;
   }

   //
   //--private--/
   //

   private Charset charsetBySystemDef() {
      //
      // "native.encoding" yields null on Java <17
      String nativeEnc = System.getProperty("native.encoding");
      if (nativeEnc != null && !nativeEnc.isEmpty()) {
         try {
            return Charset.forName(nativeEnc);
         } catch (Exception e) {
            FileUtils.log(e);
         }
      }
      //
      // In Java 8-16 on Windows: The OS encoding too, unless JVM
      // started with UTF-8 flag.
      // In Java 18+, Windows 11?, Mac/Unix: UTF-8 (i.e. no fallback)
      return Charset.defaultCharset();
   }

   private boolean detectBOM() {
      return bytes.length >= 3
            && (bytes[0] & 0xFF) == 0xEF
            && (bytes[1] & 0xFF) == 0xBB
            && (bytes[2] & 0xFF) == 0xBF;
   }
}




