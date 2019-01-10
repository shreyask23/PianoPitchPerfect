import java.util.ArrayList;

public class SignalManager {

	private ComplexNumber[] buffer;
	private double[] noise;
	private double samplingRate;
	private double targetFreq;
	private int sampleSize;
	private int sampleCount;
	private double overlap; //1 - sample every sampleSize, 0.5 - sample every half-sampleSize
	private FFT fft = new FFT();
	private Window win = new Window();
	
	public void configSignalManager(int[] signal, double samplingRate, double targetFreq, int sampleSize, int sampleCount, double overlap) {
		configSignalManager(intsToComplex(signal), samplingRate, targetFreq, sampleSize, sampleCount, overlap);
	}
	
	public void configSignalManager(double[] signal, double samplingRate, double targetFreq, int sampleSize, int sampleCount, double overlap) {
		configSignalManager(doublesToComplex(signal), samplingRate, targetFreq, sampleSize, sampleCount, overlap);
	}
	
	public void configSignalManager(ComplexNumber[] signal, double samplingRate, double targetFreq, int sampleSize, int sampleCount, double overlap) {
		if (targetFreq > 0.5 * samplingRate) {
			throw new IllegalArgumentException("Target frequency must be below half the sampling rate. targetFreq passed in as " + targetFreq);
		}
		if (!(sampleSize > 0 && (sampleSize & (sampleSize - 1)) == 0)) {
			throw new IllegalArgumentException("Sample size must be power of two. sampleSize passed in as " + sampleSize);
		}
		if (signal.length != sampleSize * sampleCount - (sampleCount - 1) * (1 - overlap) * sampleSize) {
			throw new IllegalArgumentException("Signal length mismatch:\nsignal length: " + signal.length + "\nsample size: " + sampleSize + "\nsample count: " + sampleCount + "\noverlap: " + overlap);
		}
		buffer = signal;
		this.samplingRate = samplingRate;
		this.targetFreq = targetFreq;
		this.sampleSize = sampleSize;
		this.sampleCount = sampleCount;
		this.overlap = overlap;
	}
	
	public double[] processBuffer(String content) {
		if (buffer == null) {
			throw new NullPointerException("Signal Manager muste be configured before buffer can be processed. Buffer was null.");
		}
		long startTime = System.nanoTime();
		double[] magnitudes = avgFFT();
		long endTime = System.nanoTime();
		if (noise != null) {
			magnitudes = spectralSubtraction(magnitudes, 1, 0.01);
		}
		ArrayList<Integer> peaks = fft.findLocalPeaks(magnitudes);
		double targetAmplitude = fft.checkFrequencyPeak(magnitudes, peaks, targetFreq, samplingRate);
		int maxIndex = fft.maxPeakQuick(magnitudes, peaks);
		fftLog(content, startTime, endTime, targetAmplitude, magnitudes, maxIndex);
		return magnitudes;
	}
	
	private double[] avgFFT() {
		double[][] fftMagnitudes = new double[sampleCount][sampleSize];
		ComplexNumber[] singleSample = new ComplexNumber[sampleSize];
		for (int k = 0; k < fftMagnitudes.length; k++) {
			System.arraycopy(buffer, (int) (k * overlap * sampleSize), singleSample, 0, sampleSize);
			singleSample = win.hanningWindow(singleSample, "real");
			fftMagnitudes[k] = fft.freqToMagnitude(fft.fft(singleSample));
		}
		double[] avgMagnitudes = new double[sampleSize];
		for (int k = 0; k < avgMagnitudes.length; k++) {
			double frequencySum = 0;
			for (int j = 0; j < fftMagnitudes.length; j++) {
				frequencySum += fftMagnitudes[j][k];
			}
			avgMagnitudes[k] = frequencySum / fftMagnitudes.length;
		}
		return avgMagnitudes;
	}
	
	public double[] spectralSubtraction(double[] signal, double noiseStrength, double floorCoeff) {
		if (noise == null) {
			throw new NullPointerException("Noise must be processed to evaluate spectral subtraction. Noise array was null.");
		}
		if (signal.length != noise.length) {
			throw new IllegalArgumentException("Signal length and noise length must match. Signal length was " + signal.length + ". Noise length was " + noise.length);
		}
		double[] differences = new double[signal.length];
		for (int k = 0; k < signal.length; k++) {
			double difference = signal[k] - noiseStrength * noise[k];
			if (difference < floorCoeff * signal[k]) {
				difference = floorCoeff * signal[k];
			}
			differences[k] = difference;
		}
		return differences;
	}
	
	private void fftLog(String headline, double startTime, double endTime, double amplitude, double[] magnitudes, int maxIndex) {
		System.out.println(headline + " fftLog");
		System.out.println("FFT evaluation time: " + new Double((endTime - startTime) / 1000000).toString() + " milliseconds");
		System.out.println("Target frequency magnitude: " + amplitude);
		System.out.println("FFT maximum magnitude: " + magnitudes[maxIndex]);
		System.out.println("Maximum magnitude frequency: " + new Double(maxIndex * samplingRate / magnitudes.length).toString());
		System.out.println("FFT bin 1 below: " + magnitudes[fft.approxFreqBin(magnitudes, samplingRate, targetFreq)]);
		System.out.println("FFT bin 1 above magnitude: " + new Double(magnitudes[fft.approxFreqBin(magnitudes, samplingRate, targetFreq) + 1]));
		System.out.println("---");
	}
	
	public void setNoise() {
		noise = processBuffer("Noise");
	}
	
	private double[] intsToDoubles(int[] ints) {
		double[] doubles = new double[ints.length];
		for (int k = 0; k < doubles.length; k++) {
			doubles[k] = ints[k];
		}
		return doubles;
	}
	
	private ComplexNumber[] intsToComplex(int[] ints) {
		ComplexNumber[] imaginaries = new ComplexNumber[ints.length];
		for (int k = 0; k < imaginaries.length; k++) {
			imaginaries[k] = new ComplexNumber(ints[k], 0);
		}
		return imaginaries;
	}
	
	private ComplexNumber[] doublesToComplex(double[] doubles) {
		ComplexNumber[] imaginaries = new ComplexNumber[doubles.length];
		for (int k = 0; k < imaginaries.length; k++) {
			imaginaries[k] = new ComplexNumber(doubles[k], 0);
		}
		return imaginaries;
	}
}
