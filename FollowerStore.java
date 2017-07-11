/**

  ** CLASSES **
  FollowerStore is implemented using a left leaning Red Black Binary Search Tree: followerBST. 
  followerBST stores a class called FollowerRelationship.
  FollowerRelationship stores 2 "Self-Sorting" LinkedLists called follower and follows with the UserID as the key for insetion/retrieval.
  KeyValuePair is a class used to help implement the LinkedList.
  ListElement is a class where it contains a KeyValuePair and another ListElement to point to.
  KeyValuePairTopusers is an additional class used for specific methods such as getTopUsers()
  SortedList is an additional class that is a Linked List that is sorts itself by ascending order.
   
  ** TIME AND MEMORY COMPLEXITY **
  Using standard put() and get() methods from a Binary Search Tree, all methods have an average time complexity of O(logn).
  However, in the worst case scenario, all methods can have a time complexity of O(n) because FollowerRelationships stores LinkedLists.
  For example, if 100,000 followers follow the same user, this means that the user with 100,000 followers will have a LinkedList of equal length.
  Consequently, a LinkedList uses a linear search which has an average time complexity of O(n).
  However, this is a very unlikely possibility, because over a large dataset, the linkedlist would be evenly spread across.
  As for memory, FollowerStore has a memory complexity of O(n).

  ** BINARY SEARCH TREE **
  The main reason I chose a Self-Balancing Binary Search Tree is that it offers the scalability of an Array List while it has a relatively fast insertion and retrieval time complexity of FollowerRelationship. 
  If I had used an Array List instead, it may have the same level of scalability because both are unbounded and do not require a resize operation.
  However, the insertion/retrieval method in an Array List requires a linear search or a time complexity of O(n).
  On the other hand, there are other data structures that offer faster insertion/retrieval methods such as a Hashmap.
  Although a Hashmap's insertion/retrieval method has a constant time complexity of O(1), a Binary Search Tree does not require a resize operation or is vulnerable to key collisions.
  This means that over a large data set of 500,000+, a Binary Search Tree can be more efficient because the cost of a Hashmap's resize operation will eventually become more costly than a Binary Search Tree's insertion/retrieval O(logn) time complexity.
  
  ** SELF-SORTING LINKEDLIST **
  The self-sorting Linked List is made from 3 classes: FollowerRelationship, ListElement, KeyValuePair.
  The methods addFollower() and addFollows() in FollowerRelationship help compare dates between a new relationship and stores them in an ordered position.
  Apart from that, ListElement and KeyValuePair is mainly used for storage of ID's and Dates.
  The main advantage of implementing a self-sorting Linked List is that many methods want an array of objects in order of Dates.
  This avoids the need to sort an array after retrieving them, so that methods that fetch an array can be done so quicker.
   
  ** REFERENCES **
  Red Black Binary Search Tree taken from http://algs4.cs.princeton.edu/33balanced/RedBlackLiteBST.java.html
  QUICKSORT taken from //http://www.algolist.net/Algorithms/Sorting/Quicksort
  LinkedList: ListElement and KeyValuePair is based on what we worked on during Labs.
 
 * @author: u1500212
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.Weet;
import uk.ac.warwick.java.cs126.models.User;

import java.util.Date;


public class FollowerStore implements IFollowerStore {
    
    //create the binary search tree
    FollowerRedBlackLiteBST<Integer> followerBST = new FollowerRedBlackLiteBST<Integer>();
    
    public FollowerStore() {
    }
    
    
    /**
      addFollower().
      1) check whether uid1 == uid2, impossible to follow yourself! lol
      2) check whether a FollowerRelationship class exists, if not, create one
      3) check whether uid1 is a follower of uid2 already. NOTE: have to put this after creating FollowerRelationship, otherwise NullPointerException!
      4) Add relationship: (uid1 follows uid2) and (uid2 is followed by uid1)
    */
    
    // User 1 follows User 2
    public boolean addFollower(int uid1, int uid2, Date followDate) {
	//not possible to follow yourself lol
	
        if (uid1 == uid2)
	    return false; 
	
	
	//search whether uid1 and uid2 relation exists or not
	FollowerRelationship userOne = followerBST.get(uid1);
	FollowerRelationship userTwo = followerBST.get(uid2);
	
	// if user 1 or 2 does not exists, create a relationship
	if(userOne == null) {
	    userOne = new FollowerRelationship(uid1);
	    followerBST.put(uid1, userOne);
	}
	
	if(userTwo == null) {
	    userTwo = new FollowerRelationship(uid2);
	    followerBST.put(uid2, userTwo);
	}
	
	// If uid1 ALREADY follows uid2 then return false!!
	// NOTE: have to put this after creating relationship, otherwise NullPointerException!
	if(isAFollower(uid1 ,uid2))
	    return false;
	
	// small tests
	System.out.println("user " + uid1 + " follows user " + uid2 + " on date..." + followDate);
	System.out.println("user " + uid2 + " followed by user " + uid1 + " on date..." + followDate);
	System.out.println();
	
	// update relationship
	// user 1 follows user 2
	// user 2 is followed by user 1
	userOne.addFollows(uid2, followDate);
	userTwo.addFollower(uid1, followDate);
	
        return true;
    }  
    /**
      The following methods uses a standard BST's get method O(logn), followed by different operations.
      getFollowers() and getFollows() retrieves a LinkedList of followers/follows and transfers them into an array of int[].
      For isAFollower(), this is similar except that it searches the list rather than retrieves the entire list.
      For getNumFollowers(), number of followers is already stored in the class so just retrieve it.
    */
    
    public int[] getFollowers(int uid) {
	// standard BST search
	// fetch an array of IDs using getFollowers()
        FollowerRelationship search = followerBST.get(uid);
        return search.getFollowers();
    }

    public int[] getFollows(int uid) {
	// standard BST search
	// fetch an array of IDs using getFollowe()
        FollowerRelationship search = followerBST.get(uid);
        return search.getFollows();
    }

    public boolean isAFollower(int uidFollower, int uidFollows) {
        // standard BST search
        // User linear search to check whether a corresponding user is a follower of another
        FollowerRelationship search = followerBST.get(uidFollower);
        return search.isAFollower(uidFollows);
    }

    public int getNumFollowers(int uid) { 
	// standard BST search
	// number of followers is already stored in FollowerRelationship, so just use get method.
        FollowerRelationship search = followerBST.get(uid);
        return search.getNumberOfFollowers();
    }
    
    /**
      getMutualFollowers() and getMutualFollows() are the same.
      Both fetches 2 linked lists of followers/follows.
      This is followed by comparing each ListElement with each other.
      In the worst-case scenario, this may produce a time complexity of O(n^2). 
      However, this is unlikely given the length of a linkedlist is marginal compared to the size of the Binary Search Tree.
    
     */
    
    public int[] getMutualFollowers(int uid1, int uid2) { 
        // array of ids that follow uid1 AND uid2
        
        // STEPS:
        // 1) Fetch temporary linkedlists from user 1 and user 2 
        // 2) compare each element in linkedlist. if they match, store in another SORTED linkedlist using a new method.
        // 3) turn linkedlist into array to return.
        
        //Step 1
        ListElement tempOne = followerBST.get(uid1).getFollowerListElement();
        ListElement tempTwo = followerBST.get(uid2).getFollowerListElement();
        SortedList sortedList = new SortedList();
        int count = 0;
        
        //Step 2
        // nested for loop
        for(int i = 0; i < followerBST.get(uid1).getNumberOfFollowers(); i++) {
	    for(int j = 0; j < followerBST.get(uid2).getNumberOfFollowers(); j++) {
		if(tempOne.getKVP().getKey() == tempTwo.getKVP().getKey()) {
		    //add into linkedlist if condition is satisfied
		    System.out.println("mutual follower found!: " + tempOne.getKVP().getKey());
		    sortedList.addFollower(tempOne.getKVP().getKey(), tempOne.getKVP().getDate());
		    count++;
		}
		tempTwo = tempTwo.getNext();
	    }
	    //reset list for temp Two and getNext for tempOne
	    tempTwo = followerBST.get(uid2).getFollowerListElement();
	    tempOne = tempOne.getNext();
        }
	
	
	// Step 3
	int[] array = new int[count];
	ListElement temp = sortedList.follower;
        for (int k = 0; k < count; k++) {
	    array[k] = temp.getKVP().getKey();
	    temp = temp.getNext();
        }
        
        return array;
    }

    public int[] getMutualFollows(int uid1, int uid2) {
        // array of ids that are followed by user 1 and 2
        
        // STEPS:
        // 1) Fetch temporary linkedlists from user 1 and user 2 
        // 2) compare each element in linkedlist. if they match, store in another SORTED linkedlist using a new method.
        // 3) turn linkedlist into array to return.
        
        //Step 1
        ListElement tempOne = followerBST.get(uid1).getFollowsListElement();
        ListElement tempTwo = followerBST.get(uid2).getFollowsListElement();
        SortedList sortedList = new SortedList();
        int count = 0;
        
        //Step 2: nested for loop
        for(int i = 0; i < followerBST.get(uid1).getNumberOfFollows(); i++) {
	    for(int j = 0; j < followerBST.get(uid2).getNumberOfFollows(); j++) {
		if(tempOne.getKVP().getKey() == tempTwo.getKVP().getKey()) {
		    //add into linkedlist if found
		    System.out.println("mutual follows found!: " + tempOne.getKVP().getKey());
		    sortedList.addFollower(tempOne.getKVP().getKey(), tempOne.getKVP().getDate());
		    count++;
		}
		tempTwo = tempTwo.getNext();
	    }
	    // reset list for tempTwo and getNext for temp One
	    tempTwo = followerBST.get(uid2).getFollowsListElement(); //reset list
	    tempOne = tempOne.getNext();
        }
	
	// Step 3
	int[] array = new int[count];
	ListElement temp = sortedList.follower;
        for (int k = 0; k < count; k++) {
	    array[k] = temp.getKVP().getKey();
	    temp = temp.getNext();
        }
        
        return array;
    }
    
    /**
      getTopUsers().
      1) Create an array of KeyValuePairTopUsers.
      2) Sort it using QuickSort.
      3) Transfer KeyValuePairTopUsers[] into an int[].
    */
    
    public int[] getTopUsers() {
	
	// create array of KeyValuePairTopUsers using in order traversal
        KeyValuePairTopUsers[] kvpArray = followerBST.inorder();
        
        // sort the array
        quickSort(kvpArray, 0, followerBST.size() - 1);
        
        // transform KVP array into an int array of users
        // test the array again to see sorted or not
        int[] array = new int[followerBST.size()];
        for(int i = 0; i < followerBST.size(); i++) {
	    array[i] = kvpArray[i].getKey();
	    System.out.println("user " + array[i] + " has " + kvpArray[i].getNumberOfFollowers() + " followers");
        }
        
        return array;
    }
    
    /**
    * Additional methods
    */
    
    public static void quickSort(KeyValuePairTopUsers[] array, int low, int high) {
	// if array is null, return
	if (array == null)
	    return;
	
	//pick the pivot
	int middle = low + (high - low)/2;
	KeyValuePairTopUsers pivot = array[middle];
	
	//make left < pivot and right > pivot
	int i = low, j = high;
	while (i <= j) {
	    while(array[i].getNumberOfFollowers() > pivot.getNumberOfFollowers() )
		i++;
	    while(array[j].getNumberOfFollowers() < pivot.getNumberOfFollowers() )
		j--;
	    if (i <= j) {
		KeyValuePairTopUsers temp = array[i];
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

class FollowerRedBlackLiteBST<Key extends Comparable<Key>> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST
    private int n;         // number of key-value pairs in BST

    // BST helper node data type
    private class Node {
        private Key key;           // key
        private FollowerRelationship val;         // associated data
        private Node left, right;  // links to left and right subtrees
        private boolean color;     // color of parent link

        public Node(Key key, FollowerRelationship val, boolean color) {
            this.key = key;
            this.val = val;
            this.color = color;
        }
    }

   /***************************************************************************
    *  Standard BST search.
    ***************************************************************************/

    // return value associated with the given key, or null if no such key exists
    public FollowerRelationship get(Key key) {
        return get(root, key);
    }
    public FollowerRelationship get(Node x, Key key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if      (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else              return x.val;
        }
        return null;
    }

    // is there a key-value pair in the symbol table with the given key?
    public boolean contains(Key key) {
        return get(key) != null;
    }


   /***************************************************************************
    *  Red-black tree insertion.
    ***************************************************************************/

    public void put(Key key, FollowerRelationship val) {
        root = insert(root, key, val);
        root.color = BLACK;
    }

    private Node insert(Node h, Key key, FollowerRelationship val) { 
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
	if (isRed(h.left) && isRed(h.right)) flipColors(h);

        return h;
    }

   /***************************************************************************
    *  Red-black tree helper functions.
    ***************************************************************************/

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


   /***************************************************************************
    *  Utility functions.
    ***************************************************************************/
    // return number of key-value pairs in symbol table
    public int size() {
        return n;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // height of tree (1-node tree has height 0        
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


   /***************************************************************************
    *  Iterate using an inorder traversal.
    *  Iterating through N elements takes O(N) time.
    ***************************************************************************/
    private int count = 0;
    
    public KeyValuePairTopUsers[] inorder() {
        KeyValuePairTopUsers[] kvpArray = new KeyValuePairTopUsers[size()];
        inorder(root, kvpArray);
        count = 0; // reset count to zero again
        return kvpArray;
    }

    private void inorder(Node x, KeyValuePairTopUsers[] kvpArray) { 
        if (x == null) return; 
        inorder(x.left, kvpArray); 
        
        //do stuff here

        kvpArray[count] = new KeyValuePairTopUsers(x.val.userid , x.val.numberOfFollowers);
        count++;
        
        inorder(x.right, kvpArray); 
    }
    
    
 
}

/**
    Each node of the BST stores a FollowerRelationship.
    It stores 2 LinkedList and uses the id as the key.
    addFollower() and addFollows() adds relationships and stores them automatically by dates.
    getFollowers(), getFollows() and isAFollower() are similar methods, with the difference that one retrieves everything and one searches for something.
    
*/

class FollowerRelationship {
    // store userid and number of followers/follows
    int userid;
    int numberOfFollowers;
    int numberOfFollows;
    // linked list
    public ListElement follower;
    public ListElement follows;
    
    public FollowerRelationship(int userid) {
	this.userid = userid;
	numberOfFollowers = 0;
	numberOfFollows = 0;
    }
    
    public void addFollower(Integer followerid, Date date) {
	// create KVP and List element using input
	KeyValuePair kvp = new KeyValuePair(followerid, date);
	ListElement new_element = new ListElement(kvp);
	
	
	// if follower does not exists, create one
	if(this.follower == null) {
	    this.follower = new ListElement(kvp);
	    numberOfFollowers++;
	    return;
	}

	// Three possible cases
	// case 1: Element is more recent than head.
	// case 2: Insert element somewhere in the middle
	// case 3: Element is at the end of list
	
	//case 1
	if(this.follower.getKVP().getDate().compareTo(date) <= 0) {
	    new_element.setNext(follower);  
	    follower = new_element;
	    numberOfFollowers++;
	    return;
	}
	
	//case 2
	ListElement temp = this.follower;
	// while a next element exists
	while(temp.getNext() != null) { 
	    // compares Next Element with date
	    int cmp = temp.getNext().getKVP().getDate().compareTo(date); 
	    if(cmp <= 0) {
		new_element.setNext(temp.getNext());
		temp.setNext(new_element);
		numberOfFollowers++;
		return;
	    }
	    temp = temp.getNext();
	}
	
	//case 3
	temp.setNext(new_element);
	numberOfFollowers++;
	return;
    }
    
    public void addFollows(Integer followsid, Date date) {
	// create KVP and List element using input
	KeyValuePair kvp = new KeyValuePair(followsid, date);
	ListElement new_element = new ListElement(kvp);
	
	// if follows does not exists, create one
	if(this.follows == null) { // if first element then add
	    this.follows = new ListElement(kvp);
	    numberOfFollows++;
	    return;
	}
	
	// Three possible cases
	// case 1: Element is more recent than head.
	// case 2: Insert element somewhere in the middle
	// case 3: Element is at the end of list
	
	// case 1
	if(this.follows.getKVP().getDate().compareTo(date) <= 0) {
	    new_element.setNext(follows);
	    follows = new_element;
	    numberOfFollows++;
	    return;
	}
	
	// case 2
	ListElement temp = this.follows;
	while(temp.getNext() != null) {
	
	    int cmp = temp.getNext().getKVP().getDate().compareTo(date);
	    if(cmp <= 0) {
		new_element.setNext(temp.getNext());
		temp.setNext(new_element);
		numberOfFollows++;
		return;
	    }
	    temp = temp.getNext();
	}
	
	// case 3
	temp.setNext(new_element);
	numberOfFollows++;
	return;
    }
    
    
    // standard get method
    public int getUserId() {
	return this.userid; 
    }
    
    public int[] getFollowers() {
	// create array of int
	int[] array = new int[numberOfFollowers];
	ListElement temp = this.follower;
	
	// transfer the linkedlist into the array
	for(int i = 0; i < numberOfFollowers; i++) {
	    array[i] = temp.getKVP().getKey();
	    temp = temp.getNext();
	}
	
	return array;
    }
    
    public int[] getFollows() {
	// create array of int
	int[] array = new int[numberOfFollows];
	ListElement temp = this.follows;
	
	// transfer the linkedlist into the array
	for(int i = 0; i < numberOfFollows; i++) {
	    array[i] = temp.getKVP().getKey();
	    temp = temp.getNext();
	}
	return array;
    }
    
    public boolean isAFollower(int followsid) {
	// finds out whether this.userid follows followsid
	// linear search
	int[] arrayOfFollows = getFollows();
	
	// check whether the follower exists or not
	for(int i = 0; i < numberOfFollows; i++) {
	    if(arrayOfFollows[i] == followsid) {
		return true;
	    }
	}
	return false;
    }
    
    /**
    * standard get methods
    */
    public int getNumberOfFollowers() {
	return numberOfFollowers;
    }
    
    public int getNumberOfFollows() {
	return numberOfFollows;
    }
    
    public ListElement getFollowerListElement() {
	return follower;
    }
    
    public ListElement getFollowsListElement() {
	return follows;
    }
}

class KeyValuePair {
    //stores integer and date
    private Integer id;
    private Date date;
    
    public KeyValuePair(Integer id, Date date) {
	this.id = id;
	this.date = date;
    }
    
    //standard get method
    public Integer getKey() {
	return id;
    }
    //stanfard get method
    public Date getDate() {
	return date;
    }
    //compares keyvaluepair with another
    public int compareTo(KeyValuePair o) {
	return o.getKey().compareTo(this.getKey());
    }
}

class ListElement {
    // Each list element stores a keyValuePair and another ListElement to point to
    private KeyValuePair kvp;
    private ListElement next;
    
    public ListElement(KeyValuePair kvp) {
	this.kvp = kvp;
    }
    
    // standard get method
    public KeyValuePair getKVP() {
	return this.kvp;
    }
    
    // standard get method
    public ListElement getNext() {
	return this.next;
    }
    
    // set next method
    public void setNext(ListElement e) {
	this.next = e;
    }
}

/**
  This class is used to help the last method getTopUsers().
*/

class KeyValuePairTopUsers {
    // stores id and number of followers
    private Integer id;
    private Integer followers;
    
    public KeyValuePairTopUsers(Integer id, Integer followers) {
	this.id = id;
	this.followers = followers;
    }
    // standard get methods
    public Integer getKey() {
	return id;
    }
    // standard get methods
    public Integer getNumberOfFollowers() {
	return followers;
    }
}

/**
    This class is used to help getMutualFollowers() and getMutualFollows() to store them in order.
*/

class SortedList {
    public ListElement follower;
    
    public void addFollower(Integer followerid, Date date) {
	// create kvp and listelement using the input
	KeyValuePair kvp = new KeyValuePair(followerid, date);
	ListElement new_element = new ListElement(kvp);
	
	//if the follower does not exists, create one
	if(this.follower == null) {
	    this.follower = new ListElement(kvp);
	    return;
	}

	// Three possible cases
	// case 1: Element is more recent than head.
	// case 2: Insert element somewhere in the middle
	// case 3: Element is at the end of list
	
	// case 1
	if(this.follower.getKVP().getDate().compareTo(date) <= 0) {
	    new_element.setNext(follower);  
	    follower = new_element;
	    return;
	}
	
	// case 2
	ListElement temp = this.follower;
	while(temp.getNext() != null) { // while a next element exists
	    
	    int cmp = temp.getNext().getKVP().getDate().compareTo(date); // compares Next Element with date
	    if(cmp <= 0) {
		new_element.setNext(temp.getNext());
		temp.setNext(new_element);
		return;
	    }
	    
	    temp = temp.getNext();
	}
	
	// case 3
	temp.setNext(new_element);
	return;
    }
}
