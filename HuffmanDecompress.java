import java.io.IOException;

public class HuffmanDecompress implements IHuffConstants {

   private static final int ONEBIT = 1;

   // Default constructor should suffice

   // uncompress the compressed file
   public int uncompress(BitInputStream input, BitOutputStream output) throws IOException {
      int result = 0;
      HuffmanTree tree;
      // Gets the header, already read the magic number in SimpleHuff
      int header = input.readBits(BITS_PER_INT);
      if (STORE_COUNTS == header) { // If we are reading the count format
         // Gets the frequency table and creates the tree
         int freq[] = readCount(input);
         tree = new HuffmanTree(freq);
         result = writeData(tree.getRoot(), input, output);
      } else if (STORE_TREE == header) { // if we are reading the tree format
         // Reads the size of the tree, did not use it in implementation
         input.readBits(BITS_PER_INT);
         TreeNode root = treeHelper(input);
         result = writeData(root, input, output);
      }
      return result;
   }

   // get the root which represent the entire tree used for decompression
   private TreeNode treeHelper(BitInputStream in) throws IOException {
      if (in.readBits(1) == 0) { // If we are at an internal node
         return new TreeNode(treeHelper(in), -1, treeHelper(in));
      } else { // We are at a leaf
         // we don't know about frequencies but it doesn't matter
         return new TreeNode(in.readBits(BITS_PER_WORD + 1), 0);
      }
   }

   // write the decompressed data.
   private int writeData(TreeNode n, BitInputStream in, BitOutputStream out) throws IOException {
      TreeNode temp = n;
      int result = 0;
      int asciiVal = -1;
      while (asciiVal != ALPH_SIZE) { // While we have not reached pseudo EOF
         int bit = in.readBits(ONEBIT);
         if (temp.isLeaf()) { // If we hit a leaf
            // Change asciiVal and write the bits
            asciiVal = temp.getValue();
            temp = n;
            if (asciiVal == ALPH_SIZE) { // If the new asciiVal is pseudo EOF
               break;
            }// Write out results
            out.writeBits(BITS_PER_WORD, asciiVal);
            result += BITS_PER_WORD;
         }
         temp = changeTemp(bit, temp);
      }
      return result;
   }

   // Traverses the tree depending on the direction given
   private TreeNode changeTemp(int bit, TreeNode temp) {
      // Traverses the tree depending on the direction
      if (bit == 0) {
         return temp.getLeft();
      }
      return temp.getRight();
   }

   // read the table of standard count format
   private int[] readCount(BitInputStream input) throws IOException {
      int[] freq = new int[ALPH_SIZE + 1];
      // Initializes the frequency of the pseudo EOF
      freq[ALPH_SIZE] = 1;
      for (int i = 0; i < ALPH_SIZE; i++) { // Puts in the respective
                                            // frequencies into the array
         freq[i] = input.readBits(BITS_PER_INT);
      }
      return freq;
   }
}
