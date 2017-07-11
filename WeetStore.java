/**

  ** CLASSES **
  WeetStore is implemented using 3 left leaning Red Black Binary Search Trees: idBST, dateBST and trendingBST. 
  idBST is used to store Weets using their ID as the key, with the assumption that all ID's are unique.
  dateBST is used to store Weets using their Dates as the key.
  trendingBST is used to store a Key Value Pair called TrendingTopic, with the String as the key.
  TrendingTopics stores a String topic and int timesMentioned, for the last method getTrending().
  A Queue is implemented to support methods that return an array of Weets.
 
  ** TIME AND MEMORY COMPLEXITY **
  Using standard put() and get() methods from a Binary Search Tree, addWeet() and getWeet() have an average time complexity of O(logn).
  Using an in-order traversal, getWeets(), getWeetsByUser(), getWeetsContaining(), getWeetsOn(), getWeetsBefore() and getTrending() has an average time complexity of O(n) because it has to visit all nodes in the Binary Search Tree.
  The in-order traversal works by using a Queue to enqueue any data that satisfies different conditions, followed by transfering the Queue into an array of Weets. 
  For example, getWeetsByUser() uses an in-order traversal and enqueues any Weets that was written by a specific user.
  The memory of WeetStore is 2n+m where 2n is the number of weets stored in idBST and dataBST and m is the number of Trending Topics stored in trendingBST, hence the memory complexity is O(n).
  
  ** BINARY SEARCH TREE **
  The main reason I chose a Self-Balancing Binary Search Tree is that it offers the scalability of an Array List while it has a relatively fast insertion and retrieval time complexity of Weets. 
  If I had used an Array List instead, it may have the same level of scalability because both are unbounded and do not require a resize operation.
  However, the insertion/retrieval method in an Array List requires a linear search or a time complexity of O(n).
  On the other hand, there are other data structures that offer faster insertion/retrieval methods such as a Hashmap.
  Although a Hashmap's insertion/retrieval method has a constant time complexity of O(1), a Binary Search Tree does not require a resize operation and is not vulnerable to key collisions.
  This means that over a large data set of 500,000+, a Binary Search Tree can be more efficient because the cost of a Hashmap's resize operation will eventually become more costly than a Binary Search Tree's insertion/retrieval O(logn) time complexity.
  
  ** ADDITIONAL METHODS ** 
  Apart from data structures, I implemented additional methods such as queueToWeetArray() for the last method getTrending().
  I also implemented a Quicksort algorithm to sort an array of TrendingTopics.
  Quicksort has a best case time complexity of O(n*logn).
 
  ** REFERENCES **
  Red Black Binary Search Tree taken from http://algs4.cs.princeton.edu/33balanced/RedBlackLiteBST.java.html
  Queue taken from http://algs4.cs.princeton.edu/13stacks/Queue.java.html
  QUICKSORT taken from //http://www.algolist.net/Algorithms/Sorting/Quicksort

  
 @author: u1500212
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;
import uk.ac.warwick.java.cs126.models.Weet;

import java.io.BufferedReader;
import java.util.Date;
import java.io.FileReader;
import java.text.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


//Additional imports
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.*;


public class WeetStore implements IWeetStore {
   
   
    // create idBST, dateBST and trendingBST
    WeetRedBlackLiteBST<Integer, Weet> idBST = new WeetRedBlackLiteBST<Integer, Weet>();
    WeetDateRedBlackBST dateBST = new WeetDateRedBlackBST();
    WeetRedBlackLiteBST<String, TrendingTopics> trendingBST = new WeetRedBlackLiteBST<String, TrendingTopics>();
    int size = 0;
    
    public WeetStore() {
    }

    /**
     addWeet()
     1) Check whether the ID already exists first, if yes, then return false.
     2) Insert Weet into idBST and dateBST.
     3) Check whether there is a #trend, if yes, add into trendingBST.
    */
    
    public boolean addWeet(Weet weet) {
	
	// get the ID and Date
        int id = weet.getId();
        Date date = weet.getDateWeeted();
        
        // if it already exists then return false;
        if(getWeet(id) != null)
	    return false; 
        
        // put weet in ID and Date Binary Search trees
	idBST.put(id, weet); 
	dateBST.put(weet);
	size++;
	
	// check whether there is a pattern using java.util.regex.*
	Pattern pattern = Pattern.compile("#(\\w+|\\W+)");
	Matcher match = pattern.matcher(weet.getMessage());
	
	// if there is a pattern, create a trend if it does not exists or update the current one.
	if(match.find() == true) {
	    // check whether it exists
	    String word = match.group();
	    TrendingTopics trending = trendingBST.get(word);
	    
	    //if null, create one
	    if (trending == null) {
		TrendingTopics new_topic = new TrendingTopics(word);
		trendingBST.put(new_topic.getTopic() , new_topic);
	    // else just update it
	    } else {
		trending.increaseTimesMentioned();
	    }
	}
	return true;
    }
    
    /**
      getWeet().
      Standard BST get() method.
      O(logn) time complexity.
    */
    
    public Weet getWeet(int wid) {
	// Standard BST get() method
        return idBST.get(wid);
    }
    /**
    * The following methods uses an inorder Traversal to visit all nodes from the Binary Search Tree.
    * All nodes are already sorted by order, so no need to sort it using a sorting algorithm.
    * I used a Queue to enqueue any data that satisfies each method's condition while doing the traversal.
    * The reason I used a Queue is so that I can use the size() method later to create an array of that size.
    * I implemented a method called queueToWeetArray to transfer the Queue into an array of Weets (Weet[]).
    * Note: getWeetsOn() only works for weets matching the exact date + time.
    */
    public Weet[] getWeets() {
        Queue<Weet> weetQueue = dateBST.inOrderDates();
        return queueToWeetArray(weetQueue);
    }

    public Weet[] getWeetsByUser(User usr) {
        Queue<Weet> weetQueue = dateBST.inOrderUser(usr.getId());
        return queueToWeetArray(weetQueue);
    }

    public Weet[] getWeetsContaining(String query) {
        Queue<Weet> weetQueue = dateBST.inOrderContaining(query);
        return queueToWeetArray(weetQueue);
    }

    public Weet[] getWeetsOn(Date dateOn) {
        Queue<Weet> weetQueue = dateBST.inOrderDateOn(dateOn);
        return queueToWeetArray(weetQueue);
    }

    public Weet[] getWeetsBefore(Date dateBefore) {
        Queue<Weet> weetQueue = dateBST.inOrderDateBefore(dateBefore);
        return queueToWeetArray(weetQueue);

    }
    /**
      getTrending()
      1) Create an array of 10 TrendingTopics and fetch TrendingTopic Queue.
      2) If trendingArray is less than 10, return null.
      3) Sort the trendingArray using Quicksort O(n*logn).
      4) Return an array of String.
    */
    
    public String[] getTrending() {
        
        //Create a Queue of TrendingTopics using an inorder traversal on trendingBST.
        Queue<TrendingTopics> trendingQueue = new Queue<TrendingTopics>();
        trendingQueue = trendingBST.inorderTraversal();
        
        // create an arbitrary array of 10 TrendingTopics.
        TrendingTopics[] trendingArray = new TrendingTopics[10];
        
        //Transfer the Queue into an array
        for (int i = 0; i < 10; i++) {
	    trendingArray[i] = trendingQueue.dequeue();
	    
	    // if less than 10 values then return null
	    if(trendingArray[i] == null) return null; 
        }
        
        //Sort the array such that the Weet with the most times mentioned is first
        quickSort(trendingArray, 0, 9);
        
        // transfers the trendingArray into a string array
        String[] array = new String[10];
        for (int i = 0; i < 10; i++) {
	    array[i] = trendingArray[i].getTopic();
	    System.out.println(array[i] + " has " + trendingArray[i].getTimesMentioned());
        }
        
        return array;
    }
    
    /**
     Additional methods.
    */
    public Weet[] queueToWeetArray(Queue<Weet> queue) {
    
	// create array of the size of the queue
	int sizeOfArray = queue.size();
	Weet[] weetArray = new Weet[sizeOfArray];
	
	
	// dequeue into the array
	for(int i = 0; i < sizeOfArray; i++) {
	    weetArray[i] = queue.dequeue();
	}
	
	return weetArray;
    }
    
    //http://www.algolist.net/Algorithms/Sorting/Quicksort
    public static void quickSort(TrendingTopics[] array, int low, int high) {
	// if its null, return and don't sort
	if (array == null)
	    return;
	
	//pick the pivot
	int middle = low + (high - low)/2;
	TrendingTopics pivot = array[middle];
	
	//make left < pivot and right > pivot
	int i = low, j = high;
	while (i <= j) {
	    //compare times mentioned to sort.
	    while(array[i].getTimesMentioned() > pivot.getTimesMentioned() )
		i++;
	    while(array[j].getTimesMentioned() < pivot.getTimesMentioned() )
		j--;
	    if (i <= j) {
		TrendingTopics temp = array[i];
		array[i] = array[j];
		array[j] = temp;
		i++;
		j--;
	    }   
	}
	
	//recursively sort two sub parts
	if (low < j)
	    quickSort(array, low, j);
	if (high > i)
	    quickSort(array, i, high);
    }
}

