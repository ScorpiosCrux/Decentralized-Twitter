package Main.HelperDataClasses;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

public class ReturnSearch {

	private Source source;
	private Integer iteration;

	public ReturnSearch(Source key, int i) {
		source = key;
		iteration = i;
	}

	public Source getSource() {
		return source;
	}

	public Integer getIteration() {
		return iteration;
	}

}
