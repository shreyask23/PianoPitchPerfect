public class CrossCorr {

	private int[] arr1;
	private int[] arr2;
	private int[] corrArr;
	private int max = Integer.MIN_VALUE;
	private int min = Integer.MAX_VALUE;
	private int abs_max = -1;
	
	public CrossCorr(int[] arr1, int[] arr2) {
		this.arr1 = arr1.clone();
		this.arr2 = arr2.clone();
		circCorr();
	}
	
	private void circCorr() {
		int[] corrArr = new int[arr1.length];
		for (int k = 0; k < arr1.length; k++) {
			int dotVal = dot(arr1, arr2);
			corrArr[k] = dotVal;
			if (max < dotVal) max = dotVal;
			if (min > dotVal) min = dotVal;
			arr2 = roll(arr2);
		}
		arr2 = roll(arr2);
		if (Math.abs(min) > max) {
			abs_max = Math.abs(min);
		}
		else {
			abs_max = max;
		}
		this.corrArr = corrArr;
	}
	
	public int dot (int[] arr1, int[] arr2) {
		assert arr1.length == arr2.length;
		int dotProduct = 0;
		for (int k = 0; k < arr1.length; k++) {
			dotProduct += arr1[k] * arr2[k];
		}
		return dotProduct;
	}
	
	public int[] roll(int[] arr1) {
		int[] tempArr = new int[arr1.length];
		tempArr[0] = arr1[arr1.length-1];
		for (int k = 1; k < arr1.length; k++) {
			tempArr[k] = arr1[k-1];
		}
		return tempArr;
	}
	
	public int[] getCorrArr() {
		return corrArr;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getAbsMax() {
		return abs_max;
	}
}