/**
  WeetDateRedBlackBST is an edited version of WeetRedBlackLiteBST.
  The key difference is that dateBST has more methods for in-order Traversal and the Generic 'Value' is changed to Weet.
*/

class WeetDateRedBlackBST {
    
    // Colours to self baalance
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node root; // root of BST
    private int n; // number of key value pairs
    
    // Nodes for the BST
    private class Node {
	// changed from Value to Weet
	private Weet weet; 
	private Node left, right;
	private boolean color;
	
	public Node(Weet weet, boolean color) {
	    this.weet = weet;
	    this.color = color;
	}
    }
    
    public void put(Weet weet) {
	root = insert(root, weet);
	// set the root as black
	root.color = BLACK;
    }
    
    public Node insert(Node h, Weet weet) {
	if (h == null) { // if the root is null, create red node
	    n++;
	    return new Node(weet, RED); // RED node when inserted.
	}
	
	
	//STANDARD BST INSERT METHOD
	int cmp = weet.getDateWeeted().compareTo(h.weet.getDateWeeted());
	if (cmp >= 0) // VERY IMPORTANT NOTE, MORE THAN OR EQUALS
	    h.left = insert(h.left, weet);
	else if (cmp < 0)
	    h.right = insert(h.right, weet);
	else
	    h.weet = weet;
	
	
	// RED BLACK TREE SELF-BALANCING
	// if right child red and left child black, rotate left
	if (isRed(h.right) && !isRed(h.left)) 
	    h = rotateLeft(h);
	if (isRed(h.left) && isRed(h.left.left))
	    h = rotateRight(h);
	if (isRed(h.left) && isRed(h.right))
	    flipColors(h);
	    
	return h;
    }
    
