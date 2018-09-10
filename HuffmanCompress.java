import java.io.IOException;
import java.util.Map;

public class HuffmanCompress implements IHuffConstants {

   private HuffmanTree tree;
   private int bitsSaved;
   private int header;
   private int[] freq;

   // Construct the HuffmanCompress by taking in the variables from SimpleHuff
   // Performs the preprocess method
   public HuffmanCompress(int[] freq, int headerFormat) {
      int bits = 0;
      header = headerFormat;
      // Gets the frequency and create the tree
      tree = new HuffmanTree(freq);
      // bits written in compressed file for the header
      bits -= headerBits(headerFormat);
      // (bits in data file - bits written in compressed file for the data)
      bits += tree.getBits();
      // bits written in compressed file for MAGIC_NUM and headerFormat
      bits -= 2 * BITS_PER_INT;
      // pass to compress method
      bitsSaved = bits;
      this.freq = freq;
   }

   // Returns bitsSaved for preprocess
   public int getBitsSaved() {
      return bitsSaved;
   }

   // Compresses the file
   public int compress(BitInputStream input, BitOutputStream output) throws IOException {
      int result = 0;
      // wrote two Integer
      result += writeMagic(output);
      // wrote the table according to which headerFormat
      result += writeTable(output);
      // wrote the compressed data.
      result += writeCompressData(input, output);
      return result;
   }

   // return bits written in compressed file for the header
   private int headerBits(int headerFormat) {
      if (headerFormat == STORE_COUNTS) { // Returns 256 * 32
         return ALPH_SIZE * BITS_PER_INT;
      } else if (headerFormat == STORE_TREE) { // Returns tree's size
         return tree.getSize() + BITS_PER_INT;// size of the tree and size of
                                              // the tree in
                                              // binary(BITS_PER_INT);
      }
      return 0; // Custom option
   }

   // write the Table according to the headerFormat
   // return bits written in the method.
   private int writeTable(BitOutputStream out) {
      int result = 0;
      if (header == STORE_COUNTS) { // If we are writing the table of counts
         // Loop through the frequency table and write out the bits
         for (int i = 0; i < ALPH_SIZE; i++) {
            out.writeBits(BITS_PER_INT, freq[i]);
         }
         result = ALPH_SIZE * BITS_PER_INT;
      } else if (header == STORE_TREE) { // If we are writing tree format
         String treeTable = tree.getTreeFormat();
         // Loop through the tree and write out its String representation
         for (int i = 0; i < treeTable.length(); i++) {
            out.writeBits(1, treeTable.charAt(i));
         }
         result = treeTable.length();
      }
      // May be an else for custom option, but Mike stated we don't have to
      // worry about that
      return result;
   }

   // write magic number and headerFormat
   // return bits written in this method;
   private int writeMagic(BitOutputStream out) {
      out.writeBits(BITS_PER_INT, MAGIC_NUMBER);
      out.writeBits(BITS_PER_INT, header);
      return BITS_PER_INT * 2;
   }

   // write the compressed data
   // return bits written in the method.
   private int writeCompressData(BitInputStream in, BitOutputStream out) throws IOException {
      int result = 0;
      // Gets the encoding map from the tree
      Map<Integer, String> encodeMap = tree.getTable();
      boolean peof = false;
      while (!peof) { // While we haven't reached the pseudo EOF
         int bit = in.readBits(BITS_PER_WORD);
         String code = "";
         if (bit == -1) { // If we reached the pseudo EOF
            peof = true;
            code = encodeMap.get(ALPH_SIZE);
         } else { // Gets the code from the corresponding bit
            code = encodeMap.get(bit);
         }
         for (int i = 0; i < code.length(); i++) { // Write out the bits to the file
            out.writeBits(1, code.charAt(i));
         }
         result += code.length();
      }
      return result;
   }
}
