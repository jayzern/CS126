/**

  ** CLASSES **
  UserStore is implemented using 2 left leaning Red Black Binary Search Trees: useridBST and userdateBST. 
  useridBST is used to store Users using their ID as the key, with the assumption that all ID's are unique.
  userdateBST is used to store Users using their Dates as the key.
  A Queue is implemented to support methods that return an array of Users.
 
  ** TIME AND MEMORY COMPLEXITY **
  Using standard put() and get() methods from a Binary Search Tree, addUser() and getUser() has an average time complexity of O(logn).
  Using an in-order traversal, getUsersContaining(), getUsersJoinedBefore() and getUsers() has an average time complexity of O(n) because it has to visit every node of Tree.
  The inorder traversal works by using a Queue to enqueue any data that satisfies different conditions, followed by transfering the Queue into an array of Users.
  For example, getUsersContaining() uses an in-order traversal and enqueues any data that contains the specific key word. 
  The memory of UserStore is 2n, where 2n is the number of Users stored in useridBST and userdateBST, hence the memory complexity is O(n).
  
  ** BINARY SEARCH TREE **
  The main reason I chose a Self-Balancing Binary Search Tree is that it offers the scalability of an Array List while it has a relatively fast insertion and retrieval time complexity of Users. 
  If I had used an Array List instead, it may have the same level of scalability because both are unbounded and do not require a resize operation.
  However, the insertion/retrieval method in an Array List requires a linear search or a time complexity of O(n).
  On the other hand, there are other data structures that offer faster insertion/retrieval methods such as a Hashmap.
  Although a Hashmap's insertion/retrieval method has a constant time complexity of O(1), a Binary Search Tree does not require a resize operation or is vulnerable to key collisions.
  This means that over a large data set of 500,000+, a Binary Search Tree can be more efficient because the cost of a Hashmap's resize operation will eventually become more costly than a Binary Search Tree's insertion/retrieval O(logn) time complexity.
  
  ** ADDITIONAL METHODS ** 
  Apart from data structures, I implemented additional methods such as queueToWeetArray() for the last method getTrending().
  I also implemented a Quicksort algorithm to sort an array of TrendingTopics.
  Quicksort has a best case time complexity of O(n*logn).
   
  ** REFERENCES ** 
  Red Black Binary Search Tree taken from http://algs4.cs.princeton.edu/33balanced/RedBlackLiteBST.java.html
  Queue taken from http://algs4.cs.princeton.edu/13stacks/Queue.java.html
   
   
 * @author: u1500212
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;

import java.io.BufferedReader;
import java.util.Date;
import java.io.FileReader;
import java.text.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;


//Additional imports
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UserStore implements IUserStore {
    
    //create 2 Binary Search Trees
    UserRedBlackBST<Integer> useridBST = new UserRedBlackBST<Integer>();
    UserDateRedBlackBST userdateBST = new UserDateRedBlackBST();
    int size = 0;
    
    public UserStore() {
    }

    /**
      addUser().
      1) check whether user already exists, if yes, return false.
      2) add user into date and id Tree.
    */
    
    public boolean addUser(User usr) {
        // get the ID and DATE
        int userid = usr.getId();
        Date date = usr.getDateJoined();
        
        //if user does not exists yet, put into useridBST and userdateBST
        if(getUser(userid) == null) {
            useridBST.put(userid, usr);
            userdateBST.put(usr);
            size++;
            return true;
        }
        // otherwise if it already exists, return false
        return false;
    }
    
    /**
      Standard BST get method.
    */
    
    public User getUser(int uid) {
        return useridBST.get(uid);
    }

    /** The following methods uses an inorder Traversal to visit all nodes from the Binary Search Tree.
    * Since nodes are already sorted by order, I used a Queue to enqueue any data that satisfies each method's condition.
    * I implemented a method called queueToUserArray to transfer the Queue into an array of Users.
    */
    
    public User[] getUsers() {
        // TODO
        Queue<User> userQueue = userdateBST.inOrderUsers();
        return queueToUserArray(userQueue);
    }

    public User[] getUsersContaining(String query) {
        // TODO
        Queue<User> userQueue = userdateBST.inOrderUsersContaining(query);
        return queueToUserArray(userQueue);
    }

    public User[] getUsersJoinedBefore(Date dateBefore) {
        // TODO
        Queue<User> userQueue = userdateBST.inOrderUsersBefore(dateBefore);
        return queueToUserArray(userQueue);
    }
    
 
    /**
    * Additional method for in-order Traversal.
    */
    
    public User[] queueToUserArray(Queue<User> queue) {
	// create an array of the size of the queue
	int sizeOfArray = queue.size();
	User[] userArray = new User[sizeOfArray];
	
	// dequeue into the array
	for(int i = 0; i < sizeOfArray; i++) {
	    userArray[i] = queue.dequeue();
	}
	
	return userArray;
    }
}

    /**
     UserRedBlackBST and UserDateRedBlackBST are both similar classes.
     The main difference is that UserDateRedBlackBST has more methods for in-order traversal and both uses different keys: ID and Date.
    */

