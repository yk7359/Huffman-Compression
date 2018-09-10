

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SimpleHuffProcessor implements IHuffProcessor {

   private IHuffViewer myViewer;
   private HuffmanCompress compressInstance;

   // write a new file consist of MAGIC_NUM header format table, compresses
   // data.
   public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
      BitInputStream input = new BitInputStream(in);
      BitOutputStream output = new BitOutputStream(out);
      int result = 0;
      if (!force && compressInstance.getBitsSaved() < 0) {
         myViewer.showError("The compressed file has bigger size than the original file.");
      } else {
         result += compressInstance.compress(input, output);
      }
      output.close();
      input.close();
      return result;
   }

   // Preprocess for the compress method
   public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
      BitInputStream input = new BitInputStream(in);
      // Uses an instance from HuffmanCompress class to perform
      // preprocessCompress
      compressInstance = new HuffmanCompress(freqOfChar(input), headerFormat);
      in.close();
      return compressInstance.getBitsSaved();
   }

   // return int array of the ASCII val frequencies
   private int[] freqOfChar(BitInputStream in) throws IOException {
      // Initializes the array while including the pseudo EOF
      int[] freq = new int[IHuffConstants.ALPH_SIZE + 1];
      freq[IHuffConstants.ALPH_SIZE] = 1;
      boolean done = true;
      // While there are still bits left to read
      while (done) {
         int bit = in.readBits(BITS_PER_WORD);
         if (bit == -1) { // We have reached the pseudo EOF, stop
            done = false;
         } else { // Keep going by increasing the respective frequency
            freq[bit]++;
         }
      }
      return freq;
   }

   public void setViewer(IHuffViewer viewer) {
      myViewer = viewer;
   }

   // uncompress the compressed file
   public int uncompress(InputStream in, OutputStream out) throws IOException {
      BitInputStream input = new BitInputStream(in);
      BitOutputStream output = new BitOutputStream(out);
      int result = 0;
      // Reads the first magic number and only decompress if the numbers match
      if (input.readBits(BITS_PER_INT) == MAGIC_NUMBER) {
         HuffmanDecompress uncompressInstance = new HuffmanDecompress();
         result = uncompressInstance.uncompress(input, output);
      } else {
         myViewer.showError("Can't uncompress");
      }
      in.close();
      out.close();
      return result;
   }

   private void showString(String s) {
      if (myViewer != null)
         myViewer.update(s);
   }
}
