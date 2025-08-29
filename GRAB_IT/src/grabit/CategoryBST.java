package grabit;

class BSTNodeCat {
    Categorie category;
    BSTNodeCat left, right;

    public BSTNodeCat(Categorie category) {
        this.category = category;
        this.left = this.right = null;
    }
}

public class CategoryBST {
    private BSTNodeCat root;

    public CategoryBST() {
        this.root = null;
    }

    public void insert(Categorie category) {
        root = insertRec(root, category);
    }

    private BSTNodeCat insertRec(BSTNodeCat root, Categorie category) {
        if (root == null) {
            root = new BSTNodeCat(category);
            return root;
        }

        if (category.getC_id() < root.category.getC_id()) {
            root.left = insertRec(root.left, category);
        } else if (category.getC_id() > root.category.getC_id()) {
            root.right = insertRec(root.right, category);
        }
        return root;
    }

    public Categorie find(int categoryId) {
        BSTNodeCat result = findRec(root, categoryId);
        return (result != null) ? result.category : null;
    }

    private BSTNodeCat findRec(BSTNodeCat root, int categoryId) {
        if (root == null || root.category.getC_id() == categoryId) {
            return root;
        }

        if (categoryId < root.category.getC_id()) {
            return findRec(root.left, categoryId);
        } else {
            return findRec(root.right, categoryId);
        }
    }

    public void delete(int categoryId) {
        root = deleteRec(root, categoryId);
    }

    private BSTNodeCat deleteRec(BSTNodeCat root, int categoryId) {
        if (root == null) {
            return root;
        }

        if (categoryId < root.category.getC_id()) {
            root.left = deleteRec(root.left, categoryId);
        } else if (categoryId > root.category.getC_id()) {
            root.right = deleteRec(root.right, categoryId);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            root.category = minValue(root.right).category;
            root.right = deleteRec(root.right, root.category.getC_id());
        }
        return root;
    }

    private BSTNodeCat minValue(BSTNodeCat root) {
        BSTNodeCat current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public void displayCategories() {
        System.out.println("--- All Categories (from BST) ---");
        System.out.printf("%-4s | %-25s%n", "ID", "Name");
        System.out.println("----------------------------------------");
        inOrderTraversal(root);
        System.out.println("----------------------------------------");

    }

    private void inOrderTraversal(BSTNodeCat node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.printf("%-4d | %-25s%n", node.category.getC_id(), node.category.getC_name().trim());
            inOrderTraversal(node.right);
        }
    }
}
