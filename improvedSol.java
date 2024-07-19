class Tree {
    public boolean isLocked;
    public int id;
    public Tree parent;
    public List<Tree> children;
    public int lockedDescendantsCount; // Count of locked descendants
    public List<Tree> lockedDescendants; // List of locked descendants

    public Tree(Tree parent) {
        this.isLocked = false;
        this.id = -1;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.lockedDescendantsCount = 0;
        this.lockedDescendants = new ArrayList<>();
    }

    public boolean lock(Tree node, int userId) {
        if (node.isLocked) return false;

        // Check if any ancestor is locked
        Tree current = node;
        while (current != null) {
            if (current.isLocked) return false;
            current = current.parent;
        }

        // Check if any descendant is locked
        if (node.lockedDescendantsCount > 0) return false;

        // Lock the node
        node.isLocked = true;
        node.id = userId;

        // Update locked descendants count and list for ancestors
        current = node.parent;
        while (current != null) {
            current.lockedDescendantsCount++;
            current.lockedDescendants.add(node);
            current = current.parent;
        }

        return true;
    }

    public boolean unlock(Tree node, int userId) {
        if (!node.isLocked || node.id != userId) return false;

        // Unlock the node
        node.isLocked = false;
        node.id = -1;

        // Update locked descendants count and list for ancestors
        Tree current = node.parent;
        while (current != null) {
            current.lockedDescendantsCount--;
            current.lockedDescendants.remove(node);
            current = current.parent;
        }

        return true;
    }

    public boolean upgradeLock(Tree node, int userId) {
        if (node.isLocked) return false;

        // Check if all locked descendants are locked by the same user
        for (Tree lockedDescendant : node.lockedDescendants) {
            if (lockedDescendant.id != userId) return false;
        }

        // Unlock all descendants
        for (Tree lockedDescendant : node.lockedDescendants) {
            if (!unlock(lockedDescendant, userId)) return false;
        }

        // Lock the current node
        return lock(node, userId);
    }
}
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt(); // no. of nodes
        int m = sc.nextInt(); // no. of children per node
        int q = sc.nextInt(); // number of queries
        String rootName = sc.next();

        Tree root = new Tree(null);
        Map<String, Tree> nodeMap = new HashMap<>();
        nodeMap.put(rootName, root);

        Queue<Tree> queue = new LinkedList<>();
        queue.add(root);

        for (int i = 1; i < n; i++) {
            Tree parent = queue.peek();
            String nodeName = sc.next();

            Tree node = new Tree(parent);
            nodeMap.put(nodeName, node);
            parent.children.add(node);

            if (parent.children.size() == m) {
                queue.poll();
            }
            queue.add(node);
        }

        for (int i = 0; i < q; i++) {
            int operationType = sc.nextInt();
            String nodeName = sc.next();
            int userId = sc.nextInt();
            Tree node = nodeMap.get(nodeName);
            boolean result = false;

            if (operationType == 1) {
                result = root.lock(node, userId);
            } else if (operationType == 2) {
                result = root.unlock(node, userId);
            } else if (operationType == 3) {
                result = root.upgradeLock(node, userId);
            }

            System.out.println(result ? "true" : "false");
        }

        sc.close();
    }
}
