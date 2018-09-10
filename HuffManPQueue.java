

import java.util.ArrayList;

public class HuffManPQueue<E extends Comparable<? super E>> {
    
    private ArrayList<E> con;

    // constructor
    public HuffManPQueue() {
        con = new ArrayList<>();
    }

    // add element with a tie breaker that is like in a line.
    public boolean add(E val) {
        boolean temp = false;
        for (int i = 0; i < con.size(); i++) {
            if (val.compareTo(con.get(i)) < 0) { // If value is less than current data
                con.add(i, val);
                return true;
            }
            if (val.compareTo(con.get(i)) == 0) { // Wait to be added
                temp = true;
            }
            if (val.compareTo(con.get(i)) > 0) { // If value is greater than current
                // Adds the value to the end of the line of data with equal priorities
                if (temp) { 
                    con.add(i, val);
                    return true;
                }
            }
        }
        con.add(val);
        return true;
    }

    // remove the element at the index
    public E remove(int index) {
        return con.remove(index);
    }

    // return number of element
    public int size() {
        return con.size();
    }

    // get the element at the index
    public E get(int index) {
        return con.get(index);
    }
}
