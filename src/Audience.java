import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.*;

public class Audience {
	
	static WavReader noisyCWav;
	static WavReader quietCWav;
	static WavReader constantCWav;
	static WavReader constantCNoisyWav;
	static int[] noisyCData;
	static int[] quietCData;
	static int[] constantCData;
	static int[] constantCNoisyData;
	static final int sampleSize = 8192;
	static final int sampleRate = 44100;
	static final double targetFreq = 261.6;
	static final int noiseSampleCount = 1;
	
	public static void main(String[] args) {
		//Currently testing FFT efficiency and accuracy
		loadData();
		Window window = new Window();
		int[] CChunk = new int[sampleSize];
		System.arraycopy(constantCData, 0, CChunk, 0, sampleSize);
		double[] windowedCCHunk = window.hanningWindow(CChunk);
		long startTime = System.nanoTime();
		FFT tester = new FFT(windowedCCHunk);
		ComplexNumber[] fftout = tester.fft(tester.getTData());
		long endTime = System.nanoTime();
		double[] magnitudes = tester.freqToMagnitude(fftout);
		ArrayList<Integer> peaks = tester.findLocalPeaks(magnitudes);
		double CAmplitude = tester.checkFrequencyPeak(magnitudes, peaks, targetFreq, sampleRate);
		int maxIndex = tester.maxPeakQuick(magnitudes, peaks);
		fftLog("Signal FFT", startTime, endTime, CAmplitude, magnitudes, maxIndex, tester);
		int[] noise = new int[noiseSampleCount * sampleSize];
		System.arraycopy(quietCData, 0, noise, 0, noiseSampleCount * sampleSize);
		double[] noiseDouble = new double[noiseSampleCount * sampleSize];
		for (int k = 0; k < noiseDouble.length; k++) {
			noiseDouble[k] = noise[k];
		}
		noiseDouble = window.hanningWindow(noiseDouble);
		startTime = System.nanoTime();
		tester.loadBackgroundNoise(noiseDouble);
		endTime = System.nanoTime();
		double[] adjustedMagnitudes = new double[magnitudes.length];
		adjustedMagnitudes = tester.spectralSubtraction(magnitudes, tester.getNoiseMagnitudes());
		ArrayList<Integer> adjPeaks = tester.findLocalPeaks(adjustedMagnitudes);
		double adjCAmplitude = tester.checkFrequencyPeak(adjustedMagnitudes, adjPeaks, targetFreq, sampleRate);
		int adjMaxIndex = tester.maxPeakQuick(adjustedMagnitudes, adjPeaks);
		fftLog("Noise FFT", startTime, endTime, adjCAmplitude, adjustedMagnitudes, adjMaxIndex, tester);
	}

	private static void loadData() {
		noisyCWav = new WavReader("resources/MiddleCHighNoise.wav");
		quietCWav = new WavReader("resources/MiddleCLowNoise.wav");
		constantCWav = new WavReader("resources/MiddleCConstant.wav");
		constantCNoisyWav = new WavReader("resources/MiddleCNoisyConstant.wav");
		noisyCData = noisyCWav.wavArray();
		quietCData = quietCWav.wavArray();
		constantCData = constantCWav.wavArray();
		constantCNoisyData = constantCNoisyWav.wavArray();
	}
	
	private static void fftLog(String content, double startTime, double endTime, double amplitude, double[] magnitudes, int maxIndex, FFT tester) {
		System.out.println(content +  " evaluation time: " + new Double((endTime - startTime) / 1000000).toString() + " milliseconds.");
		System.out.println("C frequency magnitude: " + new Double(amplitude).toString());
		System.out.println("FFT maximum magnitude: " + new Double(magnitudes[maxIndex]).toString());
		System.out.println("Maximum magnitude frequency: " + new Double(maxIndex * sampleRate / magnitudes.length).toString());
		System.out.println("FFT bin 1 below: " + new Double(magnitudes[tester.approxFreqBin(magnitudes, sampleRate, targetFreq)]).toString());
		System.out.println("FFT bin 1 above magnitude: " + new Double(magnitudes[tester.approxFreqBin(magnitudes, sampleRate, targetFreq) + 1]));
		System.out.println("---");
	}
}

/* WavFile.java and WavFileException.java classes are both courtesy
 * of Dr. Andrew Greenstead and can be found at http://www.labbookpages.co.uk/audio/javaWavFiles.html.
 */