    /**
    * The following methods are used to support the Self-Balancing functions
    */
    
    private boolean isRed(Node x) {
	if (x == null) return false; //all nulls are black by default
	return x.color == RED;
    }
    
    private Node rotateRight(Node h) {
	// if the node is null & left node is red
	assert (h != null) && isRed(h.left);
	Node x = h.left; // set node to left
	h.left = x.right; // set left to right
	x.right = h; // set right to head
	x.color = h.color; // set color of new x to original head color
	h.color = RED; // set h to red
	return x;
    }
    
    private Node rotateLeft(Node h) {
	// if the node is null & right node is red
	assert (h != null) && isRed(h.right);
	Node x = h.right; // set new node to right
	h.right = x.left; // set right to left of new node
	x.left = h; // set left of new node to head
	x.color = h.color; // set new node color to original color
	h.color = RED; // set h to red
	return x;
    }
    
    // pre condition two children are red, node is black
    // post condition two children are black, node is reduce
    private void flipColors(Node h) {
	assert !isRed(h) && isRed(h.left) && isRed(h.right);
	h.color = RED;
	h.left.color = BLACK;
	h.right.color = BLACK;
    }
 
    /**
    * Inorder traversal methods to fetch an array of Weets.
    */
    
    public Queue<Weet> inOrderDates() {
	Queue<Weet> queue = new Queue<Weet>();
	inOrderDates(root, queue);
	return queue;
    }
    
    private void inOrderDates(Node x, Queue<Weet> queue) { 
        if (x == null) return; 
        
        inOrderDates(x.left, queue);
	queue.enqueue(x.weet);
        inOrderDates(x.right, queue); 
    }      
    
    //inorder traversal for getWeetsByUser
    public Queue<Weet> inOrderUser(int uid) {
	Queue<Weet> queue = new Queue<Weet>();
	inOrderUser(root, queue, uid);
	return queue;
    }
    
    private void inOrderUser(Node x, Queue<Weet> queue, int uid) { 
        if (x == null) return; 
        
        inOrderUser(x.left, queue, uid);
        if (x.weet.getUserId() == uid) // if userID matches while traversing ERROR
	  queue.enqueue(x.weet);
        inOrderUser(x.right, queue, uid); 
    }
    
    //inorder traversal for getWeetsContaining
    public Queue<Weet> inOrderContaining(String query) {
	Queue<Weet> queue = new Queue<Weet>();
	inOrderContaining(root, queue, query);
	return queue;
    }
    
    private void inOrderContaining(Node x, Queue<Weet> queue, String query) {
	if (x == null) return;
	
	inOrderContaining(x.left, queue, query);
	if(x.weet.getMessage().contains(query))
	    queue.enqueue(x.weet);
	inOrderContaining(x.right, queue, query);
    }
    
    //inorder traversal for getWeetsOn
    public Queue<Weet> inOrderDateOn(Date date) {
	Queue<Weet> queue = new Queue<Weet>();
	inOrderDateOn(root, queue, date);
	return queue;
    }
    
    private void inOrderDateOn(Node x, Queue<Weet> queue, Date date) {
	if (x == null) return;
	
	inOrderDateOn(x.left, queue, date);
	int cmp = x.weet.getDateWeeted().compareTo(date);
	if(cmp == 0) {
	    queue.enqueue(x.weet);
	    //System.out.println(x.weet.getDateWeeted());
	}
	inOrderDateOn(x.right, queue, date);
    }
    
