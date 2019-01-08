import java.math.*;

public class Window {

	public double[] hanningWindow(int[] timeSignal) {
		double[] adjSignal = new double[timeSignal.length];
		for (int k = 0; k < timeSignal.length; k++) {
			adjSignal[k] = timeSignal[k] * calculateHanning(k, timeSignal.length);
		}
		return adjSignal;
	}
	
	public double[] hanningWindow(double[] timeSignal) {
		double[] adjSignal = new double[timeSignal.length];
		for (int k = 0; k < timeSignal.length; k++) {
			adjSignal[k] = timeSignal[k] * calculateHanning(k, timeSignal.length);
		}
		return adjSignal;
	}
	
	private double calculateHanning(int k, int N) {
		return 0.5 - 0.5 * (Math.cos(2 * Math.PI * k / N));
	}
	
}
