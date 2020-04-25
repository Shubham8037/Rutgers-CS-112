package structures;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	 IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		// COMPLETE THIS METHOD
		
		if(lr == 'l'){//sort the left endpoints only
			for(int i = 0; i < intervals.size(); i ++){
				int index = i; 
				for (int j = i+1; j <intervals.size(); j ++){
					if(intervals.get(j).leftEndPoint < intervals.get(index).leftEndPoint){
						index = j;
					}
	
					Interval small = intervals.get(index);
					intervals.set(index, intervals.get(i));
					intervals.set(i, small); 
				}
			}
			System.out.println("SORTED (LEFT ENDPOINTS) : "+intervals); 
		}
		 
		if (lr == 'r'){//right endpoints sort
			for(int i = 0; i < intervals.size(); i ++){
				int index = i; 
				for(int j = i+1; j <intervals.size(); j ++){
					if(intervals.get(j).rightEndPoint < intervals.get(index).rightEndPoint){
						index = j;
					}
	
					Interval small = intervals.get(index);
					intervals.set(index, intervals.get(i));
					intervals.set(i, small); 
				}
			}
			System.out.println("SORTED (RIGHT ENDPOINTS) : "+intervals); 
		}
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Integer> points = new ArrayList<Integer>();
		
		for (int i = 0; i < leftSortedIntervals.size()-1; i ++){
			if(leftSortedIntervals.get(i).leftEndPoint == leftSortedIntervals.get(i+1).leftEndPoint){
				if(points.contains(leftSortedIntervals.get(i).leftEndPoint)== false){
					points.add(leftSortedIntervals.get(i).leftEndPoint);
				}
			}
			else{
				if(i == 0){
					points.add(leftSortedIntervals.get(i).leftEndPoint);
					points.add(leftSortedIntervals.get(i+1).leftEndPoint);
				}
				else{
					points.add((leftSortedIntervals.get(i+1).leftEndPoint));
				}
			}
		}
		for (int i = 0; i < rightSortedIntervals.size()-1; i ++){
				if(points.contains(rightSortedIntervals.get(i).rightEndPoint)== false){
					//go through the list to see where to put the value
					int value = rightSortedIntervals.get(i).rightEndPoint;
				
					for(int count = 0; count < points.size(); count ++){
						if(points.get(count) > value){

							points.add(count, value);
							count++;
						}
						else if(count == points.size()-1 && points.get(count)<value){
							points.add(value);
						}
					}
				}
			}
		System.out.println(points);

		return points;
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		Queue<IntervalTreeNode> tree = new Queue<IntervalTreeNode>();
		float value;
		for (int m = 0; m < endPoints.size(); m++) {
			value = endPoints.get(m);
			IntervalTreeNode treeNode = new IntervalTreeNode(value, value, value);
			treeNode.leftIntervals = new ArrayList<Interval>();
			treeNode.rightIntervals = new ArrayList<Interval>();
			tree.enqueue(treeNode);
		}
		IntervalTreeNode result = null;
		int treeSize = tree.size;
		while (treeSize > 0) {
			if (treeSize == 1) {
				result = tree.dequeue();
				return result;
			} 
			else {
				int tempSize = treeSize;
				while (tempSize > 1) {
					IntervalTreeNode t1 = tree.dequeue();
					IntervalTreeNode t2 = tree.dequeue();
					float v1 = t1.maxSplitValue;
					float v2 = t2.minSplitValue;
					float x = (v1 + v2) / (2);
					IntervalTreeNode N = new IntervalTreeNode(x, t1.minSplitValue, t2.maxSplitValue);
					N.leftIntervals = new ArrayList<Interval>();
					N.rightIntervals = new ArrayList<Interval>();
					N.leftChild = t1;
					N.rightChild = t2;
					tree.enqueue(N);
					tempSize = tempSize - 2;
				}
				if (tempSize == 1) {
					IntervalTreeNode single = tree.dequeue();
					tree.enqueue(single);
				}
				treeSize = tree.size;
			}
		}
		result = tree.dequeue();
		return result;
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		for (Interval curr : leftSortedIntervals) {
			treeFill(curr, root, true);
		}
		for (Interval curr : rightSortedIntervals) {
			treeFill(curr, root, false);
		}
	}
	
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		
		ArrayList<Interval> result = new ArrayList<Interval>();
		
		System.out.println(root.splitValue);
		
		if(root.splitValue > q.rightEndPoint){
			ArrayList<Interval> x = root.leftIntervals;
			for(int i = 0; i <x.size(); i ++){
				result.add(x.get(i));
				System.out.println("Result: "+result);
			}
		
		}
		
		else if(root.splitValue < q.leftEndPoint){
			ArrayList<Interval> y = root.rightIntervals;
			for(int x = 0; x < y.size(); x++){
				result.add(y.get(x));
				System.out.println("result: "+result);
			}
		}
		return null;
	}

}

