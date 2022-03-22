import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class MaxTotalAbility {


	static class Node {
		int ability;
		String name;
		Node next, child, parent;
		String which;// a variable to find the lions that added in list

		public Node(String name, int ability) {
			this.name = name;
			this.ability = ability;
			next = child = parent = null;
			which = "";
		}

		public Node(String name) {
			this.name = name;
			this.ability = 0;
			next = child = parent = null;
		}

		public int getAbility() {
			return ability;
		}

		public void setAbility(int ability) {
			this.ability = ability;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getWhich() {
			return which;
		}

		public void setWhich(String which) {
			this.which = which;
		}

	}

	// --------------------------------------------------------
	static void addSibling(Node lion, String name) {
		Node parent = lion.parent;
		while (lion.next != null) {// taking last sibling to add new sibling
			lion = lion.next;
		}
		lion.next = new Node(name);
		lion.next.parent = parent;
	}

	// ---------------------------------------------------------
	static void addChild(Node lion, String name) {
		if (lion.child != null) {// if lion has a child then add new lion to the child as sibling
			addSibling(lion, name);
		} else {// if lion does not have a child then add the new lion as left child
			lion.child = new Node(name);
			lion.child.parent = lion;
		}
	}

	// ----------------------------------------------------

	// -------------------------------------------------------
	public static int DP(Node lion) {
		if (lion == null)
			return 0;
		HashMap<Node, Integer> takenLions = new HashMap<>();// a hash map to keep node that taken and the max
		// values that can be reached from them
		int a = getMaxSumUtil(lion, takenLions);// call the function for root
		return a;
	}

	// root of tree, child-sibling, parent-sibling, child or sibling
	static public boolean createTree(Node root, String from, String to, String hier) {
		boolean flag = false;
		if (root == null)
			return false;
		while (root != null && flag == false) {
			if (root.getName().equals(to) && hier.equals("Left-Child")) {// if the target lion found
				addChild(root, from); // and the hierarchy between it and lion
				root.child.parent = root; // to be added is Left-Child then call addChild function
				flag = true;
			} else if (root.getName().equals(to) && hier.equals("Right-Sibling")) {
				addSibling(root, from);// if the hierarchy is Right-Sibling then call
				flag = true; // addSibling function
			}
			if (root.child != null && flag == false)
				flag = createTree(root.child, from, to, hier);// if the still is not in
			//this level of depth then call create tree function with the child of current node
			if (flag == false) // if lion still not found then search it in the siblings
				root = root.next;
		}
		return flag;

	}

	static public Node addAbility(Node root, String name, int ability) {
		if (root == null)
			return null;

		if (root.name.equals(name)) {// if the lion found then set its ability
			root.setAbility(ability);
			return root;
		}

		if (root.child != null)// if the lion not found then search it in children
			root.child = addAbility(root.child, name, ability);
		if (root.next != null)// if the lion not found then search it in siblings
			root.next = addAbility(root.next, name, ability);

		return root;
	}

	// a fundtion to take go through all grandchilren and gather their abilities
	public static int sumOfGrandChildren(Node lion, HashMap<Node, Integer> takenLions) {
		int sum = 0;
		// call for children of left child only if it is not NULL
		if (lion.child != null) {
			sum += getMaxSumUtil(lion.child.child, takenLions);
			Node temp = lion.child.child;
			if (temp != null) {
				while (temp.next != null) {// call the function again for grandchild's siblings
					temp = temp.next;
					sum += getMaxSumUtil(temp, takenLions);
				}
			}
			Node temp2 = lion.child;
			while (temp2.next != null) {// call the other children's children that is grandChildren of main lion
				temp2 = temp2.next;
				sum += getMaxSumUtil(temp2.child, takenLions);
				Node temp3 = temp2.child;
				if (temp3 != null) {
					while (temp3.next != null) {// call the function again for this grandchild's siblings
						temp3 = temp3.next;
						sum += getMaxSumUtil(temp3, takenLions);
					}
				}

			}

		}
		return sum;
	}

	// Utility method to return maximum sum rooted at node 'node'
	public static int getMaxSumUtil(Node lion, HashMap<Node, Integer> takenLions) {
		if (lion == null)
			return 0;

		// If node is already processed then return calculated
		// value from map
		if (takenLions.containsKey(lion))
			return takenLions.get(lion);

		// take current node value and call for all grand children
		int grandChildren = lion.ability + sumOfGrandChildren(lion, takenLions);

		// don't take current node value and call for all children
		Node child = lion.child;
		int children = getMaxSumUtil(child, takenLions);
		if (child != null) {
			while (child.next != null) {
				child = child.next;
				children += getMaxSumUtil(child, takenLions);
			}
		}
		// choose maximum from both above calls and store that in map
		if (grandChildren > children)// if the grandchildren's total abilities are bigger than the children's then
			lion.setWhich("itself");// mark the lion as itself to know that this node will be used for the list
		else
			lion.setWhich("child");// if the children's total abilities are bigger than the grandchildren's then
									// mark the lion as itself to know that this node will be used for the list
		takenLions.put(lion, Math.max(grandChildren, children));// put the lion to the map with max value

		return takenLions.get(lion);
	}

	// a function to print the lions that marked as 'itself'
	public static void printDP(Node lion) {
		Node tempNode = lion;
		if (tempNode.which.equals("itself")) {
			if (tempNode.parent != null && !tempNode.parent.which.equals("itself")) {// if the lions parent also
				System.out.println(tempNode.name + " " + tempNode.ability);// marked as 'itself' then not prit the lion
			} else if (tempNode.parent != null && tempNode.parent.which.equals("itself")
					&& tempNode.parent.parent.which.equals("itself")) {// if the lions parent also marked as 'itself'
				System.out.println(tempNode.name + " " + tempNode.ability);// but its grandparent also marked as
																			// 'itself'
																			// then print the lion
			} else if (tempNode.parent == null) // if lions parent is null then the lion is root then print it
				System.out.println(tempNode.name + " " + tempNode.ability);
		}
	}

	static public void traverseTree(Node root) {// traverse the tree and send all the lions to the 'printDP' function
		if (root == null)
			return;
		while (root != null) {
			printDP(root);
			if (root.child != null)
				traverseTree(root.child);
			root = root.next;
		}
	}

	// ---------------------------------------------------
	public static int[] Greedy(Node root) {

		int sumOfGrand = root.ability + grandSum(root);// take roots ability and call grandsum function to
		// gather the grandchildren's ability
		Node child = root.child;
		int sumOfChild = child.ability + childSum(child);// take child's ability and call call 'childSum' function to
		// gather all children's ability
		int[] intArr = new int[2];
		intArr[0] = sumOfGrand;
		intArr[1] = sumOfChild;
		return intArr;
	}

	// a function to gather all the grandchildren's ability
	public static int grandSum(Node lion) {
		Node tempGrand = lion;
		Node tempChild = lion.child;
		int sumOfGrand = 0;

		while (tempGrand != null && tempGrand.child != null) {
			tempGrand = tempGrand.child.child;// take the next grandchildren (jump to 2 level below)
			if (tempGrand != null) {
				sumOfGrand += tempGrand.ability;// take the ability of the grandchild
				if (tempGrand.next != null) {
					Node tempNextGrand = tempGrand.next;// take the grandchild's sibling
					while (tempNextGrand != null) {
						sumOfGrand += tempNextGrand.ability + grandSum(tempNextGrand);// take it's ability and
						// call the function again for it to gather its grandchildren's ability
						tempNextGrand = tempNextGrand.next; // take other siblings

					}
				}

			}

		}
		while (tempChild != null && tempChild.child != null) {
			if (tempChild.next != null) {
				Node tempNextChild = tempChild.next;// take the main lions other children
				while (tempNextChild != null) {
					if (tempChild.next != null) {
						Node tempNextGrand = tempNextChild.child;// take the child's child which is grandchild of main
																	// lion
						while (tempNextGrand != null) {
							sumOfGrand += tempNextGrand.ability + grandSum(tempNextGrand);// take it's ability and call
																							// the
							// function again to gather it's grandchildren's ability
							tempNextGrand = tempNextGrand.next;// take the grandchild's siblings
						}
					}
					tempNextChild = tempNextChild.next;// go to other child
				}

			}
			tempChild = tempChild.child.child;// go down for another 2 levels to take other grandchildren
		}

		return sumOfGrand;
	}

	// a function to gather all the children's ability
	public static int childSum(Node node) {
		int sumOfChild = 0;
		Node keepDepth = node;
		while (keepDepth != null && keepDepth.child != null) {
			sumOfChild += keepDepth.ability;// take first child's ability
			Node nextChild = keepDepth.next;// go to next child
			if (nextChild != null) {
				while (nextChild != null) {
					sumOfChild += nextChild.ability;// take it's ability
					nextChild = nextChild.next;// go to other children
				}
			}
			keepDepth = keepDepth.child.child;// go down for 2 levels to take other children
		}
		return sumOfChild;
	}

	// THIS FUNCTION USES THE SAME ALGORITHM AS 'grandSum' FUNCTION BUT THIS ONLY
	// PRINTS THE LIONS
	public static void printGrand(Node root) {
		Node tempGrand = root;
		Node tempChild = root.child;

		while (tempGrand != null && tempGrand.child != null) {
			tempGrand = tempGrand.child.child;
			if (tempGrand != null) {
				System.out.println(tempGrand.name + " " + tempGrand.ability);
				if (tempGrand.next != null) {
					Node tempNextGrand = tempGrand.next;
					while (tempNextGrand != null) {
						System.out.println(tempNextGrand.name + " " + tempNextGrand.ability);
						grandSum(tempNextGrand);
						tempNextGrand = tempNextGrand.next;

					}
				}

			}

		}
		while (tempChild != null && tempChild.child != null) {
			if (tempChild.next != null) {
				Node tempNextChild = tempChild.next;
				while (tempNextChild != null) {
					if (tempChild.next != null) {
						Node tempNextGrand = tempNextChild.child;
						while (tempNextGrand != null) {
							System.out.println(tempNextGrand.name + " " + tempNextGrand.ability);
							grandSum(tempNextGrand);
							tempNextGrand = tempNextGrand.next;
						}
					}
					tempNextChild = tempNextChild.next;
				}

			}
			tempChild = tempChild.child.child;
		}
	}

	// THIS FUNCTION USES THE SAME ALGORITHM AS 'childSum' FUNCTION BUT THIS ONLY
	// PRINTS THE LIONS
	public static void printChild(Node root) {
		Node keepDepth = root;
		while (keepDepth != null && keepDepth.child != null) {
			System.out.println(keepDepth.name + " " + keepDepth.ability);
			Node nextChild = keepDepth.next;
			if (nextChild != null) {
				while (nextChild != null) {
					System.out.println(nextChild.name + " " + nextChild.ability);
					nextChild = nextChild.next;
				}
			}
			keepDepth = keepDepth.child.child;
		}
	}

	// ----------------------------------------------------

	public static void main(String[] args) {

		try {
													
			FileReader fileReader = new FileReader("xxxx.txt");
			String line;
			BufferedReader br = new BufferedReader(fileReader);
			// long startTime = System.nanoTime();//to keep starting time
			line = br.readLine();// skip first line
			line = br.readLine();// take root
			String to = line.split("\t")[0];
			String from = line.split("\t")[1];
			String hier = line.split("\t")[2];
			Node root = new Node(to);
			if (hier.equals("Left-Child") && root.child == null) {
				addChild(root, from);// add first child
			}
			while ((line = br.readLine()) != null) {// read other lines and create tree
				to = line.split("\t")[0];
				from = line.split("\t")[1];
				hier = line.split("\t")[2];
				createTree(root, from, to, hier);

			}
			br.close();
			FileReader fileReader2 = new FileReader("hunting_abilities.txt");
			String line2;
			BufferedReader br2 = new BufferedReader(fileReader2);
			line2 = br2.readLine();
			while ((line2 = br2.readLine()) != null) {// add abilities to the lions
				String name = line2.split("\t")[0];
				int ability = Integer.valueOf(line2.split("\t")[1]);
				root = addAbility(root, name, ability);
			}
			br2.close();
			System.out.println("Dynamic Programming");
			System.out.println("DP results: " + DP(root));
			System.out.println("DP results - selected lions");
			traverseTree(root);
			System.out.println("<<<----------------------------------------------->>>");
			System.out.println("Greedy Algorithm");
			int[] intArr = Greedy(root);
			if (intArr[0] > intArr[1]) { // if sum grandchildren's ability is bigger than sum of children's ability then
				// print root and call 'printGrand' function to print all the grandchildren
				System.out.println("Greedy results: " + intArr[0]);
				System.out.println("Greedy results - selected lions");
				System.out.println(root.name + " " + root.ability);
				printGrand(root);
			} else {// if sum of children's ability is bigger than sum of grandchildren's ability
					// then
					// print child and call 'printChild' function to print all the children
				System.out.println("Greedy results: " + intArr[1]);
				System.out.println("Greedy results - selected lions ");
				System.out.println(root.child.name + " " + root.child.ability);
				printChild(root.child);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