class UserRedBlackBST<Key extends Comparable<Key>> { 
    
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node root; // root of BST
    private int n; // number of key value pairs
    
    private class Node {
	private Key key;
	private User user; // changed from Value to User
	private Node left, right;
	private boolean color;
	
	public Node(Key key, User user, boolean color) {
	    this.key = key;
	    this.user = user;
	    this.color = color;
	}
    }
    
    public User get(Key key) {
      return get(root, key);
    }
    
    public User get(Node x, Key key) {
	while (x != null) { //while node is not empty
	    int cmp = key.compareTo(x.key); // compare key with current node
	    if (cmp < 0) 
            x = x.left;
	    else if (cmp > 0) 
            x = x.right;
	    else
		return x.user;
	}
	return null;
    }
    
    public boolean contains(Key key) {
	return get(key) != null;
    }
    
    public void put(Key key, User user) {
	root = insert(root, key, user);
	root.color = BLACK;
    }
    
    public Node insert(Node h, Key key, User user) {
	if (h == null) {
	    n++;
	    return new Node(key, user, RED); // RED node when inserted.
	}
	
	int cmp = key.compareTo(h.key);
	if (cmp < 0)
	    h.left = insert(h.left, key, user);
	else if (cmp > 0)
	    h.right = insert(h.right, key, user);
	else
	    h.user = user;
	
	//fixes right leaning links
	if (isRed(h.right) && !isRed(h.left)) // if right child red and left child black, rotate left
	    h = rotateLeft(h);
	if (isRed(h.left) && isRed(h.left.left))
	    h = rotateRight(h);
	if (isRed(h.left) && isRed(h.right))
	    flipColors(h);
	    
	return h;
    }
    
    private boolean isRed(Node x) {
	if (x == null) return false; //all nulls are black by default
	return x.color == RED;
    }
    
    private Node rotateRight(Node h) {
	assert (h != null) && isRed(h.left);
	Node x = h.left; // create new node x
	h.left = x.right;
	x.right = h;
	x.color = h.color;
	h.color = RED;
	return x;
    }
    
    private Node rotateLeft(Node h) {
	assert (h != null) && isRed(h.right);
	Node x = h.right;
	h.right = x.left;
	x.left = h;
	x.color = h.color;
	h.color = RED;
	return x;
    }
    
    //pre condition two children are red, node is black
    //post condition two children are black, node is reduce
    private void flipColors(Node h) {
	assert !isRed(h) && isRed(h.left) && isRed(h.right);
	h.color = RED;
	h.left.color = BLACK;
	h.right.color = BLACK;
    }
    
    public int size() {
	return n;
    }
    
    public boolean isEmpty() {
	return n == 0;
    }
    
    public int height() {
	return height(root);
    }
    
    public int height(Node x) {
	if (x == null)	
	    return -1;
	return 1 + Math.max(height(x.left), height(x.right));
    }   
}

