public class LFUCache {
 
    private int size = 0;
    private int capacity = 0;
    
    private Map<Integer,Node> data;
    private Map<Integer,NodeList> counter;
    
    private Node head;
    private Node tail;

    public LFUCache(int capacity) {
        
        this.capacity = capacity;
        this.data = new HashMap<Integer,Node>();
        this.counter = new HashMap<Integer,NodeList>();
        this.head = new Node(0,0,null,null);
        this.tail = new Node(0,0,null,null);
        head.next = tail;
        head.accessCount = Integer.MAX_VALUE;
        tail.previous = head;
        tail.accessCount = 0;
    }
    
    public int get(int key) {
        if(data.get(key)!=null){
            increaseAccessCount(data.get(key));
            return data.get(key).value;
        }
        return -1;
    }
    
    public void set(int key, int value) {
        if(capacity>0){
            if(data.get(key)!=null){
                data.get(key).value = value;
                increaseAccessCount(data.get(key));
            }else{
                if(size==capacity){
                    removeMinCounter();
                }
                addNode(key,value);
            }
        }
    }
    
    private void increaseAccessCount(Node n){
    	int count = n.accessCount;
        NodeList nl = counter.get(count);
        boolean currentOnly = nl.head == nl.tail;
        boolean newCounter = nl.head.previous.accessCount > count+1;
        Node insertAfter = newCounter?counter.get(count).head.previous:counter.get(count+1).head.previous;         
        
        if(currentOnly){
            counter.remove(count);
        }else{
        	if(nl.head == n){
                nl.head = n.next;
            }else if(nl.tail == n){
                nl.tail = n.previous;
            }
        }
        
        if(newCounter){
        	counter.put(count+1, new NodeList(n,n));
        }else{
        	counter.get(count+1).head=n;
        }
        n.previous.next=n.next;
        n.next.previous = n.previous;
        insertAfter(n,insertAfter);
    	n.accessCount = count+1;
    }
    
    private void addNode(int key, int value){
        Node n = new Node(key,value, null,null);
        data.put(key,n);
        if(size==0){
            insertAfter(n,head);
            counter.put(1,new NodeList(n,n));
        }else{
            int minCount = tail.previous.accessCount;
            if(minCount==1){
                insertAfter(n,counter.get(1).head.previous);
                counter.get(1).head=n;
            }else{
                insertAfter(n,counter.get(minCount).tail);
                counter.put(1,new NodeList(n,n));
            }
        }
        size++;
    }
    
    private void removeMinCounter(){
        data.remove(tail.previous.key);
        int minCount = tail.previous.accessCount;
        NodeList minList = counter.get(minCount);
        boolean minOnly = minList.head==minList.tail;
        
        if(minOnly){
            counter.remove(minCount);
        }else{
            counter.get(minCount).tail = tail.previous.previous;
        }
        
        tail.previous.previous.next = tail;
        tail.previous = tail.previous.previous;
        size--;
    }
    
    private void insertBefore(Node n1, Node n2){
    	if(n1==null || n2==null) return;
        n1.next = n2;
        n1.previous = n2.previous;
        if(n2.previous!=null){
            n2.previous.next = n1;
        }
        n2.previous = n1;
    }
    
    private void insertAfter(Node n1, Node n2){
    	if(n1==null || n2==null) return;
        n1.previous = n2;
        n1.next = n2.next;
        if(n2.next!=null){
            n2.next.previous = n1;
        }
        n2.next = n1;
    }
    
    static class NodeList{
        Node head;
        Node tail;
        NodeList(Node head,Node tail){
            this.head = head;
            this.tail = tail;
        }
    }
    
    
    static class Node {
        int key;
        int value;
        Node next;
        Node previous;
        int accessCount;
        Node(int key,int value, Node next, Node previous){
            this.key = key;
            this.value = value;
            this.previous = previous;
            this.next = next;
            accessCount = 1;
        }
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.set(key,value);
 */