    //inorder traversal for getWeetsBefore
    public Queue<Weet> inOrderDateBefore(Date date) {
	Queue<Weet> queue = new Queue<Weet>();
	inOrderDateBefore(root, queue, date);
	return queue;
    }
    
    private void inOrderDateBefore(Node x, Queue<Weet> queue, Date date) {
	if (x == null) return;
	
	inOrderDateBefore(x.left, queue, date);
	int cmp = x.weet.getDateWeeted().compareTo(date);
	if(cmp < 0) {
	    queue.enqueue(x.weet);
	    //System.out.println(x.weet.getDateWeeted());
	}
	inOrderDateBefore(x.right, queue, date);
    }
    
}

/**
* Generic RedBlackBinarySearchTree
*/

class WeetRedBlackLiteBST<Key extends Comparable<Key>, Value> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST
    private int n;         // number of key-value pairs in BST

    // BST helper node data type
    private class Node {
        private Key key;           // key
        private Value val;         // associated data
        private Node left, right;  // links to left and right subtrees
        private boolean color;     // color of parent link

        public Node(Key key, Value val, boolean color) {
            this.key = key;
            this.val = val;
            this.color = color;
        }
    }
    
    public Value get(Key key) {
        return get(root, key);
    }
    public Value get(Node x, Key key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if      (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else              return x.val;
        }
        return null;
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

    public void put(Key key, Value val) {
        root = insert(root, key, val);
        root.color = BLACK;
    }

    private Node insert(Node h, Key key, Value val) { 
        if (h == null) {
            n++;
            return new Node(key, val, RED);
        }

        int cmp = key.compareTo(h.key);
        if      (cmp < 0) h.left  = insert(h.left,  key, val); 
        else if (cmp > 0) h.right = insert(h.right, key, val); 
        else              h.val   = val;

        // fix-up any right-leaning links
        if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
        if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);

        return h;
    }
    
    /**
    * Methods to support self-balancing mechanism
    */
    
    // is node x red (and non-null) ?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // rotate right
    private Node rotateRight(Node h) {
        assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    // rotate left
    private Node rotateLeft(Node h) {
        assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    // precondition: two children are red, node is black
    // postcondition: two children are black, node is red
    private void flipColors(Node h) {
        assert !isRed(h) && isRed(h.left) && isRed(h.right);
        h.color = RED;
        h.left.color = BLACK;
        h.right.color = BLACK;
    }


    // return number of key-value pairs in symbol table
    public int size() {
        return n;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // height of tree (1-node tree has height 0)
    public int height() { return height(root); }
    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    // return the smallest key; null if no such key
    public Key min() { return min(root); }
    private Key min(Node x) {
        Key key = null;
        while (x != null) {
            key = x.key;
            x = x.left;
        }
        return key;
    }

    // return the largest key; null if no such key
    public Key max() { return max(root); }
    private Key max(Node x) {
        Key key = null;
        while (x != null) {
            key = x.key;
            x = x.right;
        }
        return key;
    }

    public Queue<Value> inorderTraversal() {
        Queue<Value> queue = new Queue<Value>();
        inorderTraversal(root, queue);
        return queue;
    }

    private void inorderTraversal(Node x, Queue<Value> queue) { 
        if (x == null) return;
        inorderTraversal(x.left, queue); 
        queue.enqueue(x.val);
        inorderTraversal(x.right, queue); 
    } 

}


class Queue<Item>{
    private Node<Item> first;    // beginning of queue
    private Node<Item> last;     // end of queue
    private int n;               // number of elements on queue

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    public Queue() {
        first = null;
        last  = null;
        n = 0;
    }
    
    // checks whether Queue is empty
    public boolean isEmpty() {
        return first == null;
    }
    
    // returns the size of the Queue
    public int size() {
        return n;
    }
    
    // peek at the first item
    public Item peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        return first.item;
    }
    
    //add into queue
    public void enqueue(Item item) {
	// set last as old last
        Node<Item> oldlast = last;
        last = new Node<Item>();
        last.item = item;
        last.next = null;
        if (isEmpty()) first = last;
        else           oldlast.next = last;
        n++;
    }
    
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        Item item = first.item;
        first = first.next;
        n--;
        if (isEmpty()) last = null;   // to avoid loitering
        return item;
    }
}

/**
  Class for getTrending().
*/

class TrendingTopics {
    // key value pairs
    private String topic;
    private int timesMentioned;
    
    public TrendingTopics(String topic) {
	this.topic = topic;
	timesMentioned = 1;
    }
    
    // get method
    public String getTopic() {
	return topic;
    }
    
    // get method
    public int getTimesMentioned() {
	return timesMentioned;
    }
    
    // increment times mentioned
    public void increaseTimesMentioned() {
	timesMentioned++;
    }
}