class UserDateRedBlackBST {
    
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node root; // root of BST
    private int n; // number of key value pairs
    
    private class Node {
	private User user; // changed from Value to User
	private Node left, right;
	private boolean color;
	
	public Node(User user, boolean color) {
	    this.user = user;
	    this.color = color;
	}
    }
    
    public void put(User user) {
	root = insert(root, user);
	root.color = BLACK;
    }
    
    public Node insert(Node h, User user) {
	if (h == null) {
	    n++;
	    return new Node(user, RED); // RED node when inserted.
	}
	
	int cmp = user.getDateJoined().compareTo(h.user.getDateJoined());
	// VERY IMPORTANT NOTE, changed to LESS THAN OR EQUALS, because possible for users to join on the same date!!
	if (cmp >= 0) 
	    h.left = insert(h.left, user);
	else if (cmp < 0)
	    h.right = insert(h.right, user);
	else
	    h.user = user;
	
	//fixes right leaning links
	if (isRed(h.right) && !isRed(h.left)) // if right child red and left child black, rotate left
	    h = rotateLeft(h);
	if (isRed(h.left) && isRed(h.left.left))
	    h = rotateRight(h);
	if (isRed(h.left) && isRed(h.right))
	    flipColors(h);
	    
	return h;
    }
    
    private boolean isRed(Node x) {
	if (x == null) return false; //all nulls are black by default
	return x.color == RED;
    }
    
    private Node rotateRight(Node h) {
	assert (h != null) && isRed(h.left);
	Node x = h.left; // create new node x
	h.left = x.right;
	x.right = h;
	x.color = h.color;
	h.color = RED;
	return x;
    }
    
    private Node rotateLeft(Node h) {
	assert (h != null) && isRed(h.right);
	Node x = h.right;
	h.right = x.left;
	x.left = h;
	x.color = h.color;
	h.color = RED;
	return x;
    }
    
    //pre condition two children are red, node is black
    //post condition two children are black, node is reduce
    private void flipColors(Node h) {
	assert !isRed(h) && isRed(h.left) && isRed(h.right);
	h.color = RED;
	h.left.color = BLACK;
	h.right.color = BLACK;
    }
    
    /**
    * Inorder traversal methods to fetch an array of Users.
    */
    
    public Queue<User> inOrderUsers() {
	Queue<User> queue = new Queue<User>();
	inOrderUsers(root, queue);
	return queue;
    }
    
    private void inOrderUsers(Node x, Queue<User> queue) { 
        if (x == null) return; 
        
        inOrderUsers(x.left, queue);
        queue.enqueue(x.user);
        inOrderUsers(x.right, queue); 
    }       
    
    //inorder traversal for getUsersContaining
    public Queue<User> inOrderUsersContaining(String query) {
	Queue<User> queue = new Queue<User>();
	inOrderUsersContaining(root, queue, query);
	return queue;
    }
    
    private void inOrderUsersContaining(Node x, Queue<User> queue, String query) { 
        if (x == null) return; 
        
        inOrderUsersContaining(x.left, queue, query);
	if(x.user.getName().contains(query))
	    queue.enqueue(x.user);
        inOrderUsersContaining(x.right, queue, query); 
    }    
  
    //inorder traversal for getUsersBefore
    public Queue<User> inOrderUsersBefore(Date date) {
	Queue<User> queue = new Queue<User>();
	inOrderUsersBefore(root, queue, date);
	return queue;
    }
    
    private void inOrderUsersBefore(Node x, Queue<User> queue, Date date) {
	if (x == null) return;
	
	inOrderUsersBefore(x.left, queue, date);
	int cmp = x.user.getDateJoined().compareTo(date);
	if(cmp < 0) {
	    queue.enqueue(x.user);
	}
	inOrderUsersBefore(x.right, queue, date);
    }
}

  /**
  * Queue used for inorder Traversal
  */

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

    public boolean isEmpty() {
        return first == null;
    }

    public int size() {
        return n;
    }

    public Item peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        return first.item;
    }

    public void enqueue(Item item) {
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
