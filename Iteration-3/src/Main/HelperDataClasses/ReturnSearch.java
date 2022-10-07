package Main.HelperDataClasses;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

public class ReturnSearch {

	private SourceOld source;
	private Integer iteration;

	public ReturnSearch(SourceOld key, int i) {
		source = key;
		iteration = i;
	}

	public SourceOld getSource() {
		return source;
	}

	public Integer getIteration() {
		return iteration;
	}

}
