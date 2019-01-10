import java.math.*;

public class Window {

	public ComplexNumber[] hanningWindow(ComplexNumber[] timeSignal, String space) {
		ComplexNumber[] adjSignal = new ComplexNumber[timeSignal.length];
		for (int k = 0; k < timeSignal.length; k++) {
			if (space == "real" && timeSignal[k].getImaginary() != 0) {
				throw new IllegalArgumentException("Time domain signal must be entirely in real space");
			}
			else if (space == "imaginary" && timeSignal[k].getReal() != 0) {
				throw new IllegalArgumentException("Time domain signal must be entirely imaginary space");
			}
			if (space == "real") {
				adjSignal[k] = new ComplexNumber(timeSignal[k].getReal() * calculateHanning(k, timeSignal.length), 0);
			}
			else {
				adjSignal[k] = new ComplexNumber(0, timeSignal[k].getImaginary() * calculateHanning(k, timeSignal.length));
			}
		}
		return adjSignal;
	}
	
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
