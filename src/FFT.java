import java.math.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FFT {
	
	public ComplexNumber[] fft(ComplexNumber[] arr) {
		return fftSynthesis(bitRevSort(arr));
	}
	
	private ComplexNumber[] fftSynthesis(ComplexNumber[] arr) {
		int layers = (int) (Math.log(arr.length)/Math.log(2));
		return fftButterfly(1, layers, arr);
	}
	
	private ComplexNumber[] fftButterfly(int currStep, int totalSteps, ComplexNumber[] arr) {
		ComplexNumber[] freq = arr.clone();
		int N = (int) Math.pow(2, currStep);
		ComplexNumber W = new ComplexNumber(-2 * Math.PI / N);
		ComplexNumber[] evenIndices, oddIndices; //Slices sub-arrays into even and odd indices to undo bit reverse sorting
		for (int k = 0; k < arr.length; k += N) {
			evenIndices = dilute(Arrays.copyOfRange(arr, k, k + N / 2), "even", W);
			oddIndices = dilute(Arrays.copyOfRange(arr, k + N / 2, k + N), "odd", W);
			for (int j = 0; j < evenIndices.length; j++) {
				freq[k + j] = evenIndices[j].add(oddIndices[j]);
			}
		}
		if (currStep < totalSteps) {
			return fftButterfly(currStep + 1, totalSteps, freq);
		}
		else {
			return freq;
		}
	}
	
	private ComplexNumber[] dilute(ComplexNumber[] arr, String parity, ComplexNumber sinusoid) {
		//Halfway step of FFT Butterfly– changes frequency spectrum to reflect interlaced dilution of time domain data with zeros
		ComplexNumber[] dilutedArr = new ComplexNumber[arr.length*2];
		if (parity.equals("even")) {
			//Duplicates elements in the frequency domain
			System.arraycopy(arr, 0, dilutedArr, 0, arr.length);
			System.arraycopy(arr, 0, dilutedArr, arr.length, arr.length);
		}
		else if (parity.equals("odd")) {
			//Duplicates elements in frequency domain and multiplies by sinusoids
			System.arraycopy(arr, 0, dilutedArr, 0, arr.length);
			System.arraycopy(arr, 0, dilutedArr, arr.length, arr.length);
			for (int k = 0; k < dilutedArr.length; k++) {
				dilutedArr[k] = dilutedArr[k].mult(sinusoid.exp(k));
			}
		}
		else {
			throw new IllegalArgumentException("Parity must only be \"even\" or \"odd\". The parity given was " + parity);
		}
		return dilutedArr;
	}
	
	private ComplexNumber[] bitRevSort(ComplexNumber[] unsortedArr) {
		if (!(unsortedArr.length > 0 && (unsortedArr.length & (unsortedArr.length - 1)) == 0)) {
			//Checks if unsortedArr has length that is power of 2
			throw new IllegalArgumentException("FFT input length must be power of two. The input length was " + unsortedArr.length);
		}
		ComplexNumber[] sortedArr = new ComplexNumber[unsortedArr.length];
		for (int k = 0; k < unsortedArr.length; k++) {
			int revK = bitReverse(k, unsortedArr.length);
			sortedArr[revK] = unsortedArr[k];
		}
		return sortedArr;
	}
	
	private int bitReverse(int k, int length) {
		String binary = Integer.toBinaryString(k);
		int binLength = (int) (Math.log(length)/Math.log(2));
		if (binary.length() < binLength) {
			int padLength = binLength - binary.length();
			String zeroPad = new String(new char[padLength]).replace("\0", "0");
			binary = zeroPad + binary;
		}
		binary = binary.substring(binary.length() - binLength);
		String revBinary = new StringBuilder(binary).reverse().toString();
		return Integer.parseInt(revBinary, 2);
	}
	
	public double[] freqToMagnitude(ComplexNumber[] fftArr) {
		double[] magnitudes = new double[fftArr.length];
		for (int k = 0; k < fftArr.length; k++) {
			magnitudes[k] = fftArr[k].magnitude();
		}
		return magnitudes;
	}
	
	public ArrayList<Integer> findLocalPeaks(double[] allMagnitudes) {
		ArrayList<Integer> peakIndices = new ArrayList<Integer>();
		if (allMagnitudes.length == 2) {
			peakIndices.add(1);
			return peakIndices;
		}
		else if (allMagnitudes.length == 4) {
			peakIndices.add(1);
			peakIndices.add(2);
			return peakIndices;
		}
		double[] magnitudes = new double[allMagnitudes.length / 2 + 1];
		System.arraycopy(allMagnitudes, 0, magnitudes, 0, allMagnitudes.length / 2 + 1); //Copies magnitudes including DC offset and excluding mirrored outputs
		int len = magnitudes.length;
		if (magnitudes[1] >= magnitudes[2]) {
			peakIndices.add(1);
		}
		for (int k = 2; k < len - 1; k++) {
			if (magnitudes[k - 1] <= magnitudes[k] && magnitudes[k] >= magnitudes[k + 1]) {
				peakIndices.add(k);
			}
		}
		if (magnitudes[len - 1] >= magnitudes[len - 2]) {
			peakIndices.add(len - 1);
		}
		return peakIndices;
	}
	
	public double checkFrequencyPeak(double[] magnitudes, ArrayList<Integer> peakIndices, double targetFreq, double samplingRate) {
		int approxBin = approxFreqBin(magnitudes, samplingRate, targetFreq);
		int approxBinPeak = peakIndices.indexOf(approxBin); //Checks if approxBin is in peakIndices; -1 if absent
		if (targetFreq % (targetFreq / magnitudes.length) == 0 && approxBinPeak != -1) {
			//targetFreq is a peak
			return magnitudes[approxBin];
		}
		else if (approxBinPeak != -1) {
			//targetFreq is just above peak
			return quadraticInterpolation(magnitudes[approxBin - 1], magnitudes[approxBin], magnitudes[approxBin + 1]);
		}
		else if (peakIndices.indexOf(approxBin + 1) != -1) {
			//targetFreq is just below peak
			return quadraticInterpolation(magnitudes[approxBin], magnitudes[approxBin + 1], magnitudes[approxBin + 2]);
		}
		else {
			//targetFreq not near peak
			return 0;
		}
	}
	
	public int approxFreqBin(double[] magnitudes, double samplingRate, double targetFreq) {
		if (targetFreq > samplingRate / 2) {
			throw new IllegalArgumentException("Provided frequency was " + Double.toString(targetFreq) + ". Nyquist limit was " + Double.toString(samplingRate / 2));
		}
		double binResolution = samplingRate / magnitudes.length;
		return new Double(targetFreq / binResolution).intValue(); //Bin that equals targetFreq or is just under it
	}
	
	private double quadraticInterpolation(double left, double peak, double right) {
		//Returns max of quadratic interpolation given three sample values
		if (left == peak && peak == right) return left; //Checks linearity of samples, which is only possible if all 3 samples are equal (given how peaks were selected)
		return peak - (((right - left) * (right - left)) / (4 * (right - 2 * peak + left)));
	}
	
	public int maxPeak(double[] magnitudes) {
		double max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int k = 1; k < magnitudes.length / 2 + 1; k++) {
			if (max < magnitudes[k]) {
				max = magnitudes[k];
				maxIndex = k;
			}
		}
		return maxIndex;
	}
	
	public int maxPeakQuick(double[] magnitudes, ArrayList<Integer> peaks) {
		double max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int k = 0; k < peaks.size(); k++) {
			if (magnitudes[peaks.get(k)] > max) {
				max = magnitudes[peaks.get(k)];
				maxIndex = peaks.get(k);
			}
		}
		return maxIndex;
	}
}
