package grabit;

class SinglyLinkedList {
    Node head;
    Node temp;

    public SinglyLinkedList() {
        this.head = null;
        this.temp = null;
    }
    public void addLast(Product data) {
        Node n = new Node(data);
        if (head == null) {
            head = n;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = n;
        }
    }
    void display() {
        if (head == null) {
            System.out.println("List is empty.");
        } else {
            Node temp = head;
            System.out.print("Linked List: ");
            while (temp != null) {
                System.out.print(temp.data + "-->");
                temp = temp.next;
            }
            System.out.println("null");
        }
    }
}