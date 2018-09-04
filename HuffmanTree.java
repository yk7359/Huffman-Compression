/*  Student information for assignment:
 *
 *  On our honor, Yuki Kamayama and Bohan Zhang, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: 1
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID: yk7359
 *  email address: yk7359@utexas.edu
 *  Grader name: Aish
 *
 *  Student 2 
 *  UTEID: bz3824
 *  email address: bohanz2009@gmail.com
 *
 */

import java.util.HashMap;
import java.util.Map;

public class HuffmanTree implements IHuffConstants {
    // root of the Tree
    private TreeNode root;
    // key for ASCII value, value for the compression code
    private Map<Integer, String> table;
    // frequencies for each ASCII val
    private int[] freq;
    // size of the tree
    private int size;
    // bits required to write into file
    private int bits;

    // constructor
    public HuffmanTree(int[] freq) {
        root = null;
        size = 0;
        bits = 0;
        table = new HashMap<>();
        bits += createTree(freq);
        bits -= createTable(root, "");
        this.freq = freq;
        // -1 for peof
    }
    
    // Creates the table with corresponding frequency
    private int createTable(TreeNode n, String code) {
        if (n.isLeaf()) {
            table.put(n.getValue(), code);
            return code.length() * n.getFrequency();
        } else {
            return createTable(n.getLeft(), code + "0") + createTable(n.getRight(), code + "1");
        }
    }

    // get the root node
    private int createTree(int[] freq) {
        int count = 0;
        HuffManPQueue<TreeNode> nodes = new HuffManPQueue<>();
        // +1 for PEOF
        for (int i = 0; i < IHuffConstants.ALPH_SIZE + 1; i++) {
            if (freq[i] != 0) {
                TreeNode temp = new TreeNode(i, freq[i]);
                nodes.add(temp);
                size += 10;
                count += freq[i];
            }
        }
        while (nodes.size() > 1) {
            // -1 for a node that is only for internal node
            TreeNode nod3 = new TreeNode(nodes.remove(0), -1, nodes.remove(0));
            nodes.add(nod3);
            size++;
        }
        root = nodes.get(0);
        return (count - 1) * BITS_PER_WORD;
    }
    
    // Gets the TreeFormat
    public String getTreeFormat() {
        // Calls the recursive method
        String result = treeFormat(root);
        String temp = Integer.toBinaryString(getSize());
        // Adds the leading zeros
        for (int i = temp.length(); i < BITS_PER_INT; i++) {
            temp = "0" + temp;
        }
        result = temp + result;
        return result;
    }
    
    // Returns the treeFormat
    private String treeFormat(TreeNode n) {
        if (n.isLeaf()) { // If we hit a leaf
            // Gets the binary string of the value
            String temp = Integer.toBinaryString(n.getValue());
            // Adds the leading zeros
            for (int i = temp.length(); i < BITS_PER_WORD + 1; i++) {
                temp = "0" + temp;
            }
            // Return the value with 1 in front based on instruction
            return "1" + temp;
        } else {
            return "0" + treeFormat(n.getLeft()) + treeFormat(n.getRight());
        }
    }
    
    // Returns the size
    public int getSize() {
        return size;
    }
    
    // Returns the root
    public TreeNode getRoot() {
        return root;
    }
    
    // Returns the map table
    public Map<Integer, String> getTable() {
        return table;
    }
    
    // Returns the frequency table
    public int[] getFreq() {
        return freq;
    }
    
    // Returns the number of bits
    public int getBits() {
        return bits;
    }
}